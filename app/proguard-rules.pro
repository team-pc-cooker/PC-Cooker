# PC Cooker ProGuard Rules for Play Store Release

# Keep Firebase classes
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.firebase.**
-dontwarn com.google.android.gms.**

# Fix SLF4J missing class issue
-dontwarn org.slf4j.impl.StaticLoggerBinder

# Keep ComponentModel for Firestore serialization
-keep class com.app.pccooker.ComponentModel { *; }
-keep class com.app.pccooker.models.** { *; }

# Keep Glide classes for image loading
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep class * extends com.bumptech.glide.module.AppGlideModule {
 <init>(...);
}
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

# Keep Razorpay classes
-keep class com.razorpay.** { *; }
-dontwarn com.razorpay.**

# Keep Retrofit/OkHttp classes (if used for API calls)
-keep class retrofit2.** { *; }
-keep class okhttp3.** { *; }
-dontwarn retrofit2.**
-dontwarn okhttp3.**

# Keep crash reporting attributes
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Keep Android Support/AndroidX classes
-keep class androidx.** { *; }
-dontwarn androidx.**

# General Android optimizations
-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-verbose

# Remove logging in release builds
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int d(...);
    public static int e(...);
}