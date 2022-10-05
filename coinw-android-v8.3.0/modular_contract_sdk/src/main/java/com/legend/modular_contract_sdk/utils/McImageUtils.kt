package com.legend.modular_contract_sdk.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import com.mob.tools.utils.ResHelper
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class McImageUtils {
    companion object{
        private const val SD_PATH = "/sdcard/dskqxt/pic/"
        private const val IN_PATH = "/dskqxt/pic/"
        fun saveBitmap(context: Context, mBitmap: Bitmap): String? {
            var savePath: String
            var filePic: File
            savePath = if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
                SD_PATH
            } else {
                context.applicationContext.filesDir.absolutePath + IN_PATH
            }
            try {
                filePic = File(savePath + System.currentTimeMillis() + ".jpg")
                if (!filePic.exists()) {
                    filePic.parentFile.mkdirs()
                    filePic.createNewFile()
                }
                val fos = FileOutputStream(filePic)
                mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                fos.flush()
                fos.close()
            } catch (e: IOException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
                return null
            }
            return filePic.absolutePath
        }

        //保存图片到指定路径并在相册显示
        fun saveImageToGallery(context: Context, bmp: Bitmap): Boolean {
            // 首先保存图片
            val storePath = Environment.getExternalStorageDirectory().toString() + File.separator + Environment.DIRECTORY_DCIM + File.separator + "Camera" + File.separator
            val appDir = File(storePath)
            if (!appDir.exists()) {
                appDir.mkdir()
            }
            val fileName = System.currentTimeMillis().toString() + ".jpg"
            val file = File(appDir, fileName)
            try {
                val fos = FileOutputStream(file)
                //通过io流的方式来压缩保存图片
                val isSuccess = bmp.compress(Bitmap.CompressFormat.JPEG, 60, fos)
                fos.flush()
                fos.close()
                //把文件插入到系统图库
                MediaStore.Images.Media.insertImage(context.contentResolver, file.absolutePath, fileName, null)
                //保存图片后发送广播通知更新数据库
                val uri = Uri.fromFile(file)
                context.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri))
                return isSuccess
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return false
        }

        /**
         * 获取屏幕截图的bitmap
         * @param activity
         * @return
         */
        fun getScreenShotBitmap(activity: AppCompatActivity): Bitmap? {
            if (activity == null) {
                return null
            }
            val view = activity.window.decorView
            //允许当前窗口保存缓存信息
            view.isDrawingCacheEnabled = true
            view.buildDrawingCache()
            val navigationBarHeight: Int = McScreenUtils.getNavigationBarHeight(view.context)


            //获取屏幕宽和高
            val width = ResHelper.getScreenWidth(view.context)
            val height = ResHelper.getScreenHeight(view.context)

            // 全屏不用考虑状态栏，有导航栏需要加上导航栏高度
            var bitmap: Bitmap? = null
            try {
                bitmap = Bitmap.createBitmap(view.drawingCache, 0, 0, width,
                        height + navigationBarHeight)
            } catch (e: Exception) {
                // 这里主要是为了兼容异形屏做的处理，我这里的处理比较仓促，直接靠捕获异常处理
                // 其实vivo oppo等这些异形屏手机官网都有判断方法
                // 正确的做法应该是判断当前手机是否是异形屏，如果是就用下面的代码创建bitmap
                var msg = e.message
                // 部分手机导航栏高度不占窗口高度，不用添加，比如OppoR15这种异形屏
                if (msg!!.contains("<= bitmap.height()")) {
                    try {
                        bitmap = Bitmap.createBitmap(view.drawingCache, 0, 0, width,
                                height)
                    } catch (e1: Exception) {
                        msg = e1.message
                        // 适配Vivo X21异形屏，状态栏和导航栏都没有填充
                        if (msg!!.contains("<= bitmap.height()")) {
                            try {
                                bitmap = Bitmap.createBitmap(view.drawingCache, 0, 0, width,
                                        height - McScreenUtils.getStatusHeight(view.context))
                            } catch (e2: Exception) {
                                e2.printStackTrace()
                            }
                        } else {
                            e1.printStackTrace()
                        }
                    }
                } else {
                    e.printStackTrace()
                }
            }

            //销毁缓存信息
            view.destroyDrawingCache()
            view.isDrawingCacheEnabled = false
            return bitmap
        }

        /**
         * 将两个bitmap上下拼接成一个bitmap返回
         * @param bitmapTop
         * @param bitmapBottom
         * @return
         */
        fun addBitmap(bitmapTop: Bitmap, bitmapBottom: Bitmap): Bitmap? {
            val width = bitmapTop.width
            val height = bitmapTop.height + bitmapBottom.height
            val bitmapResult = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            val canvas = Canvas(bitmapResult)
            canvas.drawBitmap(bitmapTop, 0f, 0f, null)
            canvas.drawBitmap(bitmapBottom, 0f, bitmapTop.height.toFloat(), null)
            return bitmapResult
        }
    }
}