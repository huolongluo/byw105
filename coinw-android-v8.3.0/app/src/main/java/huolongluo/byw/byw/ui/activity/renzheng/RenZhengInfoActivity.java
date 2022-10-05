package huolongluo.byw.byw.ui.activity.renzheng;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.imagepicker.view.CropImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import huolongluo.byw.R;
import huolongluo.byw.byw.base.BaseActivity;
import huolongluo.byw.byw.net.UrlConstants;
import huolongluo.byw.byw.ui.activity.main.MainActivity;
import huolongluo.byw.io.AppConstants;
import huolongluo.byw.log.Logger;
import huolongluo.byw.util.OkhttpManager;
import huolongluo.byw.util.RSACipher;
import huolongluo.byw.util.RSAEncrypt;
import huolongluo.byw.util.SPUtils;
import huolongluo.byw.util.tip.DialogUtils;
import huolongluo.byw.util.tip.MToast;
import huolongluo.byw.util.tip.SnackBarUtils;
import huolongluo.byw.util.uploadimage.BitmapUtils;
import huolongluo.byw.util.uploadimage.GlideImageLoader;
import huolongluo.byw.util.uploadimage.ImagePickerAdapter;
import huolongluo.byw.util.uploadimage.SelectDialog;
import huolongluo.bywx.helper.AppHelper;
import okhttp3.Request;

/**
 * <p>
 * Created by 火龙裸 on 2018/1/7 0007.
 * 其他地区（海外）身份认证提交信息页面
 */
