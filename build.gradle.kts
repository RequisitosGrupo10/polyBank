plugins {
  // Spring Boot and Dependency Management
  id("org.springframework.boot") version "4.0.6"
  id("io.spring.dependency-management") version "1.1.0"

  // Kotlin plugins matching your Maven configurations (jpa, all-open, spring)
  kotlin("jvm") version "2.3.21"
  kotlin("plugin.spring") version "2.3.21"
  kotlin("plugin.jpa") version "2.3.21"
  kotlin("plugin.allopen") version "2.3.21"

  // Code formatting (Spotless)
  id("com.diffplug.spotless") version "8.5.1" // Clean modern version for Gradle
}

group = "com.taw"
version = "0.0.1-SNAPSHOT"

java {
  toolchain {
    languageVersion.set(JavaLanguageVersion.of(25))
  }
}

repositories {
  mavenCentral()
}

dependencies {
  // Spring Boot Starters
  implementation("org.springframework.boot:spring-boot-starter-data-jpa")
  implementation("org.springframework.boot:spring-boot-starter-web")

  // Runtime & Provided scopes
  developmentOnly("org.springframework.boot:spring-boot-devtools")
  runtimeOnly("com.mysql:mysql-connector-j")

  // Provided standard for Tomcat JSP rendering
  compileOnly("org.apache.tomcat.embed:tomcat-embed-jasper")
  implementation("javax.servlet:jstl:1.2")

  // General Utilities
  implementation("org.springframework.security:spring-security-crypto:5.7.1")
  implementation("joda-time:joda-time:2.11.1")

  // Kotlin standards
  implementation("org.jetbrains.kotlin:kotlin-stdlib")

  // Testing
  testImplementation("org.springframework.boot:spring-boot-starter-test")
  testImplementation("org.jetbrains.kotlin:kotlin-test")
}

spotless {
  java {
    googleJavaFormat()
  }
  kotlin {
    ktlint()
  }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
  compilerOptions {
    jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_25)
  }
}

tasks.withType<Test> {
  useJUnitPlatform()
}