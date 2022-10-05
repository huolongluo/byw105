package huolongluo.byw.util.tip;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.http.SslError;
import android.os.Build;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.ConsoleMessage;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.coinw.utils.Utilities;
import com.android.legend.ui.login.LoginActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.legend.modular_contract_sdk.api.ModularContractSDK;
import com.mob.tools.utils.ResHelper;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import huolongluo.byw.R;
import huolongluo.byw.byw.base.BaseApp;
import huolongluo.byw.byw.net.Domain;
import huolongluo.byw.byw.net.DomainHelper;
import huolongluo.byw.byw.net.UrlConstants;
import huolongluo.byw.byw.share.Event;
import huolongluo.byw.byw.ui.activity.main.MainActivity;
import huolongluo.byw.io.AppConstants;
import huolongluo.byw.log.Logger;
import huolongluo.byw.reform.home.activity.JSCallAlManMachineJavaInterface;
import huolongluo.byw.reform.home.activity.NewsWebviewActivity;
import huolongluo.byw.reform.home.activity.WelfareWebviewActivity;
import huolongluo.byw.user.UserInfoManager;
import huolongluo.byw.util.Constant;
import huolongluo.byw.util.CreateQRImage;
import huolongluo.byw.util.SPUtils;
import huolongluo.byw.util.Util;
import huolongluo.byw.util.config.ConfigurationUtils;
import huolongluo.byw.util.noru.NorUtils;
import huolongluo.byw.util.webview.WebviewUtils;
import huolongluo.byw.view.CustomLoadingDialog;
import huolongluo.bywx.helper.AppHelper;
import huolongluo.bywx.utils.AppUtils;
/**
 * Created by Administrator on 2018/9/26 0026.
 */
public class DialogUtils {
    private static final String TAG = "DialogUtils";
    public static final float DIALOG_WIDTH_SCALE = 0.86f;//对话框宽度占屏幕宽度的比例
    public static final float scaleAliManMachine = 0.36f;
    private static DialogUtils self;
    private AlertDialog dialog;
    private boolean isLoadError = false;//阿里人机验证error回调后提示网络异常不弹对话框
    private onBnClickListener clickListener;
    private AlertDialog tokenExpiredDialog;

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

    public void setOnclickListener(onBnClickListener listener) {
        this.clickListener = listener;
    }

    public void showETFTipDialog(Context context, String msg, onBnClickListener listener) {
        dialog = new AlertDialog.Builder(context).create();
        View view = View.inflate(context, R.layout.dialog_etf_tip, null);
        TextView iagreeTxt = view.findViewById(R.id.tv_iagree);
        iagreeTxt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onLiftClick(dialog, v);
                }
                dismiss();
            }
        });
        dialog.setCancelable(true);
        Window windows = dialog.getWindow();
        if (windows != null) {
            windows.setBackgroundDrawableResource(android.R.color.transparent);
        }
        dialog.setView(view);
        dialog.show();
    }

    public void showETFDoubtDialog(Context context, String msg) {
        dialog = new AlertDialog.Builder(context).create();
        View view = View.inflate(context, R.layout.dialog_etf_doubt, null);
        ImageView image = view.findViewById(R.id.image);
        image.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        TextView tv_showdialog_text = view.findViewById(R.id.tv_showdialog_text);
        tv_showdialog_text.setText(msg);
        dialog.setCancelable(true);
        Window windows = dialog.getWindow();
        if (windows != null) {
            windows.setBackgroundDrawableResource(android.R.color.transparent);
        }
        dialog.setView(view);
        dialog.show();
    }

    public void showRiskTipButtonDialog(Context context, onBnClickListener listener) {
        showRiskTipButtonDialog(context, null, listener);
    }

    //singleBtnText为空则默认两个按钮，否则一个按钮且文案使用该文案
    public void showRiskTipButtonDialog(Context context, String singleBtnText, onBnClickListener listener) {
        if (dialog != null && !dialog.isShowing()) {
            dialog.dismiss();
            dialog = null;
        }
        dialog = new AlertDialog.Builder(context).create();
        View view = View.inflate(context, R.layout.dialog_risk, null);
//        ImageView image = view.findViewById(R.id.image);
//        image.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dismiss();
//            }
//        });
        TextView tv_msg = view.findViewById(R.id.tv_msg);
        TextView leftTxt = view.findViewById(R.id.tv_left);
        TextView rightTxt = view.findViewById(R.id.tv_right);
        if (!TextUtils.isEmpty(singleBtnText)) {
            leftTxt.setVisibility(View.GONE);
            tv_msg.setText(singleBtnText);
            rightTxt.setText(context.getResources().getString(R.string.str_risk_verify_know));
        }
        leftTxt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onLiftClick(dialog, v);
                }
                dismiss();
            }
        });
        rightTxt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onRightClick(dialog, v);
                }
                dismiss();
            }
        });
        dialog.setCancelable(false);
        Window windows = dialog.getWindow();
        if (windows != null) {
            windows.setBackgroundDrawableResource(android.R.color.transparent);
        }
        dialog.setView(view);
        dialog.show();
    }

    public void showTxLimitDialog(Context context) {
        if (dialog != null && !dialog.isShowing()) {
            dialog.dismiss();
            dialog = null;
        }
        dialog = new AlertDialog.Builder(context).create();
        View view = View.inflate(context, R.layout.dialog_tx_limit, null);
        TextView tv_know = view.findViewById(R.id.tv_know);
        tv_know.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        dialog.setCancelable(false);
        Window windows = dialog.getWindow();
        if (windows != null) {
            windows.setBackgroundDrawableResource(android.R.color.transparent);
        }
        dialog.setView(view);
        dialog.show();
    }

    public void showRiskVerifyButtonDialog(Context context, String msg, int resId, String clickMsg, onBnClickListener listener) {
        if (dialog != null && !dialog.isShowing()) {
            dialog.dismiss();
            dialog = null;
        }
        dialog = new AlertDialog.Builder(context).create();
        View view = View.inflate(context, R.layout.dialog_risk_status, null);
        ImageView image = view.findViewById(R.id.iv_status_close);
        image.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        ImageView statusIV = view.findViewById(R.id.iv_risk_status);
        TextView statusMsgTxt = view.findViewById(R.id.tv_risk_status_msg);
        TextView clickMsgTxt = view.findViewById(R.id.tv_click);
        statusIV.setImageResource(resId);
        statusMsgTxt.setText(msg);
        clickMsgTxt.setText(clickMsg);
        clickMsgTxt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onRightClick(dialog, v);
                }
            }
        });
        dialog.setCancelable(false);
        Window windows = dialog.getWindow();
        if (windows != null) {
            windows.setBackgroundDrawableResource(android.R.color.transparent);
        }
        dialog.setView(view);
        dialog.show();
    }

    public void showTwoButtonDialog(Context context, String title, String content, String leftBtnText, String rightBtnText, onBnClickListener bnListener) {
        if (dialog != null && !dialog.isShowing()) {
            dialog.dismiss();
            dialog = null;
        }
        dialog = new AlertDialog.Builder(context).create();
        View view = View.inflate(context, R.layout.dialog_two_btn, null);
        ImageView image = view.findViewById(R.id.image);
        image.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog != null) {
                    dialog.dismiss();
                    dialog = null;
                }
            }
        });
        TextView tvTitle = view.findViewById(R.id.tv_title);
        TextView tvContent = view.findViewById(R.id.tv_content);
        TextView tvLeft = view.findViewById(R.id.tv_left);
        TextView tvRight = view.findViewById(R.id.tv_right);
        tvTitle.setText(title);
        tvContent.setText(content);
        if(TextUtils.isEmpty(leftBtnText)){
            tvLeft.setVisibility(View.GONE);
        }
        tvLeft.setText(leftBtnText);
        tvRight.setText(rightBtnText);
        tvLeft.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bnListener != null) {
                    bnListener.onLiftClick(dialog, v);
                }
            }
        });
        tvRight.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bnListener != null) {
                    bnListener.onRightClick(dialog, v);
                }
            }
        });
        dialog.setCancelable(false);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (clickListener != null) {
                    clickListener = null;
                }
                dialog = null;
            }
        });
        Window windows = dialog.getWindow();
        if (windows != null) {
            windows.setBackgroundDrawableResource(android.R.color.transparent);
        }
        dialog.setView(view);
        dialog.show();
    }

    public void showTwoButtonDialog(Context context, String s, String lift, String right) {
        dialog = new AlertDialog.Builder(context).create();
        View view = View.inflate(context, R.layout.dialog_show, null);
        ImageView image = view.findViewById(R.id.image);
        image.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        TextView tv_dialog_cancel = view.findViewById(R.id.tv_dialog_cancel);
        TextView tv_dialog_ok = view.findViewById(R.id.tv_dialog_ok);
        TextView tv_showdialog_text = view.findViewById(R.id.tv_showdialog_text);
        tv_showdialog_text.setText(s);
        tv_dialog_cancel.setText(lift);
        tv_dialog_ok.setText(right);
        tv_dialog_cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // farLogin(s,0);
                if (clickListener != null) {
                    clickListener.onLiftClick(dialog, v);
                }
            }
        });
        tv_dialog_ok.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null) {
                    clickListener.onRightClick(dialog, v);
                }
            }
        });
        dialog.setCancelable(false);
