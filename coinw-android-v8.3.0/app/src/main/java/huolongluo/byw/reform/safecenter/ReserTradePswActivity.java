package huolongluo.byw.reform.safecenter;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.jakewharton.rxbinding.widget.RxTextView;
import com.jakewharton.rxbinding.widget.TextViewAfterTextChangeEvent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import huolongluo.byw.R;
import huolongluo.byw.byw.net.UrlConstants;
import huolongluo.byw.log.Logger;
import huolongluo.byw.reform.base.BaseActivity;
import huolongluo.byw.user.UserInfoManager;
import huolongluo.byw.util.OkhttpManager;
import huolongluo.byw.util.PasswordChecker;
import huolongluo.byw.util.noru.NorUtils;
import huolongluo.byw.util.pwd.PwdUtils;
import huolongluo.byw.util.tip.MToast;
import huolongluo.byw.util.tip.SnackBarUtils;
import huolongluo.bywx.helper.AppHelper;
import okhttp3.Request;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
/**
 * Created by Administrator on 2019/1/4 0004.
 */
public class ReserTradePswActivity extends BaseActivity implements View.OnClickListener {
    Unbinder unbinder;
    @BindView(R.id.et_pwd01)
    EditText et_pwd01;
    @BindView(R.id.et_pwd02)
    EditText et_pwd02;
    @BindView(R.id.et_code_phone)
    EditText et_code_phone;
    @BindView(R.id.tv_getCode)
    TextView tv_getCode;
    @BindView(R.id.et_code_google)
    EditText et_code_google;
    @BindView(R.id.googleCode)
    TextView googleCode;
    @BindView(R.id.et_name)
    EditText et_name;
    @BindView(R.id.card_id_tv)
    TextView card_id_tv;
    @BindView(R.id.et_card_num)
    EditText et_card_num;
    @BindView(R.id.btn_sure)
    TextView btn_sure;
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.back_iv)
    ImageButton back_iv;
    @BindView(R.id.rl_google)
    RelativeLayout rl_google;
    @BindView(R.id.select_card_type)
    RelativeLayout select_card_type;
    @BindView(R.id.root_view)
    LinearLayout root_view;
    @BindView(R.id.clIntensity)
    ConstraintLayout clIntensity;
    @BindView(R.id.tvIntensity)
    TextView tvIntensity;
    @BindView(R.id.ivPwd1)
    ImageView ivPwd1;
    @BindView(R.id.ivPwd2)
    ImageView ivPwd2;
    @BindView(R.id.ivPwd3)
    ImageView ivPwd3;
    TextView bn1;
    TextView bn2;
    TextView bn3;
    TextView bn4;
    private PopupWindow popupWindow;
    private String zhenjianId = "0";
    private Subscription subscription;
    private PasswordChecker passwordChecker;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_sure:
                if (chechInput()) {
                    resetBindtradepass();
                }
                break;
            case R.id.back_iv:
                finish();
                break;
            case R.id.select_card_type:
