pluginManagement {
    repositories {
        google()
        maven { url = java.net.URI.create("https://repo1.maven.org/maven2/") }
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.10.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        maven { url = java.net.URI.create("https://repo1.maven.org/maven2/") }
        mavenCentral()
    }
}

rootProject.name = "AppMasterMartini_Grupo4"
include(":app")