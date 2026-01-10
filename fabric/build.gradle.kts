plugins {
    id("net.fabricmc.fabric-loom-remap") version "1.14-SNAPSHOT"
}

dependencies {
    add("minecraft", "com.mojang:minecraft:${rootProject.ext["minecraft_version"]!!}")
    add("mappings", loom.officialMojangMappings())
}

tasks.shadowJar {
    archiveClassifier.set("dev-shadow")
}

tasks.remapJar {
    inputFile.set(tasks.shadowJar.flatMap { it.archiveFile })
    archiveClassifier.set(null as String?)
}