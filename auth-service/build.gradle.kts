plugins {
    kotlin("jvm") version "2.0.20"
    application
}

group = "com.Backend_RMP"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}

application {
    mainClass.set("com.Backend_RMP.MainKt")
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "com.Backend_RMP.MainKt"
    }

    from(sourceSets.main.get().output)

    dependsOn(configurations.runtimeClasspath)

    from({
        configurations.runtimeClasspath.get()
            .filter { it.name.endsWith("jar") }
            .map { zipTree(it) }
    })
}
