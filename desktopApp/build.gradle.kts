import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

repositories {
    mavenCentral()
    google()
}



dependencies {

    implementation(projects.shared)
    implementation("org.jetbrains.compose.material:material-icons-extended:1.7.3")
    implementation(compose.desktop.currentOs)
    implementation(libs.kotlinx.coroutinesSwing)

    implementation(libs.compose.uiToolingPreview)

    implementation("io.ktor:ktor-client-core:3.5.0")
    implementation("io.ktor:ktor-client-cio:3.5.0")
    implementation("io.ktor:ktor-client-content-negotiation:3.5.0")
    implementation("io.ktor:ktor-serialization-kotlinx-json:3.5.0")

    implementation("org.jetbrains.compose.material3:material3:1.9.0")

    implementation("org.jetbrains.androidx.lifecycle:lifecycle-viewmodel-compose:2.9.4")
    implementation(libs.androidx.runtime.desktop)

}


compose.desktop {
    application {
        mainClass = "org.edu.stones.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "org.edu.stones"
            packageVersion = "1.0.0"
        }
    }
}