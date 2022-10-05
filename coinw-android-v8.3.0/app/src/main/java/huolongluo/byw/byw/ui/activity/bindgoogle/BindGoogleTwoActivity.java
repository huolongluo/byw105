package huolongluo.byw.byw.ui.activity.bindgoogle;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import butterknife.BindView;
import huolongluo.byw.R;
import huolongluo.byw.byw.base.BaseActivity;
import huolongluo.byw.byw.net.UrlConstants;
import huolongluo.byw.log.Logger;
import huolongluo.byw.util.OkhttpManager;
import huolongluo.byw.util.SPUtils;
import huolongluo.byw.util.tip.SnackBarUtils;
import okhttp3.Request;
/**
 * Created by LS on 2018/7/13.
 */
public class BindGoogleTwoActivity extends BaseActivity {
    @BindView(R.id.tv_next)
    TextView tvNext;
    @BindView(R.id.tv_copy_key)
    TextView tvCopyKey;
    @BindView(R.id.tv_key)
    TextView tvKey;
    @BindView(R.id.ll_back)
    LinearLayout back;
    String key;
    String device_name;
    public static boolean isFinish = false;

    /**
     * bind layout resource file
     */
    @Override
    protected int getContentViewId() {
        return R.layout.activity_bind_google_two;
    }

    /**
     * Dagger2 use in your application module(not used in 'base' module)
     */
    @Override
    protected void injectDagger() {
    }

    /**
     * init views and events here
     */
    @Override
    protected void initViewsAndEvents() {
        getKey();
        eventClick(tvNext).subscribe(o -> {
            Intent intent = new Intent(this, BindGoogleThreeActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("totpKey", key);
            bundle.putString("totpName", device_name);
            intent.putExtras(bundle);
            startActivity(intent);
//            startActivity(BindGoogleThreeActivity.class,bundle);
        }, throwable -> {
            Logger.getInstance().error(throwable);
        });
        eventClick(tvCopyKey).subscribe(o -> {
            toCopy();
        }, throwable -> {
            Logger.getInstance().error(throwable);
        });
        eventClick(back).subscribe(o -> {
            finish();
        }, throwable -> {
            Logger.getInstance().error(throwable);
        });
    }

    private void toCopy() {
        key = tvKey.getText().toString();
        ClipboardManager copy = (ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);
        if (key != null) {
            copy.setText(key);
            //  showMessage("复制成功", 1);
            SnackBarUtils.ShowBlue(BindGoogleTwoActivity.this, getString(R.string.copy_suc));
        }
    }

    //获取Google秘钥
    private void getKey() {
        HashMap<String, String> params = new HashMap<>();
        String loginToken = SPUtils.getLoginToken();
        if (loginToken == null) {
            showMessage(getString(R.string.please_login), 2);
            return;
        }
        params.put("loginToken", loginToken);
        netTags.add(UrlConstants.DOMAIN + UrlConstants.GET_GOOGLEAUTH);
        OkhttpManager.postAsync(UrlConstants.DOMAIN + UrlConstants.GET_GOOGLEAUTH, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
            }

            @Override
            public void requestSuccess(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    int code = jsonObject.getInt("code");
                    device_name = jsonObject.getString("device_name");
                    key = jsonObject.getString("totpKey");
                    if (code == 0) {
                        tvKey.setText(key);
                    } else {
                        tvKey.setText("");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isFinish) {
            finish();
            isFinish = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
