/*
 * Copyright (c) Evan Yu 2024.
 * MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

plugins {
    id("java")
    id("maven-publish")
    id("signing")
}

group = "net.octopvp"
description = "A universal command parsing library"

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.2")

}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

tasks.compileJava {
    sourceCompatibility = "1.8"
    targetCompatibility = "1.8"
    options.compilerArgs.add("-parameters")
}
java {
    withJavadocJar()
    withSourcesJar()
}

val octomcRepository = hasProperty("octomcUsername") && hasProperty("octomcPassword")
val nexusUsername: String by project
val nexusPassword: String by project

subprojects {
    val sourcesJar = tasks.register("sourcesJar", Jar::class.java) {
        classifier = "sources"
        from(sourceSets.main.get().allSource)
    }
    val javadocJar = tasks.register("javadocJar", Jar::class.java) {
        classifier = "javadoc"
        from(sourceSets.main.get().allJava)
    }

    apply(plugin = "java")
    apply(plugin = "maven-publish")
    apply(plugin = "signing")

    publishing {
        repositories {
            maven {
                mavenLocal()
                val releasesRepoUrl = "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2"
                val snapshotsRepoUrl = "https://oss.sonatype.org/content/repositories/snapshots/"
                //url = version.endsWith('SNAPSHOT') ? snapshotsRepoUrl : releasesRepoUrl
                setUrl(releasesRepoUrl)
                credentials {
                    username = nexusUsername
                    password = nexusPassword
                }
            }
            if (octomcRepository) {
                maven ("https://repo.octopvp.net/public"){
                    name = "octomc"
                    credentials(PasswordCredentials::class)
                    authentication {
                        create<BasicAuthentication>("basic")
                    }
                }
            }
        }
        publications {
            create<MavenPublication>("mavenJava") {
                pom {
                    name.set("Commander")
                    description.set("Java annotation-based command parsing library")
                    url.set("https://github.com/OctoPvP/Commander")
                    from(components["java"])
                    artifact(sourcesJar)
                    artifact(javadocJar)
                    scm {
                        url.set("https://github.com/OctoPvP/Commander.git")
                    }

                    licenses {
                        license {
                            name.set("MIT License")
                            url.set("https://opensource.org/licenses/MIT/")
                            distribution.set("repo")
                        }
                    }

                    developers {
                        developer {
                            id.set("Badbird5907")
                            name.set("Badbird5907")
                            email.set("contact@badbird.dev")
                        }
                    }
                }
            }
            if (octomcRepository) {
                create<MavenPublication>("maven") {
                    from(components["java"])
                    artifact(sourcesJar)
                    artifact(javadocJar)
                    versionMapping {
                        usage("java-api") {
                            fromResolutionOf("runtimeClasspath")
                        }
                        usage("java-runtime") {
                            fromResolutionResult()
                        }
                    }
                    pom {
                        name.set("Commander")
                        description.set("Java annotation-based command parsing library")
                        url.set("https://github.com/OctoPvP/Commander")
                    }
                }
            }
        }
    }
    signing {
        sign(publishing.publications["mavenJava"])
    }
    tasks {
        artifacts {
            archives(javadocJar)
            archives(sourcesJar)
        }
        jar {
            duplicatesStrategy = DuplicatesStrategy.EXCLUDE
            from({
                configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) }
            })
        }
        compileJava {
            sourceCompatibility = "1.8"
            targetCompatibility = "1.8"
            options.compilerArgs.add("-parameters")
        }
    }
}
allprojects {
    tasks.withType<JavaCompile> {
        sourceCompatibility = "1.8"
        targetCompatibility = "1.8"
    }
}
