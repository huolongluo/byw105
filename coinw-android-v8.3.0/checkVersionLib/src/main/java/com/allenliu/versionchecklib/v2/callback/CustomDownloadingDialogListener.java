package com.allenliu.versionchecklib.v2.callback;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import com.allenliu.versionchecklib.v2.builder.UIData;

/**
 * Created by allenliu on 2018/1/18.
 */

public interface CustomDownloadingDialogListener {
    AlertDialog getCustomDownloadingDialog(Context context, int progress, UIData versionBundle);

    void updateUI(AlertDialog dialog, int progress, UIData versionBundle);
}
