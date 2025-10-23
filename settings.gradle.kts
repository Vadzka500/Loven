include(":core:data")

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Loven"
include(":app")
include(":core")
include(":feature")

include(":feature:game")
include(":feature:game:data")
include(":feature:game:domain")
include(":feature:game:presentation")
include(":core:domain")
include(":feature:home")
include(":feature:languages")
include(":feature:modules")
include(":feature:lessons")
include(":feature:home:data")
include(":feature:home:domain")
include(":feature:home:presentation")
include(":feature:languages:data")
include(":feature:languages:domain")
include(":feature:languages:presentation")
include(":feature:lessons:domain")
include(":feature:lessons:data")
include(":feature:lessons:presentation")
include(":feature:modules:data")
include(":feature:modules:lib")
include(":feature:modules:domain")
include(":feature:modules:presentation")
include(":core:navigation")
include(":core:presentation")
include(":feature:authorization")
include(":feature:authorization:data")
include(":feature:authorization:domain")
include(":feature:authorization:presentation")
include(":feature:authorization:di")
include(":core:di")
include(":feature:home:mylibrary")
include(":feature:home:di")
include(":core:ads")
include(":core:utils")
include(":baselineprofile")
