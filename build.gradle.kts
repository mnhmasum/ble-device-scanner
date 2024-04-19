// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
    id("com.google.dagger.hilt.android") version "2.50" apply false
    alias(libs.plugins.androidLibrary) apply false
}

buildscript {
    repositories {
        google()
    }
    dependencies {
        //classpath(libs.hilt.android.gradle.plugin)
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.50")
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}