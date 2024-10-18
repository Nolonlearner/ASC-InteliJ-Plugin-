plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "ASC-InteliJ-Plugin-"
include("src:main:Java")
findProject(":src:main:Java")?.name = "Java"
