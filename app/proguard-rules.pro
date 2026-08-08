# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.

# Firebase keep rules
-keep class com.google.firebase.** { *; }
-dontwarn com.google.firebase.**

# Google GenAI keep rules
-keep class com.google.genai.** { *; }
-dontwarn com.google.genai.**

# JSONObject/JSON keep rules for Groq serialization
-keep class org.json.** { *; }

# Prevent renaming of model serialization classes mapped by Firestore
-keepclassmembers class * {
    @com.google.firebase.firestore.PropertyName <fields>;
    @com.google.firebase.firestore.PropertyName <methods>;
}

# General Keep Attributes for reflection & generics
-keepattributes Signature, *Annotation*, InnerClasses, EnclosingMethod
-keepattributes SourceFile, LineNumberTable