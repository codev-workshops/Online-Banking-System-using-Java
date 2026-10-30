#!/usr/bin/env bash
# End-to-end smoke test against a running deployment: sign up, log in, load the main pages.
set -euo pipefail
BASE_URL="${BASE_URL:-http://localhost:8080}"
USER="smoke$(date +%s)"
fail() { echo "FAIL: $*" >&2; exit 1; }

curl -fsS "$BASE_URL/" | grep -q "Java Banking Project" || fail "index.html"
curl -fsS "$BASE_URL/CSCI6810/SignUp.html" | grep -q "online registration" || fail "SignUp.html"

curl -fsS -X POST "$BASE_URL/CSCI6810/SignUp.jsp" \
  --data-urlencode "UsernameField=$USER" --data-urlencode "PasswordField=pw123" \
  --data-urlencode "RePasswordField=pw123" --data-urlencode "NameField=Smoke Tester" \
  | grep -q "Congratulations" || fail "sign up via SignUp.jsp"

curl -fsS -X POST "$BASE_URL/OnlineSignUpServlet" \
  --data-urlencode "UsernameField=${USER}s" --data-urlencode "PasswordField=pw123" \
  --data-urlencode "RePasswordField=pw123" --data-urlencode "NameField=Smoke Tester" \
  | grep -q "Congratulations" || fail "sign up via OnlineSignUpServlet"

curl -fsS -X POST "$BASE_URL/OnlineLoginServlet" \
  --data-urlencode "UserName=$USER" --data-urlencode "PassWord=pw123" \
  | grep -q "Welcome to your link page" || fail "login with valid credentials"

curl -fsS -X POST "$BASE_URL/OnlineLoginServlet" \
  --data-urlencode "UserName=$USER" --data-urlencode "PassWord=wrong" \
  | grep -q "login failed" || fail "login with bad password should fail"

for page in Deposit Withdraw Transfer OpenBankAccount; do
  curl -fsS "$BASE_URL/CSCI6810/$page.jsp?UserID=$USER" | grep -q "<FORM" || fail "$page.jsp"
done

echo "Smoke test passed"
