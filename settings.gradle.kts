plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
rootProject.name = "Backend_RMP"
include("auth-service")
include("proxy-gateway")
include("profile-service")
include("food-service")
include("workout-service")
include("sleep-service")
include("health-service")
include("notifications-service")
include("database-service")
include("log-service")
