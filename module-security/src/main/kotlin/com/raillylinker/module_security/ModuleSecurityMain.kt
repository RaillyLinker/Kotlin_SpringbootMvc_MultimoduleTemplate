package com.raillylinker.module_security

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

// (모듈 테스트를 위한 스타터)
@SpringBootApplication
class ModuleSecurityMain

fun main(args: Array<String>) {
    runApplication<ModuleSecurityMain>(*args)
}