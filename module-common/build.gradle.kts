plugins {
}

version = "0.0.1-SNAPSHOT"

dependencies {
    // (모듈)
    implementation(project(":module-retrofit2"))

    // (Spring Starter Web)
    // : 스프링 부트 웹 개발
    implementation("org.springframework.boot:spring-boot-starter-web:3.3.5")

    // (ThymeLeaf)
    // : 웹 뷰 라이브러리
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf:3.3.5")

    // (AWS)
    implementation("io.awspring.cloud:spring-cloud-starter-aws:2.4.4")

    // (Spring email)
    // : 스프링 이메일 발송
    implementation("org.springframework.boot:spring-boot-starter-mail:3.3.5")

    // (Excel File Read Write)
    // : 액셀 파일 입출력 라이브러리
    implementation("org.apache.poi:poi:5.3.0")
    implementation("org.apache.poi:poi-ooxml:5.3.0")
    implementation("sax:sax:2.0.1")

    // (OkHttp3)
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    // (HTML 2 PDF)
    // : HTML -> PDF 변환 라이브러리
    implementation("org.xhtmlrenderer:flying-saucer-pdf:9.10.2")
}