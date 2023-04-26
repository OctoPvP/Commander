plugins {
    id("java")
}

group = "net.octopvp"
version = "0.0.3-REL"

repositories {
    mavenCentral()
}

dependencies {
    // Core
    implementation(project(":Commander-Core"))

    // JDA
    implementation("net.dv8tion:JDA:5.0.0-beta.7")

    // Tests
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.3")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.2")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}
