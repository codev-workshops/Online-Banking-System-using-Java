import com.mishra.Account;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

import static org.mockito.Mockito.*;

class LoginServletTest {

    private HttpServletRequest req;
    private HttpServletResponse res;
    private RequestDispatcher dispatcher;
    private PrintWriter writer;
    private Account account;

    private static class TestableLoginServlet extends LoginServlet {
        private final Account account;
        TestableLoginServlet(Account account) {
            this.account = account;
        }
        @Override
        protected Account createAccount(String username, String password) {
            return account;
        }
    }

    private TestableLoginServlet servlet;

    @BeforeEach
    void setUp() throws Exception {
        req = mock(HttpServletRequest.class);
        res = mock(HttpServletResponse.class);
        dispatcher = mock(RequestDispatcher.class);
        writer = mock(PrintWriter.class);
        account = mock(Account.class);
        when(res.getWriter()).thenReturn(writer);
        when(req.getParameter("UserName")).thenReturn("user");
        when(req.getParameter("PassWord")).thenReturn("pw");
        when(req.getRequestDispatcher(anyString())).thenReturn(dispatcher);
        servlet = new TestableLoginServlet(account);
    }

    @Test
    void successfulLoginForwards() throws Exception {
        when(account.signIn()).thenReturn("John Doe");
        servlet.doPost(req, res);
        verify(req).setAttribute("Username", "user");
        verify(req).setAttribute("CustomerName", "John Doe");
        verify(dispatcher).forward(req, res);
        verify(writer, never()).println("login failed");
    }

    @Test
    void failedLoginWritesMessage() throws Exception {
        when(account.signIn()).thenReturn("");
        servlet.doPost(req, res);
        verify(writer).println("login failed");
        verify(dispatcher, never()).forward(any(), any());
    }

    @Test
    void doGetDelegatesToDoPost() throws Exception {
        when(account.signIn()).thenReturn("John Doe");
        servlet.doGet(req, res);
        verify(dispatcher).forward(req, res);
    }

    @Test
    void initAndDestroy() throws Exception {
        ServletConfig config = mock(ServletConfig.class);
        servlet.init(config);
        // exercise doPost so the output writer is initialized before destroy()
        when(account.signIn()).thenReturn("");
        servlet.doPost(req, res);
        servlet.destroy();
        verify(writer).close();
    }
}
