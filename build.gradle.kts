plugins {
    id("net.neoforged.moddev") version "2.0.137"
}

group = "xland.mcmod"
version = project.ext["app_version"].toString()

repositories {
    mavenCentral()
    maven("https://maven.neoforged.net/releases")
}

neoForge {
    version = project.ext["neo_version"].toString()
    validateAccessTransformers = true
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

tasks.processResources {
    inputs.property("version", project.version)

    filesMatching("META-INF/neoforge.mods.toml") {
        expand("version" to project.version)
    }

    from("license.txt") {
        rename { "META-INF/LICENSE_${rootProject.name}.txt" }
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release.set(21)
}
