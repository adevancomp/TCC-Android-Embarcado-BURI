import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.hilt.android)
    id("kotlin-kapt")
    id("androidx.navigation.safeargs.kotlin")
    id("kotlin-parcelize")
}

android {
    namespace = "br.edu.uea.buri"
    compileSdk = 34

    defaultConfig {
        applicationId = "br.edu.uea.buri"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        val  properties : Properties = Properties()
        properties.load(project.rootProject.file("local.properties").inputStream())
        buildConfigField("String","BASE_URL_API","\"${properties.getProperty("BASE_URL_API")}\"")
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
    buildFeatures {
        buildConfig = true
        viewBinding = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.retrofit)
    implementation(libs.logging.interceptor)
    implementation(libs.jackson)
    implementation(libs.jackson.annotations)
    implementation(libs.jackson.core)
    implementation(libs.jackson.databind)
    implementation(libs.jackson.datatype)
    implementation(libs.navigation.fragment.ui.ktx)
    implementation(libs.navigation.fragment.ktx)
    implementation(libs.room.ktx)
    implementation(libs.room.runtime)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.mpandroidchart)
    implementation(libs.hilt.android)
    implementation(libs.hilt.work)
    implementation(libs.work.runtime.ktx)
    annotationProcessor(libs.room.compiler)
    kapt(libs.room.compiler)
    kapt(libs.hilt.android.compiler)
    kapt(libs.hilt.compiler)
    kapt(libs.lifecycle.compiler)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}

kapt {
    correctErrorTypes = true
}