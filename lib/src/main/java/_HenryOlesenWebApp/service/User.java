package _HenryOlesenWebApp.service;

import java.time.LocalDate;

/**
 * Represents a serializable user
 */
public class User {
	public final int id;
	public final String username;
	public final String email;
	public final LocalDate memberSince;

	public User(int id, String username, String email, LocalDate memberSince) {
		this.id = id;
		this.username = username;
		this.email = email;
		this.memberSince = memberSince;
	}

	public String toJson() {
		return "{\"username\": \"" + username + "\"," +
				"\"email\": \"" + ((email == null) ? "No email provided" : email) + "\"," +
				"\"memberSince\": \"" + memberSince.toString() + "\"}";
	}
}
