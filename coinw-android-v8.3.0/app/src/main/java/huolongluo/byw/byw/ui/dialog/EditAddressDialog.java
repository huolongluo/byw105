package huolongluo.byw.byw.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import huolongluo.byw.R;

/**
 * Created by LS on 2018/7/12.
 */

public class EditAddressDialog {
    public Dialog dialog;
    Context context;
    DialogPositiveListener positiveListener;
    DialogNegativeListener negativeListener;
    DialogClickListener clickListener;

    public EditText etInfo;

    public EditAddressDialog(Context context) {
        super();
        this.context = context;
    }

    public void setPositiveListener(DialogPositiveListener positiveListener) {
        this.positiveListener = positiveListener;
    }

    public void setNegativeListener(DialogNegativeListener negativeListener) {
        this.negativeListener = negativeListener;
    }

    public void setClickListener(DialogClickListener clickListener){
        this.clickListener = clickListener;
    }

    /**
     * context promptMsg 提示信息
     * */
    public Dialog initDialog(String promptMsg) {
        return initDialog(promptMsg, "", context.getString(R.string.cc31));
    }

    /**
     * context promptMsg 提示信息 cancleBtnMsg 取消按钮信息 sureBtnMsg 确认按钮信息
     * */
    public Dialog initDialog(String promptMsg, String cancleBtnMsg,String sureBtnMsg) {
        View view = LayoutInflater.from(context).inflate(
                R.layout.layout_send_dialog, null);
        view.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        dialog = ResultDialog.creatAlertDialog(context, view);



        TextView tvPromptMsg = view.findViewById(R.id.tvPromptMsg);
        etInfo = view.findViewById(R.id.et_info);
        etInfo.setFocusable(true);
        etInfo.setFocusableInTouchMode(true);
        etInfo.requestFocus();
        /** 3.自动弹出软键盘 **/
     //   dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        RelativeLayout rlMain = view.findViewById(R.id.rl_main);
        rlMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(clickListener != null){
                    clickListener.onClick();
                }
                InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(etInfo.getWindowToken(),0);
                dialog.dismiss();
            }
        });

        TextView btnCancle = view.findViewById(R.id.btnCancle);
        TextView btnSure =  view.findViewById(R.id.btnSure);


        btnCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (negativeListener != null) {
                    negativeListener.onClick();
                }
                InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(etInfo.getWindowToken(),0);
                dialog.dismiss();
            }
        });
        btnSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (positiveListener != null) {
                    positiveListener.onClick();
                }
                InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(etInfo.getWindowToken(),0);
//                dialog.dismiss();
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
