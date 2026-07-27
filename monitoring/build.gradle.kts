plugins { id("com.android.library"); id("org.jetbrains.kotlin.android"); id("com.google.dagger.hilt.android"); id("com.google.devtools.ksp") }
android { namespace = "org.gramkavach.monitoring"; compileSdk = 35; defaultConfig { minSdk = 26 }; compileOptions { sourceCompatibility = JavaVersion.VERSION_17; targetCompatibility = JavaVersion.VERSION_17 }; kotlinOptions { jvmTarget = "17" } }
dependencies { 
    implementation(project(":domain")); implementation(project(":core"))
    implementation("androidx.core:core-ktx:1.15.0"); implementation("androidx.lifecycle:lifecycle-service:2.8.7"); implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0"); implementation("com.google.dagger:hilt-android:2.57"); ksp("com.google.dagger:hilt-compiler:2.57") 
}
