/******************************************************************************
 *	Program Author: Dr. Yongming Tang for CSCI 6810 Java and the Internet	  *
 *	Date: September, 2012													  *
 *******************************************************************************/

import com.mishra.Account;
import com.mishra.Html;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Creates a customer login account. Accepts POST only. */
public class SignUpServlet extends HttpServlet {
  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
    PrintWriter output = res.getWriter();
    res.setContentType("text/html");
    String username = req.getParameter("UsernameField");
    String password = req.getParameter("PasswordField");
    String reEnterPassword = req.getParameter("RePasswordField");
    String customerName = req.getParameter("NameField");

    Account acct = new Account(username, password, reEnterPassword, customerName);
    if (acct.signUp()) {
      showSuccess(output, username);
    } else {
      output.println(
          "Account creation failed because of existing username or invalid username."
              + " Please try again!");
    }
  }

  private void showSuccess(PrintWriter output, String username) {
    StringBuilder buf = new StringBuilder();
    buf.append("<HTML><HEAD></HEAD>\n");
    buf.append("<BODY bgcolor='#F1F1FD'>\n");
    buf.append(
        "<h4 ALIGN='center'>Congratulations! You have an account with us. Thank you! You can login"
            + " now.</h4>\n");
    buf.append("<FORM NAME=\"LoginPage\" ACTION=\"/LoginSrvlet\" METHOD =\"POST\">\n");

    buf.append("<TABLE cellPadding='3' ALIGN='center'>\n");
    buf.append("<TR bgcolor='#ECFAEB'>\n");
    buf.append("<TD>USERNAME:</TD>\n");
    buf.append("<TD>\n");
    buf.append(
        "<INPUT TYPE='text' NAME='UserName' Value=\""
            + Html.escape(username)
            + "\" SIZE='15' focused>\n");
    buf.append("</TD>\n");
    buf.append("</TR>\n");

    buf.append("<TR bgcolor='#ECFAEB'>\n");
    buf.append("<TD>PASSWORD:</TD>\n");
    buf.append("<TD>\n");
    buf.append("<INPUT TYPE='password' NAME='PassWord' Value='' SIZE='15'>\n");
    buf.append("<INPUT TYPE='button' NAME='submitBTN' VALUE='Login' onClick=\"checkInputs()\">\n");
    buf.append("</TD>\n");
    buf.append("</TR>\n");
    buf.append("</TABLE>\n");

    buf.append("</FORM>\n");
    buf.append("</BODY>\n");

    buf.append("<SCRIPT LANGUAGE=\"JavaScript\">\n");
    buf.append("function checkInputs()\n");
    buf.append("{\n");
    buf.append("var Prompts = \"\";\n");
    buf.append("Username = window.document.LoginPage.UserName.value;\n");
    buf.append("Password = window.document.LoginPage.PassWord.value;\n");
    buf.append("if (Username == \"\" || Password == \"\") {\n");
    buf.append("if (Username == \"\")\n");
    buf.append("Prompts +=\"Please enter your username!\\n\";\n");
    buf.append("if (Password == \"\")\n");
    buf.append("Prompts +=\"Please enter your password!\\n\";\n");
    buf.append("if (Prompts != \"\")\n");
    buf.append("window.alert(Prompts);\n");
    buf.append("} else {\n");
    buf.append("document.LoginPage.submit();\n");
    buf.append("}\n");
    buf.append("}\n");

    buf.append("</SCRIPT>\n");
    buf.append("</HTML>\n");
    output.println(buf.toString());
  }
}
