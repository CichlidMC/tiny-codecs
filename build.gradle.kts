plugins {
    id("java-library")
    id("maven-publish")
}

base.archivesName = "TinyCodecs"
group = "io.github.cichlidmc"
version = "2.0.0"

repositories {
    mavenCentral()
    maven("https://mvn.devos.one/snapshots/")
}

dependencies {
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
