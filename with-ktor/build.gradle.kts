plugins {
    kotlin("jvm") version "1.6.10"
    kotlin("plugin.serialization") version "1.6.10"
}

dependencies {
    implementation(project(":either"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")
    implementation("io.ktor:ktor-client-content-negotiation:2.0.0-beta-1")
    implementation("io.ktor:ktor-client-core:2.0.0-beta-1")
    implementation("io.ktor:ktor-client-mock:2.0.0-beta-1")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.0.0-beta-1")
}
