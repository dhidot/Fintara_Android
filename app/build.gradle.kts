plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
    id("kotlin-kapt")
}

android {
    namespace = "com.bcafinance.fintara"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.bcafinance.fintara"
        minSdk = 24
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
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.firebase.messaging)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Retrofit dan Gson
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // Logging Interceptor (untuk debugging request/response)
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // Coroutine Dependencies
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.0")
    implementation ("com.google.android.material:material:1.6.0")

    // svg
    implementation("com.caverock:androidsvg:1.4")

    // serializedName
    implementation("com.google.code.gson:gson:2.10.1")

    // SnackBar
    implementation("com.google.android.material:material:1.9.0")

    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.0")

    // splash screen
    implementation("androidx.core:core-splashscreen:1.0.1")

    // Navigation
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.0")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.0")

    // Loading Shimmer
    implementation("com.facebook.shimmer:shimmer:0.5.0")

    // google oauth
    implementation("com.google.android.gms:play-services-auth:20.7.0")

    // room implementation
    implementation("androidx.room:room-runtime:2.6.1")
    annotationProcessor("androidx.room:room-compiler:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")

    kapt("androidx.room:room-compiler:2.6.1")

    // JWT
    implementation("com.auth0:java-jwt:3.18.1")
    implementation("com.auth0.android:jwtdecode:2.0.1")


}