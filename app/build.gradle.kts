plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
    id("com.google.devtools.ksp")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

android {
    namespace = "com.maurya.clouddrop"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.maurya.clouddrop"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {


    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.work.runtime.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    //firebase
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.crashlytics)


    //retrofit
    implementation (libs.logging.interceptor)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    //noinspection UseTomlInstead
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    //hilt injection
    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)

    //lifecycle ViewModel LiveData
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)

    //coroutines
    implementation(libs.kotlinx.coroutines.android)

    //lottie
    implementation(libs.lottie)

    //room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx.v240)
    ksp(libs.androidx.room.compiler)

    //drawer
    implementation(libs.minavdrawer)
}