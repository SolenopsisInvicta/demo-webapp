package _HenryOlesenWebApp.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

import _HenryOlesenWebApp.persistence.DatabaseUtils;
import _HenryOlesenWebApp.persistence.DbUser;

/**
 * Responsible for user information management logic
 */
public class UserInfoService {
	public User getByUsername(String username) {
		if (username == null)
			return null;

		User user = null;
		try (Connection conn = DatabaseUtils.getConnection()) {
			DbUser dbUser = DbUser.getByUsername(conn, username);

			if (dbUser != null) {
				user = dbUser.toUser();
			}
			conn.close();
		} catch (SQLException e) {
			return null;
		}
		return user;
	}

	public boolean changeUserEmail(String username, String newEmail) {
		if (username == null)
			return false;
		if (newEmail != null && newEmail.isBlank())
			newEmail = null;

		boolean result = false;
		try (Connection conn = DatabaseUtils.getConnection()) {
			DbUser dbUser = DbUser.getByUsername(conn, username);

			if (dbUser != null && !Objects.equals(dbUser.email, newEmail)) {
				result = dbUser.updateEmail(newEmail);
			}
			conn.close();
		} catch (SQLException e) {
			return false;
		}
		return result;
	}
}
