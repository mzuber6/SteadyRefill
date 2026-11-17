plugins {
    id("net.fabricmc.fabric-loom") version "1.17.2"
    `maven-publish`
}

group = "io.github.mzuber"
version = "1.0.1"

base {
    archivesName.set("steady-refill")
}

dependencies {
    minecraft("com.mojang:minecraft:${property("minecraft_version")}")
    implementation("net.fabricmc:fabric-loader:${property("loader_version")}")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(25))
    withSourcesJar()
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release.set(25)
}

tasks.processResources {
    val props = mapOf(
        "version" to project.version,
        "minecraft_version" to project.property("minecraft_version"),
        "loader_version" to project.property("loader_version")
    )

    inputs.properties(props)
    filesMatching("fabric.mod.json") {
        expand(props)
    }
}

val regressionTest = tasks.register<JavaExec>("regressionTest") {
    dependsOn(tasks.testClasses)
    classpath = files(sourceSets["test"].output, sourceSets["main"].output)
    mainClass.set("io.github.mzuber.steadyrefill.RefillSlotsTest")
    javaLauncher.set(javaToolchains.launcherFor {
        languageVersion.set(JavaLanguageVersion.of(25))
    })
}
tasks.check { dependsOn(regressionTest) }

// Regression checks use the standalone runner above; no test framework is needed.
tasks.test { enabled = false }
