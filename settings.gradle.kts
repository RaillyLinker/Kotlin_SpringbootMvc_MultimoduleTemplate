plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
rootProject.name = "kotlin_springboot_mvc_multimodule_template"

// (모듈 모음)
// !!!모듈 추가/수정시 아래에 반영!!!
include("module-app")
include("module-redis")
include("module-scheduler")
include("module-socket")
include("module-retrofit2")
include("module-kafka")
include("module-mongodb")
include("module-jpa")
include("module-common")
include("module-security")

include("module-api-root")
include("module-api-my_service-tk-auth")
