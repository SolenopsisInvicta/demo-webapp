package _HenryOlesenWebApp.api;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Filter that protects /user endpoints
 */
@WebFilter("/user/*")
public class AuthenticationFilter implements Filter {
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest)request;
		HttpSession session = httpRequest.getSession(false);

		// Verify the user is logged in
		if (session != null && session.getAttribute("username") != null) {
			chain.doFilter(request, response);
		} else {
			((HttpServletResponse)response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		}
	}
}
