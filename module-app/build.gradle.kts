plugins {
}

version = "0.0.1-SNAPSHOT"

dependencies {
    // (모듈)
    // app 모듈은 하위 모듈 및 하위모듈에서 사용하는 하위모듈들을 모두 추가합니다.
    implementation(project(":module-redis"))
    implementation(project(":module-api-project"))
    implementation(project(":module-scheduler"))
    implementation(project(":module-socket"))
    implementation(project(":module-retrofit2"))
    implementation(project(":module-kafka"))
    implementation(project(":module-mongodb"))
    implementation(project(":module-jpa"))
    implementation(project(":module-common"))

    // (Spring Starter Web)
    // : 스프링 부트 웹 개발
    implementation("org.springframework.boot:spring-boot-starter-web:3.3.4")

    // (Swagger)
    // : API 자동 문서화
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.6.0")

    // (Spring Actuator)
    // : 서버 모니터링 정보
    implementation("org.springframework.boot:spring-boot-starter-actuator:3.3.4")
    runtimeOnly("io.micrometer:micrometer-registry-prometheus:1.13.6")

    // (MongoDB)
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb:3.3.4")
}