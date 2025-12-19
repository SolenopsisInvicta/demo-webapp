package _HenryOlesenWebApp;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.http.HttpResponse;

import org.junit.jupiter.api.Test;

class SignupTest {
	private TestServer testServer = new TestServer();

	@Test
	void CannotSignupWithExistingUsername() {
		// Signup, then logout, a new user
		HttpResponse<String> response = testServer.postEndpoint("/signup",
				"{\"username\":\"admin\", \"password\":\"admin1\"}");
		assertEquals(302, response.statusCode(), "HTTP Status was not 302 redirect");
		response = testServer.postEndpoint("/user/logout", "{}");
		assertEquals(302, response.statusCode(), "HTTP Status was not 302 redirect");

		// Signup with identical username
		response = testServer.postEndpoint("/signup", "{\"username\":\"admin\", \"password\":\"admin2\"}");
		assertEquals(true, response.body().contains("Username is already taken"), "Response did not explain failure");
		assertEquals(200, response.statusCode(), "HTTP Status was not 200 OK");
	}

	@Test
	void CannotSignupWithShortUsernameOrPassword() {
		// Signup with short username
		HttpResponse<String> response = testServer.postEndpoint("/signup",
				"{\"username\":\"a\", \"password\":\"admin\"}");
		assertEquals(true, response.body().contains("Username must be at least 2 characters long"),
				"Response did not explain failure");
		assertEquals(200, response.statusCode(), "HTTP Status was not 200 OK");

		// Signup with short password
		response = testServer.postEndpoint("/signup", "{\"username\":\"admin\", \"password\":\"a\"}");
		assertEquals(true, response.body().contains("Password must be at least 5 characters long"),
				"Response did not explain failure");
		assertEquals(200, response.statusCode(), "HTTP Status was not 200 OK");
	}

	@Test
	void CannotSignupWhileLoggedIn() {
		// Signup a new user
		HttpResponse<String> response = testServer.postEndpoint("/signup",
				"{\"username\":\"admin1\", \"password\":\"admin1\"}");
		assertEquals(302, response.statusCode(), "HTTP Status was not 302 redirect");

		// Attempt to signup another user while logged in
		response = testServer.postEndpoint("/signup", "{\"username\":\"admin2\", \"password\":\"admin2\"}");
		assertEquals(true, response.body().contains("Cannot sign up, already logged in"),
				"Response did not explain failure");
		assertEquals(200, response.statusCode(), "HTTP Status was not 200 OK");
	}
}
