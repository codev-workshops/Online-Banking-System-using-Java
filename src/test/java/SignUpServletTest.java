import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.mockito.MockedConstruction;

import com.mishra.Account;

public class SignUpServletTest {

    private void stubParams(HttpServletRequest req) {
        when(req.getParameter("UsernameField")).thenReturn("john");
        when(req.getParameter("PasswordField")).thenReturn("pw");
        when(req.getParameter("RePasswordField")).thenReturn("pw");
        when(req.getParameter("NameField")).thenReturn("John Doe");
    }

    @Test
    public void doPost_showsSuccessWhenSignUpSucceeds() throws Exception {
        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse res = mock(HttpServletResponse.class);
        StringWriter sw = new StringWriter();
        stubParams(req);
        when(res.getWriter()).thenReturn(new PrintWriter(sw));

        try (MockedConstruction<Account> mc = mockConstruction(Account.class,
                (mock, ctx) -> when(mock.signUp()).thenReturn(true))) {
            new SignUpServlet().doPost(req, res);
        }

        assertTrue(sw.toString().contains("Congratulations"));
    }

    @Test
    public void doPost_printsFailureWhenSignUpFails() throws Exception {
        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse res = mock(HttpServletResponse.class);
        StringWriter sw = new StringWriter();
        stubParams(req);
        when(res.getWriter()).thenReturn(new PrintWriter(sw));

        try (MockedConstruction<Account> mc = mockConstruction(Account.class,
                (mock, ctx) -> when(mock.signUp()).thenReturn(false))) {
            new SignUpServlet().doPost(req, res);
        }

        assertTrue(sw.toString().contains("Account creation failed"));
    }

    @Test
    public void doGet_delegatesToDoPost() throws Exception {
        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse res = mock(HttpServletResponse.class);
        StringWriter sw = new StringWriter();
        stubParams(req);
        when(res.getWriter()).thenReturn(new PrintWriter(sw));

        try (MockedConstruction<Account> mc = mockConstruction(Account.class,
                (mock, ctx) -> when(mock.signUp()).thenReturn(true))) {
            new SignUpServlet().doGet(req, res);
        }

        assertTrue(sw.toString().contains("Congratulations"));
    }

    @Test
    public void initAndDestroy_manageLifecycle() throws Exception {
        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse res = mock(HttpServletResponse.class);
        ServletConfig config = mock(ServletConfig.class);
        stubParams(req);
        when(res.getWriter()).thenReturn(new PrintWriter(new StringWriter()));

        SignUpServlet servlet = new SignUpServlet();
        servlet.init(config);
        try (MockedConstruction<Account> mc = mockConstruction(Account.class,
                (mock, ctx) -> when(mock.signUp()).thenReturn(false))) {
            servlet.doPost(req, res);
        }
        servlet.destroy();
    }
}
