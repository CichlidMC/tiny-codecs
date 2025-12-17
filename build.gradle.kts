plugins {
    alias(libs.plugins.java.library)
    alias(libs.plugins.maven.publish)
}

group = "fish.cichlidmc"
version = "5.0.0"

repositories {
    exclusiveContent {
        forRepositories(mavenCentral()).filter {
            includeModule("org.jetbrains", "annotations")
            includeModule("org.jspecify", "jspecify")
            includeGroupAndSubgroups("org.junit")
            // these are pulled in transitively by Junit
            includeModule("org.apiguardian", "apiguardian-api")
            includeModule("org.opentest4j", "opentest4j")
        }
        forRepositories(maven("https://mvn.devos.one/releases/")).filter {
            includeModule("fish.cichlidmc", "tiny-json")
            includeModule("fish.cichlidmc", "fishflakes")
        }
    }
}

dependencies {
    compileOnlyApi(libs.bundles.annotations)
    api(libs.tiny.json)
    api(libs.fishflakes)
    testImplementation(libs.bundles.junit)
}

tasks.compileJava {
    // sync module version so it can be read at runtime
    options.javaModuleVersion = provider { version as String }
}

tasks.test {
    useJUnitPlatform()
}

java {
    withSourcesJar()
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

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
