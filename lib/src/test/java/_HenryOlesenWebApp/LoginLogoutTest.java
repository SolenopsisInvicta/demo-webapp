package _HenryOlesenWebApp;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.http.HttpResponse;

import org.junit.jupiter.api.Test;

class LoginLogoutTest {
	private TestServer testServer = new TestServer();

	@Test
	void ExistingUserCanLoginAndLogout() {
		// Signup, then logout, a new user
		HttpResponse<String> response = testServer.postEndpoint("/signup",
				"{\"username\":\"admin\", \"password\":\"admin\"}");
		assertEquals(302, response.statusCode(), "HTTP Status was not 302 redirect");
		response = testServer.postEndpoint("/user/logout", "{}");
		assertEquals(302, response.statusCode(), "HTTP Status was not 302 redirect");

		// login
		response = testServer.postEndpoint("/login", "{\"username\":\"admin\", \"password\":\"admin\"}");
		assertEquals(302, response.statusCode(), "HTTP Status was not 302 redirect");

		// logout
		response = testServer.postEndpoint("/user/logout", "{}");
		assertEquals(302, response.statusCode(), "HTTP Status was not 302 redirect");
	}

	@Test
	void CannotAccessProtectedPagesWhileLoggedOut() {
		// Access protected endpoint
		HttpResponse<String> response = testServer.postEndpoint("/user/logout", "{}");
		assertEquals(401, response.statusCode(), "HTTP Status was not 401 unauthorized");
	}
}
