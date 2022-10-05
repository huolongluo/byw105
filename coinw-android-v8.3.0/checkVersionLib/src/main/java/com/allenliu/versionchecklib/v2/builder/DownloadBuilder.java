package com.allenliu.versionchecklib.v2.builder;

import android.content.Context;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Environment;

import androidx.annotation.NonNull;

import com.allenliu.versionchecklib.callback.APKDownloadListener;
import com.allenliu.versionchecklib.callback.CommitClickListener;
import com.allenliu.versionchecklib.callback.OnCancelListener;
import com.allenliu.versionchecklib.utils.FileHelper;
import com.allenliu.versionchecklib.v2.callback.CustomDownloadFailedListener;
import com.allenliu.versionchecklib.v2.callback.CustomDownloadingDialogListener;
import com.allenliu.versionchecklib.v2.callback.CustomInstallListener;
import com.allenliu.versionchecklib.v2.callback.CustomVersionDialogListener;
import com.allenliu.versionchecklib.v2.callback.ForceUpdateListener;
import com.allenliu.versionchecklib.v2.net.RequestVersionManager;
import com.allenliu.versionchecklib.v2.ui.VersionService;

/**
 * Created by allenliu on 2018/1/12.
 */

public class DownloadBuilder {
    private RequestVersionBuilder requestVersionBuilder;
    private boolean isSilentDownload;
    private String downloadAPKPath;
    private boolean isForceRedownload;
    private String downloadUrl;
    private boolean isShowDownloadingDialog;
    private boolean isShowNotification;
    private boolean runOnForegroundService;
    private boolean isShowDownloadFailDialog;
    private boolean isDirectDownload;
    private NotificationBuilder notificationBuilder;
    private APKDownloadListener apkDownloadListener;

    private CustomDownloadFailedListener customDownloadFailedListener;
    private CustomDownloadingDialogListener customDownloadingDialogListener;
    private CustomVersionDialogListener customVersionDialogListener;
    private CustomInstallListener customInstallListener;
    private OnCancelListener onCancelListener;
    private CommitClickListener readyDownloadCommitClickListener;
    private CommitClickListener downloadFailedCommitClickListener;
    private OnCancelListener downloadingCancelListener;
    private OnCancelListener downloadFailedCancelListener;
    private OnCancelListener readyDownloadCancelListener;

    private ForceUpdateListener forceUpdateListener;
    private UIData versionBundle;
    private Integer newestVersionCode;
    private String apkName;


    public DownloadBuilder() {
        throw new RuntimeException("can not be instantiated from outside");
    }

    private void initialize() {
        isSilentDownload = false;
//        downloadAPKPath = FileHelper.getDownloadApkCachePath();
        isForceRedownload = false;
        isShowDownloadingDialog = true;
        isShowNotification = true;
        isDirectDownload = false;
        isShowDownloadFailDialog = true;
        notificationBuilder = NotificationBuilder.create();
        runOnForegroundService = true;
    }

    public DownloadBuilder(RequestVersionBuilder requestVersionBuilder, UIData versionBundle) {
        this.requestVersionBuilder = requestVersionBuilder;
        this.versionBundle = versionBundle;
        initialize();
    }


    public ForceUpdateListener getForceUpdateListener() {
        return forceUpdateListener;
    }

    public DownloadBuilder setForceUpdateListener(ForceUpdateListener forceUpdateListener) {
        this.forceUpdateListener = forceUpdateListener;
        return this;
    }

    public DownloadBuilder setApkName(String apkName) {
        this.apkName = apkName;
        return this;
    }

    public DownloadBuilder setVersionBundle(@NonNull UIData versionBundle) {
        this.versionBundle = versionBundle;
        return this;
    }

    public UIData getVersionBundle() {
        return versionBundle;
    }

    public DownloadBuilder setOnCancelListener(OnCancelListener cancelListener) {
        this.onCancelListener = cancelListener;
        return this;
    }

