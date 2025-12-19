package _HenryOlesenWebApp.api;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import _HenryOlesenWebApp.service.User;
import _HenryOlesenWebApp.service.UserInfoService;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/user/userinfo") 
public class UserInfoEndpoint extends HttpServlet  {
	private static final long serialVersionUID = 1L;
	private UserInfoService userInfoService;
	
	public void init(ServletConfig config) throws ServletException {
		EndpointUtils.commonInit(config);
		userInfoService = new UserInfoService();
		
	    super.init(config);
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String username = EndpointUtils.getUsernameFromSession(request);
		if (username == null) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}
		
		User user = userInfoService.getByUsername(username);
		
		if (user == null) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			return;
		} else {
			response.setContentType("application/json");
	        PrintWriter out = response.getWriter();
	        out.println(user.toJson());
		}
    }
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String username = EndpointUtils.getUsernameFromSession(request);
		if (username == null) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}
		
		Map<String, String> body = EndpointUtils.parseJsonFromRequest(request);
        String email = body.getOrDefault("email", "");
        
        if (userInfoService.changeUserEmail(username, email)) {
        	response.setStatus(HttpServletResponse.SC_OK);
        } else {
        	response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
	}
}
