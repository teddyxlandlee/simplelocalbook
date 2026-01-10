plugins {
    id("net.neoforged.moddev") version "2.0.137"
}

neoForge {
    version = rootProject.ext["neo_version"].toString()
    validateAccessTransformers = true
}

tasks.shadowJar {
    archiveClassifier.set(null as String?)
}
