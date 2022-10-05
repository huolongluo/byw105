package huolongluo.byw.reform.c2c.oct.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.blankj.utilcodes.utils.ToastUtils;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.imagepicker.view.CropImageView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import huolongluo.byw.R;
import huolongluo.byw.byw.manager.DialogManager;
import huolongluo.byw.byw.net.UrlConstants;
import huolongluo.byw.byw.ui.dialog.AddDialog;
import huolongluo.byw.byw.ui.dialog.DialogView;
import huolongluo.byw.databinding.ActivityOtcaddzhifubBinding;
import huolongluo.byw.reform.base.BaseActivity;
import huolongluo.byw.reform.c2c.oct.LengthFilter;
import huolongluo.byw.reform.c2c.oct.bean.AddPayAcountEntity;
import huolongluo.byw.reform.c2c.oct.bean.BaseBean;
import huolongluo.byw.reform.c2c.oct.bean.BrandCardsEneity;
import huolongluo.byw.reform.c2c.oct.bean.UploadImageBean;
import huolongluo.byw.user.UserInfoManager;
import huolongluo.byw.util.OkhttpManager;
import huolongluo.byw.util.tip.MToast;
import huolongluo.byw.util.tip.SnackBarUtils;
import huolongluo.byw.util.uploadimage.BitmapUtils;
import huolongluo.byw.util.uploadimage.GlideImageLoader;
import okhttp3.Request;

/**
 * Created by dell on 2019/6/4.
 */
