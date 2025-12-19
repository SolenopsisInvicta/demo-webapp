plugins {
    `java`
    `java-test-fixtures`
    `war`
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("jakarta.servlet:jakarta.servlet-api:6.0.0")
    testCompileOnly("jakarta.servlet:jakarta.servlet-api:6.0.0")
    testFixturesCompileOnly("jakarta.servlet:jakarta.servlet-api:6.0.0")
    
    implementation("mysql:mysql-connector-java:8.0.33")
    implementation("org.mindrot:jbcrypt:0.4")

    // Use JUnit Jupiter and an embeded tomcat server so I can run integration tests
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testRuntimeOnly("org.slf4j:slf4j-simple:2.0.7")
    testRuntimeOnly("org.apache.tomcat.embed:tomcat-embed-core:11.0.3")
    testRuntimeOnly("org.apache.tomcat.embed:tomcat-embed-jasper:11.0.3")
    testImplementation("org.reflections:reflections:0.10.2")
    testFixturesImplementation("org.apache.tomcat.embed:tomcat-embed-core:11.0.3")
	testFixturesImplementation("org.apache.tomcat.embed:tomcat-embed-jasper:11.0.3")
	testFixturesImplementation("org.reflections:reflections:0.10.2")
}

// Explicity specifiy Java toolchain
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

// Set the output file name
tasks.war {
    archiveFileName.set("ROOT.war")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
    
    testLogging {
        events(
            org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED,
            org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED,
            org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED,
            org.gradle.api.tasks.testing.logging.TestLogEvent.STANDARD_OUT,
            org.gradle.api.tasks.testing.logging.TestLogEvent.STANDARD_ERROR
        )
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        showExceptions = true
        showCauses = true
        showStackTraces = true
        showStandardStreams = true
    }
}
