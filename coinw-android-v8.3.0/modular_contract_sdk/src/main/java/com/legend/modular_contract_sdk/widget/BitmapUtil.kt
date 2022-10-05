package com.legend.modular_contract_sdk.widget

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

//保存文件到指定路径
fun saveImageToGallery(context: Context, bmp: Bitmap): Boolean {
    // 首先保存图片
    //  String storePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "dearxy";
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
        //把文件插入到系统图库，调用该方法会生成一张一样的缩略图产生两张图
//            MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), fileName, null);
        val values = ContentValues()
        values.put(MediaStore.Images.Media.DATA, file.absolutePath)
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        // MediaStore.Images.Media.insertImage(context.getContentResolver(), bmp, fileName, null);
        //保存图片后发送广播通知更新数据库
        val uri = Uri.fromFile(file)
        context.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri))
        return isSuccess
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return false
}