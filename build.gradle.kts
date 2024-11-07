plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.3.4"
    id("io.spring.dependency-management") version "1.1.6"

    // JPA 추가
    kotlin("plugin.allopen") version "2.0.21" apply false // allOpen 에 지정한 어노테이션으로 만든 클래스에 open 키워드를 적용
    kotlin("plugin.noarg") version "2.0.21" apply false // noArg 에 지정한 어노테이션으로 만든 클래스에 자동으로 no-arg 생성자를 생성

    // QueryDSL Kapt
    kotlin("kapt") version "2.0.21" apply false
}

version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

// 모든 프로젝트에 적용할 설정
allprojects {
    group = "com.raillylinker"

    repositories {
        mavenCentral()
    }
}

// 하위 모듈에만 적용할 설정
subprojects {
    // 하위 모듈 공통 플러그인
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jetbrains.kotlin.plugin.spring")
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")

    dependencies {
        // (기본)
        implementation("org.springframework.boot:spring-boot-starter:3.3.4")
        implementation("org.jetbrains.kotlin:kotlin-reflect:2.0.21")
        testImplementation("org.springframework.boot:spring-boot-starter-test:3.3.4")
        testImplementation("org.jetbrains.kotlin:kotlin-test-junit5:2.0.21")
        testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.11.2")
    }

    tasks.withType<Test> {
        useJUnitPlatform()
        jvmArgs("-XX:+EnableDynamicAgentLoading")
        jvmArgs("-Xshare:off")
    }
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}