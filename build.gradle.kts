plugins {
    kotlin("jvm") version "1.6.10"
    java
}

group = "academy.kt"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.0")
    implementation("junit:junit:4.13.1")
    implementation(kotlin("test"))
    implementation("io.mockk:mockk:1.12.2")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}