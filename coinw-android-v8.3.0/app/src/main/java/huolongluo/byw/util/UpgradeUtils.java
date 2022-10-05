package huolongluo.byw.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.Build;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.allenliu.versionchecklib.v2.AllenVersionChecker;
import com.allenliu.versionchecklib.v2.builder.DownloadBuilder;
import com.allenliu.versionchecklib.v2.builder.NotificationBuilder;
import com.allenliu.versionchecklib.v2.builder.UIData;
import com.allenliu.versionchecklib.v2.callback.CustomDownloadingDialogListener;
import com.allenliu.versionchecklib.v2.callback.CustomVersionDialogListener;
import com.allenliu.versionchecklib.v2.callback.ForceUpdateListener;
import com.allenliu.versionchecklib.v2.callback.RequestVersionListener;
import huolongluo.byw.R;
import huolongluo.byw.byw.bean.VersionInfo;
import huolongluo.byw.byw.ui.dialog.VersionDialog;
import huolongluo.byw.manager.AppManager;

public class UpgradeUtils {
    private static UpgradeUtils instance;
    private DownloadBuilder builder;
    private UpgradeUtils(){}
    public static UpgradeUtils getInstance(){
        if(instance==null){
            instance=new UpgradeUtils();
        }
        return instance;
    }

    public void upgrade(Context context, VersionInfo versionInfo){
        initDownloadBuilder(context,versionInfo);
    }

    private void initDownloadBuilder(Context context,VersionInfo versionInfo){
        if(builder!=null&&builder.getCustomVersionDialogListener()!=null){//上一个还在运行中
            return;
        }
        builder= AllenVersionChecker
                .getInstance()
                .requestVersion()
                .setRequestUrl("https://www.baidu.com/")
                .request(new RequestVersionListener() {
                    @Nullable
                    @Override
                    public UIData onRequestVersionSuccess(DownloadBuilder downloadBuilder, String result) {

                        return crateUIData(versionInfo);
                    }

                    @Override
                    public void onRequestVersionFailure(String message) {

                    }
                });
        if(versionInfo.getForce() == 1){
            builder.setForceUpdateListener(new ForceUpdateListener() {
                @Override
                public void onShouldForceUpdate() {
                    ((Activity)context).finish();
                }
            });
        }

        builder.setNotificationBuilder(createCustomNotification(context));
        builder.setCustomVersionDialogListener(createCustomDialog(versionInfo));
        builder.setCustomDownloadingDialogListener(createCustomDownloadingDialog(context));

        String apkName="Coinw_V"+versionInfo.getAndroid_version();
        builder.setApkName(apkName);
//        builder.setForceRedownload(true);//强制每次都下载，用于测试
        builder.executeMission(context);

    }
    //合适的地方需要调用销毁
    public void release(){
        AllenVersionChecker.getInstance().cancelAllMission();
    }

    /**
     * @return
     * @important 使用请求版本功能，可以在这里设置downloadUrl
     * 这里可以构造UI需要显示的数据
     * UIData 内部是一个Bundle
     */
    private UIData crateUIData(VersionInfo versionInfo) {
        UIData uiData = UIData.create();
        uiData.setTitle(versionInfo.getAndroid_version());
        uiData.setDownloadUrl(versionInfo.getAndroid_apk_download_url());
        uiData.setContent(versionInfo.getAdd_update_instructions());
        return uiData;
    }

    private NotificationBuilder createCustomNotification(Context context) {
        return NotificationBuilder.create()
                .setRingtone(true)
                .setIcon(R.drawable.launch_icon)
                .setTicker(context.getResources().getString(R.string.app_name))
                .setContentTitle("")
                .setContentText(context.getResources().getString(R.string.upgrade_progress)+"%d%%");
    }
    /**
     * 务必用库传回来的context 实例化你的dialog
     * 自定义的dialog UI参数展示，使用versionBundle
     *
     * @return
     */
    private CustomVersionDialogListener createCustomDialog(VersionInfo versionInfo) {
        return (context, versionBundle) -> {
            VersionDialog versionDialog = new VersionDialog(context);
            AlertDialog dialog=versionDialog.initDialog(versionInfo.getAndroid_version(), versionInfo.getAdd_update_instructions());
            return dialog;
        };
    }
    /**
     * 自定义下载中对话框，下载中会连续回调此方法 updateUI
     * 务必用库传回来的context 实例化你的dialog
     *
     * @return
     */
    private CustomDownloadingDialogListener createCustomDownloadingDialog(Context context) {
        return new CustomDownloadingDialogListener() {
            @Override
            public AlertDialog getCustomDownloadingDialog(Context context, int progress, UIData versionBundle) {
                VersionDialog versionDialog = new VersionDialog(context);
                AlertDialog dialog=versionDialog.initDialogUpgrading();
                return dialog;
            }

            @Override
            public void updateUI(AlertDialog dialog, int progress, UIData versionBundle) {
                TextView tvProgress = dialog.findViewById(R.id.tv_progress);
                ProgressBar progressBar = dialog.findViewById(R.id.pb);
                progressBar.setProgress(progress);
                tvProgress.setText(context.getString(R.string.versionchecklib_progress, progress));
            }
        };
    }
}
