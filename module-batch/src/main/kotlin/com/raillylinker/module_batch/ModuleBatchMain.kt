package com.raillylinker.module_batch

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

// (모듈 테스트를 위한 스타터)
@SpringBootApplication
class ModuleBatchMain

fun main(args: Array<String>) {
    runApplication<ModuleBatchMain>(*args)
}