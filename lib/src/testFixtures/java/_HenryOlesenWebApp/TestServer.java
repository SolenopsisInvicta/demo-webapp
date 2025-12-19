package _HenryOlesenWebApp;

import java.io.File;
import java.io.IOException;
import java.lang.ref.Cleaner;
import java.lang.ref.Cleaner.Cleanable;
import java.net.CookieManager;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;
import java.util.Set;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;
import org.reflections.Reflections;

import _HenryOlesenWebApp.persistence.DatabaseUtils;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.annotation.WebServlet;

/**
 * Embedded Tomcat server for integration testing
 */
class TestServer {
	private final Cleanable cleanable;
	private Tomcat tomcat;
	private int PORT;
	private final HttpClient httpClient = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NEVER)
			.cookieHandler(new CookieManager()).connectTimeout(Duration.ofSeconds(5)).build();

	static class TestServerCleaner implements Runnable {
		private Tomcat tomcat;

		TestServerCleaner(Tomcat tomcat) {
			this.tomcat = tomcat;
		}

		@Override
		public void run() {
			if (tomcat != null) {
				try {
					tomcat.stop();
					tomcat.destroy();
				} catch (LifecycleException e) {
					// Discard exceptions during cleanup
				}
			}
		}
	}

	TestServer() {
		// Run migrations to create a clean database state
		runMigrations();
		
		tomcat = new Tomcat();
		tomcat.setPort(0); // Automatic port assignment

		Context ctx = tomcat.addContext("", new File(".").getAbsolutePath());

		// Scan for @WebServlet annotations
		Reflections reflections = new Reflections("_HenryOlesenWebApp");
		Set<Class<?>> servletClasses = reflections.getTypesAnnotatedWith(WebServlet.class);
		for (Class<?> clazz : servletClasses) {
			WebServlet anno = clazz.getAnnotation(WebServlet.class);
			String servletName = anno.name().isEmpty() ? clazz.getSimpleName() : anno.name();

			Tomcat.addServlet(ctx, servletName, clazz.getName());

			for (String pattern : anno.urlPatterns().length > 0 ? anno.urlPatterns() : anno.value()) {
				ctx.addServletMappingDecoded(pattern, servletName);
			}
		}

		// Scan for @WebFilter annotations
		Set<Class<?>> filterClasses = reflections.getTypesAnnotatedWith(WebFilter.class);
		for (Class<?> clazz : filterClasses) {
			WebFilter anno = clazz.getAnnotation(WebFilter.class);
			String filterName = anno.filterName().isEmpty() ? clazz.getSimpleName() : anno.filterName();

			FilterDef filterDef = new FilterDef();
			filterDef.setFilterName(filterName);
			filterDef.setFilterClass(clazz.getName());
			ctx.addFilterDef(filterDef);

			FilterMap filterMap = new FilterMap();
			filterMap.setFilterName(filterName);
			for (String pattern : anno.urlPatterns().length > 0 ? anno.urlPatterns() : anno.value()) {
				filterMap.addURLPattern(pattern);
			}
			ctx.addFilterMap(filterMap);
		}

		tomcat.getConnector();
		try {
			tomcat.start();
		} catch (LifecycleException e) {
			throw new AssertionError("Failed to start embedded Tomcat server");
		}

		PORT = tomcat.getConnector().getLocalPort();

		// Setup the cleaner so the embedded server will be stopped automatically
		cleanable = Cleaner.create().register(this, new TestServerCleaner(tomcat));
	}

	// Manually closes the server
	public void stopServer() {
		cleanable.clean();
	}

	// ---------------- Server access utilities ----------------

	public HttpResponse<String> getEndpoint(String path) {
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://localhost:" + PORT + path))
				.header("Accept", "text/html").GET().build();

		try {
			return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
		} catch (IOException | InterruptedException e) {
			throw new AssertionError("Failed to access endpoint: " + path);
		}
	}

	public HttpResponse<String> postEndpoint(String path, String body) {
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://localhost:" + PORT + path))
				.header("Accept", "application/json").header("Content-Type", "application/json")
				.POST(HttpRequest.BodyPublishers.ofString(body)).build();

		try {
			return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
		} catch (IOException | InterruptedException e) {
			throw new AssertionError("Failed to access endpoint: " + path);
		}
	}

	// ---------------- Database Utilities ----------------

	private void runMigrations() {
		// First get the migrations and process them into statements
		Path migrationPath = Path.of("migrations", "baseline.sql");
		if (!Files.exists(migrationPath)) {
			throw new AssertionError("Failed to find migration file: " + migrationPath);
		}
		String sql;
		try {
			sql = Files.readString(migrationPath);
		} catch (IOException e) {
			throw new AssertionError("Failed to read migration file: " + migrationPath, e);
		}
		StringBuilder cleaned = new StringBuilder();
		for (String line : sql.split("\n")) {
			String trimmed = line.trim();
			if (trimmed.startsWith("--") || trimmed.isEmpty()) continue;
			cleaned.append(line).append("\n");
		}
		String[] migrationStatements = cleaned.toString().split(";");

		// Now connect to the database and run the statements
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		try (Connection conn = DatabaseUtils.getBaseConnection(); Statement stmt = conn.createStatement()) {
			for (String migrationStatement : migrationStatements) {
				if (migrationStatement.isBlank()) continue;
				stmt.execute(migrationStatement);
			}
		} catch (SQLException e) {
			throw new AssertionError("Failed to run DB migrations: " + e.getMessage(), e);
		}
	}
}
