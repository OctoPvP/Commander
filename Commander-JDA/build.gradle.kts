plugins {
    id("java")
}

group = "net.octopvp"
version = "0.0.1-DEV"

repositories {
    mavenCentral()
}

dependencies {
    // Core
    implementation(project(":Commander-Core"))

    // JDA
    implementation("net.dv8tion:JDA:5.0.0-alpha.22")

    // Tests
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.1")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}
