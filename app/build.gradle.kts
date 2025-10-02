plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-kapt")
}

android {
    namespace = "com.example.sipadam"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.sipadam"
        minSdk = 31
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true
    }
    afterEvaluate {
        tasks.matching { it.name.startsWith("package") && it.name.endsWith("Release") || it.name.endsWith("Debug") }
            .configureEach {
                doLast {
                    val appName = "SIPADAM"
                    val versionNameValue = android.defaultConfig.versionName ?: "1.0"
                    val versionCodeValue = android.defaultConfig.versionCode ?: 1

                    val buildType = when {
                        name.contains("Release", ignoreCase = true) -> "release"
                        name.contains("Debug", ignoreCase = true) -> "debug"
                        else -> "apk"
                    }

                    val apkFolder = "${project.buildDir}/outputs/apk/$buildType"
                    file(apkFolder).listFiles()?.forEach { apkFile ->
                        if (apkFile.name.endsWith(".apk")) {
                            val newName = "${appName}-v${versionNameValue}-${versionCodeValue}-${buildType}.apk"
                            apkFile.renameTo(file("$apkFolder/$newName"))
                        }
                    }
                }
            }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.play.services.maps)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.androidx.core.splashscreen)

    // Room (database lokal)
    implementation("androidx.room:room-runtime:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")

// Coroutines (sudah dipakai di kode laporan)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

// Lokasi (untuk ambil koordinat di AddLaporanActivity)
    implementation("com.google.android.gms:play-services-location:21.3.0")

// Export Word .docx
    implementation("org.apache.poi:poi:5.2.3")
    implementation("org.apache.poi:poi-ooxml:5.2.3")
}