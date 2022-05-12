plugins {
    id("java")
    id("maven-publish")
    id("signing")
}

group = "net.octopvp"
version = "1.0-SNAPSHOT"
description = "A universal command parsing library"

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")

}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

tasks.compileJava {
    sourceCompatibility = "1.8"
    targetCompatibility = "1.8"
    options.compilerArgs.add("-parameters")
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "maven-publish")
    apply(plugin = "signing")

    publishing {
        repositories {
            maven {
                mavenLocal()
            }
        }
        publications {
            create<MavenPublication>("maven") {
                from(components["java"])

            }
        }
    }
    tasks.compileJava {
        options.compilerArgs.add("-parameters")
    }
    tasks.jar {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        from({
            configurations.runtimeClasspath.get().map { if (it.isDirectory()) it else zipTree(it) }
        })
    }
}
