package _HenryOlesenWebApp.api;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Servlet that serves the home page for logged-in users
 */
@WebServlet("/user/home")
public class HomeServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        RequestDispatcher rd = req.getRequestDispatcher("/home.html");
        rd.forward(req, resp);
    }
}
