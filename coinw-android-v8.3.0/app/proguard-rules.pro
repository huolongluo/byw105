# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
-keepclassmembers class fqcn.of.javascript.interface.for.webview {
   public *;
}
-keepclassmembers class * extends android.webkit.WebViewClient {
    public void *(android.webkit.WebView, java.lang.String, android.graphics.Bitmap);
    public boolean *(android.webkit.WebView, java.lang.String);
}
-keepclassmembers class * extends android.webkit.WebViewClient {
    public void *(android.webkit.WebView, jav.lang.String);
}
# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
######################
##混淆注意项
##1、实体类
##2、Native暴露JS方法
##3、三方库
##4、删除无用代码（注意不能是接口或者是抽象类）
######################
##-ignorewarnings
#-libraryjars ../uikit/libs/nim-basesdk-6.8.0.jar
#-libraryjars ../uikit/libs/nim-chatroom-6.8.0.jar
#-libraryjars ../uikit/libs/nim-lucene-6.0.8-6.8.0.jar
#-libraryjars ../uikit/libs/nim-supertimamm-6.8.0.jar
######################
-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.**{*;}

#极光----------------------
#-dontoptimize
-dontpreverify

#-dontwarn cn.jpush.**
#-keep class cn.jpush.** { *; }
#-keep class * extends cn.jpush.android.helpers.JPushMessageReceiver { *; }

#-dontwarn cn.jiguang.**
#-keep class cn.jiguang.** { *; }
#极光----------------------
-keepattributes EnclosingMethod
-keepattributes InnerClasses
#############################################
# jar
#-ignorewarnings
#-libraryjars libs/android-support-v4.jar

###################---------------------
-dontwarn org.greenrobot.greendao.**
-keep class org.greenrobot.greendao.**{*;}
-keepclassmembers class * extends org.greenrobot.greendao.AbstractDao {
public static java.lang.String TABLENAME;
}
-keep class **$Properties
-keep class net.sqlcipher.database.**{*;}
-keep public interface net.sqlcipher.database.**
-dontwarn net.sqlcipher.database.**
-dontwarn org.greenrobot.greendao.**
###################---------------------
-dontwarn com.just.agentweb.**
-dontwarn com.cocosw.favor.**
#
#-keep class org.greenrobot.greendao.internal.DaoConfig {*;}
#
#-keep class pl.droidsonroids.gif.** {*;}
#-keep class org.intellij.lang.annotations.** {*;}
#-keep class org.jetbrains.annotations.** {*;}
#-keep class kotlin.** {*;}
#-keep class org.hamcrest.** {*;}
#-keep class org.greenrobot.greendao.** {*;}
#-keep class org.greenrobot.eventbus.** {*;}
#-keep class android.net.** {*;}
#-keep class com.android.internal.http.multipart.** {*;}
#-keep class me.imid.swipebacklayout.lib.** {*;}
#-keep class junit.** {*;}
#-keep class org.junit.** {*;}
#-keep class javax.inject.** {*;}
##
#-keep class rx.** {*;}
##
#-keep class com.umeng.** {*;}
#-keep class retrofit2.** {*;}
#-keep class okio.** {*;}
#-keep class okhttp3.** {*;}
#-keep class com.readystatesoftware.systembartint.** {*;}
#-keep class com.lzy.imagepicker.** {*;}
#-keep class com.koushikdutta.async.** {*;}
#-keep class com.just.agentweb.** {*;}
#-keep class butterknife.** {*;}
#-keep class com.jakewharton.rxbinding.** {*;}
#-keep class com.tuo.customview.** {*;}
#-keep class dagger.** {*;}
#-keep class com.google.gson.** {*;}
#-keep class com.yy.mobile.rollingtextview.** {*;}
#-keep class com.github.onlynight.noswipeviewpager.library.** {*;}
#-keep class com.android.tu.loadingdialog.** {*;}
##
#-keep class uk.co.senab.photoview.** {*;}
##
#-keep class com.bumptech.glide.** {*;}
#-keep class cn.addapp.pickers.** {*;}
#-keep class com.fasterxml.jackson.databind.** {*;}
#-keep class com.fasterxml.jackson.core.** {*;}
#-keep class com.fasterxml.jackson.annotation.** {*;}
#-keep class com.dhh.websocket.** {*;}
#-keep class com.cocosw.favor.** {*;}
#-keep class com.blankj.utilcode.** {*;}
#-keep class com.baoyz.swipemenulistview.** {*;}
#-keep class android.support.transition.** {*;}
#-keep class android.support.graphics.drawable.** {*;}
#-keep class android.support.** {*;}
#-keep class android.databinding.** {*;}
#-keep class com.android.databinding.library.** {*;}
#-keep class com.alibaba.fastjson.** {*;}
#-keep class com.uuzuche.lib_zxing.** {*;}
#-keep class com.google.zxing.** {*;}
#-keep class android.arch.lifecycle.** {*;}
#-keep class android.arch.core.internal.** {*;}
#-keep class com.tencent.captchasdk.** {*;}


