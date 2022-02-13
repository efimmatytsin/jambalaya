plugins {
    `java-library`
    id("com.tailrocks.gradle.maven-publish-conventions")
    id("com.tailrocks.gradle.signing-conventions")
    kotlin("jvm")
}

version = jambalayaLibs.versions.jambalaya.graphql.apollo.get()
description = "GraphQL Apollo client."

dependencies {
    api(project(":jambalaya-checks"))

    // Micronaut
    implementation(platform(jambalayaLibs.boms.micronaut))

    // Apollo
    api(jambalayaLibs.apollo.runtime)
    api(jambalayaLibs.apollo.rx3.support)

    // OkHttp
    api(jambalayaLibs.okhttp.urlconnection)
    api(jambalayaLibs.logging.interceptor)

    // SLF4J
    api("org.slf4j:slf4j-api")

    // Kotlin
    testImplementation(kotlin("stdlib-jdk8"))
    testImplementation(kotlin("test-junit5"))
}
