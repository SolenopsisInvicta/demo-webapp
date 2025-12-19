package _HenryOlesenWebApp.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * Utility class for endpoint servlets
 */
public class EndpointUtils  {
	// Common initialization code for the servlets
	public static void commonInit(ServletConfig config) {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public static String getUsernameFromSession(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session == null) return null;
		String username = (String)session.getAttribute("username");
		return username;
	}
	
	public static Map<String, String> parseJsonFromRequest(HttpServletRequest request) throws IOException {
	    StringBuilder sb = new StringBuilder();
		try (BufferedReader br = request.getReader()) {
			String line;
			while ((line = br.readLine()) != null) sb.append(line);
		}
		return parseJson(sb.toString());
	}
	
	private static Map<String, String> parseJson(String json) {
        Map<String, String> map = new HashMap<>();
        if (json == null) return map;
        String s = json.trim();
        if (s.startsWith("{") && s.endsWith("}")) s = s.substring(1, s.length() - 1);
        if (s.isEmpty()) return map;

        String[] pairs = s.split(",");
        for (String pair : pairs) {
            String[] kv = pair.split(":", 2);
            if (kv.length != 2) continue;
            String k = stripQuotes(kv[0].trim());
            String v = stripQuotes(kv[1].trim());
            map.put(k, v);
        }
        return map;
    }

    private static String stripQuotes(String str) {
        if (str.startsWith("\"") && str.endsWith("\"") && str.length() >= 2) {
            return str.substring(1, str.length() - 1);
        }
        return str;
    }
}
