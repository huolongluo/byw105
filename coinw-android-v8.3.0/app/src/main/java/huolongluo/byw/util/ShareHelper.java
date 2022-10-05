package huolongluo.byw.util;
import android.app.Activity;
import android.app.Dialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import huolongluo.byw.R;
public class ShareHelper {
    public static Dialog getShowPopDialog(Activity atv, View.OnClickListener listener) {
        Dialog dialog = new Dialog(atv, R.style.DialogTheme);
        View contentView = LayoutInflater.from(atv).inflate(R.layout.share_pop, null, false);
        LinearLayout wechat_bn = contentView.findViewById(R.id.wechat_bn);
        LinearLayout friend_bn = contentView.findViewById(R.id.friend_bn);
        LinearLayout qq_bn = contentView.findViewById(R.id.qq_bn);
        LinearLayout message_bn = contentView.findViewById(R.id.message_bn);
        LinearLayout savePng_bn = contentView.findViewById(R.id.savePng_bn);
        LinearLayout more_bn = contentView.findViewById(R.id.more_bn);
        TextView tvCancel = contentView.findViewById(R.id.tvCancel);
        tvCancel.setOnClickListener(listener);
        wechat_bn.setOnClickListener(listener);
        friend_bn.setOnClickListener(listener);
        qq_bn.setOnClickListener(listener);
        message_bn.setOnClickListener(listener);
        savePng_bn.setOnClickListener(listener);
        more_bn.setOnClickListener(listener);
        dialog.setContentView(contentView);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.show();
        return dialog;
    }
}
