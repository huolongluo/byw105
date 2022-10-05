package huolongluo.byw.reform.c2c.oct.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import java.util.List;
import java.util.Map;

import huolongluo.byw.R;
import huolongluo.byw.byw.base.BaseApp;
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

public class OtcAppealActivity extends BaseActivity implements ImagePickerAdapter.OnRecyclerViewItemClickListener {

    TextView title_tv;

    RecyclerView recyclerView;
    public static final int IMAGE_ITEM_ADD = -1;
    public static final int REQUEST_CODE_SELECT = 100;
    public static final int REQUEST_CODE_PREVIEW = 101;
    private ImagePickerAdapter adapter;
    private ArrayList<ImageItem> selImageList; //当前选择的所有图片
    private int maxImgCount = 1;               //允许选择图片最大数
    private List<RelativeLayout> imgViews = new ArrayList<>();
    private List<ImageView> imgs = new ArrayList<>();
    int orderId;

    String imageUrl;
    ImageView imageview;
    ImageView imageview3;
    ImageView imageview2;
    TextView upload_tv;
    TextView comit_tv;
    EditText reason_et;
    ImageButton back_iv;
    boolean UploadingImage = false;
    private List<String> urls = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_otcappeal);
        initImgView();
        recyclerView = fv(R.id.recyclerView);
        title_tv = fv(R.id.title_tv);
        imageview = fv(R.id.imageview);
        imageview3 = fv(R.id.imageview3);
        imageview2 = fv(R.id.imageview2);
        upload_tv = fv(R.id.upload_tv);
        reason_et = fv(R.id.reason_et);
        comit_tv = fv(R.id.comit_tv);
        back_iv = fv(R.id.back_iv);
        title_tv.setText(R.string.cx44);

        viewClick(back_iv, v -> {
            finish();
            BaseApp.removerActivity();

        });


        if (getIntent() != null) {
            orderId = getIntent().getIntExtra("orderId", 0);
        }

        viewClick(upload_tv, v -> {
            if (urls.size() >= 3) {
                ToastUtils.showShortToast(R.string.max_img);
                return;
            }
            ImagePicker.getInstance().setSelectLimit(1);
            Intent intent1 = new Intent(this, ImageGridActivity.class);
            startActivityForResult(intent1, REQUEST_CODE_SELECT);

        });
        viewClick(imageview, v -> {
            ImagePicker.getInstance().setSelectLimit(1);
            Intent intent1 = new Intent(this, ImageGridActivity.class);
            startActivityForResult(intent1, REQUEST_CODE_SELECT);

        });
        viewClick(imageview3, v -> {
            ImagePicker.getInstance().setSelectLimit(1);
            Intent intent1 = new Intent(this, ImageGridActivity.class);
            startActivityForResult(intent1, REQUEST_CODE_SELECT);
        });
        viewClick(imageview2, v -> {
            ImagePicker.getInstance().setSelectLimit(1);
            Intent intent1 = new Intent(this, ImageGridActivity.class);
            startActivityForResult(intent1, REQUEST_CODE_SELECT);

        });

        viewClick(comit_tv, v -> {

            complainOrder();
        });

        initImagePicker();
        //  initWidget();
    }

    private void initImgView() {
        imgViews.add(findViewById(R.id.img_rl1));
        imgViews.add(findViewById(R.id.img_rl2));
        imgViews.add(findViewById(R.id.img_rl3));
        imgs.add(findViewById(R.id.imageview));
        imgs.add(findViewById(R.id.imageview2));
        imgs.add(findViewById(R.id.imageview3));
        findViewById(R.id.delete1).setOnClickListener(view -> {
            urls.remove(0);
            upDataImg();
        });
        findViewById(R.id.delete2).setOnClickListener(view -> {
            urls.remove(1);
            upDataImg();
        });
        findViewById(R.id.delete3).setOnClickListener(view -> {
            urls.remove(2);
            upDataImg();
        });
    }

    private void upDataImg() {
        for (RelativeLayout relativeLayout : imgViews) {
            relativeLayout.setVisibility(View.GONE);
        }
        for (int i = 0; i < urls.size(); i++) {
            imgViews.get(i).setVisibility(View.VISIBLE);
            Glide.with(this).load(urls.get(i)).into(imgs.get(i));
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        BaseApp.removerActivity();
    }

    void complainOrder() {

        if (UploadingImage) {
            ToastUtils.showShortToast(R.string.cx45);
            return;
        }


        if (TextUtils.isEmpty(reason_et.getText().toString().trim()) || reason_et.getText().toString().length() < 10) {
            ToastUtils.showShortToast(R.string.cx46);
            return;
        }

        Map<String, String> params = new HashMap<>();

        params.put("orderId", orderId + "");
        if (!TextUtils.isEmpty(urls.toString())) {
            StringBuilder urlArr = new StringBuilder();
            for (String url : urls) {
                urlArr.append(url).append(",");
            }
            params.put("reasonUrl", urlArr.toString() + "");
        }


        params = OkhttpManager.encrypt(params);
        params.put("reason", reason_et.getText().toString() + "");
        params.put("loginToken", UserInfoManager.getToken() + "");


        DialogManager.INSTANCE.showProgressDialog(this);
        OkhttpManager.postAsync(UrlConstants.complain_order, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
                e.printStackTrace();
                SnackBarUtils.ShowRed(OtcAppealActivity.this, "" + errorMsg);
                DialogManager.INSTANCE.dismiss();
            }

            @Override
            public void requestSuccess(String result) {
                DialogManager.INSTANCE.dismiss();
              /*  {
                    "code": -1,
                        "error": "业务异常",
                        "result": true,
                        "value": "超出可申诉的时间"
                }*/
                try {

                    BaseBean baseBean = new Gson().fromJson(result, BaseBean.class);

                    if (baseBean.getCode() == 0) {

                        BaseApp.finishActivity();
                        Intent intent = new Intent(OtcAppealActivity.this, OtcAppealDetailActivity.class);

                        intent.putExtra("orderId", orderId);
                        startActivity(intent);
                        finish();
                    } else {

                    }
                    MToast.show(OtcAppealActivity.this, baseBean.getValue(), 1);
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
                SnackBarUtils.ShowRed(OtcAppealActivity.this, getString(R.string.cx47) + errorMsg);
                UploadingImage = false;
            }

            @Override
            public void requestSuccess(String result) {
                UploadingImage = false;
                try {
                    UploadImageBean bean = new Gson().fromJson(result, UploadImageBean.class);
                    if (bean.getCode() == 0) {
                        if (urls.size() == 0) {
                            imageview.setImageBitmap(bitmap);
                        } else if (urls.size() == 1) {
                            imageview2.setImageBitmap(bitmap);
                        } else if (urls.size() == 2) {
                            imageview3.setImageBitmap(bitmap);
                        }
                        imageUrl = bean.getUrl();
                        urls.add(bean.getUrl());
                        upDataImg();
                    } else {
                        bean = null;
                    }
                    MToast.show(OtcAppealActivity.this, bean.getValue(), 1);
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

                    // Bitmap bitmap = BitmapFactory.decodeFile(images.get(0).path);

                    // imageview.setImageBitmap(bitmap);

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
