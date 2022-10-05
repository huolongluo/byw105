package huolongluo.byw.util;

import android.app.AlertDialog;
import android.content.Context;
import android.view.KeyEvent;
import android.webkit.SslErrorHandler;

import huolongluo.byw.R;

public class WebViewSSLErrorUtil {

    public static void showDialogSSLError(SslErrorHandler mHandler, Context activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(R.string.ssl_certificate_verification);
        builder.setPositiveButton(R.string.ssl_continue, (dialog, which) -> mHandler.proceed());
        builder.setNegativeButton(R.string.g8, (dialog, which) -> mHandler.cancel());
        builder.setOnKeyListener((dialog, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                mHandler.cancel();
                dialog.dismiss();
                return true;
            }
            return false;
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
