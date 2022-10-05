package huolongluo.byw.byw.ui.activity.addaddress;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.android.legend.model.enumerate.common.CommonCodeTypeEnum;
import com.android.legend.ui.bottomSheetDialogFragment.CommonSmsGoogleVerifyBottomDialogFragment;
import com.android.legend.ui.login.LoginActivity;
import com.android.legend.view.edittext.CommonEditText;
import com.legend.common.view.textview.DinproMediumTextView;
import com.uuzuche.lib_zxing.activity.CodeUtils;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import huolongluo.byw.R;
import huolongluo.byw.byw.base.BaseApp;
import huolongluo.byw.byw.bean.UserInfoBean;
import huolongluo.byw.byw.manager.DialogManager2;
import huolongluo.byw.byw.net.UrlConstants;
import huolongluo.byw.byw.ui.activity.safe_centre.SafeCentreActivity;
import huolongluo.byw.io.AppConstants;
import huolongluo.byw.reform.base.BaseSwipeBackActivity;
import huolongluo.byw.user.UserInfoManager;
import huolongluo.byw.util.FastClickUtils;
import huolongluo.byw.util.OkhttpManager;
import huolongluo.byw.util.RSACipher;
import huolongluo.byw.util.SPUtils;
import huolongluo.byw.util.tip.DialogUtils;
import huolongluo.byw.util.tip.MToast;
import huolongluo.byw.util.tip.SnackBarUtils;
import huolongluo.byw.util.zxing.MipcaActivityCapture;
import okhttp3.Request;
/**
 * 新增地址页面
 * zh地址添加
 */
