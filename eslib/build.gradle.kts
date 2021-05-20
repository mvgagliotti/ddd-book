import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.0"
}

group = "com.github.eslib"
version = "0.1"

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    /////////////////////
    /// Versions
    /////////////////////

    /////////////////////
    /// Main dependencies
    /////////////////////
    implementation(kotlin("stdlib-jdk8"))
    
    //jackson
    implementation("com.fasterxml.jackson.core:jackson-databind:2.10.3")
    implementation("com.fasterxml.jackson.core:jackson-core:2.10.3")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.10.3")

    //slf4j-simple
    implementation("org.slf4j:slf4j-simple:1.7.25")

    /////////////////////
    /// Test dependencies
    /////////////////////
    testImplementation("junit:junit:4.12")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.test {
    useJUnit()

    maxHeapSize = "1G"
}