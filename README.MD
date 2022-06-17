# Commander
## A universal java command parsing library

# Building
This project uses [Gradle](https://gradle.org/).
1. Clone this [repository](https://gihub.com/OctoPvP/Commander): `git clone https://github.com/OctoPvP/Commander`
2. Run `./gradlew build`, optionally add `publishToMavenLocal` if you would oike it to install into your ~/.m2 directory

# Important
To allow command argument names to work correctly, you need to pass `-parameters` to your compiler <br/>
Maven:
```xml
<plugins>
    <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-compiler-plugin</artifactId>
        <version>${maven-compiler-plugin.version}</version>
        <configuration>
            <parameters>true</parameters>
        </configuration>
    </plugin>
</plugins>
```
Gradle (Groovy):
```groovy
compileJava {
    options.compilerArgs << '-parameters'
}
```
Gradle (KTS):
```kotlin
tasks.compileJava {
    options.compilerArgs.add("-parameters")
}
```

# Usage
| Implementation             | Link                                                                      |
|----------------------------|---------------------------------------------------------------------------|
| Creating An Implementation | [Here](https://github.com/OctoPvP/Commander/wiki)                         |
| Bukkit                     | [Here](https://github.com/OctoPvP/Commander/tree/master/Commander-Bukkit) |

# Features
- Annotation based commands
- Dependency Injection in command methods
- Argument parsing into Objects through [Providers](https://github.com/OctoPvP/Commander/blob/master/Commander-Core/src/main/java/net/octopvp/commander/provider/Provider.java)
- Command suggestions
- Flags & Switches
- Customizable Messages
- Validators

# License
See [LICENSE](https://github.com/OctoPvP/Commander/blob/master/LICENSE)
