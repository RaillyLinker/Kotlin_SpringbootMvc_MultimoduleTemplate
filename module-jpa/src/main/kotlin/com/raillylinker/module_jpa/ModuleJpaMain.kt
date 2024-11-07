package com.raillylinker.module_jpa

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

// (모듈 테스트를 위한 스타터)
@SpringBootApplication
class ModuleJpaMain

fun main(args: Array<String>) {
    runApplication<ModuleJpaMain>(*args)
}