public class RenZhengInfoActivity extends BaseActivity {
    @BindView(R.id.bnt_commit)
    Button bnt_commit;
    @BindView(R.id.iv_1)
    ImageView iv_1;
    @BindView(R.id.iv_2)
    ImageView iv_2;
    @BindView(R.id.iv_3)
    ImageView iv_3;
    @BindView(R.id.back_iv)
    ImageView back_iv;
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.card_tv1)
    TextView card_tv1;
    @BindView(R.id.card_tv1_1)
    TextView card_tv1_1;
    @BindView(R.id.card_tv2)
    TextView card_tv2;
    @BindView(R.id.shuoming_tv)
    TextView shuoming_tv;
    @BindView(R.id.card_id_tv)
    TextView card_id_tv;
    @BindView(R.id.root_view)
    LinearLayout root_view;
    @BindView(R.id.tv_flag)
    View flagView;
    @BindView(R.id.card_number_et)
    EditText card_number_et;
    @BindView(R.id.select_card_type)
    RelativeLayout select_card_type;
    private int AREA_CODE = 1001;
    private int ZHENG_JIAN_CODE = 1002;
    public String countryName = "";
    public String userName = "";
    public String zhengjian;
    public String zhenjianId = "2";
    private ImagePickerAdapter adapter;
    private ArrayList<ImageItem> selImageList; //当前选择的所有图片
    private ArrayList<ImageItem> images1;
    private ArrayList<ImageItem> images2;
    private ArrayList<ImageItem> images3;
    private Bitmap bitmap1;
    private Bitmap bitmap2;
    private Bitmap bitmap3;
    private String path1;
    private String path2;
    private String path3;
    private String url1;
    private String url2;
    private String url3;
    private int maxImgCount = 8;
    public static final int IMAGE_ITEM_ADD = -1;
    public static final int REQUEST_CODE_SELECT = 100;
    public static final int REQUEST_CODE_SELECT_1 = 102;
    public static final int REQUEST_CODE_SELECT_2 = 103;
    public static final int REQUEST_CODE_SELECT_3 = 104;
    public static final int REQUEST_CODE_PREVIEW = 101;
    private List<String> names;

    private PopupWindow popupWindow;
    TextView bn2;
    TextView bn3;
    TextView bn5;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    if (path1 != null) {
                        upLoad(path1, 1);
                    }
                    break;
                case 2:
                    if (path2 != null) {
                        upLoad(path2, 2);
                    }
                    break;
                case 3:
                    if (path3 != null) {
                        upLoad(path3, 3);
                    }
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected int getContentViewId() {
        return R.layout.activity_renzheng_two;
    }

    @Override
    protected void injectDagger() {
        activityComponent().inject(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void initViewsAndEvents() {
        boolean isKorea = getIntent().getBooleanExtra("isKorea", false);
        if (isKorea) {
            zhenjianId = "";
            card_id_tv.setText(getString(R.string.qs64));//海外ID
            flagView.setVisibility(View.VISIBLE);
            select_card_type.setEnabled(true);
        } else {
            flagView.setVisibility(View.GONE);
            select_card_type.setEnabled(false);
        }
        select_card_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
        zhengjian = getString(R.string.sfz);
        title_tv.setText(getString(R.string.identity1));
        countryName = getIntent().getStringExtra("country");
        userName = getIntent().getStringExtra("name");
        refreshByType();
        initImagePicker();
        initWidget();
        names = new ArrayList<>();
        names.add(getString(R.string.photograph));
        names.add(getString(R.string.photo1));
        eventClick(back_iv).subscribe(o -> close(), throwable -> {
            Logger.getInstance().error(throwable);
        });
        eventClick(bnt_commit).subscribe(o -> {
            ValidateIdentity();
        }, throwable -> {
            Logger.getInstance().error(throwable);
        });
        eventClick(iv_1).subscribe(o -> {
            showDialog(new SelectDialog.SelectDialogListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    switch (position) {
                        case 0: // 直接调起相机
                            //打开选择,本次允许选择的数量
                            ImagePicker.getInstance().setSelectLimit(1);
                            Intent intent = new Intent(RenZhengInfoActivity.this, ImageGridActivity.class);
                            intent.putExtra(ImageGridActivity.EXTRAS_TAKE_PICKERS, true); // 是否是直接打开相机
                            startActivityForResult(intent, REQUEST_CODE_SELECT_1);
                            break;
                        case 1:
                            //打开选择,本次允许选择的数量
                            ImagePicker.getInstance().setSelectLimit(1);
                            Intent intent1 = new Intent(RenZhengInfoActivity.this, ImageGridActivity.class);
                            startActivityForResult(intent1, REQUEST_CODE_SELECT_1);
                            break;
                        default:
                            break;
                    }
                }
            }, names);
        }, throwable -> {
            Logger.getInstance().error(throwable);
        });
        eventClick(iv_2).subscribe(o -> {
            showDialog(new SelectDialog.SelectDialogListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    switch (position) {
                        case 0: // 直接调起相机
                            //打开选择,本次允许选择的数量
                            ImagePicker.getInstance().setSelectLimit(maxImgCount - selImageList.size());
                            Intent intent = new Intent(RenZhengInfoActivity.this, ImageGridActivity.class);
                            intent.putExtra(ImageGridActivity.EXTRAS_TAKE_PICKERS, true); // 是否是直接打开相机
                            startActivityForResult(intent, REQUEST_CODE_SELECT_2);
                            break;
                        case 1:
                            //打开选择,本次允许选择的数量
                            ImagePicker.getInstance().setSelectLimit(maxImgCount - selImageList.size());
                            Intent intent1 = new Intent(RenZhengInfoActivity.this, ImageGridActivity.class);
                            startActivityForResult(intent1, REQUEST_CODE_SELECT_2);
                            break;
                        default:
                            break;
                    }
                }
            }, names);
        }, throwable -> {
            Logger.getInstance().error(throwable);
        });
        eventClick(iv_3).subscribe(o -> {
            showDialog(new SelectDialog.SelectDialogListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    switch (position) {
                        case 0: // 直接调起相机
                            //打开选择,本次允许选择的数量
                            ImagePicker.getInstance().setSelectLimit(maxImgCount - selImageList.size());
                            Intent intent = new Intent(RenZhengInfoActivity.this, ImageGridActivity.class);
                            intent.putExtra(ImageGridActivity.EXTRAS_TAKE_PICKERS, true); // 是否是直接打开相机
                            startActivityForResult(intent, REQUEST_CODE_SELECT_3);
                            break;
                        case 1:
                            //打开选择,本次允许选择的数量
                            ImagePicker.getInstance().setSelectLimit(maxImgCount - selImageList.size());
                            Intent intent1 = new Intent(RenZhengInfoActivity.this, ImageGridActivity.class);
                            startActivityForResult(intent1, REQUEST_CODE_SELECT_3);
                            break;
                        default:
                            break;
                    }
                }
            }, names);
        }, throwable -> {
            Logger.getInstance().error(throwable);
        });
    }

    private void refreshByType() {
        if (TextUtils.equals(zhenjianId, "2")) {
            card_tv1.setText(getString(R.string.hzxxy));
            card_tv2.setText(getString(R.string.hzqmy));
            card_tv1_1.setText(getString(R.string.gz));
            shuoming_tv.setText(getString(R.string.zjyq));
        } else if (TextUtils.equals(zhenjianId, "5")) {
            card_tv1.setText(getString(R.string.str_id_info_page));
            card_tv2.setText(getString(R.string.str_id_signature_page));
            card_tv1_1.setText(getString(R.string.gz));
            shuoming_tv.setText(getString(R.string.zjyq));
        }
    }

    @Override
    protected void onDestroy() {
        if (handler != null) {
            try {
                handler.removeCallbacksAndMessages(null);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
        super.onDestroy();
        countryName = "";
        zhenjianId = "";
        zhengjian = "";
    }

    /*  private void initImagePicker() {
          ImagePicker imagePicker = ImagePicker.getInstance();
          imagePicker.setImageLoader(new GlideImageLoader());   //设置图片加载器
          imagePicker.setShowCamera(true);                      //显示拍照按钮
          imagePicker.setCrop(true);                            //允许裁剪（单选才有效）false为不允许
          imagePicker.setSaveRectangle(true);                   //是否按矩形区域保存
          imagePicker.setSelectLimit(maxImgCount);              //选中数量限制
          imagePicker.setMultiMode(false);                      //多选
          imagePicker.setStyle(CropImageView.Style.RECTANGLE);  //裁剪框的形状
          imagePicker.setFocusWidth(800);                       //裁剪框的宽度。单位像素（圆形自动取宽高最小值）
          imagePicker.setFocusHeight(800);                      //裁剪框的高度。单位像素（圆形自动取宽高最小值）
          imagePicker.setOutPutX(1000);                         //保存文件的宽度。单位像素
          imagePicker.setOutPutY(1000);                         //保存文件的高度。单位像素
      }*/
    private void initImagePicker() {
        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new GlideImageLoader());   //设置图片加载器
        imagePicker.setShowCamera(true);                      //显示拍照按钮
        imagePicker.setCrop(true);                           //允许裁剪（单选才有效）
        imagePicker.setSaveRectangle(true);                   //是否按矩形区域保存
        imagePicker.setSelectLimit(maxImgCount);              //选中数量限制
        imagePicker.setStyle(CropImageView.Style.RECTANGLE);  //裁剪框的形状
        imagePicker.setFocusWidth(800);                       //裁剪框的宽度。单位像素（圆形自动取宽高最小值）
        imagePicker.setFocusHeight(800);                      //裁剪框的高度。单位像素（圆形自动取宽高最小值）
        imagePicker.setOutPutX(1000);                         //保存文件的宽度。单位像素
        imagePicker.setOutPutY(1000);                         //保存文件的高度。单位像素
    }

    private void initWidget() {
        selImageList = new ArrayList<>();
    }

    private SelectDialog showDialog(SelectDialog.SelectDialogListener listener, List<String> names) {
        SelectDialog dialog = new SelectDialog(this, R.style.transparentFrameWindowStyle, listener, names);
        if (!this.isFinishing()) {
            dialog.show();
        }
        return dialog;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            //添加图片返回
            if (data != null && requestCode == REQUEST_CODE_SELECT_1) {
                images1 = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                if (images1 != null) {
//                    ImagePicker.getInstance().getImageLoader().displayImage((Activity) this, images1.get(0).path, iv1, 0, 0);
                    File file = new File(images1.get(0).path);
                    Log.d("上传图片", "原始getByteCount==  " + file.length());
                    if (file.exists()) {
                        if (file.length() > 1000 * 1000 * 4) {
                            MToast.show(RenZhengInfoActivity.this, getString(R.string.png_larg), 1);
                            return;
                        }
                    }
                    bitmap1 = BitmapFactory.decodeFile(images1.get(0).path);
                    path1 = images1.get(0).path;
                    iv_1.setImageBitmap(bitmap1);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            handler.sendEmptyMessage(1);
                        }
                    }).start();
                }
            }
            //添加图片返回
            if (data != null && requestCode == REQUEST_CODE_SELECT_2) {
                images2 = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                if (images2 != null) {
//                    ImagePicker.getInstance().getImageLoader().displayImage((Activity) this, images2.get(0).path, iv2, 0, 0);
                    File file = new File(images2.get(0).path);
                    if (file.exists()) {
                        if (file.length() > 1000 * 1000 * 4) {
                            MToast.show(RenZhengInfoActivity.this, getString(R.string.aa79), 1);
                            return;
                        }
                    }
                    bitmap2 = BitmapFactory.decodeFile(images2.get(0).path);
                    path2 = images2.get(0).path;
                    iv_2.setImageBitmap(bitmap2);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            handler.sendEmptyMessage(2);
                        }
                    }).start();
                }
            }
            //添加图片返回
            if (data != null && requestCode == REQUEST_CODE_SELECT_3) {
                images3 = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                if (images3 != null) {
//                    ImagePicker.getInstance().getImageLoader().displayImage((Activity) this, images3.get(0).path, iv3, 0, 0);
                    File file = new File(images3.get(0).path);
                    if (file.exists()) {
                        if (file.length() > 1000 * 1000 * 4) {
                            MToast.show(RenZhengInfoActivity.this, getString(R.string.png_larg), 1);
                            return;
                        }
                    }
                    bitmap3 = BitmapFactory.decodeFile(images3.get(0).path);
                    path3 = images3.get(0).path;
                    iv_3.setImageBitmap(bitmap3);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            handler.sendEmptyMessage(3);
                        }
                    }).start();
                }
            }
        } else if (resultCode == ImagePicker.RESULT_CODE_BACK) {
            //预览图片返回
            if (data != null && requestCode == REQUEST_CODE_PREVIEW) {
                ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_IMAGE_ITEMS);
                if (images != null) {
                    selImageList.clear();
                    selImageList.addAll(images);
                    adapter.setImages(selImageList);
                }
            }
        }
    }

    //提交身份验证
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void ValidateIdentity() {
        HashMap<String, String> params = new HashMap<>();
        RSACipher rsaCipher = new RSACipher();
        if (TextUtils.isEmpty(zhenjianId)) {
            showMessage(getString(R.string.qs64), 2);
            return;
        }
        if (TextUtils.isEmpty(card_number_et.getText().toString())) {
            showMessage(getString(R.string.str_input_id), 2);
            return;
        }
        if (path1 == null) {
            showMessage(getString(R.string.addz), 2);
            return;
        }
        if (path2 == null) {
            showMessage(getString(R.string.addf), 2);
            return;
        }
        if (path3 == null) {
            showMessage(getString(R.string.addzf), 2);
            return;
        }
        if (url1 == null) {
            showMessage(getString(R.string.loadi), 2);
            return;
        } else if (TextUtils.equals("fail", url1)) {
            url1 = null;
            showMessage(getString(R.string.loadi), 2);
            upLoad(path1, 1);
            return;
        }
        if (url2 == null) {
            showMessage(getString(R.string.loadi), 2);
            return;
        } else if (TextUtils.equals("fail", url2)) {
            url2 = null;
            showMessage(getString(R.string.loadi), 2);
            upLoad(path2, 2);
            return;
        }
        if (url3 == null) {
            showMessage(getString(R.string.loadi), 2);
            return;
        } else if (TextUtils.equals("fail", url3)) {
            url3 = null;
            showMessage(getString(R.string.loadi), 2);
            upLoad(path3, 3);
            return;
        }
        String body = "identityNo=" + URLEncoder.encode(card_number_et.getText().toString()) + "&realName=" + URLEncoder.encode(userName) + "&identityType=" + URLEncoder.encode(zhenjianId) + "&nationality=" + URLEncoder.encode(countryName);
        try {
            String body1 = RSAEncrypt.encrypt(body, RSAEncrypt.getPublicKey(AppConstants.KEY.PUBLIC_KEY));
//             = rsaCipher.encrypt(body, UrlConstants.publicKeys);
            params.put("body", body1);
            params.put("loginToken", SPUtils.getLoginToken());
            params.put("fpictureUrl1", url1);
            params.put("fpictureUrl2", url2);
            params.put("fpictureUrl3", url3);
        } catch (Exception e) {
            showMessage(getString(R.string.reup), 2);
            e.printStackTrace();
        }
        netTags.add(UrlConstants.DOMAIN + UrlConstants.ValidateIdentity);
        OkhttpManager.postAsync(UrlConstants.DOMAIN + UrlConstants.ValidateIdentity, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
                Log.d("实名认证", e.toString());
                SnackBarUtils.ShowRed(RenZhengInfoActivity.this, errorMsg);
            }

            @Override
            public void requestSuccess(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    int code = jsonObject.getInt("code");
                    if (code == 0) {
                        DialogUtils.getInstance().showDialog(RenZhengInfoActivity.this, getString(R.string.kyc_other_info_submit_success), getString(R.string.b38), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(MainActivity.class);
                                finish();
                            }
                        });

                    } else {
                        String value = jsonObject.getString("value");
                        showMessage(value, 2);
                    }
                } catch (JSONException e) {
                    showMessage(getString(R.string.service_expec), 2);
                    e.printStackTrace();
                }
            }
        });
    }

    private void showDialog() {
        //设置要显示的view
        View view = View.inflate(this, R.layout.renzheng_doalog, null);
        bn2 = view.findViewById(R.id.bn2);
        bn3 = view.findViewById(R.id.bn3);
        bn5 = view.findViewById(R.id.bn5);
        bn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zhenjianId = "2";
                card_id_tv.setText(((TextView) v).getText().toString());//身份证0，护照2
                AppHelper.dismissPopupWindow(popupWindow);
            }
        });
        bn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppHelper.dismissPopupWindow(popupWindow);
            }
        });
        bn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zhenjianId = "5";
                card_id_tv.setText(((TextView) v).getText().toString());//海外ID
                AppHelper.dismissPopupWindow(popupWindow);
            }
        });
        //此处可按需求为各控件设置属性
        popupWindow = new PopupWindow(view, WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.FILL_PARENT);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(LinearLayout.LayoutParams.FILL_PARENT);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setContentView(view);
        // popupWindow.setAnimationStyle(R.style.pop_anim_style);
        popupWindow.showAtLocation(root_view, Gravity.BOTTOM, 0, 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void upLoad(String path, int type) {
        HashMap<String, String> params = new HashMap<>();
        //第一步:将Bitmap压缩至字节数组输出流ByteArrayOutputStream
        Bitmap bitmap = BitmapUtils.getSmallBitmap(path);
        final ByteArrayOutputStream[] byteArrayOutputStream = {new ByteArrayOutputStream()};
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream[0]);
        //第二步:利用Base64将字节数组输出流中的数据转换成字符串String
        byte[] byteArray = byteArrayOutputStream[0].toByteArray();
        String imageString1 = Base64.encodeToString(byteArray, Base64.DEFAULT);
        params.put("filedata1", imageString1);
        params.put("ext1", "png");
        params.put("loginToken", SPUtils.getLoginToken());
        netTags.add(UrlConstants.DOMAIN + UrlConstants.ValidateIdentityUpload);
        showProgress("");
        OkhttpManager.postAsync(UrlConstants.DOMAIN + UrlConstants.ValidateIdentityUpload, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
                hideProgress();
                e.printStackTrace();
                if (type == 1) {
                    showMessage(getString(R.string.uploading_fail), 2);
                    url1 = "fail";
                } else if (type == 2) {
                    showMessage(getString(R.string.uploading_fail1), 2);
                    url2 = "fail";
                } else if (type == 3) {
                    showMessage(getString(R.string.uploading_fail2), 2);
                    url3 = "fail";
                }
            }

            @Override
            public void requestSuccess(String result) {
                hideProgress();
                try {
                    if (byteArrayOutputStream[0] != null) {
                        byteArrayOutputStream[0].close();
                        byteArrayOutputStream[0] = null;
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    int code = jsonObject.getInt("code");
                    if (code == 0) {
                        if (type == 1) {
                            url1 = jsonObject.getString("url");
                        } else if (type == 2) {
                            url2 = jsonObject.getString("url");
                        } else if (type == 3) {
                            url3 = jsonObject.getString("url");
                        }
                    } else {
                        if (type == 1) {
                            showMessage(getString(R.string.uploading_fail), 2);
                            url1 = "fail";
                        } else if (type == 2) {
                            showMessage(getString(R.string.uploading_fail1), 2);
                            url2 = "fail";
                        } else if (type == 3) {
                            showMessage(getString(R.string.uploading_fail2), 2);
                            url3 = "fail";
                        }
                    }
                } catch (JSONException e) {
                    showMessage(getString(R.string.uploading_fail3), 2);
                    if (type == 1) {
                        url1 = "fail";
                    } else if (type == 2) {
                        url2 = "fail";
                    } else if (type == 3) {
                        url3 = "fail";
                    }
                    e.printStackTrace();
                }
            }
        });
    }
}
