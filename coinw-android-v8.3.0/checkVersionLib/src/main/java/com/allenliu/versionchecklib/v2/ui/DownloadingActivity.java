package com.allenliu.versionchecklib.v2.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.allenliu.versionchecklib.R;
import com.allenliu.versionchecklib.core.http.AllenHttp;
import com.allenliu.versionchecklib.utils.ALog;
import com.allenliu.versionchecklib.utils.AllenEventBusUtil;
import com.allenliu.versionchecklib.v2.eventbus.AllenEventType;
import com.allenliu.versionchecklib.v2.eventbus.CommonEvent;

import org.greenrobot.eventbus.EventBus;

public class DownloadingActivity extends AllenBaseActivity implements DialogInterface.OnCancelListener {
    public static final String PROGRESS = "progress";
    private android.app.AlertDialog downloadingDialog;
    private int currentProgress = 0;
    protected boolean isDestroy = false;
    private boolean isBackground=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ALog.e("loading activity create");

        showLoadingDialog();
    }

    public void onCancel(boolean isDownloadCompleted) {
        if (!isDownloadCompleted) {
            AllenHttp.getHttpClient().dispatcher().cancelAll();
            cancelHandler();
            checkForceUpdate();
        }
        finish();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        onCancel(false);
    }


    @Override
    public void receiveEvent(CommonEvent commonEvent) {
        super.receiveEvent(commonEvent);
        switch (commonEvent.getEventType()) {
            case AllenEventType.UPDATE_DOWNLOADING_PROGRESS:
                int progress = (int) commonEvent.getData();
                currentProgress = progress;
                updateProgress();
                break;
            case AllenEventType.DOWNLOAD_COMPLETE:
                onCancel(true);
                break;
            case AllenEventType.CLOSE_DOWNLOADING_ACTIVITY:
                destroy();
                EventBus.getDefault().removeStickyEvent(commonEvent);
                break;
        }
    }

    @Override
    public void showDefaultDialog() {
    }

    @Override
    public void showCustomDialog() {

        if(getVersionBuilder()!=null) {
            downloadingDialog = new AlertDialog.Builder(this).create();
            downloadingDialog.show();

            View view = LayoutInflater.from(this).inflate(
                    R.layout.layout_version_progress, null);
            view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));

            downloadingDialog.setCancelable(false);
            View cancelView = view.findViewById(R.id.versionchecklib_loading_dialog_cancel);
            if(getVersionBuilder().getForceUpdateListener() != null){
                cancelView.setVisibility(View.GONE);
            }else{

                if (cancelView != null) {
                    cancelView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            isBackground=true;
                            onCancel(true);
                        }
                    });
                }
            }

            downloadingDialog.setView(view);
            downloadingDialog.setContentView(view);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(!isBackground){
            AllenEventBusUtil.sendEventBusStick(AllenEventType.ON_PAUSE);
        }

        destroyWithOutDismiss();
        isDestroy = true;
    }


    @Override
    protected void onResume() {
        super.onResume();
        AllenEventBusUtil.sendEventBusStick(AllenEventType.ON_RESUME);
        isDestroy = false;
        if (downloadingDialog != null && !downloadingDialog.isShowing())
            downloadingDialog.show();
    }

    private void destroyWithOutDismiss() {
        if (downloadingDialog != null && downloadingDialog.isShowing()) {
            downloadingDialog.dismiss();
//            onCancel(false);
        }
    }

    private void destroy() {
        ALog.e("loading activity destroy");
        if (downloadingDialog != null && downloadingDialog.isShowing()) {
            downloadingDialog.dismiss();
//            onCancel(false);
        }
        finish();
    }

    private void updateProgress() {
        if (!isDestroy) {
            if (getVersionBuilder() != null && getVersionBuilder().getCustomDownloadingDialogListener() != null) {
                getVersionBuilder().getCustomDownloadingDialogListener().updateUI(downloadingDialog, currentProgress, getVersionBuilder().getVersionBundle());
            } else {
                ProgressBar pb = downloadingDialog.findViewById(R.id.pb);
                pb.setProgress(currentProgress);
                TextView tvProgress = downloadingDialog.findViewById(R.id.tv_progress);
                tvProgress.setText(String.format(getString(R.string.versionchecklib_progress), currentProgress));
                if (!downloadingDialog.isShowing())
                    downloadingDialog.show();
            }
        }
    }

    private void showLoadingDialog() {
        ALog.e("show loading");
        if (!isDestroy) {
            if (getVersionBuilder() != null && getVersionBuilder().getCustomDownloadingDialogListener() != null) {
                showCustomDialog();
            } else {
                showDefaultDialog();
            }
            if (downloadingDialog != null){
                downloadingDialog.setOnCancelListener(this);
            }
        }
    }


}
