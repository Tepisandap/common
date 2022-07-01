import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("maven-publish")
	kotlin("jvm") version "1.5.0"

}
repositories {
	mavenCentral()
}

java.sourceCompatibility = JavaVersion.VERSION_11

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "11"
	}
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
				username = "Tepisandap"
				password = "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"
			}
		}
	}
}

dependencies {
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	// spring
	implementation("org.springframework:spring-context:${property("spring-version")}")
	implementation("org.springframework:spring-webmvc:${property("spring-version")}")
	implementation("org.springframework.boot:spring-boot-actuator:${property("spring-boot-version")}")
	implementation("org.springframework.boot:spring-boot-starter-validation:${property("spring-boot-version")}")
	implementation("org.springframework.boot:spring-boot-configuration-processor:${property("spring-boot-version")}")
	implementation("org.springframework.data:spring-data-jpa:${property("spring-data-version")}")
	implementation("org.springframework.security:spring-security-core:${property("spring-security-version")}")
	// jackson-module
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin") {
		version {
			strictly("${property("jackson-module-version")}")
		}
	}
	api("org.json:json:${property("json-version")}")
	// openfeign
	implementation("io.github.openfeign:feign-core:${property("openfeign-version")}")
	implementation("io.github.openfeign:feign-slf4j:${property("openfeign-version")}")
	implementation("org.springframework.cloud:spring-cloud-openfeign-core:${property("spring-cloud-openfeign-version")}")
	// other
	implementation("javax.servlet:javax.servlet-api:${property("servlet-api-version")}")
	implementation("org.apache.commons:commons-lang3:${property("commons-lang3-version")}")
	implementation("org.hibernate.javax.persistence:hibernate-jpa-2.1-api:${property("hibernate-jpa-2.1-api-version")}")
}