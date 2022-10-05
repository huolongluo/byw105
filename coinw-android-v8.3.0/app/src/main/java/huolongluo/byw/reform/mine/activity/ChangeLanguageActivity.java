package huolongluo.byw.reform.mine.activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.legend.modular_contract_sdk.api.ModularContractSDK;

import org.greenrobot.eventbus.EventBus;

import java.util.Locale;

import huolongluo.byw.R;
import huolongluo.byw.byw.base.BaseApp;
import huolongluo.byw.byw.share.Event;
import huolongluo.byw.byw.ui.activity.main.MainActivity;
import huolongluo.byw.manager.AppManager;
import huolongluo.byw.reform.base.BaseActivity;
import huolongluo.byw.util.Constant;
import huolongluo.byw.util.SPUtils;
import huolongluo.byw.util.config.ConfigurationUtils;
import huolongluo.byw.util.pricing.PricingMethodUtil;
import huolongluo.byw.view.CustomLoadingDialog;
import huolongluo.bywx.helper.AppHelper;

/**
 * Created by Administrator on 2019/4/12 0012.
 */
public class ChangeLanguageActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "ChangeLanguageActivity";
    
    private TextView title_tv;
    private RelativeLayout chinese_ll;
    private RelativeLayout english_ll;
    private RelativeLayout korean_ll;
    private ImageView zh;
    private ImageView en;
    private ImageView ko;
    private ImageView back_iv;
    private String currentLang = null;
    private long lastTimeMills = System.currentTimeMillis();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.multi_language);
        chinese_ll = findViewById(R.id.chinese_ll);
        english_ll = findViewById(R.id.english_ll);
        korean_ll = findViewById(R.id.korean_ll);
        chinese_ll.setOnClickListener(this);
        english_ll.setOnClickListener(this);
        korean_ll.setOnClickListener(this);
        title_tv = findViewById(R.id.title_tv);
        zh = findViewById(R.id.iv_zh);
        en = findViewById(R.id.iv_en);
        ko = findViewById(R.id.iv_ko);
        title_tv.setText(getString(R.string.langswitcher));
        back_iv = findViewById(R.id.back_iv);
        back_iv.setOnClickListener(view -> finish());
        String language = SPUtils.getString(this, Constant.KEY_LANG, "");
        initChooseView(language);
    }

    private void initChooseView(String language) {
        if (language.contains("zh")) {
            zh.setVisibility(View.VISIBLE);
            en.setVisibility(View.GONE);
            ko.setVisibility(View.GONE);
        } else if(language.contains("en")){
            zh.setVisibility(View.GONE);
            en.setVisibility(View.VISIBLE);
            ko.setVisibility(View.GONE);
        }
        else if(language.contains("ko")){
            zh.setVisibility(View.GONE);
            en.setVisibility(View.GONE);
            ko.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        //控制快速点击
        long currTimeMills = System.currentTimeMillis();
        if (currTimeMills - lastTimeMills < 800L) {
            return;
        }
        switch (v.getId()) {
            case R.id.chinese_ll:
                currentLang = Locale.SIMPLIFIED_CHINESE.toString();
                PricingMethodUtil.setPricingMethod(PricingMethodUtil.PRICING_RMB);
                EventBus.getDefault().post(new Event.ChangeLanguage());
                break;
            case R.id.english_ll:
                currentLang = Locale.US.toString();
                PricingMethodUtil.setPricingMethod(PricingMethodUtil.PRICING_DOLLAR);
                EventBus.getDefault().post(new Event.ChangeLanguage());
                break;
            case R.id.korean_ll:
                currentLang = Locale.KOREAN.toString();
                PricingMethodUtil.setPricingMethod(PricingMethodUtil.PRICING_WON);
                EventBus.getDefault().post(new Event.ChangeLanguage());
                break;
        }

        Dialog dialog = null;
        try {
            dialog = CustomLoadingDialog.createLoadingDialog(this);
            dialog.show();
        } catch (Throwable t) {
            t.printStackTrace();
        }
        initChooseView(currentLang);
        ModularContractSDK.INSTANCE.resetLanguage(currentLang);
        SPUtils.saveString(this, Constant.KEY_LANG, currentLang);
        //语言切换核心代码
        ConfigurationUtils.updateActivity(this, currentLang);
        Constant.currentLanguage = currentLang;
        EventBus.getDefault().post(new Event.LanguageChange());
        BaseApp.getSelf().initDefaultLanguage();
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        if (dialog != null) {
            AppHelper.dismissDialog(dialog);
        }
        AppManager.get().finishAllActivity();
    }
}
