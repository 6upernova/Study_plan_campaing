import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlin.serialization)
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
    implementation("org.jgrapht:jgrapht-core:1.5.2")

    // SLF4J provider para silenciar los warnings de "No SLF4J providers were found."
    // que emite Ktor internamente. slf4j-simple es la opción más liviana.
    implementation("org.slf4j:slf4j-simple:2.0.13")

    testImplementation(libs.kotlin.test)
    testImplementation(libs.kotlin.testJunit)
    testImplementation(libs.junit)
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.11.0")

}


compose.desktop {
    application {
        mainClass = "org.edu.stones.MainKt"

        // Forzamos UTF-8 en la JVM para que los println() muestren correctamente
        // caracteres como "ó", "ñ", etc. en la consola de Windows (que por defecto
        // usa cp1252/cp850 y mostraba "∩┐╜" en lugar de "ó").
        jvmArgs(
            "-Dfile.encoding=UTF-8",
            "-Dsun.stdout.encoding=UTF-8",
            "-Dsun.stderr.encoding=UTF-8"
        )

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "org.edu.stones"
            packageVersion = "1.0.0"
        }
    }
}