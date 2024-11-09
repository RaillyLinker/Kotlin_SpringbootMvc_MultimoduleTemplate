plugins {
}

version = "0.0.1-SNAPSHOT"

dependencies {
    // (모듈)

    // (Redis)
    // : 메모리 키 값 데이터 구조 스토어
    implementation("org.springframework.boot:spring-boot-starter-data-redis:3.3.5")

    // (GSON)
    // : Json - Object 라이브러리
    implementation("com.google.code.gson:gson:2.11.0")
}