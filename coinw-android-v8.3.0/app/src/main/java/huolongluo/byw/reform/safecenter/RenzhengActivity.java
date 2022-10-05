package huolongluo.byw.reform.safecenter;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.blankj.utilcodes.utils.ToastUtils;
import com.google.gson.reflect.TypeToken;
import com.onfido.android.sdk.capture.ExitCode;
import com.onfido.android.sdk.capture.Onfido;
import com.onfido.android.sdk.capture.OnfidoConfig;
import com.onfido.android.sdk.capture.OnfidoFactory;
import com.onfido.android.sdk.capture.errors.OnfidoException;
import com.onfido.android.sdk.capture.ui.options.FlowStep;
import com.onfido.android.sdk.capture.upload.Captures;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import huolongluo.byw.R;
import huolongluo.byw.byw.manager.DialogManager2;
import huolongluo.byw.byw.net.UrlConstants;
import huolongluo.byw.byw.ui.activity.main.MainActivity;
import huolongluo.byw.byw.ui.activity.renzheng.CountryActiivty;
import huolongluo.byw.byw.ui.activity.renzheng.RenZhengInfoActivity;
import huolongluo.byw.helper.INetCallback;
import huolongluo.byw.helper.OKHttpHelper;
import huolongluo.byw.log.Logger;
import huolongluo.byw.model.Blank;
import huolongluo.byw.model.result.SingleResult;
import huolongluo.byw.reform.base.BaseActivity;
import huolongluo.byw.user.UserInfoManager;
import huolongluo.byw.util.FastClickUtils;
import huolongluo.byw.util.GsonUtil;
import huolongluo.byw.util.tip.DialogUtils;
import huolongluo.byw.util.tip.MToast;
import huolongluo.bywx.helper.AppHelper;
import huolongluo.bywx.utils.AppUtils;
import huolongluo.bywx.utils.EncryptUtils;
/**
 * Created by Administrator on 2018/11/23 0023.
 * 其他地区（海外）的身份认证页面
 */
