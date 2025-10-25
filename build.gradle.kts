// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.detekt)
}

// Configure Detekt for all modules
detekt {
    // Enable parallel execution
    parallel = true

    // Use the Detekt configuration file
    config.setFrom(files("$rootDir/config/detekt/detekt.yml"))

    // Path to the baseline file (for managing existing violations)
    baseline = file("$rootDir/config/detekt/baseline.xml")

    // Build upon the default configuration
    buildUponDefaultConfig = true

    // Fail build on any findings
    ignoreFailures = false

    // Don't automatically create baseline on build
    autoCorrect = false
}

// Apply Detekt to all subprojects
subprojects {
    apply(plugin = "io.gitlab.arturbosch.detekt")

    detekt {
        config.setFrom(rootProject.files("config/detekt/detekt.yml"))
        baseline = file("$rootDir/config/detekt/baseline.xml")
        buildUponDefaultConfig = true
    }

    pluginManager.apply("io.gitlab.arturbosch.detekt")

    dependencies {
        detektPlugins(rootProject.libs.detekt.formatting)
    }
}

// Task to run Detekt on all modules
tasks.register("detektAll") {
    group = "verification"
    description = "Run Detekt analysis on all modules"
    dependsOn(subprojects.map { it.tasks.named("detekt") })
}

// Task to generate baseline for all modules
//tasks.register("detektBaseline") {
//    group = "verification"
//    description = "Generate Detekt baseline for all modules"
//    dependsOn(subprojects.map { it.tasks.named("detektBaseline") })
//}