package huolongluo.byw.util

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Picture
import android.view.View
import android.webkit.WebView
import huolongluo.byw.log.Logger
import kotlinx.coroutines.*
import java.io.File
import java.io.FileOutputStream

object ScreenshotUtil {

    private inline fun exceptionHandler(crossinline errorCallback: (t: Throwable) -> Unit) = CoroutineExceptionHandler { coroutineContext, throwable ->
        Logger.getInstance().error(throwable)
        errorCallback.invoke(throwable)
    }

    /**
     * 截图webview中的内容，并将图片保存到指定路径
     * 成功和失败的回调，都在io协程
     */
    fun screenshotWebView(scope: CoroutineScope,
                          webView: WebView,
                          outputPath: String,
                          successCallback: (file: File) -> Unit = {},
                          errorCallback: (t: Throwable) -> Unit = {}) {
        val picture: Picture = webView.capturePicture()
        // 在子线程中进行io操作
        scope.launch(exceptionHandler(errorCallback)) {
            withContext(Dispatchers.IO) {
                val b = Bitmap.createBitmap(picture.width,
                        picture.height, Bitmap.Config.ARGB_8888)
                val c = Canvas(b)
                picture.draw(c)
                val outputFile = File(outputPath)
                if (!outputFile.exists()) {
                    outputFile.parentFile.mkdirs()
                    outputFile.createNewFile()
                }
                FileOutputStream(outputFile).use {
                    b.compress(Bitmap.CompressFormat.JPEG, 100, it);
                    b.recycle()
                }
                successCallback.invoke(outputFile)
            }
        }
    }

    /**
     * 截图view中的内容，并将图片保存到指定路径
     * 成功和失败的回调，都在io协程
     */
    fun screenshotView(scope: CoroutineScope,
                       view: View,
                       outputPath: String,
                       successCallback: (file: File) -> Unit = {},
                       errorCallback: (t: Throwable) -> Unit = {}) {
        // 在子线程中进行io操作
        scope.launch(exceptionHandler(errorCallback)) {
            withContext(Dispatchers.IO) {
                val b = Bitmap.createBitmap(view.width,
                        view.height, Bitmap.Config.ARGB_8888)
                val c = Canvas(b)
                view.draw(c)
                val outputFile = File(outputPath)
                if (!outputFile.exists()) {
                    outputFile.parentFile.mkdirs()
                    outputFile.createNewFile()
                }
                FileOutputStream(outputFile).use {
                    b.compress(Bitmap.CompressFormat.JPEG, 100, it);
                    b.recycle()
                }
                successCallback.invoke(outputFile)
            }
        }
    }

}