public class RenzhengActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "RenzhengActivity";
    @BindView(R.id.root_view)
    LinearLayout root_view;
    @BindView(R.id.select_card_type)
    RelativeLayout select_card_type;
    @BindView(R.id.card_id_tv)
    TextView card_id_tv;
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.ll_country)
    LinearLayout ll_country;
    @BindView(R.id.et_phone)
    TextView et_phone;
    @BindView(R.id.tvTips)
    TextView tvTips;
    @BindView(R.id.name_tv)
    EditText name_tv;
    @BindView(R.id.xing_tv)
    EditText xing_tv;
    @BindView(R.id.btn_to_sure)
    Button btn_to_sure;
    @BindView(R.id.card_number_et)
    EditText card_number_et;
    @BindView(R.id.back_iv)
    ImageButton back_iv;
    @BindView(R.id.tv_flag)
    View flagView;
    TextView bn1;
    TextView bn2;
    TextView bn3;
    TextView bn5;
    private int AREA_CODE = 1001;
    private int ZHENG_JIAN_CODE = 1002;
    Unbinder unbinder;
    private PopupWindow popupWindow;
    private String countryName,countryCode;
    private String zhengjian;
    private String zhenjianId;
    private Onfido client;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_renzheng);
        unbinder = ButterKnife.bind(this);

      /*  intent.putExtra("name",xing_tv.getText().toString());
        intent.putExtra("cardId",cardId_tv.getText().toString());*/
        if (getIntent() != null) {
            String cardId = getIntent().getStringExtra("cardId");
            if (TextUtils.equals(cardId, "unknow")) {
                zhenjianId = "2";
                card_id_tv.setText(R.string.qs57);
            } else {
                card_number_et.setText(cardId);
                zhenjianId = "0";
                card_id_tv.setText(R.string.qs58);
                et_phone.setText(R.string.qs59);
                ll_country.setEnabled(false);
            }
        }
        select_card_type.setEnabled(false);
        select_card_type.setOnClickListener(this);
        title_tv.setText(R.string.qs60);
        ll_country.setOnClickListener(this);
        btn_to_sure.setOnClickListener(this);
        back_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        client = OnfidoFactory.create(this).getClient();
        tvTips.setText(String.format(getString(R.string.overseas_identity_tips), AppUtils.getOnfidoMaxTimePerDay()));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.select_card_type:
                //KeybordS.closeKeybord(xing_tv, this);
                showDialog();
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
            case R.id.bn3:
                AppHelper.dismissPopupWindow(popupWindow);
                break;
            case R.id.bn5://海外ID
                zhenjianId = "5";
                card_id_tv.setText(((TextView) v).getText().toString());//海外ID
                AppHelper.dismissPopupWindow(popupWindow);
                break;
            case R.id.ll_country://选择国家
                Intent intent = new Intent(this, CountryActiivty.class);
                intent.putExtra("zhenjianId", zhenjianId);
                startActivityForResult(intent, AREA_CODE);
                break;
            case R.id.btn_to_sure://选择国家
                if (TextUtils.isEmpty(et_phone.getText().toString()) || TextUtils.equals(et_phone.getText().toString(), getString(R.string.qs62))) {
                    MToast.showButton(this, getString(R.string.qs63), 1);
                    return;
                }
                if (TextUtils.isEmpty(xing_tv.getText().toString()) || TextUtils.isEmpty(name_tv.getText().toString())) {
                    MToast.showButton(this, getString(R.string.qs61), 1);
                    return;
                }
                if (FastClickUtils.isFastClick(500)) {
                    return;
                }
                if(AppUtils.isOnfidoOpen()){
                    if(TextUtils.equals(countryCode,"643")){//onfido不支持俄罗斯
                        nextOld();
                    }else{
                        nextOnfido();
                    }
                }else{
                    nextOld();
                }

                break;
        }
    }
    //老的流程
    private void nextOld(){
        boolean isKorea=false;
        if(TextUtils.equals(countryCode,"410")) {
            isKorea=true;
        }
        Intent intent1 = new Intent(this, RenZhengInfoActivity.class);
        intent1.putExtra("name", xing_tv.getText().toString() + name_tv.getText().toString());
        intent1.putExtra("country", et_phone.getText().toString());
        intent1.putExtra("isKorea", isKorea);
        startActivityForResult(intent1, AREA_CODE);
    }
    //使用onfido
    private void nextOnfido(){
        getSdkToken();
    }
    private void getSdkToken(){
        DialogManager2.INSTANCE.showProgressDialog(this);
        Type type = new TypeToken<SingleResult<SingleResult<String>>>() {
        }.getType();
        Map<String, Object> params = new HashMap<>();
        params.put("firstName", name_tv.getText().toString());
        params.put("lastName", xing_tv.getText().toString());
        params.put("applicationId", getPackageName());
        Logger.getInstance().debug(TAG,"param:"+ GsonUtil.obj2Json(params,Map.class));

        OKHttpHelper.getInstance().get(UrlConstants.GET_ONFIDO_TOKEN+"?"+ EncryptUtils.encryptStr(params)+"&loginToken=" + UserInfoManager.getToken(),
                null, getSdkCallback, type);
    }
    private void onfidoCheck(){
        DialogManager2.INSTANCE.showProgressDialog(this);
        Type type = new TypeToken<SingleResult<SingleResult<Blank>>>() {
        }.getType();
        Map<String, Object> params = new HashMap<>();
        params.put("type", 1);
        params=EncryptUtils.encrypt(params);
        params.put("loginToken", UserInfoManager.getToken());

        OKHttpHelper.getInstance().post(UrlConstants.CHECK_ONFIDO_RESULT,params, checkCallback, type);
    }
    private void startFlow(String sdkToken) {
        final FlowStep[] flowStepsWithOptions = new FlowStep[]{
                FlowStep.WELCOME,
                FlowStep.CAPTURE_DOCUMENT,
                FlowStep.CAPTURE_FACE,
                FlowStep.FINAL
        };

        startFlow(flowStepsWithOptions,sdkToken);
    }
    private void startFlow(final FlowStep[] flowSteps,String sdkToken) {
        OnfidoConfig onfidoConfig =
                OnfidoConfig.builder(this)
                        .withSDKToken(sdkToken)
                        .withCustomFlow(flowSteps)
                        .build();

        client.startActivityForResult(this, 1, onfidoConfig);
    }
    private void showDialog() {
        //设置要显示的view
        View view = View.inflate(this, R.layout.renzheng_doalog, null);
        bn1 = view.findViewById(R.id.bn1);
        bn2 = view.findViewById(R.id.bn2);
        bn3 = view.findViewById(R.id.bn3);
        bn5 = view.findViewById(R.id.bn5);
        bn1.setOnClickListener(this);
        bn2.setOnClickListener(this);
        bn3.setOnClickListener(this);
        bn5.setOnClickListener(this);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppHelper.dismissPopupWindow(popupWindow);
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1){//onfido
            client.handleActivityResult(resultCode, data, new Onfido.OnfidoResultListener() {
                @Override
                public void userExited(@NonNull ExitCode exitCode) {
                    Logger.getInstance().debug(TAG,"userExited exitCode:"+exitCode);
                    if(exitCode==ExitCode.CAMERA_PERMISSION_DENIED){
                        ToastUtils.showShortToast(R.string.permossion_denied);
                    }
                }

                @Override
                public void userCompleted(@NonNull Captures captures) {
                    Logger.getInstance().debug(TAG,"userCompleted captures:"+captures.toString());
                    onfidoCheck();
                }

                @Override
                public void onError(OnfidoException e) {
                    Logger.getInstance().debug(TAG,"onError msg:"+e.getMessage());
                    e.printStackTrace();
                }
            });
        }else{
            if (resultCode == -1) {
                if (requestCode == AREA_CODE) {
                    countryName = data.getStringExtra("countryName");
                    countryCode=data.getStringExtra("code");
                    String name = getString(R.string.str_korea);
                    if (TextUtils.equals(name, countryName)) {
                        String id = data.getStringExtra("id");
                        Logger.getInstance().debug("RenzhengActivity", "onActivityResult-id: " + id + " countryName: " + countryName);
                        //TODO 说明是韩国
                        flagView.setVisibility(View.VISIBLE);
                        select_card_type.setEnabled(true);
                    } else {
                        flagView.setVisibility(View.GONE);
                        select_card_type.setEnabled(false);
                    }
                    if (!TextUtils.isEmpty(countryName)) {
                        et_phone.setText(countryName);
                    }
                }
            }
        }

    }
    private INetCallback<SingleResult<SingleResult<String>>> getSdkCallback = new INetCallback<SingleResult<SingleResult<String>>>() {
        @Override
        public void onSuccess(SingleResult<SingleResult<String>> response) throws Throwable {
            DialogManager2.INSTANCE.dismiss();
            Logger.getInstance().debug(TAG,"result:"+GsonUtil.obj2Json(response,SingleResult.class));
            if (response == null || response.data == null) {
                Logger.getInstance().debug(TAG, "result is null.");
                return;
            }
            if (response.code.equals("200")&&response.data.code.equals("0")) {
                startFlow(response.data.data);
            }else{
                ToastUtils.showShortToast(response.data.msg);
            }
        }

        @Override
        public void onFailure(Exception e) throws Throwable {
            DialogManager2.INSTANCE.dismiss();
            Logger.getInstance().debug(TAG, "error", e);
        }
    };
    private INetCallback<SingleResult<SingleResult<Blank>>> checkCallback = new INetCallback<SingleResult<SingleResult<Blank>>>() {
        @Override
        public void onSuccess(SingleResult<SingleResult<Blank>> response) throws Throwable {
            DialogManager2.INSTANCE.dismiss();
            Logger.getInstance().debug(TAG,"result:"+GsonUtil.obj2Json(response,SingleResult.class));
            if (response == null || response.data == null) {
                Logger.getInstance().debug(TAG, "result is null.");
                return;
            }
            if (response.code.equals("200")&&response.data.code.equals("0")) {
                DialogUtils.getInstance().showDialog(RenzhengActivity.this, getString(R.string.kyc_other_info_submit_success), getString(R.string.b38), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(RenzhengActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        startActivity(intent);
                        finish();
                    }
                });
            }else{
                ToastUtils.showShortToast(response.data.msg);
            }
        }

        @Override
        public void onFailure(Exception e) throws Throwable {
            DialogManager2.INSTANCE.dismiss();
            Logger.getInstance().debug(TAG, "error", e);
        }
    };
}
