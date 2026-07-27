plugins { id("com.android.library"); id("org.jetbrains.kotlin.android"); id("com.google.devtools.ksp"); id("com.google.dagger.hilt.android") }
android { namespace = "org.gramkavach.data"; compileSdk = 35; defaultConfig { minSdk = 26 }; compileOptions { sourceCompatibility = JavaVersion.VERSION_17; targetCompatibility = JavaVersion.VERSION_17 }; kotlinOptions { jvmTarget = "17" } }
ksp { arg("room.schemaLocation", "$projectDir/schemas") }
dependencies { 
    implementation(project(":domain")); implementation(project(":core"))
    implementation("androidx.room:room-runtime:2.6.1"); implementation("androidx.room:room-ktx:2.6.1"); ksp("androidx.room:room-compiler:2.6.1")
    implementation("androidx.datastore:datastore-preferences:1.1.1")
    implementation("com.google.dagger:hilt-android:2.57"); ksp("com.google.dagger:hilt-compiler:2.57")
    testImplementation("junit:junit:4.13.2"); testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0")
    testImplementation("androidx.room:room-testing:2.6.1")
    testImplementation("org.robolectric:robolectric:4.11.1")
    testImplementation("androidx.test:core-ktx:1.6.1")
}
