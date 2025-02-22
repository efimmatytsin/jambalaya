import org.gradle.api.publish.maven.internal.publication.DefaultMavenPom
import org.gradle.kotlin.dsl.`java-library`
import org.gradle.kotlin.dsl.`maven-publish`

plugins {
    `java-library`
    `maven-publish`
    id("com.tailrocks.maven-publish")
    id("com.tailrocks.signing")
}

java {
    withJavadocJar()
    withSourcesJar()
}

publishing {
    publications {
        getByName<MavenPublication>("mavenJava") {
            pom {
                developers {
                    developer {
                        id.set("donbeave")
                        name.set("Alexey Zhokhov")
                        email.set("alexey@zhokhov.com")
                    }
                }
            }
        }
    }
}
