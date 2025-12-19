package _HenryOlesenWebApp.service;

import java.sql.Connection;
import java.sql.SQLException;

import org.mindrot.jbcrypt.BCrypt;

import _HenryOlesenWebApp.persistence.DatabaseUtils;
import _HenryOlesenWebApp.persistence.DbUser;

/**
 * Responsible for account management logic
 */
public class AccountService {
	public String Signup(String candidateUsername, String candidatePassword) {
		// Validate candidate username and password
		if (candidateUsername == null || candidateUsername.isBlank())
			return "Username cannot be blank.";
		if (candidateUsername.length() < 2)
			return "Username must be at least 2 characters long.";
		if (candidateUsername.length() > 30)
			return "Username cannot be longer than 30 characters.";
		if (candidatePassword == null || candidatePassword.isBlank())
			return "Password cannot be blank.";
		if (candidatePassword.length() < 5)
			return "Password must be at least 5 characters long.";
		if (candidatePassword.length() > 30)
			return "Password cannot be longer than 30 characters.";

		// Salt and hash the password (salt is stored within the hash)
		String hashed = BCrypt.hashpw(candidatePassword, BCrypt.gensalt());

		try (Connection conn = DatabaseUtils.getConnection()) {
			// Verify username is not duplicate
			if (DbUser.getByUsername(conn, candidateUsername) != null) {
				conn.close();
				return "Username is already taken.";
			}

			// Insert new user into database
			DbUser newUser = DbUser.CreateNewUser(conn, candidateUsername, hashed);
			if (newUser == null) {
				conn.close();
				return "Failed to signup due to a database error.";
			} else {
				conn.close();
				return ""; // Empy string indicates success
			}
		} catch (SQLException e) {
			return "The server encountered a database error.";
		}
	}

	public boolean ValidateLogin(String username, String password) {
		try (Connection conn = DatabaseUtils.getConnection()) {
			DbUser user = DbUser.getByUsername(conn, username);
			if (user == null) {
				conn.close();
				return false;
			}
			boolean passwordMatches = BCrypt.checkpw(password, user.pwHash);
			conn.close();
			return passwordMatches;
		} catch (SQLException e) {
			return false;
		}
	}
}
