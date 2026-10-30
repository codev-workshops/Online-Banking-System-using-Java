package com.mishra;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Set;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Lets only logged-in customers reach anything but the public login and sign-up pages, and rejects
 * state-changing requests that lack the session's CSRF token.
 *
 * <p>The customer is identified solely by the {@link #USER_ID} session attribute set at login;
 * pages and servlets must never take the customer ID from request parameters.
 */
public class AuthFilter implements Filter {
  /** Session attribute holding the logged-in username (the CustomerID in the database). */
  public static final String USER_ID = "UserID";

  /** Session attribute holding the logged-in customer's display name. */
  public static final String CUSTOMER_NAME = "CustomerName";

  /** Session attribute and form field name of the CSRF token. */
  public static final String CSRF_TOKEN = "csrfToken";

  private static final String CSRF_HEADER = "X-CSRF-Token";
  private static final String LEGACY_PREFIX = "/CSCI6810";
  private static final Set<String> PUBLIC_PATHS =
      Set.of(
          "/",
          "/index.html",
          "/Logo.html",
          "/Display.html",
          "/login.html",
          "/SignUp.html",
          "/SignUp.jsp",
          "/error.html",
          "/OnlineLoginServlet",
          "/LoginSrvlet",
          "/OnlineSignUpServlet");
  private static final SecureRandom RANDOM = new SecureRandom();

  @Override
  public void init(FilterConfig filterConfig) {}

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    HttpServletRequest req = (HttpServletRequest) request;
    HttpServletResponse res = (HttpServletResponse) response;

    if (isPublic(req)) {
      chain.doFilter(request, response);
      return;
    }
    HttpSession session = req.getSession(false);
    if (session == null || session.getAttribute(USER_ID) == null) {
      res.sendRedirect(req.getContextPath() + "/login.html");
      return;
    }
    if (isStateChanging(req) && !hasValidCsrfToken(req, session)) {
      res.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid or missing CSRF token");
      return;
    }
    chain.doFilter(request, response);
  }

  @Override
  public void destroy() {}

  /** Returns the session's CSRF token, creating it on first use. */
  public static String csrfToken(HttpSession session) {
    synchronized (session) {
      String token = (String) session.getAttribute(CSRF_TOKEN);
      if (token == null) {
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        session.setAttribute(CSRF_TOKEN, token);
      }
      return token;
    }
  }

  private static boolean isPublic(HttpServletRequest req) {
    String path = req.getServletPath() + (req.getPathInfo() == null ? "" : req.getPathInfo());
    if (path.equals(LEGACY_PREFIX) || path.startsWith(LEGACY_PREFIX + "/")) {
      path = path.substring(LEGACY_PREFIX.length());
    }
    return path.isEmpty() || PUBLIC_PATHS.contains(path);
  }

  private static boolean isStateChanging(HttpServletRequest req) {
    String method = req.getMethod();
    return !("GET".equals(method) || "HEAD".equals(method) || "OPTIONS".equals(method));
  }

  private static boolean hasValidCsrfToken(HttpServletRequest req, HttpSession session) {
    String expected = (String) session.getAttribute(CSRF_TOKEN);
    String actual = req.getHeader(CSRF_HEADER);
    if (actual == null) {
      actual = req.getParameter(CSRF_TOKEN);
    }
    return expected != null
        && actual != null
        && MessageDigest.isEqual(
            expected.getBytes(StandardCharsets.UTF_8), actual.getBytes(StandardCharsets.UTF_8));
  }
}