//        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
//            @Override
//            public void onDismiss(DialogInterface dialog) {
//                if (clickListener != null) {
//                    clickListener = null;
//                }
//                dialog = null;
//            }
//        });
        Window windows = dialog.getWindow();
        if (windows != null) {
            windows.setBackgroundDrawableResource(android.R.color.transparent);
        }
//        windows.setContentView(view);
        dialog.setView(view);
        dialog.show();
    }

    public void showTwoButtonDialog(Context context, String s, String lift, String right, OnClickListener listener) {
        dialog = new AlertDialog.Builder(context).create();
        View view = View.inflate(context, R.layout.dialog_show, null);
        ImageView image = view.findViewById(R.id.image);
        image.setOnClickListener(v -> dismiss());
        TextView tv_dialog_cancel = view.findViewById(R.id.tv_dialog_cancel);
        TextView tv_dialog_ok = view.findViewById(R.id.tv_dialog_ok);
        TextView tv_showdialog_text = view.findViewById(R.id.tv_showdialog_text);
        tv_showdialog_text.setText(s);
        tv_dialog_cancel.setText(lift);
        tv_dialog_ok.setText(right);
        tv_dialog_cancel.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onLiftClick(dialog, v);
            }
            dismiss();
        });
        tv_dialog_ok.setOnClickListener(v -> {
            if (listener != null) {
                listener.onClick(tv_dialog_ok);
            }
            dismiss();
//            if (clickListener != null) {
//                clickListener.onRightClick(dialog, v);
//            }
        });
        dialog.setCancelable(false);
        Window windows = dialog.getWindow();
        if (windows != null) {
            windows.setBackgroundDrawableResource(android.R.color.transparent);
        }
        dialog.setView(view);
        dialog.show();
    }

    public void showTwoButtonDialog(Context context, String s, String lift, String right, DialogUtils.onBnClickListener listener) {
        dialog = new AlertDialog.Builder(context).create();
        View view = View.inflate(context, R.layout.dialog_show, null);
        ImageView image = view.findViewById(R.id.image);
        image.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog != null) {
                    dialog.dismiss();
                    dialog = null;
                }
            }
        });
        TextView tv_dialog_cancel = view.findViewById(R.id.tv_dialog_cancel);
        TextView tv_dialog_ok = view.findViewById(R.id.tv_dialog_ok);
        TextView tv_showdialog_text = view.findViewById(R.id.tv_showdialog_text);
        tv_showdialog_text.setText(s);
        tv_dialog_cancel.setText(lift);
        tv_dialog_ok.setText(right);
        tv_dialog_cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // farLogin(s,0);
                if (listener != null) {
                    listener.onLiftClick(dialog, v);
                }
            }
        });
        tv_dialog_ok.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onRightClick(dialog, v);
                }
            }
        });
        dialog.setCancelable(true);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (clickListener != null) {
                    clickListener = null;
                }
                dialog = null;
            }
        });
        Window windows = dialog.getWindow();
        if (windows != null) {
            windows.setBackgroundDrawableResource(android.R.color.transparent);
        }
//        windows.setContentView(view);
        dialog.setView(view);
        dialog.show();
    }

    //otc订单状态提示弹框
    public void showOtcOrderTipDialog(Context context, String s, String lift, String right) {
        if (dialog != null && !dialog.isShowing()) {
            dialog.dismiss();
            dialog = null;
        }
        dialog = new AlertDialog.Builder(context).create();
        View view = View.inflate(context, R.layout.dialog_show_otctip, null);
        ImageView image = view.findViewById(R.id.image);
        image.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog != null) {
                    dialog.dismiss();
                    dialog = null;
                }
            }
        });
        TextView tv_dialog_cancel = view.findViewById(R.id.tv_dialog_cancel);
        TextView tv_dialog_ok = view.findViewById(R.id.tv_dialog_ok);
        TextView tv_showdialog_text = view.findViewById(R.id.tv_showdialog_text);
        tv_showdialog_text.setText(s);
        tv_dialog_cancel.setText(lift);
        tv_dialog_ok.setText(right);
        tv_dialog_cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // farLogin(s,0);
                if (clickListener != null) {
                    clickListener.onLiftClick(dialog, v);
                }
            }
        });
        tv_dialog_ok.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null) {
                    clickListener.onRightClick(dialog, v);
                }
            }
        });
        dialog.setCancelable(true);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (clickListener != null) {
                    clickListener = null;
                }
                dialog = null;
            }
        });
        Window windows = dialog.getWindow();
        if (windows != null) {
            windows.setBackgroundDrawableResource(android.R.color.transparent);
        }
