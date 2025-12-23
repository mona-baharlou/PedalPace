# Retrofit: Preserve interfaces and JSON data models
-keepattributes Signature, InnerClasses, AnnotationDefault
-keep class com.baharlou.pedalpace.data.remote.** { *; }
-keep class com.baharlou.pedalpace.domain.model.** { *; }

# Koin: Prevent obfuscation of constructors used for DI
-keepclassmembers class * {
    @org.koin.core.annotation.KoinInternalApi *;
}

# Google Gemini / Generative AI
-keep class com.google.ai.client.generativeai.** { *; }