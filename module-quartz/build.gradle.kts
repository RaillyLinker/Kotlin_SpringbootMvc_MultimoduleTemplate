plugins {
}

version = "0.0.1-SNAPSHOT"

dependencies {
    // (모듈)
    implementation(project(":module-jpa"))

    // (Spring Quartz)
    implementation("org.springframework.boot:spring-boot-starter-quartz:3.3.5")
}