//        windows.setContentView(view);
        dialog.setView(view);
        dialog.show();
    }

    public void showOneButtonDialog(Context context, String s, String button) {
        showDialog(context, s, button, null);
    }

    public void showContentOneButtonDialog(Context context, String s, String button) {
        clickListener = null;
        dialog = new AlertDialog.Builder(context).create();
        View view = View.inflate(context, R.layout.dialog_show_one, null);
        ImageView image = view.findViewById(R.id.image);
        image.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog != null) {
                    dialog.dismiss();
                    dialog = null;
                }
            }
        });
        TextView tv_dialog_cancel = view.findViewById(R.id.tv_dialog_cancel);
        TextView tv_dialog_ok = view.findViewById(R.id.tv_dialog_ok);
        TextView tv_showdialog_text = view.findViewById(R.id.tv_showdialog_text);
        tv_showdialog_text.setGravity(Gravity.LEFT);
        tv_showdialog_text.setText(s);
        tv_dialog_ok.setText(button);
        tv_dialog_ok.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onRightClick(dialog, v);
            }
            dismiss();
        });
        dialog.setCancelable(false);
        Window windows = dialog.getWindow();
        if (windows != null) {
            windows.setBackgroundDrawableResource(android.R.color.transparent);
        }
        dialog.setView(view);
        dialog.show();
    }

    public void showOneButtonDialog(Context context, String s, String button, OnClickListener listener) {
        showDialog(context, s, button, listener);
    }

    public void showDialog(Context context, String s, String button, OnClickListener listener) {
        clickListener = null;
        dialog = new AlertDialog.Builder(context).create();
        View view = View.inflate(context, R.layout.dialog_show_one, null);
        ImageView image = view.findViewById(R.id.image);
        image.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog != null) {
                    dialog.dismiss();
                    dialog = null;
                }
            }
        });
        TextView tv_dialog_cancel = view.findViewById(R.id.tv_dialog_cancel);
        TextView tv_dialog_ok = view.findViewById(R.id.tv_dialog_ok);
        TextView tv_showdialog_text = view.findViewById(R.id.tv_showdialog_text);
        tv_showdialog_text.setText(s);
        tv_dialog_ok.setText(button);
        tv_dialog_ok.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onRightClick(dialog, v);
            }
            if (listener != null) {
                listener.onClick(tv_dialog_ok);
            }
            dismiss();
        });
        dialog.setCancelable(false);
        Window windows = dialog.getWindow();
        if (windows != null) {
            windows.setBackgroundDrawableResource(android.R.color.transparent);
        }
        dialog.setView(view);
        dialog.show();
    }

    public void showConfirmDialog(Context context, String msg, String right, final onBnClickListener listener) {
        this.showConfirmDialog(context, msg, "", right, listener);
    }

    public void showConfirmDialog(Context context, String msg, String left, String right, final onBnClickListener listener) {
        try {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
        } catch (Throwable t) {
            Logger.getInstance().error(t);
        }
        dialog = new AlertDialog.Builder(context).create();
        View view = View.inflate(context, R.layout.dialog_show_confirm, null);
        TextView cancelTxt = view.findViewById(R.id.tv_cancel);
        TextView confirmTxt = view.findViewById(R.id.tv_confirm);
        TextView descTxt = view.findViewById(R.id.tv_showdialog_text);
        descTxt.setText(msg);
        if (TextUtils.isEmpty(left)) {
            cancelTxt.setVisibility(View.GONE);
        } else {
            cancelTxt.setText(left);
        }
        confirmTxt.setText(right);
        cancelTxt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onLiftClick(dialog, v);
                }
                dismiss();
            }
        });
        confirmTxt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onRightClick(dialog, v);
                }
                dismiss();
            }
        });
        dialog.setCancelable(false);
        Window windows = dialog.getWindow();
        if (windows != null) {
            windows.setBackgroundDrawableResource(android.R.color.transparent);
        }
        dialog.setView(view);
        dialog.show();
    }

    public void showOtcTipDialog(Context context, String s, final onBnClickListener listener) {
        try {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
        } catch (Throwable t) {
            Logger.getInstance().error(t);
        }
        dialog = new AlertDialog.Builder(context).create();
        View view = View.inflate(context, R.layout.dialog_show_otc_tip, null);
        TextView reorderTxt = view.findViewById(R.id.tv_reorder);
        TextView otcTxt = view.findViewById(R.id.tv_otc);
        TextView descTxt = view.findViewById(R.id.tv_showdialog_text);
        descTxt.setText(s);
        reorderTxt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onLiftClick(dialog, v);
                }
                dismiss();
            }
        });
        otcTxt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onRightClick(dialog, v);
                }
                dismiss();
            }
        });
        dialog.setCancelable(false);
        Window windows = dialog.getWindow();
        if (windows != null) {
            windows.setBackgroundDrawableResource(android.R.color.transparent);
        }
        dialog.setView(view);
        dialog.show();
    }

    public void showOneButtonDialog1(Context context, String s, String button) {
        dialog = new AlertDialog.Builder(context).create();
        View view = View.inflate(context, R.layout.dialog_show_one1, null);
        ImageView image = view.findViewById(R.id.image);
        image.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog != null) {
                    dialog.dismiss();
                    dialog = null;
                }
            }
        });
        TextView tv_dialog_cancel = view.findViewById(R.id.tv_dialog_cancel);
        TextView tv_dialog_ok = view.findViewById(R.id.tv_dialog_ok);
        TextView tv_showdialog_text = view.findViewById(R.id.tv_showdialog_text);
        tv_showdialog_text.setText(s);
        tv_dialog_ok.setText(button);
        // tv_dialog_cancel.setText(lift);
        //  tv_dialog_ok.setText(right);
        /*tv_dialog_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // farLogin(s,0);
                if (clickListener != null) {
                    clickListener.onLiftClick(dialog, v);
                }

            }
        });*/
        tv_dialog_ok.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null) {
                    clickListener.onRightClick(dialog, v);
                }
            }
        });
        dialog.setCancelable(false);
//        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
//            @Override
//            public void onDismiss(DialogInterface dialog) {
//                if (clickListener != null) {
//                    clickListener = null;
//                }
//                dialog = null;
//            }
//        });
        Window windows = dialog.getWindow();
        if (windows != null) {
            windows.setBackgroundDrawableResource(android.R.color.transparent);
        }
//        windows.setContentView(view);
        dialog.setView(view);
        dialog.show();
    }

    /**
     * 和上面只是布局不一样
     */
    public void showTwoButtonDialog1(Context context, String s, String lift, String right) {
        dialog = new AlertDialog.Builder(context).create();
        View view = View.inflate(context, R.layout.dialog_show_3, null);
        ImageView image = view.findViewById(R.id.image);
        image.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        TextView tv_dialog_cancel = view.findViewById(R.id.tv_dialog_cancel);
        TextView tv_dialog_ok = view.findViewById(R.id.tv_dialog_ok);
        TextView tv_showdialog_text = view.findViewById(R.id.tv_showdialog_text);
        tv_showdialog_text.setText(s);
        tv_dialog_cancel.setText(lift);
        tv_dialog_ok.setText(right);
        tv_dialog_cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // farLogin(s,0);
                if (clickListener != null) {
                    clickListener.onLiftClick(dialog, v);
                }
            }
        });
        tv_dialog_ok.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null) {
                    clickListener.onRightClick(dialog, v);
                }
            }
        });
        dialog.setCancelable(true);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (clickListener != null) {
                    clickListener = null;
                }
                dialog = null;
            }
        });
        Window windows = dialog.getWindow();
        if (windows != null) {
            windows.setBackgroundDrawableResource(android.R.color.transparent);
        }
//        windows.setContentView(view);
        dialog.setView(view);
        dialog.show();
    }

    public void showEditDialog(Context context, String s, String lift, String right) {
        dialog = new AlertDialog.Builder(context).create();
        View view = View.inflate(context, R.layout.dialog_show_2, null);
        TextView tv_dialog_cancel = view.findViewById(R.id.tv_dialog_cancel);
        TextView tv_dialog_ok = view.findViewById(R.id.tv_dialog_ok);
        TextView tv_showdialog_text = view.findViewById(R.id.tv_showdialog_text);
        EditText editText = view.findViewById(R.id.editText);
        ImageView image = view.findViewById(R.id.image);
        image.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        tv_showdialog_text.setText(s);
        tv_dialog_cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // farLogin(s,0);
                if (clickListener != null) {
                    clickListener.onLiftClick(dialog, v);
                }
            }
        });
        tv_dialog_ok.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null) {
                    clickListener.onEdit(dialog, v, editText.getText().toString());
                }
            }
        });
        dialog.setCancelable(true);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (clickListener != null) {
                    clickListener = null;
                }
                dialog = null;
            }
        });
        Window windows = dialog.getWindow();
        if (windows != null) {
            windows.setBackgroundDrawableResource(android.R.color.transparent);
        }
//        windows.setContentView(view);
        dialog.setView(view);
        dialog.show();
    }

    public void showImageDialog(Context context, String s, int resId) {
        dialog = new AlertDialog.Builder(context).create();
        View view = View.inflate(context, R.layout.dialog_show_1, null);
        ImageView image = view.findViewById(R.id.image);
        ImageView cancle_iv = view.findViewById(R.id.cancle_iv);
        TextView tv_showdialog_text = view.findViewById(R.id.tv_showdialog_text);
        tv_showdialog_text.setText(s);
        image.setImageResource(resId);
        cancle_iv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        dialog.setCancelable(true);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (clickListener != null) {
                    clickListener = null;
                }
                dialog = null;
            }
        });
        Window windows = dialog.getWindow();
        if (windows != null) {
            windows.setBackgroundDrawableResource(android.R.color.transparent);
        }
