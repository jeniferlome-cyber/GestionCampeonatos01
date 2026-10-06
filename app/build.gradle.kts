plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.example.gestion_de_campeonatos01"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.example.gestion_de_campeonatos01"
        minSdk = 26
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        //sourceCompatibility = JavaVersion.VERSION_11
        //targetCompatibility = JavaVersion.VERSION_11
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin{
        jvmToolchain(17)
    }
}

dependencies {
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.9.3")


    implementation("androidx.room:room-runtime:2.8.0")
    implementation("androidx.room:room-ktx:2.8.0")
    ksp("androidx.room:room-compiler:2.8.0")


    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.9.3")


    implementation("androidx.recyclerview:recyclerview:1.4.0")

    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
}