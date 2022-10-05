package huolongluo.byw.reform.home.activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.system.text.ShortMessage;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;
import huolongluo.byw.R;
import huolongluo.byw.byw.manager.DialogManager;
import huolongluo.byw.byw.net.UrlConstants;
import huolongluo.byw.helper.INetCallback;
import huolongluo.byw.helper.OKHttpHelper;
import huolongluo.byw.log.Logger;
import huolongluo.byw.model.result.SingleResult;
import huolongluo.byw.reform.base.BaseActivity;
import huolongluo.byw.user.UserInfoManager;
import huolongluo.byw.util.ImageHelper;
import huolongluo.byw.util.ShareHelper;
import huolongluo.byw.util.domain.DomainUtil;
import huolongluo.byw.util.tip.MToast;
import huolongluo.bywx.helper.AppHelper;
import huolongluo.bywx.utils.AppUtils;
import huolongluo.bywx.utils.EncryptUtils;
/**
 * 福利中心
 */
public class WelfareCenterShareActivity extends BaseActivity implements View.OnClickListener {
    private ImageButton back_iv;
    private TextView title_tv;
    private TextView share_image;
    private ImageView sharePicIV;
    private TextView awardTxt;
    private RelativeLayout sharePicLayout;
    private Dialog dialog;
    private Bitmap bitmap;
    private String localTargetPath = "";
    private final String TAG = "WelfareCenterShareActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.atv_welfare_center_share);
        sharePicLayout = findViewById(R.id.rl_share_pic);
        title_tv = findViewById(R.id.title_tv);
        awardTxt = findViewById(R.id.tv_award);//奖励数量
        share_image = findViewById(R.id.share_image);
        title_tv.setText(R.string.str_welfare_center);
        back_iv = findViewById(R.id.back_iv);
        sharePicIV = findViewById(R.id.iv_share_pic);
        //
        RequestOptions ro = new RequestOptions();
        Glide.with(WelfareCenterShareActivity.this).load(R.mipmap.ic_share_welfare).apply(ro).into(sharePicIV);
        //
        share_image.setOnClickListener(this);
        back_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        sharePic();
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    private void settingPosition(int height, String award) {//重新设置显示位置
        if (sharePicIV == null || awardTxt == null || TextUtils.isEmpty(award)) {
            return;
        }
        try {
            if (height <= 0) {
                height = AppUtils.getDisplayMetrics(this).heightPixels - AppUtils.dp2px(80);
            }
            //根据设计图计算出文本显示位置的比例为（0.40）左右
            int marginTop = (int) (height * 0.40);
            Logger.getInstance().debug(TAG, "marginTop: " + marginTop + " award: " + award);
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) awardTxt.getLayoutParams();
            if (lp != null) {
                if (lp.topMargin > 0) {//如果topMargin大于0，则直接返回
                    awardTxt.setText(award);
                    return;
                }
                lp.topMargin = marginTop;
            }
            awardTxt.setText(award);
            awardTxt.setLayoutParams(lp);
        } catch (Throwable t) {
            Logger.getInstance().error(t);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.share_image:
                if (dialog == null) {
                    dialog = ShareHelper.getShowPopDialog(this, this);
                } else {
                    dialog.show();
                }
                break;
            case R.id.wechat_bn://微信分享
                showShare(Wechat.NAME);
                AppHelper.dismissDialog(dialog);
                break;
            case R.id.friend_bn://
                showShare(WechatMoments.NAME);
                AppHelper.dismissDialog(dialog);
                break;
            case R.id.qq_bn://
                showShare(QQ.NAME);
                AppHelper.dismissDialog(dialog);
                break;
            case R.id.message_bn://短信分享
               /* Uri smsToUri = Uri.parse("smsto:");
                Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);
                //"sms_body"必须一样，smsbody是发送短信内容content
                intent.putExtra("sms_body", "你好");
                startActivity(intent);*/
                showShare(ShortMessage.NAME);
                break;
            case R.id.savePng_bn://保存图片
                saveImage();
                if (!TextUtils.isEmpty(localTargetPath)) {//已经生成好图片-保存成功
                    MToast.show(this, getString(R.string.d5), 2);
                } else {//保存失败
                    MToast.show(this, getString(R.string.d6), 2);
                }
                AppHelper.dismissDialog(dialog);
                break;
            case R.id.more_bn://
                systemShare();
                AppHelper.dismissDialog(dialog);
                break;
            case R.id.tvCancel://
                AppHelper.dismissDialog(dialog);
                break;
        }
    }

    private void saveImage() {
        //生成图片并保存
        if (sharePicLayout != null && bitmap == null || TextUtils.isEmpty(localTargetPath)) {
            bitmap = ImageHelper.createViewBitmap(sharePicLayout);
            localTargetPath = ImageHelper.saveImageToGallery(getApplicationContext(), bitmap);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void sharePic() {
        Map<String, Object> params = new HashMap<>();
        params.put("bodyType", "1");
//        params.put("loginToken", UserInfoManager.getToken());
        params = EncryptUtils.encrypt(params);
        params.put("loginToken", UserInfoManager.getToken());
        netTags.add(UrlConstants.ACTION_SHARE_WELFARE);
        //测试地址
//        String url = "http://172.24.249.2:3000/mock/21/app/welfare/user/share";
        Type type = new TypeToken<SingleResult<HashMap<String, String>>>() {
        }.getType();
        //UrlConstants.ACTION_SHARE_WELFARE
        OKHttpHelper.getInstance().post(UrlConstants.ACTION_SHARE_WELFARE, params, new INetCallback<SingleResult<HashMap<String, String>>>() {
            @Override
            public void onSuccess(SingleResult<HashMap<String, String>> result) throws Throwable {
                if (result == null) {
                    //TODO 异常处理情况
                    return;
                }
                try {
                    handleResult(result.data);
                } catch (Throwable t) {
                    Logger.getInstance().error(t);
                }
            }

            @Override
            public void onFailure(Exception e) throws Throwable {
                //TODO 处理异常情况
                Logger.getInstance().debug(TAG, "onFailure", e);
                showErrorMessage(R.string.service_expec);
            }
        }, type);
    }

    private void showErrorMessage(int resId) {
        if (resId <= 0) {
            return;
        }
        Toast.makeText(WelfareCenterShareActivity.this, getString(resId), Toast.LENGTH_LONG);
    }

    private void handleResult(HashMap<String, String> dataMap) {
        if (dataMap == null) {
            showErrorMessage(R.string.service_expec);
            //TODO 处理异常情况
            return;
        }
        //图片地址：welfare_share_pic
        //数据显示：award
        String sharePic = dataMap.get("welfare_share_pic");
        String award = dataMap.get("award");
        Logger.getInstance().debug(TAG, "sharePic: " + sharePic + " award: " + award);
        if (TextUtils.isEmpty(sharePic)) {
            showErrorMessage(R.string.service_expec);
            return;
        }
        RequestOptions ro = new RequestOptions();
        ro.error(R.mipmap.ic_share_welfare);
        ro.placeholder(R.mipmap.ic_share_welfare);
        ro.fallback(R.mipmap.ic_share_welfare);
        ro.diskCacheStrategy(DiskCacheStrategy.ALL);
        //
        if (TextUtils.isEmpty(award)) {//根据产品要求，如果为0时，服务器数据返回为空时，显示无值图片
            Glide.with(this).load(R.mipmap.ic_share_welfare_novalue).apply(ro).into(sharePicIV);
            return;
        }
        //
        Glide.with(this).load(sharePic).addListener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                Logger.getInstance().debug(TAG, "onLoadFailed", e);
                showErrorMessage(R.string.service_expec);
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                if (!TextUtils.isEmpty(award) && sharePicIV != null) {
                    Logger.getInstance().debug(TAG, "resource-height: " + resource.getIntrinsicHeight());
                    sharePicIV.post(new Runnable() {
                        @Override
                        public void run() {
                            settingPosition(resource.getIntrinsicHeight(), award);
                        }
                    });
                }
                return false;
            }
        }).apply(ro).into(sharePicIV);
    }

    private void systemShare() {
        try {
            saveImage();
            Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, null, null));
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            startActivity(Intent.createChooser(intent, DomainUtil.INSTANCE.getWebUrlSuffix()));
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private void showShare(String platform) {
        //首先生成图片
        saveImage();
        Logger.getInstance().debug(TAG, "platform: " + platform + " localTargetPath: " + localTargetPath);
        try {
            final OnekeyShare oks = new OnekeyShare();
            //指定分享的平台，如果为空，还是会调用九宫格的平台列表界面
            if (platform != null) {
                oks.setPlatform(platform);
            }
            //
            if (TextUtils.equals(platform, Wechat.NAME) || TextUtils.equals(platform, WechatMoments.NAME)) {
                oks.setImageData(bitmap);
            } else {
                oks.setImagePath(localTargetPath);
            }
            //启动分享
            oks.show(this);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    @Override
    public void finish() {
        AppHelper.dismissDialog(dialog);
        ImageHelper.recycle(bitmap);
        DialogManager.INSTANCE.dismiss();
        super.finish();
    }
}
