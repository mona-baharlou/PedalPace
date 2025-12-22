plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    //alias(libs.plugins.google.secrets)
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

android {
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


        // OpenWeather API Constraints
        buildConfigField(
            "String", "OPEN_WEATHER_BASE_URL", "\"https://api.openweathermap.org/data/2.5/\""
        )

        buildConfigField(
            "String", "OPEN_WEATHER_PLAN", "\"FREE_TIER_ONLY\""
        )

        buildConfigField(
            "Int", "OPEN_WEATHER_MAX_CALLS_PER_MIN", "60"
        )

    }

    signingConfigs {
        create("release") {
            storeFile = file(project.property("STORE_FILE") as String)
            storePassword = project.property("KEYSTORE_PASSWORD") as String
            keyPassword = project.property("KEY_PASSWORD") as String
            keyAlias = project.property("KEY_ALIAS") as String
            storeType = project.property("STORE_TYPE") as String

        }
    }


    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")

            isMinifyEnabled = false
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