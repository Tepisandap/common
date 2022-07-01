import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.7.1"
	id("io.spring.dependency-management") version "1.0.11.RELEASE"
	id("maven-publish")
	kotlin("jvm") version "1.6.21"
	kotlin("plugin.spring") version "1.6.21"
	kotlin("plugin.jpa") version "1.6.21"
}

group = "kh.org.nbc"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
	mavenCentral()
}

apply {
	plugin("maven-publish")
}

publishing {
	publications {
		create<MavenPublication>("maven") {
			groupId = project.properties["group"].toString()
			artifactId = project.name
			version = project.properties["version"].toString()
			from(components["kotlin"])
		}
	}
	repositories {
		maven {
			name = "GitHubPackages"
			url = uri("https://maven.pkg.github.com/Tepisandap/common")
			credentials {
				username = System.getenv("COMMON_USR")
				password = System.getenv("COMMON_PSW")
			}
		}
	}
}

dependencies {
	// Spring
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.security:spring-security-core:${property("spring-security-version")}")
	implementation("org.springframework:spring-context:${property("spring-version")}")
	implementation("org.springframework:spring-web:5.3.20")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

	// Other
	api("org.json:json:${property("json-version")}")
	implementation("org.apache.commons:commons-lang3:${property("commons-lang3-version")}")

	// Open fiegn
	implementation("io.github.openfeign:feign-core:${property("openfeign-version")}")
	implementation("io.github.openfeign:feign-slf4j:${property("openfeign-version")}")
	implementation("org.springframework.cloud:spring-cloud-openfeign-core:${property("spring-cloud-openfeign-version")}")

	// Test
	testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "17"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
