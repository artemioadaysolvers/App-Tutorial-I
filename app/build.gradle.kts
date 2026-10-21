plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}
android {
    namespace = "com.retrolab.casiofx2200"
    compileSdk = 36
    defaultConfig {
        applicationId = "com.retrolab.casiofx2200"
        minSdk = 26
        targetSdk = 36
        versionCode = 5
        versionName = "2.0.2"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
    buildTypes {
        release { isMinifyEnabled = false }
    }
    androidResources { noCompress += "glb" }
}
dependencies {
    implementation("androidx.webkit:webkit:1.14.0")
    testImplementation("junit:junit:4.13.2")
}