//        windows.setContentView(view);
        dialog.setView(view);
        dialog.show();
    }

    public void showImageDialogStopCharge(Context context, String s, int resId) {
        dialog = new AlertDialog.Builder(context).create();
        View view = View.inflate(context, R.layout.dialog_stop_tixian, null);
        ImageView image = view.findViewById(R.id.image);
        ImageView cancle_iv = view.findViewById(R.id.cancle_iv);
        TextView tv_showdialog_text = view.findViewById(R.id.tv_showdialog_text);
        tv_showdialog_text.setText(s);
        image.setImageResource(resId);
        cancle_iv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        dialog.setCancelable(true);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (clickListener != null) {
                    clickListener = null;
                }
                dialog = null;
            }
        });
        Window windows = dialog.getWindow();
        if (windows != null) {
            windows.setBackgroundDrawableResource(android.R.color.transparent);
        }
//        windows.setContentView(view);
        dialog.setView(view);
        dialog.show();
    }

    public void showRegistSucDialog(Context context) {
        dialog = new AlertDialog.Builder(context).create();
        View view = View.inflate(context, R.layout.regist_suc_view, null);
        ImageView cancle_iv = view.findViewById(R.id.cancle_iv);
        cancle_iv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        dialog.setCancelable(true);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                dialog = null;
            }
        });
        Window windows = dialog.getWindow();
        if (windows != null) {
            windows.setBackgroundDrawableResource(android.R.color.transparent);
        }
//        windows.setContentView(view);
        dialog.setView(view);
        dialog.show();
    }

    /**
     * 统一对话框待重构
     * @param context
     * @param confirmMsg
     * @param message
     * @param cancelable
     */
    public void showCommonInfoDialog(Context context, String confirmMsg, String message, boolean cancelable) {
        final AlertDialog infoDialog = new AlertDialog.Builder(context).create();
        View view = View.inflate(context, R.layout.dialog_common_info, null);
        TextView tvMessage = view.findViewById(R.id.tvMessage);
        TextView tvConfirm = view.findViewById(R.id.tvConfirm);
        tvConfirm.setOnClickListener(v -> {
            dismiss();
            if (infoDialog != null) {
                infoDialog.dismiss();
            }
        });
        tvMessage.setText(message);
        tvConfirm.setText(confirmMsg);
        infoDialog.setCancelable(cancelable);
        Window windows = infoDialog.getWindow();
        if (windows != null) {
            windows.setBackgroundDrawableResource(android.R.color.transparent);
        }
        infoDialog.setView(view);
        infoDialog.show();
    }

    public void showC2cInfoDialog(Context context) {
        dialog = new AlertDialog.Builder(context).create();
        View view = View.inflate(context, R.layout.c2c_info, null);
        TextView know_tv = view.findViewById(R.id.know_tv);
        ImageView cancle_iv = view.findViewById(R.id.cancle_iv);
        cancle_iv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        know_tv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        dialog.setCancelable(true);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                dialog = null;
            }
        });
        Window windows = dialog.getWindow();
        if (windows != null) {
            windows.setBackgroundDrawableResource(android.R.color.transparent);
        }
//        windows.setContentView(view);
        dialog.setView(view);
        dialog.show();
    }

    public void showChangeIp(Activity atv) {
        dialog = new AlertDialog.Builder(atv).create();
        LinearLayout layout = (LinearLayout) View.inflate(atv, R.layout.change_ip_view, null);
        LinearLayout contentLayout = layout.findViewById(R.id.ll_content);
        dialog.setCancelable(true);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                dialog = null;
            }
        });
//        ((TextView) view.findViewById(R.id.current_ip)).setText(UrlConstants.DOMAIN);
        Domain currDomain = DomainHelper.getDomain();
        List<Domain> domainList = DomainHelper.getDomainList();
        int index = 0;
        for (Domain domain : domainList) {
            TextView itemTxt = new TextView(atv);
            itemTxt.setTextSize(14f);
            itemTxt.setText(domain.host);
            itemTxt.setPadding(20, 10, 20, 0);
            index++;
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0, 2, 0, 2);
            itemTxt.setLayoutParams(lp);
            if (TextUtils.equals(domain.host, currDomain.host)) {
                //当前选择主机域名
                itemTxt.setTextSize(16f);
                itemTxt.setTextColor(atv.getResources().getColor(R.color.red));
                itemTxt.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            } else {
                itemTxt.setTextSize(14f);
                itemTxt.setTextColor(atv.getResources().getColor(R.color.F000000));
            }
            itemTxt.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    String host = itemTxt.getText().toString();
                    if (TextUtils.equals(currDomain.host, host)) {
                        Utilities.getMainHandler().post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(atv.getApplicationContext(), "当前主机名未切换，不重启!", Toast.LENGTH_SHORT).show();
                            }
                        });
                        return;
                    }
                    //更新选择主机名
                    SPUtils.saveString(atv, AppConstants.LOCAL.KEY_LOCAL_HOST, host);
                    //退出合约登录，通知SDK
                    ModularContractSDK.INSTANCE.logout();
                    //重启服务进程或更新服务器进程数据
                    //
                    //重启
                    SPUtils.saveLoginToken("");
                    UserInfoManager.clearUser();
                    System.exit(0);
                }
            });
            contentLayout.addView(itemTxt);
        }
        Window windows = dialog.getWindow();
        if (windows != null) {
            windows.setBackgroundDrawableResource(android.R.color.transparent);
        }
        //windows.setContentView(layout);
        dialog.setView(layout);
        dialog.show();
    }
    public void showChangeContractIp(Activity atv) {
        dialog = new AlertDialog.Builder(atv).create();
        LinearLayout layout = (LinearLayout) View.inflate(atv, R.layout.change_ip_view, null);
        LinearLayout contentLayout = layout.findViewById(R.id.ll_content);
        TextView tvName=layout.findViewById(R.id.tvName);
        tvName.setText("切换合约服务器");
        dialog.setCancelable(true);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                dialog = null;
            }
        });
        String currDomain = DomainHelper.getSwapUrl();
        List<String> domainList = DomainHelper.getDomainContractList();
        int index = 0;
        for (String domain : domainList) {
            TextView itemTxt = new TextView(atv);
            itemTxt.setText(domain);
            itemTxt.setTextSize(14f);
            itemTxt.setPadding(20, 10, 20, 0);
            index++;
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0, 2, 0, 2);
            itemTxt.setLayoutParams(lp);
            if (TextUtils.equals(domain, currDomain)) {
                //当前选择主机域名
                itemTxt.setTextSize(16f);
                itemTxt.setTextColor(atv.getResources().getColor(R.color.red));
                itemTxt.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            } else {
                itemTxt.setTextSize(14f);
                itemTxt.setTextColor(atv.getResources().getColor(R.color.F000000));
            }
            itemTxt.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    String host = itemTxt.getText().toString();
                    if (TextUtils.equals(currDomain, host)) {
                        Utilities.getMainHandler().post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(atv.getApplicationContext(), "当前主机名未切换，不重启!", Toast.LENGTH_SHORT).show();
                            }
                        });
                        return;
                    }
                    //更新选择主机名
                    SPUtils.saveString(atv, AppConstants.LOCAL.KEY_LOCAL_HOST_SWAP, host);
                    //退出合约登录，通知SDK
                    ModularContractSDK.INSTANCE.logout();
                    //重启服务进程或更新服务器进程数据
                    //
                    //重启
                    System.exit(0);
                }
            });
            contentLayout.addView(itemTxt);
        }
        Window windows = dialog.getWindow();
        if (windows != null) {
            windows.setBackgroundDrawableResource(android.R.color.transparent);
        }
        dialog.setView(layout);
        dialog.show();
    }
    private void restartApp(int type) {
        SPUtils.saveInt(BaseApp.getSelf(), Constant.IP_TYPT, type);
        SPUtils.saveLoginToken("");
        UserInfoManager.clearUser();
        System.exit(0);
    }

    public void showOtcInfoDialog(Context context) {
        Logger.getInstance().debug(TAG, "showOTCInfo", new Exception());
        dismiss();
        dialog = new AlertDialog.Builder(context).create();
        View view = View.inflate(context, R.layout.otc_tip_info, null);
        TextView know_tv = view.findViewById(R.id.know_tv);
        ImageView cancle_iv = view.findViewById(R.id.cancle_iv);
        cancle_iv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        know_tv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        dialog.setCancelable(true);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                dialog = null;
            }
        });
        Window windows = dialog.getWindow();
        if (windows != null) {
            windows.setBackgroundDrawableResource(android.R.color.transparent);
        }
