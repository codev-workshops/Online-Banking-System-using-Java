import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.mockito.MockedConstruction;

import com.mishra.Account;

public class LoginServletTest {

    @Test
    public void doPost_forwardsToAfterLoginOnSuccess() throws Exception {
        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse res = mock(HttpServletResponse.class);
        RequestDispatcher rd = mock(RequestDispatcher.class);

        when(req.getParameter("UserName")).thenReturn("john");
        when(req.getParameter("PassWord")).thenReturn("pw");
        when(res.getWriter()).thenReturn(new PrintWriter(new StringWriter()));
        when(req.getRequestDispatcher(anyString())).thenReturn(rd);

        try (MockedConstruction<Account> mc = mockConstruction(Account.class,
                (mock, ctx) -> when(mock.signIn()).thenReturn("John Doe"))) {
            new LoginServlet().doPost(req, res);
        }

        verify(req).setAttribute("Username", "john");
        verify(req).setAttribute("CustomerName", "John Doe");
        verify(rd).forward(req, res);
    }

    @Test
    public void doPost_printsLoginFailedOnFailure() throws Exception {
        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse res = mock(HttpServletResponse.class);
        StringWriter sw = new StringWriter();

        when(req.getParameter("UserName")).thenReturn("john");
        when(req.getParameter("PassWord")).thenReturn("bad");
        when(res.getWriter()).thenReturn(new PrintWriter(sw));

        try (MockedConstruction<Account> mc = mockConstruction(Account.class,
                (mock, ctx) -> when(mock.signIn()).thenReturn(""))) {
            new LoginServlet().doPost(req, res);
        }

        assertTrue(sw.toString().contains("login failed"));
    }

    @Test
    public void doGet_delegatesToDoPost() throws Exception {
        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse res = mock(HttpServletResponse.class);
        StringWriter sw = new StringWriter();

        when(req.getParameter(anyString())).thenReturn("");
        when(res.getWriter()).thenReturn(new PrintWriter(sw));

        try (MockedConstruction<Account> mc = mockConstruction(Account.class,
                (mock, ctx) -> when(mock.signIn()).thenReturn(""))) {
            new LoginServlet().doGet(req, res);
        }

        assertTrue(sw.toString().contains("login failed"));
    }

    @Test
    public void initAndDestroy_manageLifecycle() throws Exception {
        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse res = mock(HttpServletResponse.class);
        ServletConfig config = mock(ServletConfig.class);
        when(req.getParameter(anyString())).thenReturn("");
        when(res.getWriter()).thenReturn(new PrintWriter(new StringWriter()));

        LoginServlet servlet = new LoginServlet();
        servlet.init(config);
        try (MockedConstruction<Account> mc = mockConstruction(Account.class,
                (mock, ctx) -> when(mock.signIn()).thenReturn(""))) {
            servlet.doPost(req, res);
        }
        servlet.destroy();
    }
}
