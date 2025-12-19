package _HenryOlesenWebApp;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.http.HttpResponse;

import org.junit.jupiter.api.Test;

class UserInfoTest {
	private TestServer testServer = new TestServer();

	@Test
	void UserInfoReturnsLoggedInUsersData() {
		// Signup a new user
		HttpResponse<String> response = testServer.postEndpoint("/signup",
				"{\"username\":\"admin\", \"password\":\"admin\"}");
		assertEquals(302, response.statusCode(), "HTTP Status was not 302 redirect");

		response = testServer.getEndpoint("/user/userinfo");
		assertEquals(true, response.body().contains("\"username\": \"admin\""),
				"Response did not contain correct username");
		assertEquals(200, response.statusCode(), "HTTP Status was not 200 OK");
	}

	@Test
	void UserCanChangeEmail() {
		String firstEmail = "admin@gmail.com";
		String randomEmail = "admin" + String.valueOf(System.currentTimeMillis() % 1000000) + "@gmail.com";

		// Signup a new user
		HttpResponse<String> response = testServer.postEndpoint("/signup",
				"{\"username\":\"admin\", \"password\":\"admin\"}");
		assertEquals(302, response.statusCode(), "HTTP Status was not 302 redirect");

		// Email is initially empty
		response = testServer.getEndpoint("/user/userinfo");
		assertEquals(false, response.body().contains("@gmail.com"), "Response contained an email");
		assertEquals(200, response.statusCode(), "HTTP Status was not 200 OK");

		// User sets an email
		response = testServer.postEndpoint("/user/userinfo", "{\"email\":\"" + firstEmail + "\"}");
		assertEquals(200, response.statusCode(), "HTTP Status was not 200 OK");

		// First email is present
		response = testServer.getEndpoint("/user/userinfo");
		assertEquals(true, response.body().contains(firstEmail), "Response did not contain first email");
		assertEquals(200, response.statusCode(), "HTTP Status was not 200 OK");

		// User changes email
		response = testServer.postEndpoint("/user/userinfo", "{\"email\":\"" + randomEmail + "\"}");
		assertEquals(200, response.statusCode(), "HTTP Status was not 200 OK");

		// New email is present
		response = testServer.getEndpoint("/user/userinfo");
		assertEquals(true, response.body().contains(randomEmail), "Response did not contain new email");
		assertEquals(200, response.statusCode(), "HTTP Status was not 200 OK");
	}

	@Test
	void CannotChangeEmailToSameEmail() {
		String firstEmail = "admin@gmail.com";

		// Signup a new user
		HttpResponse<String> response = testServer.postEndpoint("/signup",
				"{\"username\":\"admin\", \"password\":\"admin\"}");
		assertEquals(302, response.statusCode(), "HTTP Status was not 302 redirect");

		// User sets an email
		response = testServer.postEndpoint("/user/userinfo", "{\"email\":\"" + firstEmail + "\"}");
		assertEquals(200, response.statusCode(), "HTTP Status was not 200 OK");

		// First email is present
		response = testServer.getEndpoint("/user/userinfo");
		assertEquals(true, response.body().contains(firstEmail), "Response did not contain first email");
		assertEquals(200, response.statusCode(), "HTTP Status was not 200 OK");

		// User changes to same email
		response = testServer.postEndpoint("/user/userinfo", "{\"email\":\"" + firstEmail + "\"}");
		assertEquals(400, response.statusCode(), "HTTP Status was not 400 Bad Request");
	}

	@Test
	void CannotAccessUserInfoWhileLoggedOut() {
		HttpResponse<String> response = testServer.postEndpoint("/user/userinfo", "{}");
		assertEquals(401, response.statusCode(), "HTTP Status was not 401 unauthorized");
	}
}
