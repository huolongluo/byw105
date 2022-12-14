package com.allenliu.versionchecklib.v2.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.allenliu.versionchecklib.R;
import com.allenliu.versionchecklib.utils.ALog;
import com.allenliu.versionchecklib.utils.AllenEventBusUtil;
import com.allenliu.versionchecklib.utils.AppUtils;
import com.allenliu.versionchecklib.v2.AllenVersionChecker;
import com.allenliu.versionchecklib.v2.builder.DownloadBuilder;
import com.allenliu.versionchecklib.v2.builder.UIData;
import com.allenliu.versionchecklib.v2.eventbus.AllenEventType;

import java.io.File;

public class UIActivity extends AllenBaseActivity implements DialogInterface.OnCancelListener {

    private static final int DIALOG_WIDTH=300;//对话框的宽度 单位dp
    private AlertDialog versionDialog;
    private boolean isDestroy = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ALog.e("version activity create");

        showVersionDialog();
    }

    @Override
    protected void onDestroy() {
        isDestroy = true;
        ALog.e("version activity destroy");
        super.onDestroy();
    }

//    @Override
//    public void receiveEvent(CommonEvent commonEvent) {
//        switch (commonEvent.getEventType()) {
//            case AllenEventType.SHOW_VERSION_DIALOG:
//                showVersionDialog();
//                break;
//        }
//
//    }

    @Override
    public void showDefaultDialog() {
        if (getVersionBuilder() != null) {
            UIData uiData = getVersionBuilder().getVersionBundle();
            String title = "提示", content = "检测到新版本";
            if (uiData != null) {
                title = uiData.getTitle();
                content = uiData.getContent();
            }
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this).setTitle(title).setMessage(content).setPositiveButton(getString(R.string.versionchecklib_confirm), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dealVersionDialogCommit();

                }
            });
            if (getVersionBuilder().getForceUpdateListener() == null) {
                alertBuilder.setNegativeButton(getString(R.string.versionchecklib_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onCancel(versionDialog);
                    }
                });
                alertBuilder.setCancelable(false);


            } else {
                alertBuilder.setCancelable(false);
            }
            versionDialog = alertBuilder.create();
            versionDialog.setCanceledOnTouchOutside(false);
            versionDialog.show();
        }
    }

    @Override
    public void showCustomDialog() {
        if (getVersionBuilder() != null) {
            ALog.e("show customization dialog");
            versionDialog = getVersionBuilder().getCustomVersionDialogListener().getCustomVersionDialog(this, getVersionBuilder().getVersionBundle());
//            versionDialog.show();
            try {
                //自定义dialog，commit button 必须存在
                final View view = versionDialog.findViewById(R.id.versionchecklib_version_dialog_commit);
                if (view != null) {
                    ALog.e("view not null");

                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ALog.e("click");
                            dealVersionDialogCommit();
                        }
                    });

                } else {
                    throwWrongIdsException();
                }
                //如果有取消按钮，id也必须对应
                View cancelView = versionDialog.findViewById(R.id.versionchecklib_version_dialog_cancel);
                View closeView = versionDialog.findViewById(R.id.versionchecklib_version_dialog_close);
                if (cancelView != null) {
                    cancelView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            onCancel(versionDialog);
                        }
                    });
                }
                if (closeView != null) {
                    closeView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            onCancel(versionDialog);
                        }
                    });
                }
                if (getVersionBuilder().getForceUpdateListener() != null) {//强制升级
                    cancelView.setVisibility(View.GONE);
                    closeView.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                e.printStackTrace();
                throwWrongIdsException();
            }
            Window window = versionDialog.getWindow();
            if (window != null) {
                WindowManager.LayoutParams lp = window.getAttributes();//对话框宽度设置为固定值
                lp.width = dipToPx(UIActivity.this, DIALOG_WIDTH);
                window.setAttributes(lp);
            }
        }
    }
    public int dipToPx(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    private void showVersionDialog() {
        if (getVersionBuilder() != null && getVersionBuilder().getCustomVersionDialogListener() != null) {
            showCustomDialog();
        } else {
            showDefaultDialog();
        }
        if (versionDialog != null) {
            versionDialog.setOnCancelListener(this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (versionDialog != null && versionDialog.isShowing())
            versionDialog.dismiss();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (versionDialog != null && !versionDialog.isShowing())
            versionDialog.show();
    }

    private void dealVersionDialogCommit() {
        DownloadBuilder versionBuilder = getVersionBuilder();
        if (versionBuilder != null) {
            //增加commit 回调
            if(versionBuilder.getReadyDownloadCommitClickListener()!=null){
                versionBuilder.getReadyDownloadCommitClickListener().onCommitClick();
            }
            //如果是静默下载直接安装
            if (versionBuilder.isSilentDownload()) {
                String downloadPath = versionBuilder.getDownloadAPKPath() + getString(R.string.versionchecklib_download_apkname, versionBuilder.getApkName() != null ? versionBuilder.getApkName() : getPackageName());
                AppUtils.installApk(this, new File(downloadPath), versionBuilder.getCustomInstallListener());
                checkForceUpdate();
                //否定开始下载
            } else {
                AllenEventBusUtil.sendEventBus(AllenEventType.START_DOWNLOAD_APK);
            }
            finish();
        }
    }


    @Override
    public void onCancel(DialogInterface dialogInterface) {
        cancelHandler();
        checkForceUpdate();
        AllenVersionChecker.getInstance().cancelAllMission();
        finish();
    }

}
