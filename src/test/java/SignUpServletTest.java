import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

import com.mishra.Account;

class SignUpServletTest {
    private void requestParameters(HttpServletRequest request) {
        when(request.getParameter("UsernameField")).thenReturn("alice");
        when(request.getParameter("PasswordField")).thenReturn("secret");
        when(request.getParameter("RePasswordField")).thenReturn("secret");
        when(request.getParameter("NameField")).thenReturn("Alice");
    }

    @Test
    void doPostShowsLoginFormAfterSuccessfulSignup() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        StringWriter body = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(body));
        requestParameters(request);
        try (MockedConstruction<Account> ignored = mockConstruction(Account.class,
                (mock, context) -> when(mock.signUp()).thenReturn(true))) {
            new SignUpServlet().doPost(request, response);
        }
        assertTrue(body.toString().contains("alice"));
        assertTrue(body.toString().contains("ACTION=\"/LoginSrvlet\""));
        assertTrue(body.toString().contains("Congratulations!"));
        verify(response).setContentType("text/html");
    }

    @Test
    void doPostShowsFailureAfterRejectedSignup() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        StringWriter body = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(body));
        requestParameters(request);
        try (MockedConstruction<Account> ignored = mockConstruction(Account.class,
                (mock, context) -> when(mock.signUp()).thenReturn(false))) {
            new SignUpServlet().doPost(request, response);
        }
        assertTrue(body.toString().contains("Account creation failed"));
    }

    @Test
    void doGetDelegatesToPost() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        StringWriter body = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(body));
        requestParameters(request);
        try (MockedConstruction<Account> ignored = mockConstruction(Account.class,
                (mock, context) -> when(mock.signUp()).thenReturn(false))) {
            new SignUpServlet().doGet(request, response);
        }
        assertTrue(body.toString().contains("Account creation failed"));
        verify(response).setContentType("text/html");
    }

    @Test
    void initAndDestroyManageServletState() throws Exception {
        SignUpServlet servlet = new SignUpServlet();
        servlet.init(mock(ServletConfig.class));
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        StringWriter body = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(body));
        requestParameters(request);
        try (MockedConstruction<Account> ignored = mockConstruction(Account.class,
                (mock, context) -> when(mock.signUp()).thenReturn(false))) {
            servlet.doPost(request, response);
        }
        assertDoesNotThrow(servlet::destroy);
    }
}
