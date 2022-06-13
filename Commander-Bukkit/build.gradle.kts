plugins {
    id("java")
}

group = "net.octopvp"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://oss.sonatype.org/content/repositories/central")
}

dependencies {
    //Core
    implementation(project(":Commander-Core"))

    //Server API
    compileOnly("org.spigotmc:spigot-api:1.8.8-R0.1-SNAPSHOT")

    //Testing
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")

    //Lombok
    compileOnly("org.projectlombok:lombok:1.18.24")
    annotationProcessor("org.projectlombok:lombok:1.18.24")

    testCompileOnly("org.projectlombok:lombok:1.18.24")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.24")

    implementation("org.reflections:reflections:0.10.2")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}
