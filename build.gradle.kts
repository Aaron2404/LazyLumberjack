plugins {
    java
    alias(libs.plugins.shadow)
    alias(libs.plugins.runPaper)
}

group = "dev.boostio.lazylogger"
description = rootProject.name
version = "1.0.0"

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
    disableAutoTargetJvm()
}

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://repo.codemc.io/repository/maven-releases/")
    maven("https://repo.codemc.io/repository/maven-snapshots/")
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly(libs.paper)
    compileOnly(libs.packetevents.spigot)
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
}

tasks {
    jar {
        enabled = false
    }

    shadowJar {
        archiveFileName.set("${project.name}-${project.version}.jar")
        archiveClassifier = null

        relocate(
            "net.kyori.adventure.text.serializer.gson",
            "io.github.retrooper.packetevents.adventure.serializer.gson"
        )
        relocate(
            "net.kyori.adventure.text.serializer.legacy",
            "io.github.retrooper.packetevents.adventure.serializer.legacy"
        )
        minimize()
    }

    assemble {
        dependsOn(shadowJar)
    }

    withType<JavaCompile> {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(8)
    }

    defaultTasks("build")

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
