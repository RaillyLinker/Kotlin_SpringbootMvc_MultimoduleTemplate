plugins {
}

version = "0.0.1-SNAPSHOT"

dependencies {
    // (모듈)

    // (retrofit2 네트워크 요청)
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.retrofit2:converter-scalars:2.11.0")

    // (OkHttp3)
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // (Jackson Core)
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.18.0")
}