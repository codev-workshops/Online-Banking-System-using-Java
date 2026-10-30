/******************************************************************************
 *	Program Author: Dr. Yongming Tang for CSCI 6810 Java and the Internet	  *
 *	Date: September, 2012													  *
 *******************************************************************************/

import com.mishra.Account;
import com.mishra.AuthFilter;
import java.io.IOException;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/** Checks a customer's credentials and starts their session. Accepts POST only. */
public class LoginServlet extends HttpServlet {
  private static final Logger LOG = Logger.getLogger(LoginServlet.class.getName());

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
    res.setContentType("text/html");
    String username = req.getParameter("UserName");
    String password = req.getParameter("PassWord");
    String customerName = new Account(username, password).signIn();
    if (customerName == null || "".equals(customerName)) {
      res.getWriter().println("login failed");
      return;
    }

    // Start a fresh session so a session ID planted before login can't be reused.
    HttpSession oldSession = req.getSession(false);
    if (oldSession != null) {
      oldSession.invalidate();
    }
    HttpSession session = req.getSession(true);
    session.setAttribute(AuthFilter.USER_ID, username);
    session.setAttribute(AuthFilter.CUSTOMER_NAME, customerName);
    AuthFilter.csrfToken(session);
    LOG.info("login username=" + username.replaceAll("[\\r\\n]", "_"));
    req.getRequestDispatcher("/CSCI6810/afterlogin.jsp").forward(req, res);
  }
}