//                showDialog();
                break;
            case R.id.bn1:
                zhenjianId = "0";
                card_id_tv.setText(((TextView) v).getText().toString());//身份证0，护照2
                AppHelper.dismissPopupWindow(popupWindow);
                break;
            case R.id.bn2:
                zhenjianId = "2";
                card_id_tv.setText(((TextView) v).getText().toString());//身份证0，护照2
                AppHelper.dismissPopupWindow(popupWindow);
                break;
            case R.id.bn4:
                zhenjianId = "3";
                card_id_tv.setText(((TextView) v).getText().toString());//身份证0，护照2
                AppHelper.dismissPopupWindow(popupWindow);
                break;
            case R.id.bn3:
                //  zhenjianId = "2";
                //  card_id_tv.setText(((TextView) v).getText().toString());//身份证0，护照2
                AppHelper.dismissPopupWindow(popupWindow);
                break;
            case R.id.tv_getCode:
                if (!UserInfoManager.getUserInfo().isBindMobil()) {
                    MToast.show(ReserTradePswActivity.this, getString(R.string.qs67), 1);
                }
                sendCode();
                break;
            case R.id.googleCode:
                et_code_google.setText(NorUtils.getClipboardText(ReserTradePswActivity.this));
                break;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_tradepsw);
        passwordChecker=new PasswordChecker();
        unbinder = ButterKnife.bind(this);
        btn_sure.setOnClickListener(this);
        back_iv.setOnClickListener(this);
        select_card_type.setOnClickListener(this);
        tv_getCode.setOnClickListener(this);
        googleCode.setOnClickListener(this);
        title_tv.setText(R.string.qe9);
        // rl_google.setVisibility(UserInfoManager.getUserInfo().isGoogleBind() ? View.VISIBLE : View.GONE);
        RxTextView.afterTextChangeEvents(et_pwd01).onErrorReturn(throwable -> {
            Logger.getInstance().errorLog(throwable);
            return null;
        }).subscribe(event -> {
            PwdUtils.refreshIntensity(this,et_pwd01,clIntensity,tvIntensity,ivPwd1,ivPwd2,ivPwd3,passwordChecker);
        },throwable -> Logger.getInstance().error(throwable));
        if(UserInfoManager.getUserInfo()!=null) {
            switch (UserInfoManager.getUserInfo().getIdentityType()){
                case 0:
                    card_id_tv.setText(getString(R.string.sfz));
                    break;
                case 2:
                    card_id_tv.setText(getString(R.string.str_passport));
                    break;
                case 4:
                    card_id_tv.setText(getString(R.string.gat));
                    break;
                case 5:
                    card_id_tv.setText(getString(R.string.str_overseas_id));
                    break;
            }
            zhenjianId=UserInfoManager.getUserInfo().getIdentityType()+"";
        }
    }

    void sendCode() {
        Map<String, String> params = new HashMap<>();
        params.put("type", "7");
        params = OkhttpManager.encrypt(params);
        params.put("loginToken", UserInfoManager.getToken());
        netTags.add(UrlConstants.DOMAIN + UrlConstants.GET_SMS);
        OkhttpManager.postAsync(UrlConstants.DOMAIN + UrlConstants.GET_SMS, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
                e.printStackTrace();
                SnackBarUtils.ShowRed(ReserTradePswActivity.this, errorMsg);
            }

            @Override
            public void requestSuccess(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    int result1 = jsonObject.getInt("code");
                    String msg = jsonObject.getString("value");
                    if (result1 == 0) {
                        SnackBarUtils.ShowBlue(ReserTradePswActivity.this, getString(R.string.qs68));
                        subscription = Observable.interval(0, 1, TimeUnit.SECONDS).limit(61).map(aLong -> 120 - aLong).doOnSubscribe(() -> {
                            if (tv_getCode != null) {
                                tv_getCode.setEnabled(false);
                            }
                        }).observeOn(AndroidSchedulers.mainThread()).doOnNext(aLong -> {
                            if (tv_getCode != null) {
                                tv_getCode.setText(aLong + " s");
                            }
                        }).doOnCompleted(() -> {
                            if (tv_getCode != null) {
                                tv_getCode.setText(R.string.qs69);
                                tv_getCode.setEnabled(true);
                            }
                        }).onErrorReturn(throwable -> {
                            Logger.getInstance().errorLog(throwable);
                            return 1L;
                        }).doOnError(throwable -> Logger.getInstance().error(throwable)).subscribe();
                    } else {
                        MToast.showButton(ReserTradePswActivity.this, msg, 2);
                    }
                } catch (JSONException e) {
                    SnackBarUtils.ShowRed(ReserTradePswActivity.this, getString(R.string.qs70));
                    e.printStackTrace();
                }
            }
        });
    }

    boolean chechInput() {
        if (TextUtils.isEmpty(et_pwd01.getText().toString())) {
            MToast.showButton(ReserTradePswActivity.this, getString(R.string.qe1), 1);
            return false;
        }
        if (TextUtils.isEmpty(et_pwd02.getText().toString())) {
            MToast.showButton(ReserTradePswActivity.this, getString(R.string.qe2), 1);
            return false;
        }
        if(!PwdUtils.isPwdRuleMatch(et_pwd01.getText().toString(),passwordChecker)){
            return false;
        }
        if (!TextUtils.equals(et_pwd01.getText().toString(), et_pwd02.getText().toString())) {
            MToast.showButton(ReserTradePswActivity.this, getString(R.string.qe3), 1);
            return false;
        }
        if (TextUtils.isEmpty(et_code_phone.getText().toString())) {
            MToast.showButton(ReserTradePswActivity.this, getString(R.string.qe4), 1);
            return false;
        }
        if (TextUtils.isEmpty(et_code_google.getText().toString())) {
            MToast.showButton(ReserTradePswActivity.this, getString(R.string.qe5), 1);
            return false;
        }
        if (TextUtils.isEmpty(et_name.getText().toString())) {
            MToast.showButton(ReserTradePswActivity.this, getString(R.string.qe6), 1);
            return false;
        }
        if (TextUtils.isEmpty(et_card_num.getText().toString())) {
            MToast.showButton(ReserTradePswActivity.this, getString(R.string.qe7), 1);
            return false;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        AppHelper.unsubscribe(subscription);
        AppHelper.dismissPopupWindow(popupWindow);
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    void resetBindtradepass() {
        Map<String, String> paream = new HashMap<>();
        paream.put("newPassword", et_pwd01.getText().toString());
        paream.put("resetName", et_name.getText().toString());
        paream.put("msgcode", et_code_phone.getText().toString());
        paream.put("resetGoogle", et_code_google.getText().toString());
        paream = OkhttpManager.encrypt(paream);
        paream.put("loginToken", UserInfoManager.getToken());
        paream.put("idcardno", et_card_num.getText().toString());//155586635456665 ,王洋
        paream.put("idcard", zhenjianId);//0身份证，2护照，3港澳台
        netTags.add(UrlConstants.DOMAIN + UrlConstants.resetBindtradepass);
        OkhttpManager.postAsync(UrlConstants.DOMAIN + UrlConstants.resetBindtradepass, paream, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
                SnackBarUtils.ShowRed(ReserTradePswActivity.this, errorMsg);
                e.printStackTrace();
            }

            @Override
            public void requestSuccess(String result) {
                // {"code":"200","data":{"code":-1,"value":"姓名错误"},"message":"执行成功"}
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject != null) {
                        int code = jsonObject.getInt("code");
                        String value = jsonObject.getString("value");
                        if (code == 0) {
                            MToast.show(ReserTradePswActivity.this, value, 1);
                            finish();
                        } else {
                            MToast.show(ReserTradePswActivity.this, value, 1);
                        }
                    }
                } catch (JSONException e) {
                    SnackBarUtils.ShowRed(ReserTradePswActivity.this, getString(R.string.qe8));
                    e.printStackTrace();
                }
            }
        });
    }

    private void showDialog() {
        //设置要显示的view
        View view = View.inflate(this, R.layout.renzheng_doalog, null);
        bn1 = view.findViewById(R.id.bn1);
        bn2 = view.findViewById(R.id.bn2);
        bn3 = view.findViewById(R.id.bn3);
        bn4 = view.findViewById(R.id.bn4);
        bn1.setOnClickListener(this);
        bn2.setOnClickListener(this);
        bn3.setOnClickListener(this);
        bn4.setOnClickListener(this);
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
}
