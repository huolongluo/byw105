package com.android.legend.ui.share

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.text.TextUtils
import android.view.View
import cn.sharesdk.onekeyshare.OnekeyShare
import cn.sharesdk.system.text.ShortMessage
import cn.sharesdk.tencent.qq.QQ
import cn.sharesdk.wechat.friends.Wechat
import cn.sharesdk.wechat.moments.WechatMoments
import com.android.legend.base.BaseActivity
import com.bumptech.glide.Glide
import huolongluo.byw.R
import huolongluo.byw.log.Logger
import huolongluo.byw.util.ImageHelper
import huolongluo.byw.util.ShareHelper
import huolongluo.byw.util.domain.DomainUtil.getWebUrlSuffix
import huolongluo.byw.util.tip.MToast
import huolongluo.bywx.helper.AppHelper
import kotlinx.android.synthetic.main.activity_share2.*

class ShareActivity : BaseActivity(), View.OnClickListener {
    val url by lazy { intent.getStringExtra("url") }
    val dialog by lazy { ShareHelper.getShowPopDialog(this, this) }
    var bitmap:Bitmap?=null
    var localTargetPath:String?=null

    companion object{
        fun launch(context: Context, url: String){
            val intent= Intent(context, ShareActivity::class.java)
            intent.putExtra("url", url)
            context.startActivity(intent)
        }
    }

    override fun getContentViewId(): Int {
        return R.layout.activity_share2
    }

    override fun initView() {
        Glide.with(this).load(url).error(R.mipmap.ic_network_error).into(ivSharePic)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setCancelable(false)
        dialog.show()
    }

    override fun initData() {
    }

    override fun initObserve() {
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.wechat_bn -> {
                showShare(Wechat.NAME)
            }
            R.id.friend_bn -> {
                showShare(WechatMoments.NAME)
            }
            R.id.qq_bn -> {
                showShare(QQ.NAME)
            }
            R.id.message_bn ->                /* Uri smsToUri = Uri.parse("smsto:");
                Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);
                //"sms_body"???????????????smsbody?????????????????????content
                intent.putExtra("sms_body", "??????");
                startActivity(intent);*/showShare(ShortMessage.NAME)
            R.id.savePng_bn -> {
                saveImage()
                if (!TextUtils.isEmpty(localTargetPath)) { //?????????????????????-????????????
                    MToast.show(this, getString(R.string.d5), 2)
                } else { //????????????
                    MToast.show(this, getString(R.string.d6), 2)
                }
            }
            R.id.more_bn -> {
                systemShare()
            }
            R.id.tvCancel -> {
                AppHelper.dismissDialog(dialog)
                finish()
            }
        }
    }
    private fun systemShare() {
        try {
            saveImage()
            val uri = Uri.parse(MediaStore.Images.Media.insertImage(contentResolver, bitmap, null, null))
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_STREAM, uri)
            startActivity(Intent.createChooser(intent, getWebUrlSuffix()))
        } catch (t: Throwable) {
            t.printStackTrace()
        }
    }

    private fun showShare(platform: String?) {
        //??????????????????
        saveImage()
        try {
            val oks = OnekeyShare()
            //????????????????????????????????????????????????????????????????????????????????????
            if (platform != null) {
                oks.setPlatform(platform)
            }
            //
            if (TextUtils.equals(platform, Wechat.NAME) || TextUtils.equals(platform, WechatMoments.NAME)) {
                oks.setImageData(bitmap)
            } else {
                oks.setImagePath(localTargetPath)
            }
            //????????????
            oks.show(this)
        } catch (t: Throwable) {
            t.printStackTrace()
        }
    }
    private fun saveImage() {
        //?????????????????????
        if (ivSharePic != null && bitmap == null || TextUtils.isEmpty(localTargetPath)) {
            bitmap = ImageHelper.createViewBitmap(ivSharePic)
            localTargetPath = ImageHelper.saveImageToGallery(applicationContext, bitmap)
        }
    }
}