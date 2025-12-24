import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    //alias(libs.plugins.google.secrets)
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

android {
    val localProperties = Properties()

    namespace = "com.baharlou.pedalpace"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.baharlou.pedalpace"
        minSdk = 28
        targetSdk = 36
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        // 1. Manually load local.properties
        val localPropertiesFile = rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            localProperties.load(FileInputStream(localPropertiesFile))
        }

        // 2. Read the values (Use the EXACT name from your CI: WEATHER_API_KEY)
        val weatherKey = localProperties.getProperty("WEATHER_API_KEY") ?: ""
        val geminiKey = localProperties.getProperty("GEMINI_API_KEY") ?: ""

        // 3. Inject into BuildConfig with surrounding quotes
        buildConfigField("String", "WEATHER_API_KEY", "\"$weatherKey\"")
        buildConfigField("String", "GEMINI_API_KEY", "\"$geminiKey\"")

        // OpenWeather API Constraints
        buildConfigField(
            "String", "OPEN_WEATHER_BASE_URL", "\"https://api.openweathermap.org/data/2.5/\""
        )

        buildConfigField(
            "String", "OPEN_WEATHER_PLAN", "\"FREE_TIER_ONLY\""
        )

        buildConfigField(
            "int", "OPEN_WEATHER_MAX_CALLS_PER_MIN", "60"
        )

    }

    signingConfigs {
        create("release") {
            storeFile = rootProject.file(localProperties.getProperty("STORE_FILE") as String)
            storePassword = localProperties.getProperty("KEYSTORE_PASSWORD") as String
            keyPassword = localProperties.getProperty("KEY_PASSWORD") as String
            keyAlias = localProperties.getProperty("KEY_ALIAS") as String
            storeType = localProperties.getProperty("STORE_TYPE") as String

        }
    }


    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")

            isMinifyEnabled = true
            isShrinkResources = true

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }

    }

    buildFeatures {
        compose = true
        buildConfig = true

    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

/*ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}*/

dependencies {
    implementation("androidx.annotation:annotation-experimental:1.5.1")


    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.compose.foundation)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.material.icons.extended)
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // ViewModel
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    // Networking
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)
    implementation(libs.gson)


    // Image loading
    implementation(libs.coil.compose)

    // Location services
    implementation(libs.location.services)

    // Koin for DI
    implementation(libs.koin.android)

    //implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose.v410)


    // Generative AI SDK for Gemini
    implementation(libs.generativeai)

    // Koin for Testing
    androidTestImplementation(libs.koin.test)
    testImplementation(libs.truth)

}

tasks.register("assembleReleaseApk") {
    group = "build"
    description = "Assemble Release APK"
    dependsOn(tasks.named("assembleRelease"))
}

tasks.register("assembleReleaseBundle") {
    group = "build"
    description = "Assemble Release AAB"
    dependsOn(tasks.named("bundleRelease"))
}