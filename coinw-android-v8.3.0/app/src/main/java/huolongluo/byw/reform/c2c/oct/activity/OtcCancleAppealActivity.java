package huolongluo.byw.reform.c2c.oct.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcodes.utils.ToastUtils;
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
import huolongluo.byw.reform.base.BaseActivity;
import huolongluo.byw.reform.c2c.oct.bean.BaseBean;
import huolongluo.byw.reform.c2c.oct.bean.UploadImageBean;
import huolongluo.byw.user.UserInfoManager;
import huolongluo.byw.util.OkhttpManager;
import huolongluo.byw.util.tip.MToast;
import huolongluo.byw.util.tip.SnackBarUtils;
import huolongluo.byw.util.uploadimage.BitmapUtils;
import huolongluo.byw.util.uploadimage.GlideImageLoader;
import huolongluo.byw.util.uploadimage.ImagePickerAdapter;
import okhttp3.Request;

/**
 * Created by Administrator on 2019/5/16 0016.
 */

public class OtcCancleAppealActivity extends BaseActivity implements ImagePickerAdapter.OnRecyclerViewItemClickListener {

    TextView title_tv;

    RecyclerView recyclerView;
    public static final int IMAGE_ITEM_ADD = -1;
    public static final int REQUEST_CODE_SELECT = 100;
    public static final int REQUEST_CODE_PREVIEW = 101;
    private ImagePickerAdapter adapter;
    private ArrayList<ImageItem> selImageList; //当前选择的所有图片
    private int maxImgCount = 1;               //允许选择图片最大数

    int orderId;

    String imageUrl;
    ImageView imageview;
    TextView upload_tv;
    TextView comit_tv;
    EditText reason_et;

