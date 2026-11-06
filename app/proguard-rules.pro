# ====================================================================
# Aegis Survival - Google Play R8 / ProGuard Hardening Configuration
# ====================================================================

# 1. Android & Jetpack Compose Rules
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod,SourceFile,LineNumberTable
-dontwarn sun.misc.**

# 2. Room Persistence Architecture
-keep class androidx.room.RoomDatabase
-keep class * extends androidx.room.RoomDatabase { *; }
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao class * { *; }
-keep class com.antigravity.survival.data.entity.** { *; }
-keep class com.antigravity.survival.data.dao.** { *; }
-keep class com.antigravity.survival.data.database.** { *; }
-keep class * implements androidx.room.RoomOpenDelegate { *; }

# 3. WorkManager Background Worker Keep Rules
-keep class androidx.work.Worker { *; }
-keep class androidx.work.CoroutineWorker { *; }
-keep class androidx.work.ListenableWorker { *; }
-keep class * extends androidx.work.ListenableWorker {
    public <init>(android.content.Context, androidx.work.WorkerParameters);
}
-keep class com.antigravity.survival.data.sync.DataSyncWorker {
    public <init>(android.content.Context, androidx.work.WorkerParameters);
}

# 4. JSON Serialization & Reflection Resilience
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}
-keep class org.json.** { *; }
-keepclassmembers class com.antigravity.survival.data.** { *; }

# 5. Coil Image Loader & AndroidSVG Vector Decoder
-keep class coil.** { *; }
-keep class com.caverock.androidsvg.** { *; }
-dontwarn com.caverock.androidsvg.**

# 6. Android Custom Tabs & Browser Integration
-keep class androidx.browser.customtabs.** { *; }

# 7. Kotlin Coroutines & Flow
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembernames class kotlinx.coroutines.** {
    volatile <fields>;
}

# 8. Optimize & Strip Unused Debug Logging in Release
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int d(...);
}
