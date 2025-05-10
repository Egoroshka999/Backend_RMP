plugins {
    kotlin("jvm") version "2.0.20"
    application
    id("org.jetbrains.kotlin.plugin.serialization") version "2.0.20"
}

group = "com.Backend_RMP"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    // Ktor Client
    implementation("io.ktor:ktor-client-core:2.3.7")
    implementation("io.ktor:ktor-client-cio:2.3.7")
    implementation("io.ktor:ktor-client-content-negotiation:2.3.7")
    // Сериализация
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
    // Корутины
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    // AtomicFU
    implementation("org.jetbrains.kotlinx:atomicfu:0.21.0")
    // Логирование
    implementation("ch.qos.logback:logback-classic:1.4.11")
    // Тестирование
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}

application {
    mainClass.set("com.Backend_RMP.LoadTesterKt")
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "com.Backend_RMP.LoadTesterKt"
    }

    from(sourceSets.main.get().output)

    dependsOn(configurations.runtimeClasspath)

    from({
        configurations.runtimeClasspath.get()
            .filter { it.name.endsWith("jar") }
            .map { zipTree(it) }
    })
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}