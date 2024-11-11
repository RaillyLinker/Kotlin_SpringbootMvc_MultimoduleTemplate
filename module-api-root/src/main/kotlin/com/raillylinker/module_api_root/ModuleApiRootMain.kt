package com.raillylinker.module_api_root

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

// (모듈 테스트를 위한 스타터)
@SpringBootApplication
class ModuleApiRootMain

fun main(args: Array<String>) {
    runApplication<ModuleApiRootMain>(*args)
}