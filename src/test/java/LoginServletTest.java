import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

import com.mishra.Account;

class LoginServletTest {
    @Test
    void doPostForSuccessfulLoginForwardsWithAttributes() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        RequestDispatcher dispatcher = mock(RequestDispatcher.class);
        StringWriter body = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(body));
        when(request.getParameter("UserName")).thenReturn("alice");
        when(request.getParameter("PassWord")).thenReturn("secret");
        when(request.getRequestDispatcher("/CSCI6810/afterlogin.jsp")).thenReturn(dispatcher);
        try (MockedConstruction<Account> ignored = mockConstruction(Account.class,
                (mock, context) -> when(mock.signIn()).thenReturn("Alice"))) {
            new LoginServlet().doPost(request, response);
        }
        verify(response).setContentType("text/html");
        verify(request).setAttribute("Username", "alice");
        verify(request).setAttribute("CustomerName", "Alice");
        verify(dispatcher).forward(request, response);
        assertEquals("", body.toString());
    }

    @Test
    void doPostPrintsFailureForUnknownLogin() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        StringWriter body = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(body));
        when(request.getParameter("UserName")).thenReturn("alice");
        when(request.getParameter("PassWord")).thenReturn("wrong");
        try (MockedConstruction<Account> ignored = mockConstruction(Account.class,
                (mock, context) -> when(mock.signIn()).thenReturn(""))) {
            new LoginServlet().doPost(request, response);
        }
        assertEquals("login failed\n", body.toString());
        verify(request, never()).getRequestDispatcher(anyString());
    }

    @Test
    void doGetDelegatesToPost() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        StringWriter body = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(body));
        when(request.getParameter(anyString())).thenReturn("");
        try (MockedConstruction<Account> ignored = mockConstruction(Account.class,
                (mock, context) -> when(mock.signIn()).thenReturn(""))) {
            new LoginServlet().doGet(request, response);
        }
        assertEquals("login failed\n", body.toString());
        verify(response).setContentType("text/html");
    }

    @Test
    void initAndDestroyManageServletState() throws Exception {
        LoginServlet servlet = new LoginServlet();
        servlet.init(mock(ServletConfig.class));
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        StringWriter body = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(body));
        when(request.getParameter(anyString())).thenReturn("");
        try (MockedConstruction<Account> ignored = mockConstruction(Account.class,
                (mock, context) -> when(mock.signIn()).thenReturn(""))) {
            servlet.doPost(request, response);
        }
        assertDoesNotThrow(servlet::destroy);
    }
}
