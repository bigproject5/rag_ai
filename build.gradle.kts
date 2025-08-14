plugins {
    java
    id("org.springframework.boot") version "3.5.3"
    id("io.spring.dependency-management") version "1.1.7"
}
group = "aivle.project"
version = "0.0.1-SNAPSHOT"

java { toolchain { languageVersion = JavaLanguageVersion.of(21) } }

repositories { mavenCentral() }

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")      // MVC
    implementation("org.springframework.boot:spring-boot-starter-webflux")  // WebClient
    implementation("org.springframework.kafka:spring-kafka")                 // Kafka
    implementation("org.springframework.boot:spring-boot-starter-actuator")  // health/metrics
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")  // Database & JPA
    implementation("org.apache.poi:poi-ooxml:5.2.5")                         // DOCX parsing
    runtimeOnly("org.postgresql:postgresql")                                // PostgreSQL Driver
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.kafka:spring-kafka-test")
}

tasks.withType<Test> { useJUnitPlatform() }