//        windows.setContentView(view);
        dialog.setView(view);
        dialog.show();
    }

    public void showKefuDialog(Context context, String message) {
        dialog = new AlertDialog.Builder(context).create();
        View view = View.inflate(context, R.layout.kefu_dialog, null);
        TextView know_tv = view.findViewById(R.id.know_tv);
        TextView text_tv = view.findViewById(R.id.text_tv);
        if (!android.text.TextUtils.isEmpty(message)) {
            text_tv.setText(message);
        }
        ImageView cancle_iv = view.findViewById(R.id.cancle_iv);
        cancle_iv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        know_tv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        dialog.setCancelable(true);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                dialog = null;
            }
        });
        Window windows = dialog.getWindow();
        if (windows != null) {
            windows.setBackgroundDrawableResource(android.R.color.transparent);
        }
//        windows.setContentView(view);
        dialog.setView(view);
        dialog.show();
    }

    public void showGradeDialog(Context context, String message) {
        dialog = new AlertDialog.Builder(context).create();
        View view = View.inflate(context, R.layout.grade_dialog, null);
        TextView know_tv = view.findViewById(R.id.know_tv);
        TextView text_tv = view.findViewById(R.id.text_tv);
        if (!android.text.TextUtils.isEmpty(message)) {
            text_tv.setText(message);
        }
        ImageView cancle_iv = view.findViewById(R.id.cancle_iv);
        cancle_iv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        know_tv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        dialog.setCancelable(true);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                dialog = null;
            }
        });
        Window windows = dialog.getWindow();
        if (windows != null) {
            windows.setBackgroundDrawableResource(android.R.color.transparent);
        }
//        windows.setContentView(view);
        dialog.setView(view);
        dialog.show();
    }

    public void showChongzhiDialog(Context context, String coinName, String address, String memo, String logUrl, boolean isEos, String specification, boolean isShow) {
        dismiss();
        dialog = new AlertDialog.Builder(context).create();
        View view = View.inflate(context, R.layout.recharge_dialog, null);
        ImageView head_iv = view.findViewById(R.id.head_iv);
        ImageView qr_code_iv = view.findViewById(R.id.qr_code_iv);
        TextView coinName_tv = view.findViewById(R.id.coinName_tv);
        TextView address_tv = view.findViewById(R.id.address_tv);
        TextView memo_tv = view.findViewById(R.id.memo_tv);
        TextView explain_tv = view.findViewById(R.id.explain_tv);
        TextView main_tv = view.findViewById(R.id.main_tv);
        Button cope_bn = view.findViewById(R.id.cope_bn);
        Button copememo_bn = view.findViewById(R.id.copememo_bn);
        coinName_tv.setText(coinName);
        address_tv.setText(address);
        if (isShow) {
            main_tv.setVisibility(View.VISIBLE);
            main_tv.setText("(" + specification + ")");
        } else {
            main_tv.setVisibility(View.GONE);
        }
        CreateQRImage mCreateQRImage = new CreateQRImage();
        RequestOptions ro = new RequestOptions();
        ro.error(R.mipmap.rmblogo);
        ro.centerCrop();
        Glide.with(context).load(logUrl).apply(ro).into(head_iv);
        if (!android.text.TextUtils.isEmpty(address)) {
            qr_code_iv.setImageBitmap(mCreateQRImage.createQRImage(address, Util.dp2px(context, 140), Util.dp2px(context, 140), false));
        }
        if (isEos) {
            memo_tv.setVisibility(View.VISIBLE);
            copememo_bn.setVisibility(View.VISIBLE);
            memo_tv.setText(address);
            address_tv.setText(memo);
        } else {
            memo_tv.setVisibility(View.GONE);
            copememo_bn.setVisibility(View.GONE);
        }
        cope_bn.setText(context.getString(R.string.copy) + coinName + context.getString(R.string.address));
        explain_tv.setText(explain_tv.getText().toString().replace(context.getString(R.string.coin), coinName));
        cope_bn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isEos) {
                    if (!android.text.TextUtils.isEmpty(address)) {
                        NorUtils.copeText(context, address);
                        MToast.showButton(context, context.getString(R.string.copy_suc), 1);
                    } else {
                        MToast.show(context, context.getString(R.string.empty_address), 1);
                    }
                } else {
                    if (!android.text.TextUtils.isEmpty(memo)) {
                        NorUtils.copeText(context, memo);
                        MToast.showButton(context, context.getString(R.string.copy_suc), 1);
                    } else {
                        MToast.show(context, context.getString(R.string.empty_address), 1);
                    }
                }
            }
        });
        copememo_bn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!android.text.TextUtils.isEmpty(address)) {
                    NorUtils.copeText(context, address);
                    MToast.showButton(context, context.getString(R.string.copy_suc), 1);
                } else {
                    MToast.show(context, context.getString(R.string.memo_empty), 1);
                }
            }
        });
        ImageView cancle_iv = view.findViewById(R.id.cancle_iv);
        cancle_iv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        dialog.setCancelable(true);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                dialog = null;
            }
        });
        Window windows = dialog.getWindow();
        if (windows != null) {
            windows.setBackgroundDrawableResource(android.R.color.transparent);
        }
//        windows.setContentView(view);
        dialog.setView(view);
        dialog.show();
    }

    /* public void showChongzhiDialog(Context context, String coinName, String address, String memo, String logUrl,boolean isEos) {
         if (dialog != null && dialog.isShowing()) {
             dialog.dismiss();
             dialog=null;
         }
         dialog = new AlertDialog.Builder(context).create();

         View view = View.inflate(context, R.layout.recharge_dialog, null);

         ImageView head_iv = view.findViewById(R.id.head_iv);
         ImageView qr_code_iv = view.findViewById(R.id.qr_code_iv);
         TextView coinName_tv = view.findViewById(R.id.coinName_tv);
         TextView address_tv = view.findViewById(R.id.address_tv);
         TextView memo_tv = view.findViewById(R.id.memo_tv);
         TextView explain_tv = view.findViewById(R.id.explain_tv);
         Button cope_bn = view.findViewById(R.id.cope_bn);
         Button copememo_bn = view.findViewById(R.id.copememo_bn);
         coinName_tv.setText(coinName);
         address_tv.setText(address);

          CreateQRImage mCreateQRImage = new CreateQRImage();

         Glide.with(context).load(logUrl).error(R.mipmap.rmblogo).centerCrop().into(head_iv);

         qr_code_iv.setImageBitmap(mCreateQRImage.createQRImage(address, Util.dp2px(context,140), Util.dp2px(context,140),false));
         if (isEos) {
             memo_tv.setVisibility(View.VISIBLE);
             copememo_bn.setVisibility(View.VISIBLE);
             memo_tv.setText(address);
             address_tv.setText(memo);


         } else {

             memo_tv.setVisibility(View.GONE);
             copememo_bn.setVisibility(View.GONE);
         }
         cope_bn.setText("复制" + coinName + "地址");
         explain_tv.setText(explain_tv.getText().toString().replace("币种",coinName));

         cope_bn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 if (!isEos){
                     if (!android.text.TextUtils.isEmpty(address)) {
                         NorUtils.copeText(context, address);
                         MToast.showButton(context, "复制成功", 1);
                     } else {
                         MToast.show(context, "地址为空", 1);
                     }

                 }else {
                     if (!android.text.TextUtils.isEmpty(memo)) {
                         NorUtils.copeText(context, memo);
                         MToast.showButton(context, "复制成功", 1);
                     } else {
                         MToast.show(context, "地址为空", 1);
                     }
                 }

             }
         });
         copememo_bn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 if (!android.text.TextUtils.isEmpty(address)) {
                     NorUtils.copeText(context, address);
                     MToast.showButton(context, "复制成功", 1);
                 } else {
                     MToast.show(context, "MEMO地址为空", 1);
                 }
             }
         });

         dialog.setView(view);

         ImageView cancle_iv = view.findViewById(R.id.cancle_iv);
         cancle_iv.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 if (dialog != null) {
                     dialog.dismiss();
                 }

             }
         });


        *//* TextView know_tv = view.findViewById(R.id.know_tv);
        cancle_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog != null) {
                    dialog.dismiss();
                }

            }
        });
        know_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog != null) {
                    dialog.dismiss();
                }

            }
        });*//*
        dialog.setCancelable(true);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {

                dialog = null;
            }
        });
        Window windows = dialog.getWindow();
                if (windows != null) {
            windows.setBackgroundDrawableResource(android.R.color.transparent);
        }
        windows.setContentView(view);
        dialog.show();

    }*/
    //添加备注
    public void showAddressRemarkDialog(Context context, onBnClickListener clickListener) {
        this.clickListener = clickListener;
        dismiss();
        dialog = new AlertDialog.Builder(context).create();
        View view = View.inflate(context, R.layout.add_address_remark_dialog, null);
        EditText et_market = view.findViewById(R.id.et_market);
        Button know_tv = view.findViewById(R.id.know_tv);
        ImageView cancle_iv = view.findViewById(R.id.cancle_iv);
        cancle_iv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        know_tv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null) {
                    clickListener.onEdit(dialog, view, et_market.getText().toString());
                }
            }
        });
        dialog.setCancelable(true);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                dialog = null;
            }
        });
        Window windows = dialog.getWindow();
        if (windows != null) {
            windows.setBackgroundDrawableResource(android.R.color.transparent);
        }
