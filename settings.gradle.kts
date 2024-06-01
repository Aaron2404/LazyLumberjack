pluginManagement {
    plugins {
        id("org.jetbrains.kotlin.jvm") version "1.9.23"
    }
}

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("libs.versions.toml"))
        }
    }
}

rootProject.name = "LazyLogger"