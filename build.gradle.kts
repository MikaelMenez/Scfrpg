plugins {
    kotlin("jvm") version "2.2.20"
    application
    id("org.openjfx.javafxplugin") version "0.1.0"
}

group = "com.escudomestre.digital"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    // Motor de regras e aplicação
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")

    // Repositório de dados — Exposed ORM sobre SQLite (RNF03)
    implementation("org.jetbrains.exposed:exposed-core:0.56.0")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.56.0")
    implementation("org.jetbrains.exposed:exposed-dao:0.56.0")
    implementation("org.xerial:sqlite-jdbc:3.47.1.0")

    // Testes (JUnit 5 + kotlin-test)
    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.11.3")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
    jvmToolchain(21)
}

javafx {
    version = "21.0.4"
    modules = listOf("javafx.controls", "javafx.fxml")
}

application {
    mainClass.set("com.escudomestre.digital.presentation.MainAppKt")
}

tasks.test {
    useJUnitPlatform()
}