    public DownloadBuilder setCustomDownloadFailedListener(CustomDownloadFailedListener customDownloadFailedListener) {
        this.customDownloadFailedListener = customDownloadFailedListener;
        return this;
    }

    public DownloadBuilder setCustomDownloadingDialogListener(CustomDownloadingDialogListener customDownloadingDialogListener) {
        this.customDownloadingDialogListener = customDownloadingDialogListener;
        return this;
    }

    public DownloadBuilder setCustomVersionDialogListener(CustomVersionDialogListener customVersionDialogListener) {
        this.customVersionDialogListener = customVersionDialogListener;
        return this;
    }

    public DownloadBuilder setCustomDownloadInstallListener(CustomInstallListener customDownloadInstallListener) {
        this.customInstallListener = customDownloadInstallListener;
        return this;
    }

    public boolean isRunOnForegroundService() {
        return runOnForegroundService;
    }

    public DownloadBuilder setRunOnForegroundService(boolean runOnForegroundService) {
        this.runOnForegroundService = runOnForegroundService;
        return this;
    }

    public DownloadBuilder setSilentDownload(boolean silentDownload) {
        isSilentDownload = silentDownload;
        return this;
    }

    public Integer getNewestVersionCode() {
        return newestVersionCode;
    }

    public DownloadBuilder setNewestVersionCode(Integer newestVersionCode) {
        this.newestVersionCode = newestVersionCode;
        return this;
    }

    public DownloadBuilder setDownloadAPKPath(String downloadAPKPath) {
        this.downloadAPKPath = downloadAPKPath;
        return this;
    }

    public DownloadBuilder setForceRedownload(boolean forceRedownload) {
        isForceRedownload = forceRedownload;
        return this;
    }

    public DownloadBuilder setDownloadUrl(@NonNull String downloadUrl) {
        this.downloadUrl = downloadUrl;
        return this;
    }

    public DownloadBuilder setShowDownloadingDialog(boolean showDownloadingDialog) {
        isShowDownloadingDialog = showDownloadingDialog;
        return this;
    }

    public DownloadBuilder setShowNotification(boolean showNotification) {
        isShowNotification = showNotification;
        return this;
    }

    public DownloadBuilder setShowDownloadFailDialog(boolean showDownloadFailDialog) {
        isShowDownloadFailDialog = showDownloadFailDialog;
        return this;
    }

    public DownloadBuilder setApkDownloadListener(APKDownloadListener apkDownloadListener) {
        this.apkDownloadListener = apkDownloadListener;
        return this;
    }

    public CommitClickListener getReadyDownloadCommitClickListener() {
        return readyDownloadCommitClickListener;
    }

    public DownloadBuilder setReadyDownloadCommitClickListener(CommitClickListener readyDownloadCommitClickListener) {
        this.readyDownloadCommitClickListener = readyDownloadCommitClickListener;
        return this;
    }

    public CommitClickListener getDownloadFailedCommitClickListener() {
        return downloadFailedCommitClickListener;
    }

    public DownloadBuilder setDownloadFailedCommitClickListener(CommitClickListener downloadFailedCommitClickListener) {
        this.downloadFailedCommitClickListener = downloadFailedCommitClickListener;
        return this;
    }

    public OnCancelListener getDownloadingCancelListener() {
        return downloadingCancelListener;
    }

    public DownloadBuilder setDownloadingCancelListener(OnCancelListener downloadingCancelListener) {
        this.downloadingCancelListener = downloadingCancelListener;
        return this;
    }

    public OnCancelListener getDownloadFailedCancelListener() {
        return downloadFailedCancelListener;
    }

    public DownloadBuilder setDownloadFailedCancelListener(OnCancelListener downloadFailedCancelListener) {
        this.downloadFailedCancelListener = downloadFailedCancelListener;
        return this;
    }

