import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar.Companion.shadowJar

plugins {
    java
    id("net.neoforged.moddev") version "2.0.137" apply false
    id("com.gradleup.shadow") version "9.3.1" apply false
}

apply(plugin = "net.neoforged.moddev")

group = "xland.mcmod"
version = project.ext["app_version"].toString()

repositories {
    mavenCentral()
    maven("https://maven.neoforged.net/releases")
}

extensions.configure<net.neoforged.moddevgradle.dsl.NeoForgeExtension>("neoForge") {
    neoFormVersion = project.ext["mappings_version"].toString()
}

allprojects {
    apply(plugin = "java")

    java {
        toolchain.languageVersion.set(JavaLanguageVersion.of(21))
        withSourcesJar()
    }

    tasks.withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
        options.release.set(21)
    }
}

tasks.processResources {
    from("license.txt") {
        rename { "META-INF/LICENSE_${rootProject.name}.txt" }
    }
}

subprojects {
    apply(plugin = "com.gradleup.shadow")

    val commonShadow by configurations.creating
    configurations.implementation {
        extendsFrom(commonShadow)
    }

    dependencies {
        add("commonShadow", project(":")) {
            isTransitive = false
        }
    }

    tasks.jar {
        archiveClassifier.set("dev")
    }

    tasks.shadowJar {
        configurations.set(listOf(commonShadow))
    }

    tasks.processResources {
        val properties = mapOf(
            "mod_version" to project.version,
            "mod_id" to "simplelocalbook",
            "mod_name" to "Simple Local Book",
            "display_url" to "https://modrinth.com/mod/simple-local-notebook",
            "mod_author" to "teddyxlandlee",
            "mod_description" to "Add a client-only book for each world, accessed via command /localnotebook.",
            "mixin_config" to "simplelocalbook.mixins.json",
            "mod_license" to "Apache-2.0",
        )

        inputs.properties(properties)

        filesMatching(setOf("META-INF/neoforge.mods.toml", "fabric.mod.json")) {
            expand(properties)
        }
    }

    base {
        archivesName.set("${rootProject.name}-${project.name}")
    }
}
