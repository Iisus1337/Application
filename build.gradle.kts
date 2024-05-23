import java.util.Properties

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath(libs.google.services)
        classpath(libs.gradle)
        classpath(libs.firebase.appdistribution.gradle)
    }
}

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
    alias(libs.plugins.googleGmsGoogleServices) apply false
}

val mapkitApiKey: String by extra {
    val properties = Properties()
    file("local.properties").inputStream().use { properties.load(it) }
    properties.getProperty("MAPKIT_API_KEY", "")
}
