import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.6.0"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    kotlin("jvm") version "1.6.0"
    kotlin("plugin.spring") version "1.6.0"
}

group = "com.oriolsoler"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
    mavenCentral()
}

extra["testcontainersVersion"] = "1.16.0"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator:2.6.1")
    implementation("org.springframework.boot:spring-boot-starter-web:2.6.1")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.6.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.6.0")
    implementation("org.junit.jupiter:junit-jupiter:5.8.2")
    implementation("org.projectlombok:lombok:1.18.22")
    implementation("com.google.api-client:google-api-client:1.33.2")
    implementation("com.google.firebase:firebase-admin:8.1.0")

    testImplementation("org.springframework.boot:spring-boot-starter-test:2.6.1")
    testImplementation("org.testcontainers:junit-jupiter:1.16.2")
    testImplementation("io.rest-assured:spring-mock-mvc:4.4.0")

    // Kotlin test
    testImplementation("org.jetbrains.kotlin:kotlin-test:1.6.0")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5:1.6.0")
    testImplementation("com.nhaarman:mockito-kotlin:1.6.0")
    testImplementation("org.mockito:mockito-inline:4.2.0")

    //Security
    implementation("io.jsonwebtoken:jjwt:0.9.1")
    implementation("org.springframework.security:spring-security-crypto:5.6.0")

    // Database
    implementation("org.springframework.boot:spring-boot-starter-jdbc:2.6.1")
    implementation("org.postgresql:postgresql:42.3.1")
    implementation("org.flywaydb:flyway-core:8.2.3")

    //Jwt
    implementation("com.auth0:java-jwt:3.18.2")

    //Email
    implementation("org.springframework.boot:spring-boot-starter-mail:2.6.1")

    //Encryptable
    implementation("com.github.ulisesbocchio:jasypt-spring-boot-starter:3.0.4")

}

dependencyManagement {
    imports {
        mavenBom("org.testcontainers:testcontainers-bom:${property("testcontainersVersion")}")
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