#############################################
#
# 基本指令区域（没什么别的需求不需要动）
#
#############################################
# 代码混淆压缩比，在0~7之间，默认为5，一般不做修改
-optimizationpasses 5
# 混合时不使用大小写混合，混合后的类名为小写
-dontusemixedcaseclassnames

# 指定不去忽略非公共库的类
-dontskipnonpubliclibraryclasses

# 这句话能够使我们的项目混淆后产生映射文件
# 包含有类名->混淆后类名的映射关系
-verbose

# 指定不去忽略非公共库的类成员
-dontskipnonpubliclibraryclassmembers

# 不做预校验，preverify是proguard的四个步骤之一，Android不需要preverify，去掉这一步能够加快混淆速度。
-dontpreverify

# 保留Annotation不混淆
-keepattributes *Annotation*,InnerClasses

# 避免混淆泛型
-keepattributes Signature

# 抛出异常时保留代码行号
-keepattributes SourceFile,LineNumberTable

# 指定混淆是采用的算法，后面的参数是一个过滤器
# 这个过滤器是谷歌推荐的算法，一般不做更改
-optimizations !code/simplification/cast,!field/*,!class/merging/*
#############################################
#
# Android开发中一些需要保留的公共部分（没什么别的需求不需要动）
#
#############################################

# 保留我们使用的四大组件，自定义的Application等等这些类不被混淆
# 因为这些子类都有可能被外部调用
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Appliction
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.view.View
-keep public class com.android.vending.licensing.ILicensingService

# 保留support下的所有类及其内部类
##test
#-keep class android.support.** {*;}

##test
# 保留继承的
#-keep public class * extends android.support.v4.**
#-keep public class * extends android.support.v7.**
-keep public class * extends android.support.annotation.**

# 保留R下面的资源
-keep class **.R$* {*;}

# 保留本地native方法不被混淆
-keepclasseswithmembernames class * {
    native <methods>;
}

# 保留在Activity中的方法参数是view的方法，
# 这样以来我们在layout中写的onClick就不会被影响
-keepclassmembers class * extends android.app.Activity{
    public void *(android.view.View);
}

# 保留枚举类不被混淆
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# 保留我们自定义控件（继承自View）不被混淆
-keep public class * extends android.view.View{
    *** get*();
    void set*(***);
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

# 保留Parcelable序列化类不被混淆
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# 保留Serializable序列化的类不被混淆
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    !private <fields>;
    !private <methods>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# 对于带有回调函数的onXXEvent、**On*Listener的，不能被混淆
-keepclassmembers class * {
    void *(**On*Event);
    void *(**On*Listener);
    public void *ButtonClicked(android.view.View);
}

 # AndroidEventBus
-keep class org.simple.** { *; }
-keep interface org.simple.** { *; }
-keepclassmembers class * {
    @org.simple.eventbus.Subscriber <methods>;
}

# Bugly
#-dontwarn com.tencent.bugly.**
#-keep class com.tencent.bugly.** {*;}

# ButterKnife
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }
-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}
-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}

# EventBus
-keepattributes *Annotation*
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }

# FastJson
-dontwarn com.alibaba.fastjson.**
#-keep class com.alibaba.fastjson.** { *; }
-keepattributes Signature
-keepattributes *Annotation*
# 定位
-dontwarn com.amap.api.location.**
-dontwarn com.aps.**
-keep class com.amap.api.location.**{*;}
-keep class com.aps.**{*;}
# 导航
-dontwarn com.amap.api.navi.**
-dontwarn com.autonavi.**
-keep class com.amap.api.navi.** {*;}
-keep class com.autonavi.** {*;}

# Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
-keep class com.bumptech.glide.GeneratedAppGlideModuleImpl { *; }

# Gson
-keepattributes Signature
-keepattributes *Annotation*
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.stream.** { *; }
# 使用Gson时需要配置Gson的解析对象及变量都不混淆。不然Gson会找不到变量。
# 将下面替换成自己的实体类
-keep class com.example.bean.** { *; }

# Jackson
-dontwarn org.codehaus.jackson.**
-dontwarn com.fasterxml.jackson.databind.**
-keep class org.codehaus.jackson.** { *;}
-keep class com.fasterxml.jackson.** { *; }

# 极光推送
#-dontoptimize
-dontpreverify
-dontwarn cn.jpush.**
-keep class cn.jpush.** { *; }

# OkHttp3
-dontwarn okhttp3.**
-dontwarn com.squareup.okhttp3.**
-keep class com.squareup.okhttp3.** { *;}
-dontwarn okio.**

# Retrofit
-dontwarn retrofit2.**
#-keep class retrofit2.** { *; }
#-keepattributes Signature
#-keepattributes Exceptions
-dontnote retrofit2.Platform
-dontnote retrofit2.Platform$IOS$MainThreadExecutor
-dontwarn retrofit2.Platform$Java8
-keepattributes Signature
-keepattributes Exceptions

# Retrolambda
-dontwarn java.lang.invoke.*

# RxJava RxAndroid
-dontwarn sun.misc.**
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
    long producerIndex;
    long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode producerNode;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode consumerNode;
}

# 微信支付
-dontwarn com.tencent.mm.**
-dontwarn com.tencent.wxop.stat.**
-keep class com.tencent.mm.** {*;}
-keep class com.tencent.wxop.stat.**{*;}

############umeng#############
-keep class com.umeng.** {*;}

-keep class com.uc.** {*;}

-keepclassmembers class * {
   public <init> (org.json.JSONObject);
}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keep class com.zui.** {*;}
-keep class com.miui.** {*;}
-keep class com.heytap.** {*;}
-keep class a.a.a.a.** {*;}
-keep class com.vivo.** {*;}
##############################

# 友盟自动更新
-keepclassmembers class * { public <init>(org.json.JSONObject); }
-keep public class cn.irains.parking.cloud.pub.R$*{ public static final int *; }
-keep public class * extends com.umeng.**
-keep class com.umeng.** { *; }

#################################################################
 -keepattributes *JavascriptInterface*
 -keepclassmembers class **.ClassName$H5_Object{*;}
 -keep class android.webkit.JavascriptInterface {*;}
 -keepclassmembers class huolongluo.byw.reform.home.activity.JSCallJavaInterface{public *;}
 -keepclassmembers class huolongluo.byw.reform.home.activity.JSCallAlManMachineJavaInterface{public *;}
#################################################################
#-dontwarn javax.swing.**
#-dontwarn java.awt.event.**
#-dontwarn com.f2prateek.**
-dontwarn com.mob.**
#-dontwarn rx.internal.**
# 保留完整包
#-keep class javax.swing.** {*;}
#-keep class java.awt.event.** {*;}
#-keep class com.f2prateek.** {*;}
#-keep class com.alipay.sdk.** {*;}
#-keep class com.mob.** {*;}
#-keep class rx.internal.** {*;}
#-keep class javax annotation.** {*;}
#-keep class net.sqlcipher.** {*;}
#-keep class java.awt.** {*;}
###############################
##
-keep class huolongluo.byw.byw.net.okhttp.** {*;}
##
#应用内部传输对像，可以进行混淆
-keep class com.android.coinw.model.result.** {*;}
##
-dontwarn huolongluo.byw.model.**
-keep class huolongluo.byw.model.** {*;}
-keep class huolongluo.byw.byw.bean.** {*;}
-keep class huolongluo.byw.byw.base.** {*;}
##
-keep class huolongluo.byw.reform.base.** {*;}
-keep class huolongluo.byw.reform.bean.** {*;}
-keep class huolongluo.byw.reform.trade.bean.** {*;}
-keep class huolongluo.byw.reform.mine.bean.** {*;}
-keep class huolongluo.byw.reform.home.bean.** {*;}
-keep class huolongluo.byw.reform.c2c.oct.bean.** {*;}
-keep class huolongluo.byw.byw.inform.bean.** {*;}
##
-keep class com.github.mikephil.charting.data.** {*;}
-keep class huolongluo.byw.byw.ui.fragment.maintab01.KlineBean {*;}
-keep class huolongluo.byw.byw.ui.fragment.maintab01.bean.** {*;}
-keep class huolongluo.byw.byw.ui.fragment.maintab03.bean.** {*;}
-keep class huolongluo.byw.byw.ui.fragment.maintab05.bean.** {*;}
##
-keep class huolongluo.byw.byw.ui.activity.feedback.Feed {*;}
-keep class huolongluo.byw.byw.ui.activity.feedback.FeedList {*;}
-keep class huolongluo.byw.byw.ui.activity.feedback.FeedHistory {*;}
-keep class huolongluo.byw.byw.ui.activity.feedback.FeedListHis {*;}
##
-keep class huolongluo.byw.byw.share.** {*;}
-keep class com.legend.common.event.** {*;}
-keep class huolongluo.byw.byw.ui.activity.address.*Bean {*;}
-keep class huolongluo.byw.byw.ui.activity.cthistory.*Bean {*;}
-keep class huolongluo.byw.reform.safecenter.RenzhengBean {*;}
-keep class huolongluo.byw.byw.ui.fragment.latestdeal.LatestBean {*;}
-keep class huolongluo.byw.byw.ui.fragment.latestdeal.LatestListBean {*;}
-keep class huolongluo.byw.byw.ui.fragment.maintab05.cnycthis.ChongzhiBean {*;}
-keep class huolongluo.byw.byw.ui.fragment.maintab05.cnycthis.ChongzhiListBean {*;}
-keep class huolongluo.byw.reform.socket.SocketParams {*;}
##
###########
####一键买卖币
-keep class huolongluo.byw.byw.ui.oneClickBuy.bean.** {*;}
###########
##
##合约
-keep class huolongluo.byw.byw.ui.fragment.** {*;}
-keep class com.swap.common.trans.data.** {*;}
-keep class com.swap.common.heyue.bean.** {*;}
-keep class com.swap.common.trans.contract.** {*;}
##sharesdk
-keep class cn.sharesdk.**{*;}
-keep class com.sina.*{*;}
-keep class *.R$ {*;}
-keep class *.R{*;}
-keep class onekeyshare.themes.classic.*{*;}
-keep class onekeyshare.*{*;}
-dontwarn cn.sharesdk.**
-dontwarn *.R$
-dontwarn cn.sharesdk.onekeyshare.**
-keep class cn.sharesdk.onekeyshare.**
#################################################################
-keep public class android.net.http.SslError{*;}
-keep class cn.smssdk.**{*;}
-keep class com.mob.**{*;}
#################################################################
##云信
#-libraryjars libs/java-json.jar
-dontwarn com.alibaba.fastjson.**
-dontwarn com.netease.**
#-dontwarn com.bumptech.glide.**
#-keep class org.json.** {*;}
#-keep class com.bumptech.glide.** {*;}
-keep class com.netease.nim.uikit.** {*;}
-keepclassmembers class **.R$* {
 public static <fields>;
}
-keepclassmembers class ** {
    public void onEvent*(**);
    void onEvent*(**);
}
-keepattributes Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,Annotation,EnclosingMethod,MethodParameters
#-keep class com.lzy.imagepicker.R$id {*;}
-keep class com.lzy.** {*;}
-keep class com.netease.** {*;}
#如果你使用全文检索插件，需要加入
-dontwarn org.apache.lucene.**
-keep class org.apache.lucene.** {*;}
##
#-keep class com.vivo.push.** {*;}
#-keep class com.xiaomi.mipush.** {*;}
#-keep class com.google.android.gms.common.** {*;}
#-keep class com.coloros.mcssdk.** {*;}
#-keep class com.huawei.hms.api.** {*;}
###
#-keep class android.arch.lifecycle.Observer {*;}
#-keep class rx.Completable {*;}
#-keep class com.f2prateek.rx.preferences.** {*;}
#-keep class com.alipay.sdk.app.** {*;}
#-keep class net.sqlcipher.database.** {*;}
#-keep class org.w3c.dom.bootstrap.** {*;}
#-keep class kotlin.collections.** {*;}
#################################################################
##
############assumenosideeffects 不能写通配符{*};否则会出现误删###################
##调试打开日志
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String,int);
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int d(...);
    public static int e(...);
}
-assumenosideeffects class java.io.PrintStream {
    public *** println(...);
    public *** print(...);
}
##
##删除代码里面的方法
-assumenosideeffects class huolongluo.byw.util.tip.DialogUtils {
    public void showChangeIp(...);
}
##
-assumenosideeffects class huolongluo.byw.byw.net.DomainHelper {
    private static *** getAllTestList();
}
##删除测试环境资源
-assumenosideeffects class com.android.coinw.test.CoinwTest {
    *** <fields>;
    *** <methods>;
}
##
##删除无用测试代码
## 接口和抽象类不能删除 ##
-assumenosideeffects class org.apache.lucene.index.DocumentsWriterPerThread{
    public *** testPoint(...);
}
-assumenosideeffects class org.apache.lucene.index.IndexWriter{
    public *** testPoint(...);
}
-assumenosideeffects class com.fasterxml.jackson.databind.util.ISO8601Utils{
    *** test1(...);
}
-assumenosideeffects class com.netease.nim.uikit.common.util.log.sdk.wrapper.AbsNimLog{
    public *** test(...);
}
-assumenosideeffects class com.tencent.bugly.crashreport.CrashReport{
    public *** testANRCrash(...);
}
#-assumenosideeffects class com.umeng.commonsdk.statistics.noise.ABTest{*;}
#-assumenosideeffects class org.greenrobot.greendao.test.AbstractDaoTest{*;}
#-assumenosideeffects class org.greenrobot.greendao.test.AbstractDaoTestSinglePk{*;}
#-assumenosideeffects class org.greenrobot.greendao.test.AbstractDaoSessionTest{*;}
#-assumenosideeffects class org.greenrobot.greendao.test.AbstractDaoTestStringPk{*;}
#-assumenosideeffects class org.greenrobot.greendao.test.DbTest{*;}
##
#-assumenosideeffects class f.i.d.getLatest{*;}
#################################################################
##note
## can't find dynamically referenced class
-dontnote com.alibaba.**
-dontnote com.blankj.utilcode.**
-dontnote com.cocosw.**
-dontnote com.google.gson.**
-dontnote com.just.agentweb.**
-dontnote com.lzy.**
-dontnote com.tencent.bugly.**
-dontnote com.umeng.analytics.**
-dontnote com.umeng.commonsdk.**
-dontnote com.uuzuche.**
-dontnote com.xw.**
-dontnote huolongluo.byw.**
-dontnote kotlin.**
-dontnote okhttp3.**
##
#the configuration keeps the entry point
#-dontnote android.databinding.**
#-dontnote cn.addapp.pickers.**
#-dontnote cn.sharesdk.**
#-dontnote com.flyco.**
#-dontnote com.github.mikephil.**
#-dontnote com.google.zxing.**
#-dontnote com.hp.hpl.**
#-dontnote com.koushikdutta.**
#-dontnote com.mob.**
#-dontnote com.yy.**
#-dontnote okio.**
#-dontnote rx.**
#-dontnote uk.**
#-dontnote com.baoyz.**
#-dontnote com.trycatch.**
#-dontnote com.tuo.**
##-dontnote dagger.**
#-dontnote pl.droidsonroids.**
#-dontnote retrofit2.**
#################################################################
##阿里face验证
-keep class com.taobao.securityjni.**{*;}
-keep class com.taobao.wireless.security.**{*;}
-keep class com.ut.secbody.**{*;}
-keep class com.taobao.dp.**{*;}
-keep class com.alibaba.wireless.security.**{*;}
-keep class com.alibaba.security.rp.**{*;}
-keep class com.alibaba.security.cloud.**{*;}
-keep class com.alibaba.security.realidentity.**{*;}
-keep class com.alibaba.security.biometrics.**{*;}
-keep class com.alibaba.sdk.android.**{*;}
-keep class android.taobao.windvane.**{*;}

#阿里face使用下面的混淆要崩溃，慎用

#-dontwarn com.taobao.securityjni.**
#-keep class com.taobao.securityjni.*{*;}
#-dontwarn com.taobao.wireless.security.**
#-keep class com.taobao.wireless.security.*{*;}
#-dontwarn com.ut.secbody.**
#-keep class com.ut.secbody.*{*;}
#-dontwarn com.taobao.dp.**
#-keep class com.taobao.dp.*{*;}
#-dontwarn com.alibaba.wireless.security.**
#-keep class com.alibaba.wireless.security.*{*;}
#-dontwarn com.alibaba.security.rp.**
#-keep class com.alibaba.security.rp.*{*;}
#-dontwarn com.alibaba.sdk.android.**
#-keep class com.alibaba.sdk.android.*{*;}
#-dontwarn com.alibaba.security.biometrics.**
#-keep class com.alibaba.security.biometrics.*{*;}
#-dontwarn android.taobao.windvane.**
#-keep class android.taobao.windvane.**{*;}
#################################################################
#kyc接口解析实体
-keep class huolongluo.byw.byw.ui.activity.renzheng.model.**{*;}
#################################################################
#HBT海报实体
-keep class huolongluo.byw.byw.ui.activity.hbtpartner.HBTSharePosterBean {*;}
#################################################################
##公共
-keep class androidx.core.app.CoreComponentFactory {*;}
#################################################################
##杠杆
#################################################################
##K线
-keep class com.android.coinw.api.kx.model.**{*;}
#################################################################
## 业务
-keep class com.android.legend.model.**{*;}
-keep class com.android.coinw.biz.trade.model.**{*;}
-keep class huolongluo.byw.byw.ui.fragment.contractTab.**{*;}
#################################################################
#apk 包内所有 class 的内部结构
-dump class_files.txt
##未混淆的类和成员
#-printseeds seeds.txt
##列出从 apk 中删除的代码
#-printusage unused.txt
##混淆前后的映射
#-printmapping mapping.txt

#极光
#-dontoptimize
-dontpreverify

-dontwarn cn.jpush.**
-keep class cn.jpush.** { *; }
-keep class huolongluo.byw.util.jpush.** {*;}

#checkVersionLib app升级库
-keepattributes *Annotation*
-keepclassmembers class * {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }
# Only required if you use AsyncExecutor
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}
 -keep class com.allenliu.versionchecklib.**{*;}
 -keep class androidx.constraintlayout.**{*;}

-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

#合约
-keep class com.legend.modular_contract_sdk.repository.model.** {*;}
-keep class com.legend.modular_contract_sdk.component.** {*;}

## 首页banner混淆 否则指示器Indicator失效（一直在最右边）
-keep class com.to.aboomy.pager2banner.** {*;}
-keep class androidx.viewpager2.widget.** {*;}