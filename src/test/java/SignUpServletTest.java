import com.mishra.Account;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SignUpServletTest {

    private HttpServletRequest req;
    private HttpServletResponse res;
    private PrintWriter writer;
    private Account account;

    private static class TestableSignUpServlet extends SignUpServlet {
        private final Account account;
        TestableSignUpServlet(Account account) {
            this.account = account;
        }
        @Override
        protected Account createAccount(String u, String p, String r, String n) {
            return account;
        }
    }

    private TestableSignUpServlet servlet;

    @BeforeEach
    void setUp() throws Exception {
        req = mock(HttpServletRequest.class);
        res = mock(HttpServletResponse.class);
        writer = mock(PrintWriter.class);
        account = mock(Account.class);
        when(res.getWriter()).thenReturn(writer);
        when(req.getParameter("UsernameField")).thenReturn("user");
        when(req.getParameter("PasswordField")).thenReturn("pw");
        when(req.getParameter("RePasswordField")).thenReturn("pw");
        when(req.getParameter("NameField")).thenReturn("John Doe");
        servlet = new TestableSignUpServlet(account);
    }

    @Test
    void signUpSuccessWritesHtml() throws Exception {
        when(account.signUp()).thenReturn(true);
        servlet.doPost(req, res);
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(writer).println(captor.capture());
        String html = captor.getValue();
        assertTrue(html.contains("Congratulations! You have an account with us."));
        assertTrue(html.contains("<FORM NAME=\"LoginPage\""));
    }

    @Test
    void signUpFailureWritesMessage() throws Exception {
        when(account.signUp()).thenReturn(false);
        servlet.doPost(req, res);
        verify(writer).println(contains("Account creation failed"));
    }

    @Test
    void doGetDelegatesToDoPost() throws Exception {
        when(account.signUp()).thenReturn(false);
        servlet.doGet(req, res);
        verify(writer).println(contains("Account creation failed"));
    }

    @Test
    void showSuccessWritesExpectedHtml() throws Exception {
        when(account.signUp()).thenReturn(true);
        servlet.doPost(req, res); // initializes output
        // showSuccess already invoked once by the successful doPost above
        verify(writer, atLeastOnce()).println(contains("You can login now"));
    }

    @Test
    void initAndDestroy() throws Exception {
        ServletConfig config = mock(ServletConfig.class);
        servlet.init(config);
        when(account.signUp()).thenReturn(false);
        servlet.doPost(req, res);
        servlet.destroy();
        verify(writer).close();
    }
}
