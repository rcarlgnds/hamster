plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.hamster"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.hamster"
        minSdk = 30
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

    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation (libs.lifecycle.viewmodel.ktx)
    implementation (libs.lifecycle.livedata.ktx)

    //  Untuk API dan Debugging
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)

    // Untuk Gambar
    implementation(libs.glide)
    annotationProcessor(libs.compiler)

    // Untuk QR
    implementation(libs.barcode.scanning)

    // Untuk preview kamera dan image analysis
    val cameraxVersion = "1.3.1"
    implementation(libs.camera.core)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)
}