//        windows.setContentView(view);
        dialog.setView(view);
        dialog.show();
    }

    //下载hyperpay
    public void showDownloadHPDialog(Context context, final onBnClickListener listener) {
        dismiss();
        dialog = new AlertDialog.Builder(context).create();
        View view = View.inflate(context, R.layout.download_hpy_dialog, null);
        LinearLayout know_tv = view.findViewById(R.id.know_tv);
        ImageView cancle_iv = view.findViewById(R.id.cancle_iv);
        cancle_iv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        know_tv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onRightClick(dialog, v);
                }
            }
        });
        dialog.setCancelable(true);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                dialog = null;
            }
        });
        Window windows = dialog.getWindow();
        if (windows != null) {
            windows.setBackgroundDrawableResource(android.R.color.transparent);
        }
//        windows.setContentView(view);
        dialog.setView(view);
        dialog.show();
    }

    public View showFingerprintDialog(Context context, onBnClickListener clickListener) {
        this.clickListener = clickListener;
        dismiss();
        dialog = new AlertDialog.Builder(context).create();
        View view = View.inflate(context, R.layout.finger_print_dialog, null);
        Button know_tv = view.findViewById(R.id.know_tv);
        ImageView cancle_iv = view.findViewById(R.id.cancle_iv);
        cancle_iv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DialogUtils.this.clickListener != null) {
                    DialogUtils.this.clickListener.onRightClick(dialog, v);
                }
                dismiss();
            }
        });
        know_tv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DialogUtils.this.clickListener != null) {
                    DialogUtils.this.clickListener.onRightClick(dialog, v);
                }
                dismiss();
            }
        });
        dialog.setCancelable(true);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                dialog = null;
            }
        });
        Window windows = dialog.getWindow();
        if (windows != null) {
            windows.setBackgroundDrawableResource(android.R.color.transparent);
        }
//        windows.setContentView(view);
        dialog.setView(view);
        dialog.show();
        return view;
    }

    public void cancelOrder(Context context, onBnClickListener clickListener) {
        this.clickListener = clickListener;
        dismiss();
        dialog = new AlertDialog.Builder(context).create();
        View view = View.inflate(context, R.layout.dialog_cancel_order, null);
        TextView tv_cancel = view.findViewById(R.id.tv_cancel);
        TextView tv_ok = view.findViewById(R.id.tv_ok);
        ImageView image = view.findViewById(R.id.image);
        image.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        tv_cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null) {
                    clickListener.onLiftClick(dialog, v);
                }
                dismiss();
            }
        });
        tv_ok.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null) {
                    clickListener.onRightClick(dialog, v);
                }
            }
        });
        dialog.setCancelable(false);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                dialog = null;
            }
        });
        Window windows = dialog.getWindow();
        if (windows != null) {
            windows.setBackgroundDrawableResource(android.R.color.transparent);
        }
//        windows.setContentView(view);
        dialog.setView(view);
        dialog.show();
    }

    public void showLoginDialog(Context context, onBnClickListener clickListener) {
        this.clickListener = clickListener;
        dismiss();
        dialog = new AlertDialog.Builder(context).create();
        View view = View.inflate(context, R.layout.fingerprint_login_dialog, null);
        EditText et_info = view.findViewById(R.id.et_info);
        TextView cancle_tv = view.findViewById(R.id.cancle_tv);
        TextView btnSure = view.findViewById(R.id.btnSure);
        cancle_tv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null) {
                    clickListener.onLiftClick(dialog, v);
                }
                dismiss();
            }
        });
        btnSure.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null) {
                    clickListener.onEdit(dialog, v, et_info.getText().toString());
                }
            }
        });
        dialog.setCancelable(true);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                dialog = null;
            }
        });
        Window windows = dialog.getWindow();
        if (windows != null) {
            windows.setBackgroundDrawableResource(android.R.color.transparent);
        }
//        windows.setContentView(view);
        dialog.setView(view);
        dialog.show();
    }

    public void showPayGradeDialog(Context context, String t1, String t4, String t2, String t3, onBnClickListener clickListener) {
        this.clickListener = clickListener;
        dismiss();
        dialog = new AlertDialog.Builder(context).create();
        View view = View.inflate(context, R.layout.pay_grade_dialog, null);
        TextView title_tv = view.findViewById(R.id.title_tv);
        TextView tip_tv = view.findViewById(R.id.tip_tv);
        EditText et_info = view.findViewById(R.id.et_info);
        TextView need_tv = view.findViewById(R.id.need_tv);
        TextView balance_tv = view.findViewById(R.id.balance_tv);
        TextView cancle_tv = view.findViewById(R.id.cancle_tv);
        TextView btnSure = view.findViewById(R.id.btnSure);
        title_tv.setText(t1);
        need_tv.setText(t2);
        balance_tv.setText(t3);
        tip_tv.setText(t4);
        cancle_tv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null) {
                    clickListener.onLiftClick(dialog, v);
                }
                dismiss();
            }
        });
        btnSure.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null) {
                    clickListener.onRightClick(dialog, view);
                }
            }
        });
        dialog.setCancelable(true);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                dialog = null;
            }
        });
        Window windows = dialog.getWindow();
        if (windows != null) {
            windows.setBackgroundDrawableResource(android.R.color.transparent);
        }
//        windows.setContentView(view);
        dialog.setView(view);
        dialog.show();
    }

    //otc取消订单
    public void showCancleOtcDialog(Context context, String des1, String des2, onBnClickListener clickListener) {
        this.clickListener = clickListener;
        dismiss();
        dialog = new AlertDialog.Builder(context).create();
        View view = View.inflate(context, R.layout.cancle_otctrade_dialog, null);
        ((TextView) view.findViewById(R.id.title1)).setText(des1);
        ((TextView) view.findViewById(R.id.des)).setText(des2);
        TextView cancle_tv = view.findViewById(R.id.cancle_tv);
        TextView btnSure = view.findViewById(R.id.btnSure);
        cancle_tv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null) {
                    clickListener.onLiftClick(dialog, v);
                }
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        btnSure.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null) {
                    clickListener.onRightClick(dialog, view);
                }
            }
        });
        dialog.setCancelable(true);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                dialog = null;
            }
        });
        Window windows = dialog.getWindow();
        if (windows != null) {
            windows.setBackgroundDrawableResource(android.R.color.transparent);
        }
