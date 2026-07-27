plugins { id("com.android.library"); id("org.jetbrains.kotlin.android") }
android { namespace = "org.gramkavach.alerts"; compileSdk = 35; defaultConfig { minSdk = 26 }; compileOptions { sourceCompatibility = JavaVersion.VERSION_17; targetCompatibility = JavaVersion.VERSION_17 }; kotlinOptions { jvmTarget = "17" } }
dependencies { implementation(project(":domain")); implementation(project(":core")); implementation("androidx.core:core-ktx:1.15.0"); implementation("androidx.activity:activity-ktx:1.10.0") }
