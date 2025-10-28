# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile


-optimizationpasses 1
-dontobfuscate

-keepnames class com.sidspace.loven.authorization.presentation.navigation.** { *; }
-keepnames class com.sidspace.loven.home.presentation.navigation.** { *; }
-keepnames class com.sidspace.loven.languages.presentation.navigation.** { *; }
-keepnames class com.sidspace.loven.modules.presentation.navigation.** { *; }
-keepnames class com.sidspace.loven.lessons.presentation.navigation.** { *; }
-keepnames class com.sidspace.loven.game.presentation.navigation.** { *; }
-keep class com.sidspace.core.data.model.** { *; }
