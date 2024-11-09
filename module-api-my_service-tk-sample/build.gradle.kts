plugins {
}

version = "0.0.1-SNAPSHOT"

dependencies {
    // (모듈)
    implementation(project(":module-common"))
    implementation(project(":module-retrofit2"))
    implementation(project(":module-kafka"))
    implementation(project(":module-jpa"))
    implementation(project(":module-redis"))
    implementation(project(":module-mongodb"))

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

    // (GSON)
    // : Json - Object 라이브러리
    implementation("com.google.code.gson:gson:2.11.0")

    // (OkHttp3)
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    // (폰트 파일 내부 이름 가져오기용)
    implementation("org.apache.pdfbox:pdfbox:3.0.3")

    // (JSOUP - HTML 태그 조작)
    implementation("org.jsoup:jsoup:1.18.1")
}