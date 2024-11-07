plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
rootProject.name = "kotlin_springboot_mvc_multimodule_template"

// (모듈 모음)
// !!!모듈 추가/수정시 아래에 반영!!!
include("module-app")
include("module-redis")
