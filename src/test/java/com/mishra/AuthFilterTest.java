package com.mishra;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;

class AuthFilterTest {
  private final AuthFilter filter = new AuthFilter();
  private final Map<String, Object> sessionAttributes = new HashMap<>();
  private final HttpSession session = fake(HttpSession.class, this::sessionCall);
  private boolean passedThrough;
  private String redirect;
  private Integer error;

  @Test
  void publicPagesPassWithoutSession() throws Exception {
    run("POST", "/OnlineLoginServlet", null, Map.of());
    assertTrue(passedThrough);
    run("GET", "/CSCI6810/login.html", null, Map.of());
    assertTrue(passedThrough);
  }

  @Test
  void protectedPagesRedirectToLoginWithoutAuthenticatedSession() throws Exception {
    run("GET", "/CSCI6810/Deposit.jsp", null, Map.of("UserID", "victim"));
    assertFalse(passedThrough);
    assertEquals("/login.html", redirect);

    run("GET", "/CSCI6810/AccountOverview.jsp", session, Map.of());
    assertFalse(passedThrough);
    assertEquals("/login.html", redirect);
  }

  @Test
  void authenticatedGetPasses() throws Exception {
    sessionAttributes.put(AuthFilter.USER_ID, "alice");
    run("GET", "/CSCI6810/AccountOverview.jsp", session, Map.of());
    assertTrue(passedThrough);
  }

  @Test
  void postRequiresTheSessionCsrfToken() throws Exception {
    sessionAttributes.put(AuthFilter.USER_ID, "alice");
    String token = AuthFilter.csrfToken(session);

    run("POST", "/CSCI6810/Deposit.jsp", session, Map.of());
    assertEquals(HttpServletResponse.SC_FORBIDDEN, error);
    run("POST", "/CSCI6810/Deposit.jsp", session, Map.of(AuthFilter.CSRF_TOKEN, "wrong"));
    assertEquals(HttpServletResponse.SC_FORBIDDEN, error);
    run("POST", "/CSCI6810/Deposit.jsp", session, Map.of(AuthFilter.CSRF_TOKEN, token));
    assertTrue(passedThrough);
    assertNull(error);
  }

  @Test
  void csrfTokenIsStablePerSessionAndRandomAcrossSessions() {
    String token = AuthFilter.csrfToken(session);
    assertEquals(token, AuthFilter.csrfToken(session));
    sessionAttributes.clear();
    assertNotEquals(token, AuthFilter.csrfToken(session));
    assertTrue(token.length() >= 40);
  }

  private void run(String method, String path, HttpSession current, Map<String, String> params)
      throws Exception {
    passedThrough = false;
    redirect = null;
    error = null;
    HttpServletRequest req =
        fake(
            HttpServletRequest.class,
            (name, args) ->
                switch (name) {
                  case "getMethod" -> method;
                  case "getServletPath" -> path;
                  case "getContextPath" -> "";
                  case "getSession" -> current;
                  case "getParameter" -> params.get((String) args[0]);
                  default -> null;
                });
    HttpServletResponse res =
        fake(
            HttpServletResponse.class,
            (name, args) -> {
              if (name.equals("sendRedirect")) {
                redirect = (String) args[0];
              } else if (name.equals("sendError")) {
                error = (Integer) args[0];
              }
              return null;
            });
    FilterChain chain = (request, response) -> passedThrough = true;
    filter.doFilter(req, res, chain);
  }

  private Object sessionCall(String name, Object[] args) {
    return switch (name) {
      case "getAttribute" -> sessionAttributes.get((String) args[0]);
      case "setAttribute" -> sessionAttributes.put((String) args[0], args[1]);
      default -> null;
    };
  }

  private interface Handler {
    Object handle(String method, Object[] args);
  }

  private static <T> T fake(Class<T> type, Handler handler) {
    return type.cast(
        Proxy.newProxyInstance(
            type.getClassLoader(),
            new Class<?>[] {type},
            (proxy, method, args) -> {
              if (method.getDeclaringClass() == Object.class) {
                return switch (method.getName()) {
                  case "hashCode" -> System.identityHashCode(proxy);
                  case "equals" -> proxy == args[0];
                  default -> type.getSimpleName();
                };
              }
              return handler.handle(method.getName(), args);
            }));
  }
}
