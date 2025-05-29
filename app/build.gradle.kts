plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
    alias(libs.plugins.hilt)
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
    // AndroidX & Material
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

    // Firebase
    implementation(libs.firebase.messaging)
    implementation("com.google.firebase:firebase-auth:22.3.1")

    // Retrofit & Gson
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation("com.google.code.gson:gson:2.10.1")

    // Coroutine
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.0")

    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")

    // Navigation
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.0")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.0")

    // Room
    implementation("androidx.room:room-runtime:2.6.1")
    implementation(libs.androidx.lifecycle.viewmodel.android)
    kapt("androidx.room:room-compiler:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")

    // JWT
    implementation("com.auth0:java-jwt:3.18.1")
    implementation("com.auth0.android:jwtdecode:2.0.1")

    // Google
    implementation("com.google.android.gms:play-services-auth:20.7.0")
    implementation("com.google.android.gms:play-services-location:21.0.1")

    // UI/UX
    implementation("com.facebook.shimmer:shimmer:0.5.0")
    implementation("com.airbnb.android:lottie:6.1.0")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    kapt("com.github.bumptech.glide:compiler:4.16.0")
    implementation("jp.wasabeef:glide-transformations:4.3.0")
    implementation("com.caverock:androidsvg:1.4")

    // GRPC
    implementation("io.grpc:grpc-okhttp:1.45.1")
    implementation("io.grpc:grpc-protobuf:1.45.1")
    implementation("io.grpc:grpc-stub:1.45.1")

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Hilt (Dependency Injection)
    implementation("com.google.dagger:hilt-android:2.51.1")
    kapt("com.google.dagger:hilt-android-compiler:2.51.1")

    // AndroidX Hilt integration (stable)
    implementation("androidx.hilt:hilt-navigation-fragment:1.0.0")
    kapt("androidx.hilt:hilt-compiler:1.0.0")

    // Lifecycle ViewModel SavedState (optional tapi sering dibutuhkan)
    implementation("androidx.lifecycle:lifecycle-viewmodel-savedstate:2.6.2")

    // Hilt untuk testing
    androidTestImplementation("com.google.dagger:hilt-android-testing:2.51.1")
    kaptAndroidTest("com.google.dagger:hilt-android-compiler:2.51.1")

    // Coroutine Test
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")

    // LiveData Test
    testImplementation("androidx.arch.core:core-testing:2.2.0")

    // Mockito Core - Versi Terbaru
    testImplementation("org.mockito:mockito-core:5.11.0")

    // Mockito Kotlin
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.2.0")

    // (Opsional) Byte Buddy - Versi Terbaru (ikut dinaikkan agar kompatibel)
    testImplementation("net.bytebuddy:byte-buddy:1.14.12")
    testImplementation("net.bytebuddy:byte-buddy-agent:1.14.12")
}