    public OnCancelListener getReadyDownloadCancelListener() {
        return readyDownloadCancelListener;
    }

    public DownloadBuilder setReadyDownloadCancelListener(OnCancelListener readyDownloadCancelListener) {
        this.readyDownloadCancelListener = readyDownloadCancelListener;
        return this;
    }

    public boolean isSilentDownload() {
        return isSilentDownload;
    }

    public String getDownloadAPKPath() {
        return downloadAPKPath;
    }

    public boolean isForceRedownload() {
        return isForceRedownload;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public boolean isShowDownloadingDialog() {
        return isShowDownloadingDialog;
    }

    public boolean isShowNotification() {
        return isShowNotification;
    }

    public boolean isShowDownloadFailDialog() {
        return isShowDownloadFailDialog;
    }

    public APKDownloadListener getApkDownloadListener() {
        return apkDownloadListener;
    }


    public CustomDownloadFailedListener getCustomDownloadFailedListener() {
        return customDownloadFailedListener;
    }

    public OnCancelListener getOnCancelListener() {
        return onCancelListener;
    }

    public CustomDownloadingDialogListener getCustomDownloadingDialogListener() {
        return customDownloadingDialogListener;
    }

    public CustomInstallListener getCustomInstallListener() {
        return customInstallListener;
    }

    public CustomVersionDialogListener getCustomVersionDialogListener() {
        return customVersionDialogListener;
    }

    public RequestVersionBuilder getRequestVersionBuilder() {
        return requestVersionBuilder;
    }

    public NotificationBuilder getNotificationBuilder() {
        return notificationBuilder;
    }

    public DownloadBuilder setNotificationBuilder(NotificationBuilder notificationBuilder) {
        this.notificationBuilder = notificationBuilder;
        return this;
    }

    public String getApkName() {
        return apkName;
    }

    public boolean isDirectDownload() {
        return isDirectDownload;
    }

    public DownloadBuilder setDirectDownload(boolean directDownload) {
        isDirectDownload = directDownload;
        return this;
    }

    public void executeMission(Context context) {
        if (apkName == null) {
            apkName = context.getApplicationContext().getPackageName();
        }

        if (notificationBuilder.getIcon() == 0) {
            final PackageManager pm = context.getPackageManager();
            final ApplicationInfo applicationInfo;
            try {
                applicationInfo = pm.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
                final int appIconResId = applicationInfo.icon;
                notificationBuilder.setIcon(appIconResId);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        //fix path permission
        setupDownloadPath(context);
//        downloadAPKPath=context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getPath() + "/";
        if (checkWhetherNeedRequestVersion()) {
            RequestVersionManager.getInstance().requestVersion(this, context.getApplicationContext());
        } else {
            download(context);
        }

    }

    private void setupDownloadPath(Context context) {
        if (downloadAPKPath == null) {
            downloadAPKPath = FileHelper.getDownloadApkCachePath(context);
        }
        downloadAPKPath = FileHelper.dealDownloadPath(downloadAPKPath);
    }

    public void download(Context context) {
        VersionService.enqueueWork(context.getApplicationContext(), this);
    }

    private boolean checkWhetherNeedRequestVersion() {
        if (getRequestVersionBuilder() != null)
            return true;
        else
            return false;
    }

    public void destory() {
        setCustomDownloadFailedListener(null);
        setCustomDownloadingDialogListener(null);
        setCustomVersionDialogListener(null);
        setForceUpdateListener(null);
        setApkDownloadListener(null);
        setOnCancelListener(null);
        setReadyDownloadCommitClickListener(null);
        setDownloadFailedCommitClickListener(null);
        setReadyDownloadCancelListener(null);
        setDownloadingCancelListener(null);
        setDownloadFailedCancelListener(null);

        if (getRequestVersionBuilder() != null)
            getRequestVersionBuilder().destory();
        requestVersionBuilder = null;
    }
}
