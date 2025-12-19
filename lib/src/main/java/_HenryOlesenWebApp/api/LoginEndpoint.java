package _HenryOlesenWebApp.api;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import _HenryOlesenWebApp.service.AccountService;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/login") 
public class LoginEndpoint extends HttpServlet  {
	private static final long serialVersionUID = 1L;
	private AccountService accountService;
	
	public void init(ServletConfig config) throws ServletException {
		EndpointUtils.commonInit(config);
		accountService = new AccountService();
		
	    super.init(config);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Map<String, String> body = EndpointUtils.parseJsonFromRequest(request);
        String username = body.getOrDefault("username", "");
        String password = body.getOrDefault("password", "");
		
		if (accountService.ValidateLogin(username, password)) {
			// Create a logged-in session
			HttpSession session = request.getSession(false);
			if (session != null) session.invalidate();
			session = request.getSession(true);
			session.setMaxInactiveInterval(30*60); // 30 minute timeout
			session.setAttribute("username", username);
			
			// Redirect user to homepage
			response.sendRedirect("/user/home");
		} else {
			response.setContentType("text/plain");
	        PrintWriter out = response.getWriter();
	        out.println("Incorrect username or password.");
		}
    }
}
