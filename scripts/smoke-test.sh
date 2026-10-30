#!/usr/bin/env bash
# End-to-end smoke test against a running deployment: sign up, log in, load the main pages,
# and check the session, IDOR and CSRF protections.
set -euo pipefail
BASE_URL="${BASE_URL:-http://localhost:8080}"
USER="smoke$(date +%s)"
JAR="$(mktemp)"
trap 'rm -f "$JAR"' EXIT
fail() { echo "FAIL: $*" >&2; exit 1; }
status() { curl -s -o /dev/null -w '%{http_code}' "$@"; }

curl -fsS "$BASE_URL/" | grep -q "Java Banking Project" || fail "index.html"
curl -fsS "$BASE_URL/CSCI6810/SignUp.html" | grep -q "online registration" || fail "SignUp.html"
if curl -fsS "$BASE_URL/CSCI6810/login.html" | grep -q "UserID="; then fail "login.html still links with UserID"; fi

curl -fsS -X POST "$BASE_URL/CSCI6810/SignUp.jsp" \
  --data-urlencode "UsernameField=$USER" --data-urlencode "PasswordField=pw123" \
  --data-urlencode "RePasswordField=pw123" --data-urlencode "NameField=Smoke Tester" \
  | grep -q "Congratulations" || fail "sign up via SignUp.jsp"

curl -fsS -X POST "$BASE_URL/OnlineSignUpServlet" \
  --data-urlencode "UsernameField=${USER}s" --data-urlencode "PasswordField=pw123" \
  --data-urlencode "RePasswordField=pw123" --data-urlencode "NameField=Smoke Tester" \
  | grep -q "Congratulations" || fail "sign up via OnlineSignUpServlet"

[[ "$(status "$BASE_URL/OnlineLoginServlet?UserName=$USER&PassWord=pw123")" == 405 ]] \
  || fail "login over GET should be rejected"

curl -fsS -X POST "$BASE_URL/OnlineLoginServlet" \
  --data-urlencode "UserName=$USER" --data-urlencode "PassWord=wrong" \
  | grep -q "login failed" || fail "login with bad password should fail"

curl -fsS -X POST "$BASE_URL/OnlineLoginServlet" \
  --data-urlencode "UserName=$USER' OR '1'='1" --data-urlencode "PassWord=x' OR '1'='1" \
  | grep -q "login failed" || fail "SQL injection login should fail"

for page in Deposit Withdraw Transfer OpenBankAccount AccountOverview afterlogin; do
  [[ "$(status "$BASE_URL/CSCI6810/$page.jsp?UserID=$USER")" == 302 ]] \
    || fail "$page.jsp should redirect to login without a session"
done

curl -fsS -c "$JAR" -X POST "$BASE_URL/OnlineLoginServlet" \
  --data-urlencode "UserName=$USER" --data-urlencode "PassWord=pw123" \
  | grep -q "Welcome to your link page" || fail "login with valid credentials"

for page in Deposit Withdraw Transfer OpenBankAccount AccountOverview; do
  curl -fsS -b "$JAR" "$BASE_URL/CSCI6810/$page.jsp" | grep -q "<FORM" || fail "$page.jsp"
done
TOKEN="$(curl -fsS -b "$JAR" "$BASE_URL/CSCI6810/Deposit.jsp" \
  | sed -n "s/.*NAME='csrfToken' VALUE='\([^']*\)'.*/\1/p")"
[[ -n "$TOKEN" ]] || fail "Deposit.jsp should carry a CSRF token"

[[ "$(status -b "$JAR" -X POST "$BASE_URL/CSCI6810/Deposit.jsp" -d "AmountField=5")" == 403 ]] \
  || fail "POST without a CSRF token should be rejected"
[[ "$(status -b "$JAR" -X POST "$BASE_URL/CSCI6810/Deposit.jsp" -d "AmountField=5&csrfToken=bogus")" == 403 ]] \
  || fail "POST with a wrong CSRF token should be rejected"
[[ "$(status -b "$JAR" -X POST "$BASE_URL/CSCI6810/Deposit.jsp" \
  --data-urlencode "AmountField=5" --data-urlencode "csrfToken=$TOKEN")" == 200 ]] \
  || fail "POST with the CSRF token should be accepted"

[[ "$(status -b "$JAR" -X POST "$BASE_URL/LogoutServlet" --data-urlencode "csrfToken=$TOKEN")" == 302 ]] \
  || fail "logout"
[[ "$(status -b "$JAR" "$BASE_URL/CSCI6810/Deposit.jsp")" == 302 ]] \
  || fail "session should be gone after logout"

echo "Smoke test passed"
