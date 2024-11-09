plugins {
    // MongoDB 추가
    kotlin("plugin.allopen") // allOpen 에 지정한 어노테이션으로 만든 클래스에 open 키워드를 적용
    kotlin("plugin.noarg")  // noArg 에 지정한 어노테이션으로 만든 클래스에 자동으로 no-arg 생성자를 생성
}

version = "0.0.1-SNAPSHOT"

dependencies {
    // (모듈)

    // (MongoDB)
    api("org.springframework.boot:spring-boot-starter-data-mongodb:3.3.5")

    // (AOP)
    implementation("org.springframework.boot:spring-boot-starter-aop:3.3.5")
}

// kotlin MongoDB : 아래의 어노테이션 클래스에 no-arg 생성자를 생성
noArg {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

// kotlin MongoDB : 아래의 어노테이션 클래스를 open class 로 자동 설정
allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}