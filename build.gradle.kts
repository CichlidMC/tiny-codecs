plugins {
    id("java-library")
    id("maven-publish")
}

group = "fish.cichlidmc"
version = "3.1.0"

repositories {
    mavenCentral()
    maven("https://mvn.devos.one/releases/")
}

dependencies {
    compileOnlyApi("org.jetbrains:annotations:24.1.0")
    api("fish.cichlidmc:tiny-json:1.2.0")

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
        listOf("Releases", "Snapshots").forEach {
            maven("https://mvn.devos.one/${it.lowercase()}") {
                name = "devOs$it"
                credentials(PasswordCredentials::class)
            }
        }
    }
}
