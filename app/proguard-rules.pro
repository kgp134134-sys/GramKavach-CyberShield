# Hilt rules
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep class  **_HiltModules* { *; }
-keep class dagger.hilt.android.internal.managers.** { *; }

# Room rules
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class * { *; }
-keep interface * extends androidx.room.Dao { *; }

# Moshi & Retrofit rules
-keep class com.squareup.moshi.** { *; }
-keepclassmembers class * { @com.squareup.moshi.Json *; }
-keep class org.gramkavach.bhashini.** { *; }
-keepattributes Signature, RuntimeVisibleAnnotations, AnnotationDefault

# ONNX Runtime rules
-keep class ai.onnxruntime.** { *; }
-keepattributes *Annotation*

# Compose rules
-keep class androidx.compose.material3.** { *; }
-keep class androidx.compose.material.icons.** { *; }
