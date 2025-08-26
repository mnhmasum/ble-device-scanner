// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
    alias (libs.plugins.daggerHiltAndroid) apply false
    alias(libs.plugins.androidLibrary) apply false
    id("com.google.devtools.ksp") version "1.9.0-1.0.13" apply false
}

buildscript {
    repositories {
        google()
    }
    dependencies {
        //classpath(libs.hilt.android.gradle.plugin)
        classpath(libs.hilt.android.gradle.plugin)
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}