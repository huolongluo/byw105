package com.legend.modular_contract_sdk.widget.dialog

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import cn.sharesdk.system.text.ShortMessage
import cn.sharesdk.tencent.qq.QQ
import cn.sharesdk.wechat.friends.Wechat
import cn.sharesdk.wechat.moments.WechatMoments
import com.legend.modular_contract_sdk.R
import com.legend.modular_contract_sdk.databinding.McSdkDialogSharePopBinding
import com.legend.modular_contract_sdk.onekeyshare.OnekeyShare
import com.legend.modular_contract_sdk.utils.ToastUtils
import com.legend.modular_contract_sdk.widget.saveImageToGallery
import com.lxj.xpopup.impl.FullScreenPopupView
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class ShareDialog(context: Context, private val shareBitmap: Bitmap) : FullScreenPopupView(context){

    lateinit var mBinding : McSdkDialogSharePopBinding

    lateinit var mOneKerShare: OnekeyShare

    override fun getImplLayoutId() = R.layout.mc_sdk_dialog_share_pop

    override fun onCreate() {
        super.onCreate()

        mOneKerShare = OnekeyShare()
        mOneKerShare.setImageData(shareBitmap)
        mBinding = McSdkDialogSharePopBinding.bind(popupImplView)

        mBinding.ivShareImg.setImageBitmap(shareBitmap)

        mBinding.friendBn.setOnClickListener {
            mOneKerShare.setPlatform(WechatMoments.NAME)
            mOneKerShare.show(context)
            dismiss()
        }

        mBinding.wechatBn.setOnClickListener {
            mOneKerShare.setPlatform(Wechat.NAME)
            mOneKerShare.show(context)
            dismiss()
        }

        mBinding.savePngBn.setOnClickListener {
            val isSuccess = saveImageToGallery(context, shareBitmap)
            if (isSuccess) {
                ToastUtils.showShortToast(context.getString(R.string.d5))
            } else {
                ToastUtils.showShortToast(context.getString(R.string.d6))
            }
            dismiss()
        }

        mBinding.qqBn.setOnClickListener {
            val path = saveBitmap(shareBitmap)
            mOneKerShare.setImagePath(path)
            mOneKerShare.setPlatform(QQ.NAME)
            mOneKerShare.show(context)
            dismiss()
        }

        mBinding.messageBn.setOnClickListener {
            mOneKerShare.setPlatform(ShortMessage.NAME)
            mOneKerShare.show(context)
            dismiss()
        }

        mBinding.moreBn.setOnClickListener {
            try {

                val uri = Uri.parse(MediaStore.Images.Media.insertImage(context.contentResolver, shareBitmap, null, null))
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "image/*"
                intent.putExtra(Intent.EXTRA_STREAM, uri)
                context.startActivity(Intent.createChooser(intent, "CoinW"))
                dismiss()
            } catch (t: Throwable) {
                t.printStackTrace()
                dismiss()
            }
        }

        mBinding.ivClose.setOnClickListener {
            dismiss()
        }
    }

    fun saveBitmap(mBitmap: Bitmap): String? {
        val savePath: String
        val filePic: File
        val SD_PATH = "/sdcard/dskqxt/pic/"
        val IN_PATH = "/dskqxt/pic/"
        savePath = if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            SD_PATH
        } else {
            context.applicationContext.filesDir.absolutePath + IN_PATH
            //  savePath = Environment.getDataDirectory().getAbsolutePath() + IN_PATH;
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
        //  return filePic.getAbsolutePath();
        return filePic.absolutePath
    }

}