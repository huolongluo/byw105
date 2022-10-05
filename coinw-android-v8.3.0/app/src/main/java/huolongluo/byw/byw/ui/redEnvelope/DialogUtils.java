package huolongluo.byw.byw.ui.redEnvelope;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import huolongluo.byw.R;
import huolongluo.byw.byw.net.UrlConstants;
import huolongluo.byw.reform.home.activity.NewsWebviewActivity;
import huolongluo.byw.user.UserInfoManager;
import huolongluo.bywx.helper.AppHelper;

public class DialogUtils {

    private static DialogUtils self;
    private AlertDialog dialog;
    private huolongluo.byw.util.tip.DialogUtils.onBnClickListener clickListener;

    private DialogUtils(DialogUtils self) {
        DialogUtils.self = self;
    }

    private DialogUtils() {
    }

    public static DialogUtils getInstance() {
        if (self == null) {
            self = new DialogUtils();
        }
        return self;
    }

    public void dismiss() {
        AppHelper.dismissDialog(dialog);
        dialog = null;
    }

    public static abstract class onBnClickListener {

        public abstract void onLiftClick(AlertDialog dialog, View view);

        public abstract void onRightClick(AlertDialog dialog, View view);

        public void onEdit(AlertDialog dialog, View view, String text) {
        }
    }

    public void setOnclickListener(huolongluo.byw.util.tip.DialogUtils.onBnClickListener listener) {
        this.clickListener = listener;
    }

}