//        windows.setContentView(view);
        dialog.setView(view);
        dialog.show();
    }

    //otc卖出取消订单
    public void showCancleOtcSellDialog(Context context, onBnClickListener clickListener) {
        this.clickListener = clickListener;
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        dialog = new AlertDialog.Builder(context).create();
        View view = View.inflate(context, R.layout.cancle_otctradesell_dialog, null);
        TextView cancle_tv = view.findViewById(R.id.cancle_tv);
        TextView btnSure = view.findViewById(R.id.btnSure);
        cancle_tv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null) {
                    clickListener.onLiftClick(dialog, v);
                }
                dismiss();
            }
        });
        btnSure.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null) {
                    clickListener.onRightClick(dialog, view);
                }
            }
        });
        dialog.setCancelable(true);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                dialog = null;
            }
        });
        Window windows = dialog.getWindow();
        if (windows != null) {
            windows.setBackgroundDrawableResource(android.R.color.transparent);
        }
//        windows.setContentView(view);
        dialog.setView(view);
        dialog.show();
    }

    //otc取消订单
    public void showConfirmOtcDialog(Context context, onBnClickListener clickListener) {
        this.clickListener = clickListener;
        dismiss();
        dialog = new AlertDialog.Builder(context).create();
        View view = View.inflate(context, R.layout.confirm_otctrade_dialog, null);
        TextView cancle_tv = view.findViewById(R.id.cancle_tv);
        TextView btnSure = view.findViewById(R.id.btnSure);
        cancle_tv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null) {
                    clickListener.onLiftClick(dialog, v);
                }
                dismiss();
            }
        });
        btnSure.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null) {
                    clickListener.onRightClick(dialog, view);
                }
            }
        });
        dialog.setCancelable(true);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                dialog = null;
            }
        });
        Window windows = dialog.getWindow();
        if (windows != null) {
            windows.setBackgroundDrawableResource(android.R.color.transparent);
        }
//        windows.setContentView(view);
        dialog.setView(view);
        dialog.show();
    }

    public void showCancelAppealDialog(Context context, onBnClickListener clickListener) {
        this.clickListener = clickListener;
        dismiss();
        dialog = new AlertDialog.Builder(context).create();
        View view = View.inflate(context, R.layout.cancel_appeal, null);
        TextView cancle_tv = view.findViewById(R.id.cancle_tv);
        TextView btnSure = view.findViewById(R.id.btnSure);
        cancle_tv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null) {
                    clickListener.onLiftClick(dialog, v);
                }
                dismiss();
            }
        });
        btnSure.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null) {
                    clickListener.onRightClick(dialog, view);
                }
            }
        });
        dialog.setCancelable(true);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                dialog = null;
            }
        });
        Window windows = dialog.getWindow();
        if (windows != null) {
            windows.setBackgroundDrawableResource(android.R.color.transparent);
        }
//        windows.setContentView(view);
        dialog.setView(view);
        dialog.show();
    }

    /**
     * 商家兑换
     * @param context
     * @param clickListener
     */
    public void merchantsExchange(Context context, String maxCount, onBnClickListener clickListener) {
        this.clickListener = clickListener;
        dismiss();
        dialog = new AlertDialog.Builder(context).create();
        View view = View.inflate(context, R.layout.layout_exchange_dialog, null);
        TextView tv_ok = view.findViewById(R.id.tv_ok);
        TextView desContent = view.findViewById(R.id.des_content);
        desContent.setText(context.getString(R.string.exchange_des, maxCount));
        ImageView btnCancle = view.findViewById(R.id.btnCancle);
        btnCancle.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onLiftClick(dialog, v);
            }
            dismiss();
        });
        tv_ok.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onRightClick(dialog, view);
            }
        });
        dialog.setCancelable(true);
        dialog.setOnDismissListener(dialog -> dialog = null);
        Window windows = dialog.getWindow();
        if (windows != null) {
            windows.setBackgroundDrawableResource(android.R.color.transparent);
        }
