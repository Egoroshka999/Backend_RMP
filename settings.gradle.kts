pluginManagement {
    plugins {
        kotlin("jvm") version "1.9.23"
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
rootProject.name = "Backend_RMP"
include("auth-service")
include("proxy-gateway")
include("profile-service")
include("database-service")
include("log-service")
include("data-service")
include("report-service")
include("load-tester")
