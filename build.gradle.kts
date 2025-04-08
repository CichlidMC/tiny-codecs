plugins {
    id("java-library")
    id("maven-publish")
}

base.archivesName = "TinyCodecs"
group = "io.github.cichlidmc"
version = "3.1.0"

repositories {
    mavenCentral()
    maven("https://mvn.devos.one/snapshots/")
}

dependencies {
    compileOnlyApi("org.jetbrains:annotations:24.1.0")
    api("io.github.cichlidmc:TinyJson:1.0.1")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

java.withSourcesJar()

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }

    repositories {
        maven("https://mvn.devos.one/snapshots") {
            name = "devOS"
            credentials(PasswordCredentials::class)
        }
    }
}
