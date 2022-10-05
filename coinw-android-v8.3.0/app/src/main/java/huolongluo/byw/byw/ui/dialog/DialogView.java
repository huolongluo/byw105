package huolongluo.byw.byw.ui.dialog;


import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import huolongluo.byw.R;
import huolongluo.byw.byw.bean.UserInfoBean;

/**
 * Created by LS on 2018/8/6.
 */

public class DialogView {
    public Dialog dialog;
    Context context;
    DialogPositiveListener positiveListener;
    DialogNegativeListener negativeListener;
    DialogClickListener clickListener;
    private UserInfoBean userInfo;

    private DialogView(Context context) {
        super();
        this.context = context;
        this.userInfo = userInfo;
    }

    static DialogView instance;


    public static DialogView getDefault(Context context) {
        instance = new DialogView(context);
        return instance;
    }


    public void setPositiveListener(DialogPositiveListener positiveListener) {
        this.positiveListener = positiveListener;
    }

    public void setNegativeListener(DialogNegativeListener negativeListener) {
        this.negativeListener = negativeListener;
    }

    public void setClickListener(DialogClickListener clickListener) {
        this.clickListener = clickListener;
    }

    /**
     * context promptMsg 提示信息
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public Dialog initDialog(String promptMsg) {
        return initDialog(promptMsg, "", context.getString(R.string.cc31), false);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public Dialog initDialog(String promptMsg, String btAction, boolean force) {
        return initDialog(promptMsg, "", btAction, force);
    }

    /**
     * context promptMsg 提示信息
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public Dialog initDialog(String promptMsg, boolean force) {
        return initDialog(promptMsg, "", context.getString(R.string.cc30), force);
    }

    /**
     * context promptMsg 提示信息 cancleBtnMsg 取消按钮信息 sureBtnMsg 确认按钮信息
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public Dialog initDialog(String virsonName, String instructions, String sureBtnMsg, boolean force) {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
            dialog = null;
        }
        View view = LayoutInflater.from(context).inflate(
                R.layout.layout_warndialog_view, null);
        view.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        dialog = ResultDialog.creatAlertDialog(context, view);
        TextView tv_cancle = view.findViewById(R.id.tv_cancle);
        TextView version_name_tv = view.findViewById(R.id.version_name_tv);
        TextView instructions_tv = view.findViewById(R.id.instructions_tv);
        TextView okAction = view.findViewById(R.id.tv_ok);
        if (!TextUtils.isEmpty(sureBtnMsg)) {
            okAction.setText(sureBtnMsg);
        }
        okAction.setOnClickListener(view1 -> {
            if (positiveListener != null) {
                positiveListener.onClick();
            }
            dialog.dismiss();
        });
        version_name_tv.setText("Android: " + virsonName);
        if (!TextUtils.isEmpty(instructions)) {
            instructions_tv.setText(instructions);
        }
        if (force) {
            dialog.setCancelable(false);
            tv_cancle.setVisibility(View.GONE);

        } else {
            dialog.setCancelable(true);
            tv_cancle.setVisibility(View.VISIBLE);
        }

        tv_cancle.setOnClickListener(v -> {
            if (negativeListener != null) {
                negativeListener.onClick();
            }
            dialog.dismiss();

        });

        //dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);//将弹出框设置为全局
        return dialog;
    }


    public interface DialogPositiveListener {
        void onClick();
    }

    public interface DialogNegativeListener {
        void onClick();
    }

    public interface DialogClickListener {
        void onClick();
    }
}
