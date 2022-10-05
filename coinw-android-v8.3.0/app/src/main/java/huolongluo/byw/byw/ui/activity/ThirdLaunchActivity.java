package huolongluo.byw.byw.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import java.util.List;

import huolongluo.byw.BuildConfig;
import huolongluo.byw.R;
import huolongluo.byw.byw.ui.activity.main.MainActivity;
import huolongluo.byw.log.Logger;
import huolongluo.byw.util.Constant;
import huolongluo.byw.util.GsonUtil;

//三方跳转的activity用于判断应用是否启动，启动则跳转MainActivity，未启动则跳转启动页
public class ThirdLaunchActivity extends AppCompatActivity {
    private static final String TAG = "ThirdLaunchActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third_launch);
        Uri uri = getIntent().getData();
        if (!isOtherUIExisting(getApplicationContext())) {
            Intent intent = new Intent(ThirdLaunchActivity.this, StartUpActivity.class);
            intent.putExtra(Constant.KEY_THIRD_LAUNCH_URI, uri);
            startActivity(intent);
        } else {
            Intent intent = new Intent(ThirdLaunchActivity.this, MainActivity.class);
            intent.putExtra(Constant.KEY_THIRD_LAUNCH_URI, uri);
            startActivity(intent);
        }
        if (BuildConfig.ENV_DEV && uri != null) {
            String urlComplete = uri.toString();
            Logger.getInstance().debug(TAG, "gotoByScheme 完整urlComplete:" + urlComplete);
            String host = uri.getHost();
            Logger.getInstance().debug(TAG, "gotoByScheme host:" + host);
        }
        finish();
    }

    /**
     * 判断是否有别的页面存在
     *
     * @param context
     * @return
     */
    private boolean isOtherUIExisting(Context context) {
        boolean existing = false;
        try {
            String currClassName = getClass().getName();
            String currPackageName = getPackageName();
            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(10);
            if (list.size() <= 0) {
                existing = false;
            }
            for (ActivityManager.RunningTaskInfo info : list) {
                String activityName = info.baseActivity.getClassName();
                if (activityName.equals(currClassName)) {
                    continue;
                }
                if (info.baseActivity.getPackageName().equals(currPackageName)) {
                    existing = true;
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            existing = false;
        }
        return existing;
    }
}
