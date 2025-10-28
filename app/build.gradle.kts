plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.services)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kapt)

    alias(libs.plugins.crashlytics)
    alias(libs.plugins.baselineprofile)

}

android {
    namespace = "com.sidspace.loven"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.sidspace.loven"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = 9
        versionName = "1.0.8"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            storeFile = file(System.getenv("RELEASE_STORE_FILE") ?: "$rootDir/sidspacekeystore.jks")
            storePassword = System.getenv("RELEASE_STORE_PASSWORD") ?: "8870606v"
            keyAlias = System.getenv("RELEASE_KEY_ALIAS") ?: "Stary"
            keyPassword = System.getenv("RELEASE_KEY_PASSWORD") ?: "8870606v"
        }
    }

    buildTypes {
        release {
            signingConfig = signingConfigs["release"]
            isMinifyEnabled = true
            isShrinkResources = true
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
        compose = true
    }


}



dependencies {

    implementation(projects.core.navigation)
    implementation(projects.core.ads)
    implementation(projects.core.data)

    implementation(libs.hilt.android)
    //implementation(libs.google.firebase.auth.ktx)
    //implementation(libs.firebase.auth.common)
    implementation(libs.firebase.auth)
    implementation(libs.androidx.profileinstaller)
    "baselineProfile"(project(":baselineprofile"))



    kapt(libs.hilt.compiler)


    implementation(libs.compose.navigation)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.material3)
    implementation(libs.androidx.navigation.compose)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)


    implementation(libs.firebase.firestore.ktx)

    implementation(libs.firebase.crashlytics.ndk)

    //auth
    //implementation(libs.firebase.auth.ktx)
    implementation(libs.play.services.auth)
    //implementation(libs.google.firebase.firestore.ktx)
}
