package com.android.legend.extension

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Environment
import android.view.View
import android.widget.ImageView
import com.blankj.utilcodes.utils.ToastUtils
import com.bumptech.glide.Glide
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.runtime.Permission
import huolongluo.byw.R
import java.io.File
import java.io.FileOutputStream

fun View.gone() {
    this.visibility = View.GONE
}

fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}

fun View.setVisible(isVisible: Boolean) {
    this.visibility = if (isVisible) View.VISIBLE else View.GONE
}

/**
 * 加载用户圆形头像
 */
fun ImageView.loadAvatar(url: Any?) {
    Glide.with(this)
        .load(url)
//        .apply(RequestOptions.circleCropTransform().placeholder(R.color.color_common_bg))
        .into(this)
}

/**
 * 使用glide来加载图片
 */
fun ImageView.loadImage(url: Any?) {
    Glide.with(this)
        .load(url)
//        .apply(RequestOptions().placeholder(R.color.color_common_bg))
        .into(this)
}

fun ImageView.loadGif(url: Any?) {
    Glide.with(this)
        .asGif()
        .load(url)
        .into(this)
}


fun ImageView.saveToGallery(
    fileName: String,
    onSuccess: (file: File) -> Unit,
    onFailed: (t: Throwable) -> Unit
) {
    fun action() {
        try {
            val draw = this.drawable as BitmapDrawable
            val bitmap = draw.bitmap
            val sdcard = Environment.getExternalStorageDirectory()
            val dir = File(sdcard.absolutePath + "/" + Environment.DIRECTORY_DCIM + "/Camera")
            dir.mkdirs()
            val outputFile = File(dir, fileName)
            FileOutputStream(outputFile).use {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
            }
            onSuccess(outputFile)
        } catch (t: Throwable) {
            onFailed(t)
        }
    }

    if (AndPermission.hasPermissions(this.context, Permission.Group.STORAGE)) {
        action()
    } else {
        AndPermission.with(this.context)
            .runtime()
            .permission(Permission.Group.STORAGE)
            .onGranted { action() }
            .onDenied { ToastUtils.showLongToast(R.string.permissions_help_text) }
            .start()
    }
}