//        windows.setContentView(view);
        dialog.setView(view);
        dialog.show();
    }

    /**
     * 显示阿里人机智能验证的对话框
     * @param context
     */
    public AlertDialog showAliManMachineDialog(Context context, int type) {
        dismiss();
        isLoadError = false;
        Dialog dialogLoading = CustomLoadingDialog.createLoadingDialog(context);
        dialog = new AlertDialog.Builder(context).create();
        Window windows = dialog.getWindow();
        View view = View.inflate(context, R.layout.dialog_webview, null);
        ImageView ivClose = view.findViewById(R.id.iv_close);
        WebView webview = view.findViewById(R.id.webview);
        ConfigurationUtils.resetLanguage(context);
        ivClose.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AppHelper.dismissDialog(dialog);
            }
        });
        int dialogWidth = (int) (DIALOG_WIDTH_SCALE * ResHelper.getScreenWidth(context));
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(dialogWidth, (int) (dialogWidth * scaleAliManMachine));
        webview.setLayoutParams(layoutParams);
        WebviewUtils.getWebViewSetSettings(webview);
        webview.getSettings().setSupportZoom(false); //支持缩放，默认为true。是下面那个的前提。
        webview.getSettings().setBuiltInZoomControls(false); //设置内置的缩放控件。若为false，则该WebView不可缩放
        webview.getSettings().setUseWideViewPort(true);
        webview.getSettings().setLoadWithOverviewMode(true);
        // 建议禁止缓存加载，以确保在攻击发生时可快速获取最新的滑动验证组件进行对抗。
        webview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webview.setHorizontalScrollBarEnabled(false);//水平不显示
        webview.setVerticalScrollBarEnabled(false); //垂直不显示
        // 设置不使用默认浏览器，而直接使用WebView组件加载页面。
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                Logger.getInstance().debug(TAG, "showAliManMachineDialog onPageStarted");
            }

            @Override
            public void onPageFinished(WebView webView, String url) {
                Logger.getInstance().debug(TAG, "showAliManMachineDialog onPageFinished");
                if (dialogLoading == null || !dialogLoading.isShowing())//loading被用户取消则不显示对话框
                {
                    return;
                }
                if (webView != null) {
                    webView.getSettings().setBlockNetworkImage(false);
                    //判断webview是否加载了，图片资源
                    if (!webView.getSettings().getLoadsImagesAutomatically()) {
                        //设置wenView加载图片资源
                        webView.getSettings().setLoadsImagesAutomatically(true);
                    }
                }
                super.onPageFinished(webView, url);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Logger.getInstance().debug(TAG, "showAliManMachineDialog  errorCode: " + errorCode + " description: " + description + " failingUrl: " + failingUrl);
                webviewLoadError(view, context, dialogLoading);
            }

            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                super.onReceivedHttpError(view, request, errorResponse);
                Logger.getInstance().debug(TAG, "showAliManMachineDialog onReceivedHttpError");
                webviewLoadError(view, context, dialogLoading);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                Logger.getInstance().debug(TAG, "showAliManMachineDialog onReceivedSslError");
                // android 5.0以上默认不支持Mixed Content
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    webview.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
                }
                handler.proceed();
            }
        });
        webview.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage cm) {
                Logger.getInstance().debug(TAG, "showAliManMachineDialog onConsoleMessage " + cm.message() + " -- From line " + cm.lineNumber() + " of " + cm.sourceId());
                return true;
            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                Logger.getInstance().debug(TAG, "showAliManMachineDialog onJsAlert url: " + url + " message: " + message, new Exception());
                return super.onJsAlert(view, url, message, result);
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                Logger.getInstance().debug(TAG, "showAliManMachineDialog onProgressChanged newProgress:" + newProgress);
                if (newProgress >= 100 && !isLoadError) {
                    if (AppHelper.isDestroy(context)) {
                        return;
                    }
                    if(dialog.isShowing()) return;
                    dialog.show();
                    WindowManager.LayoutParams lp = windows.getAttributes();//对话框宽度设置为固定值
                    lp.width = dialogWidth;
                    windows.setAttributes(lp);
                    AppHelper.dismissDialog(dialogLoading);
                }
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                Logger.getInstance().debug(TAG, "showAliManMachineDialog onReceivedTitle title:" + title);
            }
        });
        webview.addJavascriptInterface(new JSCallAlManMachineJavaInterface(type), "JSCallJava");
        //webview.loadUrl(UrlConstants.ALI_MAN_MACHINE+ConfigManager.getInstance().getAliVerifyLanguage());
        String url = "file:///android_asset/aliverify.html?lang=" + AppUtils.getAppLanguage();
        webview.loadUrl(url);
        if (windows != null) {
            windows.setBackgroundDrawableResource(android.R.color.transparent);
        }
        dialog.setCancelable(false);
        dialog.setView(view);
        if (dialogLoading != null) {
            dialogLoading.show();
        }
        return dialog;
    }

    //无网络或弱网包括一切回调error的情况调用该方法，不弹出阿里验证的对话框
    private void webviewLoadError(WebView view, Context context, Dialog dialogLoading) {
//        view.loadUrl("about:blank");// 避免出现默认的错误界面
        isLoadError = true;
        AppHelper.dismissDialog(dialogLoading);
        if (context != null && !((Activity) context).isFinishing()) {
            SnackBarUtils.ShowRed((Activity) context, context.getResources().getString(R.string.e9));
        }
    }

    //红包领取失败对话框
    public void showRedEnvelopesFailedDialog(Context context, String title, String content, boolean isOneBtn) {
        dismiss();
        dialog = new AlertDialog.Builder(context).create();
        View view = View.inflate(context, R.layout.dialog_red_envelopes_failed, null);
        TextView tvTitle = view.findViewById(R.id.tvTitle);
        TextView tvContent = view.findViewById(R.id.tvContent);
        TextView tvKnow = view.findViewById(R.id.tvKnow);
        TextView tvRule = view.findViewById(R.id.tvRule);
        TextView tvWelfare = view.findViewById(R.id.tvWelfare);
        LinearLayout llTwoBtn = view.findViewById(R.id.llTwoBtn);
        tvTitle.setText(title);
        tvContent.setText(content);
        if (isOneBtn) {
            llTwoBtn.setVisibility(View.GONE);
            tvKnow.setVisibility(View.VISIBLE);
            tvKnow.setOnClickListener(v -> {
                dismiss();
            });
        } else {
            llTwoBtn.setVisibility(View.VISIBLE);
            tvKnow.setVisibility(View.GONE);
            tvRule.setOnClickListener(v -> {
                dismiss();
                gotoH5(context, UrlConstants.getRedRuleUrl(context));
            });
            tvWelfare.setOnClickListener(v -> {
                dismiss();
                Intent intent = new Intent(context, WelfareWebviewActivity.class);
                intent.putExtra("url", UrlConstants.ACTION_VIEWWELFARE);
                intent.putExtra("token", UserInfoManager.getToken());
                intent.putExtra("title", context.getString(R.string.str_welfare_center));
                context.startActivity(intent);
            });
        }
        dialog.setCancelable(true);
        Window windows = dialog.getWindow();
        if (windows != null) {
            windows.setBackgroundDrawableResource(android.R.color.transparent);
        }
        dialog.setView(view);
        dialog.show();
        setDialogWidth(context, windows);
    }

    //红包领取成功对话框
    public void showRedEnvelopesSuccessDialog(Context context, String num, String currency) {
        dismiss();
        dialog = new AlertDialog.Builder(context).create();
        View view = View.inflate(context, R.layout.dialog_red_envelopes_success, null);
        TextView tvNum = view.findViewById(R.id.tvNum);
        TextView tvContent = view.findViewById(R.id.tvContent);
        TextView tvKnow = view.findViewById(R.id.tvKnow);
        TextView tvGo = view.findViewById(R.id.tvGo);
        tvNum.setText("+" + num);
        tvContent.setText(String.format(context.getResources().getString(R.string.red_content), num, currency));
        tvKnow.setOnClickListener(v -> {
            dismiss();
        });
        tvGo.setOnClickListener(v -> {
            dismiss();
            gotoH5(context, UrlConstants.MY_REDENVELOPE_URL, context.getResources().getString(R.string.red_envelope));
        });
        dialog.setCancelable(true);
        Window windows = dialog.getWindow();
        if (windows != null) {
            windows.setBackgroundDrawableResource(android.R.color.transparent);
        }
        dialog.setView(view);
        dialog.show();
        setDialogWidth(context, windows);
    }

    private void gotoH5(Context context, String url) {
        gotoH5(context, url, "");
    }

    private void gotoH5(Context context, String url, String title) {
        Intent intent = new Intent(context, NewsWebviewActivity.class);
        intent.putExtra("url", url);
        intent.putExtra("token", UserInfoManager.getToken());
        if (TextUtils.isEmpty(title)) {
            intent.putExtra("hideTitle", true);
        } else {
            intent.putExtra("title", title);
        }
        context.startActivity(intent);
    }

    private void setDialogWidth(Context context, Window windows) {
        int dialogWidth = (int) (DIALOG_WIDTH_SCALE * ResHelper.getScreenWidth(context));
        WindowManager.LayoutParams lp = windows.getAttributes();//对话框宽度设置为固定值
        lp.width = dialogWidth;
        windows.setAttributes(lp);
    }

    public void showTokenExpiredDialog(Context context) {
        if (TextUtils.isEmpty(UserInfoManager.getToken())) {//通过判断有没有token来区分游客还是token失效和登录密码修改，游客不弹窗
            return;
        }
        if (tokenExpiredDialog == null) {
            tokenExpiredDialog = new AlertDialog.Builder(context).create();
            View view = View.inflate(context, R.layout.dialog_token_expired, null);
            TextView tv_cancel = view.findViewById(R.id.tv_cancel);
            TextView tv_ok = view.findViewById(R.id.tv_ok);
            tv_cancel.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppHelper.dismissDialog(tokenExpiredDialog);
                    tokenExpiredDialog = null;
                    Intent intent = new Intent(context, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(intent);
                }
            });
            tv_ok.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppHelper.dismissDialog(tokenExpiredDialog);
                    tokenExpiredDialog = null;
                    Intent intent = new Intent(context, LoginActivity.class);
                    intent.putExtra("isTokenExpired", true);
                    context.startActivity(intent);
                }
            });
            tokenExpiredDialog.setCanceledOnTouchOutside(false);
            tokenExpiredDialog.setCancelable(false);
            Window windows = tokenExpiredDialog.getWindow();
            if (windows != null) {
                windows.setBackgroundDrawableResource(android.R.color.transparent);
            }
            tokenExpiredDialog.setView(view);
        }
        if (!tokenExpiredDialog.isShowing()) {
            tokenExpiredDialog.show();
            EventBus.getDefault().post(new Event.exitApp());
        }
    }
    //otc放币的2次确认弹窗
    public void showOtcConfirmDialog(Context context, onBnClickListener clickListener) {
        this.clickListener = clickListener;
        AlertDialog dialog = new AlertDialog.Builder(context).create();
        View view = View.inflate(context, R.layout.dialog_otc_confirm_coin, null);
        LinearLayout llAgree = view.findViewById(R.id.llAgree);
        TextView tvCancel = view.findViewById(R.id.tvCancel);
        TextView tvSure = view.findViewById(R.id.tvSure);
        tvCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null) {
                    clickListener.onLiftClick(dialog, v);
                }
                dismiss();
            }
        });
        tvSure.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null) {
                    clickListener.onRightClick(dialog, view);
                }
            }
        });
        llAgree.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                llAgree.setSelected(!llAgree.isSelected());
                tvSure.setEnabled(llAgree.isSelected());
            }
        });
        dialog.setCancelable(true);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                dialog = null;
            }
        });
        Window windows = dialog.getWindow();
        if (windows != null) {
            windows.setBackgroundDrawableResource(android.R.color.transparent);
        }
        dialog.setView(view);
        dialog.show();
        setDialogWidth(context, windows);
    }
}
