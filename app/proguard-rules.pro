# Ktor
-keep class io.ktor.** { *; }
-dontwarn io.ktor.**

# Kotlinx Serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** { *** Companion; }
-keepclasseswithmembers class kotlinx.serialization.json.** { kotlinx.serialization.KSerializer serializer(...); }
-keep,includedescriptorclasses class com.aethertv.**$$serializer { *; }
-keepclassmembers class com.aethertv.** { *** Companion; }
-keepclasseswithmembers class com.aethertv.** { kotlinx.serialization.KSerializer serializer(...); }

# Keep @SerialName annotations (M34 fix)
-keepattributes RuntimeVisibleAnnotations
-keep class kotlinx.serialization.SerialName { *; }

# Room
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# Media3 / ExoPlayer (M32 fix)
# Keep all Media3 classes that may be accessed via reflection
-keep class androidx.media3.** { *; }
-keep interface androidx.media3.** { *; }
-dontwarn androidx.media3.**

# Keep ExoPlayer's extractors and renderers
-keep class androidx.media3.exoplayer.** { *; }
-keep class androidx.media3.decoder.** { *; }
-keep class androidx.media3.source.** { *; }

# Keep MediaSession classes
-keep class androidx.media3.session.** { *; }
