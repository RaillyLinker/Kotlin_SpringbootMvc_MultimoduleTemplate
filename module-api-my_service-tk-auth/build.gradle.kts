plugins {
}

version = "0.0.1-SNAPSHOT"

dependencies {
    // (모듈)
    implementation(project(":module-common"))
    implementation(project(":module-security"))
    implementation(project(":module-redis"))
    implementation(project(":module-jpa"))
    implementation(project(":module-retrofit2"))

    // (Spring Starter Web)
    // : 스프링 부트 웹 개발
    implementation("org.springframework.boot:spring-boot-starter-web:3.3.5")

    // (Swagger)
    // : API 자동 문서화
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.6.0")

    // (Spring Security)
    // : 스프링 부트 보안
    implementation("org.springframework.boot:spring-boot-starter-security:3.3.5")
    testImplementation("org.springframework.security:spring-security-test:6.3.4")
}