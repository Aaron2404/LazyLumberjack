plugins {
    java
    alias(libs.plugins.shadow)
    alias(libs.plugins.runPaper)
}

group = "dev.boostio.lazylogger"
version = "1.0.0"

java {
    sourceCompatibility = JavaVersion.toVersion(libs.versions.java.get())
    targetCompatibility = JavaVersion.toVersion(libs.versions.java.get())
    disableAutoTargetJvm()
}

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://repo.codemc.io/repository/maven-releases/")
    maven("https://repo.aikar.co/content/groups/aikar/")
}

dependencies {
    compileOnly(libs.spigotApi)
    compileOnly(libs.packetevents)
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
}

tasks {
    build {
        dependsOn("shadowJar")
    }

    withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.release.set(8)
    }

    shadowJar {
        minimize()
        archiveFileName.set("${project.name}-${project.version}.jar")

        relocate(
            "net.kyori.adventure.text.serializer.gson",
            "io.github.retrooper.packetevents.adventure.serializer.gson"
        )
        relocate(
            "net.kyori.adventure.text.serializer.legacy",
            "io.github.retrooper.packetevents.adventure.serializer.legacy"
        )
    }

    val version = "1.20.6"
    val javaVersion = 21

    val requiredPlugins = runPaper.downloadPluginsSpec {
        url("https://ci.codemc.io/job/retrooper/job/packetevents/lastSuccessfulBuild/artifact/spigot/build/libs/packetevents-spigot-2.3.1-SNAPSHOT.jar")
    }

    val jvmArgsExternal = listOf(
        "-Dcom.mojang.eula.agree=true"
    )

    runServer {
        minecraftVersion(version)
        runDirectory.set(file("server/paper/$version"))

        javaLauncher.set(project.javaToolchains.launcherFor {
            languageVersion.set(JavaLanguageVersion.of(javaVersion))
        })

        downloadPlugins.from(requiredPlugins)
        downloadPlugins {
            url("https://download.luckperms.net/1543/bukkit/loader/LuckPerms-Bukkit-5.4.130.jar")
            url("https://ci.lucko.me/job/spark/410/artifact/spark-bukkit/build/libs/spark-1.10.65-bukkit.jar")
        }

        jvmArgs = jvmArgsExternal
    }

    runPaper.folia.registerTask {
        minecraftVersion(version)
        runDirectory.set(file("server/folia/$version"))

        javaLauncher.set(project.javaToolchains.launcherFor {
            languageVersion.set(JavaLanguageVersion.of(javaVersion))
        })

        jvmArgs = jvmArgsExternal
    }
}
