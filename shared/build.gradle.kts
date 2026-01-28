import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.sqldelight)
    alias(libs.plugins.androidMultiplatformLibrary)
    kotlin("plugin.serialization") version "2.1.0"
}


sqldelight {
    databases {
        create("Database") {
            packageName.set("org.swg.swiftgroups_app.db")
            version = 2
        }
    }
    linkSqlite.set(true)
}

kotlin {
    androidLibrary {
        namespace = "org.swg.swiftgroups.shared"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
        }

        androidResources {
            enable = true
        }
    }
    
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }
    
    sourceSets {

        androidMain.dependencies {
            implementation(libs.androidx.activity.compose)
            implementation(libs.androidx.security.crypto.ktx)
            implementation(libs.android.driver)
            implementation(libs.ktor.client.cio)
            implementation(libs.ui.tooling)
            implementation(libs.androidx.ui.graphics.android)
        }

        commonMain.dependencies {
            implementation(libs.androidx.datastore)
            implementation(libs.androidx.datastore.preferences)
            implementation(libs.jetbrains.ui.tooling.preview)
            implementation("org.jetbrains.compose.runtime:runtime:1.10.0")
            implementation("org.jetbrains.compose.foundation:foundation:1.10.0")
            implementation("org.jetbrains.compose.ui:ui:1.10.0")
            implementation("org.jetbrains.compose.components:components-resources:1.10.0")
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(libs.voyager.navigator)
            implementation(libs.voyager.transitions)
            implementation(libs.voyager.tabNavigator)
            implementation(libs.fontAwesome)
            implementation(libs.compose.webview.multiplatform)
            implementation(libs.coil.compose)
            implementation(libs.coil.network.ktor3)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.voyager.screenmodel)
            implementation(libs.kotlinx.datetime)
            implementation(libs.qrose)
            implementation(libs.kachetor)
            implementation(libs.compose.material3)
            implementation(libs.voyager.koin)
            implementation(project.dependencies.platform(libs.koin.bom))
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.autolinktext)
            implementation(libs.lifecycle)
        }
        appleMain.dependencies {
            implementation(libs.ktor.client.darwin)
            implementation(libs.native.driver)
        }
    }

    targets.configureEach {
        compilations.configureEach {
            compileTaskProvider.get().compilerOptions {
                freeCompilerArgs.add("-Xexpect-actual-classes")
            }
        }
    }
}