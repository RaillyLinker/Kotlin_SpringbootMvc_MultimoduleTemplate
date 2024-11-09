plugins {
}

version = "0.0.1-SNAPSHOT"

dependencies {
    // (모듈)
    implementation(project(":module-common"))
    implementation(project(":module-redis"))
    implementation(project(":module-retrofit2"))
    implementation(project(":module-jpa"))

    // (Spring Starter Web)
    // : 스프링 부트 웹 개발
    implementation("org.springframework.boot:spring-boot-starter-web:3.3.5")

    // (Spring Security)
    // : 스프링 부트 보안
    implementation("org.springframework.boot:spring-boot-starter-security:3.3.5")
    testImplementation("org.springframework.security:spring-security-test:6.3.4")

    // (JWT)
    // : JWT 인증 토큰 라이브러리
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.6")
    implementation("io.jsonwebtoken:jjwt-api:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.6")

    // (GSON)
    // : Json - Object 라이브러리
    implementation("com.google.code.gson:gson:2.11.0")
}