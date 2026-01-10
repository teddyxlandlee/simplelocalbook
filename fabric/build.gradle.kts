plugins {
    id("net.fabricmc.fabric-loom-remap") version "1.14-SNAPSHOT"
}

dependencies {
    minecraft("com.mojang:minecraft:${rootProject.ext["minecraft_version"]!!}")
    mappings(loom.officialMojangMappings())

    modImplementation("net.fabricmc:fabric-loader:${rootProject.ext["fabric_loader_version"]!!}")

    val fabricApiVersion: String = rootProject.ext["fabric_api_version"].toString()
    modImplementation(fabricApi.module("fabric-command-api-v2", fabricApiVersion))
    modRuntimeOnly(fabricApi.module("fabric-resource-loader-v0", fabricApiVersion))
}

tasks.shadowJar {
    archiveClassifier.set("dev-shadow")
}

tasks.remapJar {
    inputFile.set(tasks.shadowJar.flatMap { it.archiveFile })
    archiveClassifier.set(null as String?)
}