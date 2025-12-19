package _HenryOlesenWebApp.api;

import java.io.IOException;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/user/logout") 
public class LogoutEndpoint extends HttpServlet  {
	private static final long serialVersionUID = 1L;
	
	public void init(ServletConfig config) throws ServletException {
		EndpointUtils.commonInit(config);
	    super.init(config);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Invalidate the current user session
		HttpSession session = request.getSession(false);
		if (session != null) session.invalidate();
		
		// Redirect user to logged-out landing page
		response.sendRedirect("/");
    }
}
