package huolongluo.byw.byw.ui.activity.bindgoogle;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import huolongluo.byw.R;
import huolongluo.byw.byw.base.BaseActivity;
import huolongluo.byw.log.Logger;
import huolongluo.byw.reform.safecenter.BindGoogleActivity;
/**
 * Created by LS on 2018/7/13.
 */
public class BindGoogleOneActivity extends BaseActivity {
    @BindView(R.id.tv_down)
    TextView tvDown;
    @BindView(R.id.tv_next)
    TextView tvNext;
    @BindView(R.id.back_iv)
    ImageView back_iv;
    @BindView(R.id.title_tv)
    TextView title_tv;
    public static boolean isFinish = false;

    /**
     * bind layout resource file
     */
    @Override
    protected int getContentViewId() {
        //return R.layout.activity_bind_google_one;
        return R.layout.activity_bindgoogle_one;
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
        title_tv.setText(getString(R.string.bind_google));
        eventClick(tvNext).subscribe(o -> {
            //startActivity(BindGoogleTwoActivity.class);
            startActivity(BindGoogleActivity.class);
        }, throwable -> {
            Logger.getInstance().error(throwable);
        });
        eventClick(tvDown).subscribe(o -> {
            openApplicationMarket("Google Authenticator");
        }, throwable -> {
            Logger.getInstance().error(throwable);
        });
        eventClick(back_iv).subscribe(o -> {
            finish();
        }, throwable -> {
            Logger.getInstance().error(throwable);
        });
    }

    /**
     * 通过包名 在应用商店打开应用
     * @param packageName 包名
     */
    private void openApplicationMarket(String packageName) {
        //JIRA: https://jira.legenddigital.com.au/browse/COIN-3304?filter=10552
        try {
            String str = "https://btc018.oss-cn-shenzhen.aliyuncs.com/coinw2/com-google-android-apps-authenticator2-5000100-51532195-7f40d893535070cde0ac091d7105bb66.apk";
            Intent localIntent = new Intent(Intent.ACTION_VIEW);
            localIntent.setData(Uri.parse(str));
            localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(localIntent);
        } catch (Exception e) {
            // 打开应用商店失败 可能是没有手机没有安装应用市场
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), getString(R.string.openfail), Toast.LENGTH_SHORT).show();
            // 调用系统浏览器进入商城
            String url = "http://app.mi.com/detail/163525?ref=search";
            openLinkBySystem(url);
        }
//        try {
//            String url = "http://shouji.baidu.com/software/22417419.html";
//            openLinkBySystem(url);
//        }catch(Throwable t){
//            Logger.getInstance().error(t);
//        }
    }

    /**
     * 调用系统浏览器打开网页
     * @param url 地址
     */
    private void openLinkBySystem(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isFinish) {
            isFinish = false;
            Intent intent = new Intent();
            intent.putExtra("message", getString(R.string.bind_suc));
            setResult(212, intent);
            finish();
        }
    }
}