public class OtcAddZhifubActivityNew extends BaseActivity {
    ActivityOtcaddzhifubBinding mBinding;
    public static final int REQUEST_CODE_SELECT = 100;
    int type = 1;
    String accountId;
    String imageUrl;
    BrandCardsEneity.DataBeanX.DataBean alipayBean;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_otcaddzhifub);
        viewClick(mBinding.include.backIv, v -> finish());
        viewClick(mBinding.deleteBtn, v -> deleteCard());
        if (getIntent() != null) {
            type = getIntent().getIntExtra("type", 1);
            accountId = getIntent().getStringExtra("accountId");
            alipayBean = (BrandCardsEneity.DataBeanX.DataBean) getIntent().getSerializableExtra("data");
        }
        if (alipayBean != null) {
            mBinding.etAccount.setText(alipayBean.getAccount() + "");
            //   DisplayImageUtils.displayImage(mBinding.image, alipayBean.getQrcode(), this, 0, R.mipmap.add_view, true, false);
            Glide.with(this).load(alipayBean.getQrcode()).into(mBinding.image);
            mBinding.addLl.setVisibility(View.GONE);
            imageUrl = alipayBean.getQrcode();
        }
        if (type == 1) {
            mBinding.include.titleTv.setText(R.string.cx35);
            mBinding.deleteBtn.setVisibility(View.GONE);
        } else {
            mBinding.include.titleTv.setText(R.string.cx36);
        }
        mBinding.realNameTv.setText(UserInfoManager.getUserInfo().getRealName());
        viewClick(mBinding.addLl, v -> {
            ImagePicker.getInstance().setSelectLimit(1);
            Intent intent1 = new Intent(this, ImageGridActivity.class);
            startActivityForResult(intent1, REQUEST_CODE_SELECT);
        });
        viewClick(mBinding.image, v -> {
            ImagePicker.getInstance().setSelectLimit(1);
            Intent intent1 = new Intent(this, ImageGridActivity.class);
            startActivityForResult(intent1, REQUEST_CODE_SELECT);
        });
        viewClick(mBinding.btnNext, v -> {
            if (TextUtils.isEmpty(mBinding.etAccount.getText().toString())) {
                ToastUtils.showShortToast(R.string.cx37);
                return;
            }
            if (TextUtils.isEmpty(mBinding.etTradepsw.getText().toString())) {
                ToastUtils.showShortToast(R.string.cx38);
                return;
            }
            if (imageUrl == null) {
                ToastUtils.showShortToast(R.string.cx39);
                return;
            }
            updateThirdpay();
        });
        initImagePicker();
        mBinding.etAccount.setFilters(new InputFilter[]{new LengthFilter(this, 30, getString(R.string.str_account_limit))});
    }

    private void deleteCard() {
        AddDialog dialog = new AddDialog();
        dialog.setDialog(AddDialog.DELETE_CARD);
        dialog.show(getSupportFragmentManager(), getClass().getSimpleName());
        dialog.setOnClick(() -> {
            Map<String, String> params = new HashMap<>();
            params.put("id", String.valueOf(alipayBean.getId()));//1 新增银行卡 , 2 修改银行卡
            params.put("loginToken", UserInfoManager.getToken());
            DialogManager.INSTANCE.showProgressDialog(this);
            OkhttpManager.postAsync(UrlConstants.deleteBankCard, params, new OkhttpManager.DataCallBack() {
                @Override
                public void requestFailure(Request request, Exception e, String errorMsg) {
                    e.printStackTrace();
                    SnackBarUtils.ShowRed(OtcAddZhifubActivityNew.this, errorMsg);
                    DialogManager.INSTANCE.dismiss();
                    DialogView aDefault = DialogView.getDefault(OtcAddZhifubActivityNew.this);
                    aDefault.initDialog("", getString(R.string.cx40), false).show();
                    aDefault.setPositiveListener(() -> {
                        if (UserInfoManager.getOtcUserInfoBean().getData().isMerch()) {
                            Intent intent = new Intent(OtcAddZhifubActivityNew.this, SellerInfoActivity.class);
                            intent.putExtra("userId", UserInfoManager.getUserInfo().getFid() + "");
                            startActivity(intent);
                        }
                    });
                }

                @Override
                public void requestSuccess(String result) {
                    DialogManager.INSTANCE.dismiss();
                    try {
                        BaseBean baseBean = new Gson().fromJson(result, BaseBean.class);
                        if (baseBean.getCode() == 0) {
                            finish();
                        }
                        MToast.show(OtcAddZhifubActivityNew.this, baseBean.getValue(), 1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        });
    }

    void upload_image(String path) {
        //第一步:将Bitmap压缩至字节数组输出流ByteArrayOutputStream
        Bitmap bitmap = BitmapUtils.getSmallBitmap(path);
        final ByteArrayOutputStream[] byteArrayOutputStream = {new ByteArrayOutputStream()};
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream[0]);
        //第二步:利用Base64将字节数组输出流中的数据转换成字符串String
        byte[] byteArray = byteArrayOutputStream[0].toByteArray();
        String imageString1 = new String(Base64.encodeToString(byteArray, Base64.DEFAULT));
        HashMap<String, String> params = new HashMap<>();
        params.put("filedata1", imageString1);
        params.put("ext1", "zhifubao.png");
        params.put("loginToken", UserInfoManager.getToken());
        DialogManager.INSTANCE.showProgressDialog(this);
        OkhttpManager.postAsync(UrlConstants.upload_image, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
                e.printStackTrace();
                SnackBarUtils.ShowRed(OtcAddZhifubActivityNew.this, errorMsg);
                DialogManager.INSTANCE.dismiss();
                ToastUtils.showShortToast(R.string.cx41);
                mBinding.image.setImageBitmap(null);
                mBinding.addLl.setVisibility(View.VISIBLE);
            }

            @Override
            public void requestSuccess(String result) {
//{"code":"200","data":{"result":true,"code":0,"value":"上传成功","url":"https://btc018.oss-cn-shenzhen.aliyuncs.com/201906061643043_Xg56G.zhifubao.png"},"message":"执行成功"}
                DialogManager.INSTANCE.dismiss();
                try {
                    UploadImageBean bean = new Gson().fromJson(result, UploadImageBean.class);
                    if (bean.getCode() == 0) {
                        MToast.show(OtcAddZhifubActivityNew.this, bean.getValue(), 1);
                        imageUrl = bean.getUrl();
                        //  DisplayImageUtils.displayImage(mBinding.image,imageUrl, OtcAddZhifubActivity.this, 0, R.mipmap.add_view, true, false);
                        // Glide.with(OtcAddZhifubActivity.this).load(imageUrl).error(R.mipmap.add_view).centerCrop().into((ImageView) mBinding.image);
                        mBinding.image.setImageBitmap(bitmap);
                        //   ImageLoader.getInstance().displayImage(imageUrl,mBinding.image);
                    } else {
                        ToastUtils.showShortToast(R.string.cx42);
                        mBinding.image.setImageBitmap(null);
                        mBinding.addLl.setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {
                    ToastUtils.showShortToast(R.string.cx43);
                    mBinding.image.setImageBitmap(null);
                    mBinding.addLl.setVisibility(View.VISIBLE);
                    e.printStackTrace();
                }
            }
        });
    }

    void updateThirdpay() {
        Map<String, String> params = new HashMap<>();
        params.put("type", 3 + "");//3 支付宝,  2 微信
//        params.put("payType", "1");//1 支付宝,  2 微信
        params.put("ftradePassword", mBinding.etTradepsw.getText().toString() + "");
        params.put("account", mBinding.etAccount.getText().toString() + "");
        if (type == 2) {
            params.put("accountId", accountId + "");//
            params.put("id", alipayBean == null ? "" : alipayBean.getId() + "");//1 新增银行卡 , 2 修改银行卡
        }
        params = OkhttpManager.encrypt(params);
        params.put("qrcode", imageUrl);
        params.put("loginToken", UserInfoManager.getToken());
        //   params.put("accountId",  ""); //账户 ID , 修改有用
        DialogManager.INSTANCE.showProgressDialog(this);
        OkhttpManager.postAsync(type == 1 ? UrlConstants.AddAcount : UrlConstants.update_bankCard, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
                e.printStackTrace();
                SnackBarUtils.ShowRed(OtcAddZhifubActivityNew.this, errorMsg);
                DialogManager.INSTANCE.dismiss();
            }

            @Override
            public void requestSuccess(String result) {
                DialogManager.INSTANCE.dismiss();
                try {
                    AddPayAcountEntity baseBean = new Gson().fromJson(result, AddPayAcountEntity.class);
                    if (baseBean.getCode() == 0) {
                        finish();
                    }
                    MToast.show(OtcAddZhifubActivityNew.this, baseBean.getValue(), 1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initImagePicker() {
        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new GlideImageLoader());   //设置图片加载器
        imagePicker.setShowCamera(true);                      //显示拍照按钮
        imagePicker.setCrop(false);                           //允许裁剪（单选才有效）
        imagePicker.setSaveRectangle(true);                   //是否按矩形区域保存
        imagePicker.setSelectLimit(1);              //选中数量限制
        imagePicker.setStyle(CropImageView.Style.RECTANGLE);  //裁剪框的形状
        imagePicker.setFocusWidth(800);                       //裁剪框的宽度。单位像素（圆形自动取宽高最小值）
        imagePicker.setFocusHeight(800);                      //裁剪框的高度。单位像素（圆形自动取宽高最小值）
        imagePicker.setOutPutX(1000);                         //保存文件的宽度。单位像素
        imagePicker.setOutPutY(1000);                         //保存文件的高度。单位像素
    }

    ArrayList<ImageItem> images = null;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            //添加图片返回
            if (data != null && requestCode == REQUEST_CODE_SELECT) {
                images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                if (images != null && images.size() > 0) {
                    Bitmap bitmaps = BitmapFactory.decodeFile(images.get(0).path);
                    // ImageLoader.getInstance().displayImage(images.get(0).path,mBinding.image);
                    mBinding.image.setImageBitmap(bitmaps);
                    mBinding.addLl.setVisibility(View.GONE);
                    upload_image(images.get(0).path);
                }
            }
        }
    }
}
