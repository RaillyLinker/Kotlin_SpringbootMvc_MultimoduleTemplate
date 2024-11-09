plugins {
}

version = "0.0.1-SNAPSHOT"

dependencies {
    // (모듈)

    // (WebSocket)
    // : 웹소켓
    implementation("org.springframework.boot:spring-boot-starter-websocket:3.3.5")

    // (ORM 관련 라이브러리)
    // WebSocket STOMP Controller 에서 입력값 매핑시 사용됨
    implementation("javax.persistence:persistence-api:1.0.2")

    // (GSON)
    // : Json - Object 라이브러리
    implementation("com.google.code.gson:gson:2.11.0")
}