package _HenryOlesenWebApp.persistence;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

import _HenryOlesenWebApp.service.User;

/**
 * Represents a user in the database. Only valid within the context of the
 * database connection it's created with.
 */
public class DbUser {
	Connection conn;
	public int id;
	public String username;
	public String pwHash;
	public String email;
	public LocalDate memberSince;

	public User toUser() {
		return new User(this.id, this.username, this.email, this.memberSince);
	}

	public boolean updateEmail(String newEmail) {
		try (PreparedStatement pstmt = conn.prepareStatement("UPDATE users SET email = ? WHERE id = ?")) {
			pstmt.setString(1, newEmail);
			pstmt.setInt(2, this.id);
			int rowsAffected = pstmt.executeUpdate();
			if (rowsAffected == 0) {
				pstmt.close();
				return false;
			}
			this.email = newEmail;
			pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static DbUser getById(Connection conn, int userId) {
		DbUser user = null;
		try (PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM users WHERE id = ?")) {
			pstmt.setInt(1, userId);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				user = new DbUser();
				user.conn = conn;
				user.id = rs.getInt("id");
				user.username = rs.getString("username");
				user.pwHash = rs.getString("pw_hash");
				user.email = rs.getString("email");
				user.memberSince = rs.getDate("member_since").toLocalDate();
			}
			pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return user;
	}

	public static DbUser getByUsername(Connection conn, String username) {
		DbUser user = null;
		try (PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM users WHERE username = ?")) {
			pstmt.setString(1, username);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				user = new DbUser();
				user.conn = conn;
				user.id = rs.getInt("id");
				user.username = rs.getString("username");
				user.pwHash = rs.getString("pw_hash");
				user.email = rs.getString("email");
				user.memberSince = rs.getDate("member_since").toLocalDate();
			}
			pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return user;
	}

	public static DbUser CreateNewUser(Connection conn, String username, String pwHash) {
		if (username == null || pwHash == null)
			return null;

		DbUser newUser = null;
		LocalDate now = LocalDate.now();
		try (PreparedStatement pstmt = conn.prepareStatement(
				"INSERT INTO users (username, pw_hash, member_since) VALUES (?, ?, ?)",
				Statement.RETURN_GENERATED_KEYS)) {
			pstmt.setString(1, username);
			pstmt.setString(2, pwHash);
			pstmt.setDate(3, Date.valueOf(now));
			int rowsAffected = pstmt.executeUpdate();
			if (rowsAffected == 0) {
				pstmt.close();
				return null;
			}
			newUser = new DbUser();

			ResultSet rs = pstmt.getGeneratedKeys();
			if (rs.next()) {
				newUser.id = rs.getInt(1);
			} else {
				pstmt.close();
				return null;
			}
			newUser.conn = conn;
			newUser.username = username;
			newUser.pwHash = pwHash;
			newUser.email = null;
			newUser.memberSince = now;

			pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return newUser;
	}
}
