plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.theateam.vitaflex"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.theateam.vitaflex"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    // for view Binding
    buildFeatures{
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    // Retrofit for API calls
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")

    // For ViewModel and LiveData
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:2.3.1")
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.3.1")

// Coroutines for background processing
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.2")

    implementation ("androidx.recyclerview:recyclerview:1.2.1")
    implementation(libs.firebase.auth)
    implementation(libs.firebase.database)

    implementation("com.google.android.gms:play-services-auth:21.2.0")

    implementation ("com.google.android.material:material:1.9.0")
    implementation ("androidx.viewpager2:viewpager2:1.0.0")
    implementation ("com.github.PhilJay:MPAndroidChart:3.1.0")
    implementation(libs.androidx.swiperefreshlayout)
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.firebase.firestore)
    implementation(libs.androidx.biometric.ktx)
    implementation(libs.androidx.junit.ktx)

    // Unit testing dependencies
    testImplementation ("junit:junit:4.13.2")

    // MockWebServer for mocking API responses
    testImplementation ("com.squareup.okhttp3:mockwebserver:4.9.3")

    // Coroutines for testing (if using coroutines)
    testImplementation ("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.5.2")

    // Unit Testing
    testImplementation ("org.mockito:mockito-core:3.11.2")
    testImplementation ("org.mockito:mockito-inline:3.11.2")
    testImplementation ("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.0")


    //Biometrics
    implementation("androidx.biometric:biometric-ktx:1.4.0-alpha02")

//    testImplementation ("org.robolectric:robolectric:4.7.3")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}