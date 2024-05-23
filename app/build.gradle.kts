import java.util.Properties

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("com.google.gms.google-services")
    id("com.google.firebase.appdistribution")
}

// Load the local.properties file
val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(localPropertiesFile.inputStream())
} else {
    throw GradleException("local.properties file not found. Please create it and define MAPKIT_API_KEY.")
}

// Read the MAPKIT_API_KEY property
val mapkitApiKey: String = localProperties.getProperty("MAPKIT_API_KEY")
    ?: throw GradleException("MAPKIT_API_KEY not defined in local.properties")

android {
    namespace = "com.example.myapplication4444"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.myapplication4444"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        // Add the buildConfigField here
        buildConfigField("String", "MAPKIT_API_KEY", "\"$mapkitApiKey\"")
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
    buildFeatures {
        compose = true
        dataBinding = true
        viewBinding = true
        buildConfig = true

    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildToolsVersion = "34.0.0"
}

dependencies {
    // Import the BoM for the Firebase platform
    implementation(platform("com.google.firebase:firebase-bom:33.0.0"))
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation(libs.firebase.database)
    implementation(libs.androidx.constraintlayout.core)
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")
    // Add the dependency for the Firebase Authentication library
    // When using the BoM, you don't specify versions in Firebase library dependencies
    implementation("com.google.firebase:firebase-auth")
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.appcompat)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.database.ktx)
    implementation(libs.androidx.recyclerview)
    implementation(libs.firebase.storage)
    implementation(libs.firebase.firestore)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation("com.google.android.material:material:1.12.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("com.google.firebase:firebase-storage")
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation("com.google.android.gms:play-services-location:21.2.0")
    implementation("com.google.code.gson:gson:2.10.1")
    // Google Maps
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation("com.google.android.gms:play-services-location:21.2.0")
    // OkHttp для HTTP запросов
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    // Google Maps Android API utility library
    implementation("com.google.maps.android:android-maps-utils:3.8.2")
    implementation("com.google.maps:google-maps-services:2.2.0")
    implementation("com.yandex.android:maps.mobile:4.6.1-navikit")
    implementation("com.yandex.android:maps.mobile:4.6.1-full")

    implementation("com.yandex.mapkit.styling:automotivenavigation:4.6.1")
    implementation("com.yandex.mapkit.styling:roadevents:4.6.1")
    implementation ("com.android.billingclient:billing:7.0.0")
    implementation ("com.journeyapps:zxing-android-embedded:4.3.0")

}
