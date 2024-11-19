plugins {
}

version = "0.0.1-SNAPSHOT"

dependencies {
    // (모듈)
    implementation(project(":module-redis"))

    implementation(project(":module-scheduler"))
    implementation(project(":module-batch"))
    implementation(project(":module-socket"))
    implementation(project(":module-kafka"))
    implementation(project(":module-security"))

    implementation(project(":module-api-root"))
    implementation(project(":module-api-my_service-sc"))
    implementation(project(":module-api-my_service-tk-auth"))
    implementation(project(":module-api-my_service-tk-sample"))

    // (Spring Starter Web)
    // : 스프링 부트 웹 개발
    implementation("org.springframework.boot:spring-boot-starter-web:3.3.5")

    // (Swagger)
    // : API 자동 문서화
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.6.0")

    // (Spring Actuator)
    // : 서버 모니터링 정보
    implementation("org.springframework.boot:spring-boot-starter-actuator:3.3.5")
    runtimeOnly("io.micrometer:micrometer-registry-prometheus:1.13.6")

    // (MongoDB)
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb:3.3.5")
}