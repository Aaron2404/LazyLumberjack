plugins {
    java
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("xyz.jpenilla.run-paper") version "2.2.3"
}

group = "dev.boostio.lazylogger"
version = "1.0.0"

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
    disableAutoTargetJvm()
}

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://repo.codemc.io/repository/maven-releases/")
    maven("https://repo.aikar.co/content/groups/aikar/")
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.20.6-R0.1-SNAPSHOT")
    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")
    implementation("net.kyori:adventure-api:4.17.0")
    implementation("org.bstats:bstats-bukkit:3.0.2")
}

tasks {
    build {
        dependsOn("shadowJar")
    }

    withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.release = 8
    }

    shadowJar {
        minimize()
        archiveFileName.set("${project.name}-${project.version}.jar")

        relocate("org.bstats", "dev.boostio.lazylogger.bstats")
    }

    // 1.8.8 - 1.16.5 = Java 8
    // 1.17           = Java 16
    // 1.18 - 1.20.4  = Java 17
    // 1-20.5+        = Java 21
    val version = "1.20.6"
    val javaVersion = 21


    val jvmArgsExternal = listOf(
            "-Dcom.mojang.eula.agree=true"
    )

    runServer {
        minecraftVersion(version)
        runDirectory.set(file("server/paper/$version"))

        javaLauncher.set(project.javaToolchains.launcherFor {
            languageVersion.set(JavaLanguageVersion.of(javaVersion))
        })

        downloadPlugins {
            url("https://download.luckperms.net/1543/bukkit/loader/LuckPerms-Bukkit-5.4.130.jar")
            url("https://ci.lucko.me/job/spark/410/artifact/spark-bukkit/build/libs/spark-1.10.65-bukkit.jar")
        }

        jvmArgs = jvmArgsExternal
    }

    runPaper.folia.registerTask() {
        minecraftVersion(version)
        runDirectory.set(file("server/folia/$version"))

        javaLauncher.set(project.javaToolchains.launcherFor {
            languageVersion.set(JavaLanguageVersion.of(javaVersion))
        })

        jvmArgs = jvmArgsExternal
    }
}