    ImageView back_iv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_otcanclecappeal);

        recyclerView = fv(R.id.recyclerView);
        title_tv = fv(R.id.title_tv);
        imageview = fv(R.id.imageview);
        upload_tv = fv(R.id.upload_tv);
        reason_et = fv(R.id.reason_et);
        comit_tv = fv(R.id.comit_tv);
        back_iv = fv(R.id.back_iv);
        title_tv.setText(R.string.xx1);

        viewClick(back_iv, v -> finish());
        if (getIntent() != null) {
            orderId = getIntent().getIntExtra("orderId", 0);
        }

        viewClick(upload_tv, v -> {

            ImagePicker.getInstance().setSelectLimit(1);
            Intent intent1 = new Intent(this, ImageGridActivity.class);
            startActivityForResult(intent1, REQUEST_CODE_SELECT);

        });
        viewClick(comit_tv, v -> {

            cancel_complain();
        });

        initImagePicker();
        //  initWidget();
    }

    boolean UploadingImage = false;

    void cancel_complain() {
        if (UploadingImage) {
            ToastUtils.showShortToast(R.string.xx2);
            return;
        }
        if (TextUtils.isEmpty(reason_et.getText().toString().trim()) || reason_et.getText().toString().length() < 10) {
            ToastUtils.showShortToast(R.string.xx3);
            return;
        }

        Map<String, String> params = new HashMap<>();

        params.put("orderId", orderId + "");
        if (!TextUtils.isEmpty(imageUrl)) {
            params.put("reasonUrl", imageUrl + "");
        }

        params = OkhttpManager.encrypt(params);
        params.put("reason", reason_et.getText().toString() + "");
        params.put("loginToken", UserInfoManager.getToken() + "");

        DialogManager.INSTANCE.showProgressDialog(this);

        OkhttpManager.postAsync(UrlConstants.cancel_complain, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
                e.printStackTrace();
                SnackBarUtils.ShowRed(OtcCancleAppealActivity.this, "" + errorMsg);
                DialogManager.INSTANCE.dismiss();
            }

            @Override
            public void requestSuccess(String result) {

                try {
                    DialogManager.INSTANCE.dismiss();
                    BaseBean baseBean = new Gson().fromJson(result, BaseBean.class);

                    if (baseBean.getCode() == 0) {
                        finish();
                    } else {

                    }
                    MToast.show(OtcCancleAppealActivity.this, baseBean.getValue(), 1);
                } catch (Exception e) {

                    e.printStackTrace();

                }
            }
        });


    }

    //上传图片
    void upload_image(String path) {


        //第一步:将Bitmap压缩至字节数组输出流ByteArrayOutputStream
        Bitmap bitmap = BitmapUtils.getSmallBitmap(path);

        final ByteArrayOutputStream[] byteArrayOutputStream = {new ByteArrayOutputStream()};
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream[0]);
        //第二步:利用Base64将字节数组输出流中的数据转换成字符串String
        byte[] byteArray = byteArrayOutputStream[0].toByteArray();


        String imageString1 = new String(Base64.encodeToString(byteArray, Base64.DEFAULT));

        Log.e("aaadsaf", "" + imageString1);
        HashMap<String, String> params = new HashMap<>();

        params.put("filedata1", imageString1);
        params.put("ext1", "zhifubao.png");
        params.put("loginToken", UserInfoManager.getToken());
        UploadingImage = true;
        OkhttpManager.postAsync(UrlConstants.upload_image, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
                e.printStackTrace();
                SnackBarUtils.ShowRed(OtcCancleAppealActivity.this, getString(R.string.xx4) + errorMsg);
                UploadingImage = false;
            }

            @Override
            public void requestSuccess(String result) {
//{"code":"200","data":{"result":true,"code":0,"value":"上传成功","url":"https://btc018.oss-cn-shenzhen.aliyuncs.com/201906061643043_Xg56G.zhifubao.png"},"message":"执行成功"}
                UploadingImage = false;
                try {

                    UploadImageBean bean = new Gson().fromJson(result, UploadImageBean.class);

                    if (bean.getCode() == 0) {
                        imageUrl = bean.getUrl();
                        imageview.setImageBitmap(bitmap);
                    } else {
                        bean = null;
                    }
                    MToast.show(OtcCancleAppealActivity.this, bean.getValue(), 1);

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
        imagePicker.setSelectLimit(maxImgCount);              //选中数量限制
        imagePicker.setStyle(CropImageView.Style.RECTANGLE);  //裁剪框的形状
        imagePicker.setFocusWidth(800);                       //裁剪框的宽度。单位像素（圆形自动取宽高最小值）
        imagePicker.setFocusHeight(800);                      //裁剪框的高度。单位像素（圆形自动取宽高最小值）
        imagePicker.setOutPutX(1000);                         //保存文件的宽度。单位像素
        imagePicker.setOutPutY(1000);                         //保存文件的高度。单位像素
    }

    private void initWidget() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        selImageList = new ArrayList<>();
        adapter = new ImagePickerAdapter(this, selImageList, maxImgCount);
        adapter.setOnItemClickListener(this);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(View view, int position) {
        ImagePicker.getInstance().setSelectLimit(1);
        Intent intent1 = new Intent(this, ImageGridActivity.class);
        startActivityForResult(intent1, REQUEST_CODE_SELECT);
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

                    //  Bitmap bitmap = BitmapFactory.decodeFile(images.get(0).path);

                    //  imageview.setImageBitmap(bitmap);

                    upload_image(images.get(0).path);

                    //  selImageList.addAll(images);
                    //   adapter.setImages(selImageList);
                }
            }
        } /*else if (resultCode == ImagePicker.RESULT_CODE_BACK) {
            //预览图片返回
            if (data != null && requestCode == REQUEST_CODE_PREVIEW) {
                images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_IMAGE_ITEMS);
                if (images != null) {
                    selImageList.clear();
                    selImageList.addAll(images);
                    adapter.setImages(selImageList);
                }
            }
        }*/
    }
}