public class AddAddressNewActivity extends BaseSwipeBackActivity {
    private String shortName;
    private String coinId;
    private String logo;
    private String fromClass;
    private String address;
    @BindView(R.id.back_iv)
    ImageView back;
    /*@BindView(R.id.iv_logo)
    ImageView ivLogo;*/
    @BindView(R.id.title_tv)
    TextView tvName;
    @BindView(R.id.et_address)
    EditText etAdd;
    @BindView(R.id.iv_qr_code)
    ImageView ivQr;
    @BindView(R.id.et_beizhu)
    EditText etBeizhu;
    @BindView(R.id.et_zijin)
    CommonEditText etZijin;
    @BindView(R.id.tvGoTradePwd)
    TextView tvGoTradePwd;
    @BindView(R.id.tv_submit)
    Button tvSubmit;
    @BindView(R.id.lian)
    LinearLayout lianView;
    @BindView(R.id.name_code)
    DinproMediumTextView name_code;
    @BindView(R.id.vAddress)
    View vAddress;
    @BindView(R.id.vRemarks)
    View vRemarks;
    @BindView(R.id.vTradePwd)
    View vTradePwd;
    Unbinder unbinder;
    private String selectName;
    private ArrayList<String> names;
    private String chainName;
    @BindView(R.id.spinner_code)
    Spinner spinner_code;

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_new);
        unbinder = ButterKnife.bind(this);
        initViewsAndEvents();
    }

    @Override
    protected void onDestroy() {
        if (unbinder != null) {
            unbinder.unbind();
        }
        super.onDestroy();
    }

    /**
     * init views and events here
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    protected void initViewsAndEvents() {
        tvName.setText(getString(R.string.addadd));
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            fromClass = bundle.getString("fromClass");
            if (fromClass.equals("NewWalletListActivity")) {
                shortName = bundle.getString("shortName");
                coinId = bundle.getString("id");
                logo = bundle.getString("logo");
                names = (ArrayList<String>) bundle.getSerializable("names");
                chainName = getIntent().getStringExtra("chainName");//不为空提现进入，为空地址管理进入
                this.selectName = chainName;
                if (!TextUtils.isEmpty(chainName)) {
                    name_code.setVisibility(View.VISIBLE);
                    spinner_code.setVisibility(View.GONE);
                    name_code.setText(chainName);
                } else {
                    spinner_code.setVisibility(View.VISIBLE);
                    name_code.setVisibility(View.GONE);
                }
            } else {
                shortName = bundle.getString("shortName");
                coinId = String.valueOf(bundle.getInt("id"));
                logo = bundle.getString("logo");
                address = bundle.getString("address");
            }
        }
        initSpinner();
        getUserInfo();
        tvName.setText(getString(R.string.add) + " " + shortName + " " + getString(R.string.address));
        if (fromClass.equals("CoinTiXianActivity")) {
            etAdd.setText(address);
        }
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tvSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });
        ivQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddAddressNewActivity.this, MipcaActivityCapture.class);
                startActivityForResult(intent, 1);
            }
        });
        etAdd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                vAddress.setSelected(hasFocus);
            }
        });
        etBeizhu.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                vRemarks.setSelected(hasFocus);
            }
        });
        etZijin.hideEye();
        etZijin.getEt().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                vTradePwd.setSelected(hasFocus);
            }
        });
    }
    private void checkTradePwd(){
        if (UserInfoManager.getUserInfo().getIsHasTradePWD() == 1) {
            tvGoTradePwd.setVisibility(View.GONE);
        }else{
            tvGoTradePwd.setVisibility(View.VISIBLE);
            tvGoTradePwd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (TextUtils.isEmpty(SPUtils.getLoginToken())) {
                        startActivity(LoginActivity.class);
                    } else {
                        startActivity(SafeCentreActivity.class);
                    }
                }
            });
        }
    }
    public void startActivity(Class<?> clazz) {
        Intent intent = new Intent(this, clazz);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

    //保存地址
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void submit() {
        if (FastClickUtils.isFastClick(500)) return;
        if (etAdd.getText().toString().isEmpty()) {
            MToast.showButton(AddAddressNewActivity.this, getString(R.string.address_empt), 1);
            return;
        }
        if (etBeizhu.getText().toString().isEmpty()) {
            MToast.showButton(AddAddressNewActivity.this, getString(R.string.in_remarks), 1);
            return;
        }
        if (UserInfoManager.getUserInfo().getIsHasTradePWD() == 0) {
            //.. showMessage("您还没有设置交易密码，请先设置交易密码",2);
            DialogUtils.getInstance().showTwoButtonDialog(AddAddressNewActivity.this, getString(R.string.aa28), getString(R.string.aa29), getString(R.string.aa30));
            DialogUtils.getInstance().setOnclickListener(new DialogUtils.onBnClickListener() {
                @Override
                public void onLiftClick(AlertDialog dialog, View view) {
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                }

                @Override
                public void onRightClick(AlertDialog dialog, View view) {
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    if (TextUtils.isEmpty(SPUtils.getLoginToken())) {
                        startActivity(new Intent(AddAddressNewActivity.this, LoginActivity.class));
                    } else {
                        startActivity(new Intent(AddAddressNewActivity.this, SafeCentreActivity.class));
                    }
                }
            });
            return;
        }
        if (etZijin.getText().toString().isEmpty()) {
            MToast.showButton(AddAddressNewActivity.this, getString(R.string.psw_empty), 1);
            return;
        }
        CommonSmsGoogleVerifyBottomDialogFragment.Companion.newInstance(CommonCodeTypeEnum.TYPE_ADD_WITHDRAW_ADDRESS.getType(), new CommonSmsGoogleVerifyBottomDialogFragment.CodeListener() {
            @Override
            public void getCode(@NotNull String code, boolean isSms) {
                submit(code,isSms);
            }
        } ).show(getSupportFragmentManager(), "Dialog");
    }

    private void submit(String code,boolean isSms){
        HashMap<String, String> params = new HashMap<>();
        String loginToken = SPUtils.getLoginToken();
        String id=coinId;
        RSACipher rsaCipher = new RSACipher();
        String body;
        String phoneCode=isSms?code:"";
        String googleCode=isSms?"":code;
        body = "symbol=" + URLEncoder.encode(id) + "&phoneCode=" + URLEncoder.encode(phoneCode)
                + "&totpCode=" + URLEncoder.encode(googleCode) + "&tradePwd="
                + URLEncoder.encode(etZijin.getText().toString());
        try {
            String body1 = rsaCipher.encrypt(body,AppConstants.KEY.PUBLIC_KEY);
            params.put("body", body1);
            params.put("hasSecond", String.valueOf(names != null));
            params.put("chainName", this.selectName);
            params.put("withdrawAddr", etAdd.getText().toString());
            params.put("remark", etBeizhu.getText().toString());
            params.put("loginToken", loginToken);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        DialogManager2.INSTANCE.showProgressDialog(this);
        OkhttpManager.postAsync(UrlConstants.DOMAIN + UrlConstants.SUMBIT_WITHDRAW_ADDRESS, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
                DialogManager2.INSTANCE.dismiss();
            }

            @Override
            public void requestSuccess(String result) {
                DialogManager2.INSTANCE.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    int code = jsonObject.getInt("code");
                    String result1 = jsonObject.getString("result");
                    String value = jsonObject.getString("value");
                    if (code == 0) {
                        MToast.showButton(AddAddressNewActivity.this, getString(R.string.add_suc), 1);
                        finish();
                    } else {
                        MToast.showButton(AddAddressNewActivity.this, value, 1);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (data != null) {
                Bundle bundle = data.getExtras();
                if (bundle == null) {
                    return;
                }
                if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                    String ret = bundle.getString(CodeUtils.RESULT_STRING);
                    etAdd.setText(ret);
                    Toast.makeText(this, getString(R.string.srcsuc), Toast.LENGTH_SHORT).show();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 获取InputMethodManager，隐藏软键盘
     * @param token
     */
    private void hideKeyboard(IBinder token) {
        if (token != null) {
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideKeyboard(v, ev)) {
                hideKeyboard(v.getWindowToken());
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时则不能隐藏
     * @param v
     * @param event
     * @return
     */
    private boolean isShouldHideKeyboard(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right && event.getY() > top && event.getY() < bottom) {
                // 点击EditText的事件，忽略它。
                return false;
            } else {
                return true;
            }
        }
        // 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditText上，和用户用轨迹球选择其他的焦点
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkTradePwd();
        // getUserInfo();
    }

    private void getUserInfo() {
        HashMap<String, String> params = new HashMap<>();
        String loginToken = SPUtils.getLoginToken();
        if (loginToken.isEmpty()) {
            return;
        }
        params.put("loginToken", loginToken);
        OkhttpManager.postAsync(UrlConstants.DOMAIN + UrlConstants.GET_USER_INFO, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
            }

            @Override
            public void requestSuccess(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    int code = jsonObject.getInt("code");
                    if (code == 0) {
                        JSONObject object = jsonObject.getJSONObject("userInfo");
                        UserInfoBean userInfoBean = com.alibaba.fastjson.JSONObject.parseObject(object.toString(), UserInfoBean.class);
                        if(userInfoBean!=null){
                            UserInfoManager.setUserInfoBean(userInfoBean);
                        }
                        //登录IM
                        BaseApp.getSelf().loginIM();
                        //
                    } else {
                        //  showMessage("获取用户信息失败",2);
                        SnackBarUtils.ShowRed(AddAddressNewActivity.this, getString(R.string.getuseinfo_error));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initSpinner() {
        Spinner codeSp = (Spinner) findViewById(R.id.spinner_code);
        if (names != null) {
            lianView.setVisibility(View.VISIBLE);
        } else {
            lianView.setVisibility(View.GONE);
            return;
        }
        String[] arr = new String[names.size()];
        for (int i = 0; i < names.size(); i++) {
            arr[i] = names.get(i);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.item_spinner, arr);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        codeSp.setAdapter(adapter);
        codeSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(TextUtils.isEmpty(chainName)){//地址管理进入才可以切换链
                    selectName = arr[position];
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
}
