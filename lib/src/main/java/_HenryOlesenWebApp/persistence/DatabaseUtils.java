package _HenryOlesenWebApp.persistence;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Utility class for database access
 */
public class DatabaseUtils  {
	private static String baseUrl;
	private static String url;
	private static String user;
	private static String password;
	
	static {
		Properties properties = new Properties();
        try (InputStream input = DatabaseUtils.class.getClassLoader().getResourceAsStream("config.properties")) {
        	properties.load(input);
        	baseUrl = properties.getProperty("db.url");
        	url = baseUrl + "/" + properties.getProperty("db.name");
            user = properties.getProperty("db.user");
            password = properties.getProperty("db.password");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
	
	public static Connection getConnection() throws SQLException {
		Connection conn = DriverManager.getConnection(url, user, password);
		if (conn == null) throw new SQLException("Failed to establish database connection.");
		return conn;
    }
	
	public static Connection getBaseConnection() throws SQLException {
		Connection conn = DriverManager.getConnection(baseUrl, user, password);
		if (conn == null) throw new SQLException("Failed to establish database connection.");
		return conn;
	}
}
