plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.devtools.ksp")
}

android {

    buildFeatures {
        viewBinding = true
        dataBinding = true
    }

    namespace = "com.example.flo_clone2"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.flo_clone2"
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
}

dependencies {


    implementation("androidx.core:core-splashscreen:1.0.1")

    // If this project uses any Kotlin source, use Kotlin Symbol Processing (KSP)
    ksp("androidx.room:room-compiler:2.6.1")
    // optional - Kotlin Extensions and Coroutines support for Room
    // annotation 활용을 위해 사용
    implementation("androidx.room:room-ktx:2.6.1")

    // RoomDB
    val roomDBVersion = "2.7.0"
    implementation("androidx.room:room-ktx:$roomDBVersion")
    implementation("androidx.room:room-runtime:$roomDBVersion")
    ksp("androidx.room:room-compiler:2.5.0")

    // retrofit - Gson
    val retrofitVersion = "2.11.0"
    implementation("com.squareup.retrofit2:retrofit:$retrofitVersion")
    implementation("com.squareup.retrofit2:converter-gson:$retrofitVersion")
    // https://mvnrepository.com/artifact/com.squareup.retrofit2/adapter-rxjava2
    implementation("com.squareup.retrofit2:adapter-rxjava2:2.9.0")

    // okHttp
    // https://mvnrepository.com/artifact/com.squareup.okhttp3/okhttp
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    // https://mvnrepository.com/artifact/com.squareup.okhttp3/logging-interceptor
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // Glide
    // https://mvnrepository.com/artifact/com.github.bumptech.glide/glide
    implementation("com.github.bumptech.glide:glide:4.11.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.11.0")

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}