import org.gradle.kotlin.dsl.support.uppercaseFirstChar

plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.ktor)

    alias(libs.plugins.buildConfig)
}

group = "template.group"
version = "1.0"

dependencies {
    implementation(platform(libs.ktor.bom))

    implementation(libs.ktor.contentnegotiation.json)
    implementation(libs.ktor.contentnegotiation.server)

    implementation(libs.ktor.server.cio)
    implementation(libs.ktor.server.logging)

    implementation(platform(libs.stdx.bom))
    implementation(libs.stdx.core)
    implementation(libs.stdx.envconf)
    implementation(libs.stdx.logging)
    implementation(libs.stdx.coroutines)

    implementation(libs.kmongo)

    implementation(libs.klogger)
    implementation(libs.slf4jSimple)
}

tasks {
    register("runWithEnv", RunWithEnvironmentConfig::class) {
        dependsOn("installDist")
        environmentFile.set(File(".env"))
    }

    kotlin {
        // https://docs.gradle.org/8.4/release-notes.html#:~:text=Currently%2C%20you-,cannot,-run%20Gradle%20on
        jvmToolchain(17)
    }

    jar {
        manifest {
            attributes["Main-Class"] = "$group.$name.${name.uppercaseFirstChar()}Kt"
        }
    }
}

// https://ktor.io/docs/server-packaging.html#run

val versionKey = "VERSION"

buildConfig {
    val git = Git
    buildConfigField("String?", githubShaKey, git.gitHashFromCommandline)
    buildConfigField("String?", githubBranchKey, git.gitBranchFromCommandline)

    buildConfigField(String::class.java, versionKey, version.toString())
}