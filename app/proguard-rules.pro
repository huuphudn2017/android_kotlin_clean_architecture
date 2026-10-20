# Keep Retrofit/Gson DTOs: field names are read by reflection.
-keep class com.example.base.data.**.dto.** { *; }
-keepattributes Signature, *Annotation*
