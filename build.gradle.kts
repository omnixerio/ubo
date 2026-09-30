plugins {
    id("java")
    id("java-library")
    id("maven-publish")
}

apply(plugin = "java")
apply(plugin = "java-library")
apply(plugin = "maven-publish")
apply(plugin = "signing")

group = project.property("group")!!
version = "${project.property("version")}"

base {
    archivesName.set(project.property("archivesBaseName").toString())
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")

    compileOnly("org.jetbrains:annotations:23.0.0")
}


java {
    withSourcesJar()
    withJavadocJar()

    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8

    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}

tasks.test {
    useJUnitPlatform()
}

publishing {
    repositories {
        maven {
            name = "UltreonMavenReleases"
            url = uri("https://maven.ultreon.dev/releases")
            credentials {
                username = (findProperty("ultreonmvn.name") ?: System.getenv("ULTREON_MVN_NAME")).toString()
                password = (findProperty("ultreonmvn.secret") ?: System.getenv("ULTREON_MVN_SEC")).toString()
            }
        }

        maven {
            name = "UltreonMavenSnapshots"
            url = uri("https://maven.ultreon.dev/snapshots")
            credentials {
                username = (findProperty("ultreonmvn.name") ?: System.getenv("ULTREON_MVN_NAME")).toString()
                password = (findProperty("ultreonmvn.secret") ?: System.getenv("ULTREON_MVN_SEC")).toString()
            }
        }
    }

    publications {
        register("mavenJava", MavenPublication::class) {
            from(components["java"])

            groupId = project.group.toString()
            artifactId = project.name
            version = project.version.toString()

            pom {
                name.set("UBO")
                description.set("Extensible NBT-like data API.")

                url.set("https://github.com/Ultreon/ubo")
                inceptionYear.set("2022")

                developers {
                    developer {
                        name.set("XyperCode")
                        email.set("xyppercode@ultreon.dev")

                        organization.set("Ultreon")
                        organizationUrl.set("https://github.com/Ultreon")
                    }
                }

                organization {
                    name.set("Ultreon")
                    url.set("https://github.com/Ultreon")
                }

                issueManagement {
                    system.set("GitHub")
                    url.set("https://github.com/Ultreon/ubo/issues")
                }

                licenses {
                    license {
                        name.set("Apache License")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }

                scm {
                    connection.set("scm:git:git://github.com/Ultreon/ubo.git")
                    developerConnection.set("scm:git:ssh://github.com/Ultreon/ubo.git")

                    url.set("https://github.com/Ultreon/ubo/tree/main")
                }

                contributors {
                    contributor {
                        name.set("XyperCode")
                        url.set("https://github.com/XyperCode")
                    }

                    contributor {
                        name.set("AndEditor7")
                        url.set("https://github.com/AndEditor7")
                    }
                }
            }
        }
    }
}

tasks.test {
    useJUnitPlatform()
}

tasks.publish.get().dependsOn(tasks.build)

tasks.withType<GenerateModuleMetadata> {
    enabled = false
}
