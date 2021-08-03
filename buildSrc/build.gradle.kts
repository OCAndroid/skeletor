plugins {
    `kotlin-dsl`
}

repositories {
    google()
    mavenCentral()
}

apply(from = "extra.gradle.kts")

dependencies {
    implementation(rootProject.extra["androidPlugin"].toString())
    implementation(rootProject.extra["kotlinPlugin"].toString())
}

kotlinDslPluginOptions {
    experimentalWarning.set(false)
}