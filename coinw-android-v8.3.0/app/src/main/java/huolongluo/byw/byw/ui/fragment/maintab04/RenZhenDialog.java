package huolongluo.byw.byw.ui.fragment.maintab04;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import huolongluo.byw.R;
import huolongluo.byw.byw.ui.dialog.ResultDialog;
import huolongluo.bywx.helper.AppHelper;

/**
 * Created by LS on 2018/7/26.
 */

public class RenZhenDialog {
    public Dialog dialog;
    Context context;
    DialogPositiveListener positiveListener;
    DialogNegativeListener negativeListener;
    DialogClickListener clickListener;


    public RenZhenDialog(Context context) {
        super();
        this.context = context;

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

    public Dialog initDialog(String promptMsg) {
        return initDialog(promptMsg, "", context.getString(R.string.dd93));
    }

    /**
     * context promptMsg 提示信息 cancleBtnMsg 取消按钮信息 sureBtnMsg 确认按钮信息
     */

    public Dialog initDialog(String promptMsg, String cancleBtnMsg, String sureBtnMsg) {
        View view = LayoutInflater.from(context).inflate(
                R.layout.layout_real_name_c2, null);
        view.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        dialog = ResultDialog.creatAlertDialog(context, view);

        TextView tv_ok = view.findViewById(R.id.tv_ok);
        TextView tv_dialog_cancel = view.findViewById(R.id.tv_dialog_cancel);


        tv_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (positiveListener != null) {
                    positiveListener.onClick();
                }
                AppHelper.dismissDialog(dialog);
            }
        });
        tv_dialog_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (negativeListener != null) {
                    negativeListener.onClick();
                }
                AppHelper.dismissDialog(dialog);
            }
        });
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
