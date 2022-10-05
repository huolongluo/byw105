# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in d:\Program Files\Android\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}


###云信
##-libraryjars libs/java-json.jar
#-dontwarn com.alibaba.fastjson.**
#-dontwarn com.netease.**
#-dontwarn com.bumptech.glide.**
##-keep class org.json.** {*;}
#-keep class com.bumptech.glide.** {*;}
#-keep class com.netease.** {*;}
##如果你使用全文检索插件，需要加入
#-dontwarn org.apache.lucene.**
#-keep class org.apache.lucene.** {*;}

-libraryjars libs/nim-basesdk-6.8.0.jar
-libraryjars libs/nim-chatroom-6.8.0.jar
-libraryjars libs/nim-lucene-6.0.8-6.8.0.jar
-libraryjars libs/nim-supertimamm-6.8.0.jar

