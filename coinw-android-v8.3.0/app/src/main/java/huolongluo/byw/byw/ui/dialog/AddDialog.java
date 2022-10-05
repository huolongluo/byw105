package huolongluo.byw.byw.ui.dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.legend.socketio.SocketIOClient;
import com.legend.modular_contract_sdk.api.ModularContractSDK;

import org.greenrobot.eventbus.EventBus;

import huolongluo.byw.R;
import huolongluo.byw.byw.base.BaseDialog;
import huolongluo.byw.byw.share.Event;
import com.android.legend.ui.login.LoginActivity;
import huolongluo.byw.io.AppConstants;
import huolongluo.byw.log.Logger;
import huolongluo.byw.util.AgreementUtils;
/**
 * 添加商品
 * <p>
 * Created by SLAN on 2016/11/3.
 */
public class AddDialog extends BaseDialog {
    private long goodsNums = 1; // 默认添加一件商品
    private OnClick onitemClick;

    @Override
    protected int getContentViewId() {
        if (currentStyle == EXIT_APP) {
            return R.layout.dialog_exit_tip;
        } else if (currentStyle == CANCEL_ORDER) {
            return R.layout.dialog_cancel_order;
        } else if (currentStyle == UP_VERSION) {
            return R.layout.dialog_exit_tip;
        } else if (currentStyle == START_TRADE) {
            return R.layout.dialog_trade;
        } else if (currentStyle == AppConstants.TIMER.TOKEN_EXPIRED) {
            return R.layout.dialog_token_expired;
        } else if (currentStyle == DELETE_CARD) {
            return R.layout.delete_card;
        } else {
            return 0;
        }
    }

    @Override
    protected void initViewsAndEvents(View rootView) {
        if (currentStyle == EXIT_APP) {
            exitApp();
        } else if (currentStyle == CANCEL_ORDER) {
            cancelOrder();
        } else if (currentStyle == UP_VERSION) {
            upVersion();
        } else if (currentStyle == START_TRADE) {
            start_trade();
        } else if (currentStyle == AppConstants.TIMER.TOKEN_EXPIRED) {
            tokenExpired();
        } else if (currentStyle == DELETE_CARD) {
            deleteCard();
        } else {
            // do nothing
        }
    }

    private void deleteCard() {
        initShowStyle(mContext.getResources().getDimensionPixelSize(R.dimen.x640), WindowManager.LayoutParams.WRAP_CONTENT, Gravity.CENTER, R.style.dialog_animation2);
        Bundle bundle = getArguments();
        if (null != bundle) {
            String content = bundle.getString("content");
            if (null != content) {
                TextView tv_tip = (TextView) findViewById(R.id.tv_tip);
                tv_tip.setText(content);
            }
        }
        TextView tv_cancel = (TextView) findViewById(R.id.tv_cancel);
        TextView tv_ok = (TextView) findViewById(R.id.tv_ok);
        ImageView image = (ImageView) findViewById(R.id.image);
        image.setOnClickListener(v -> dismiss());
        tv_cancel.setOnClickListener(v -> dismiss());
        tv_ok.setOnClickListener(v -> {
            dismiss();
            onitemClick.onItemClick();
        });
    }

    //<!-- 退出确定 <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<-->
    private void exitApp() {
        initShowStyle(mContext.getResources().getDimensionPixelSize(R.dimen.x640), WindowManager.LayoutParams.WRAP_CONTENT, Gravity.CENTER, R.style.dialog_animation2);
        Bundle bundle = getArguments();
        if (null != bundle) {
            String content = bundle.getString("content");
            if (null != content) {
                TextView tv_tip = (TextView) findViewById(R.id.tv_tip);
                tv_tip.setText(content);
            }
        }
        TextView tv_cancel = (TextView) findViewById(R.id.tv_cancel);
        TextView tv_ok = (TextView) findViewById(R.id.tv_ok);
        ImageView image = (ImageView) findViewById(R.id.image);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        tv_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();

                //退出合约登录，通知SDK
                ModularContractSDK.INSTANCE.logout();
                SocketIOClient.logout();
                if(onitemClick!=null){
                    onitemClick.onItemClick();
                }
            }
        });
    }

    private void cancelOrder() {
        initShowStyle(mContext.getResources().getDimensionPixelSize(R.dimen.x640), WindowManager.LayoutParams.WRAP_CONTENT, Gravity.CENTER, R.style.dialog_animation2);
        Bundle bundle = getArguments();
        String orderId = null;
        int position = 0;
        if (null != bundle) {
            orderId = bundle.getString("orderId");
            position = bundle.getInt("position");
        }
        TextView tv_cancel = (TextView) findViewById(R.id.tv_cancel);
        TextView tv_ok = (TextView) findViewById(R.id.tv_ok);
        ImageView image = (ImageView) findViewById(R.id.image);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        eventClick(tv_cancel, 1000).subscribe(o -> dismiss(), throwable -> {
            Logger.getInstance().error(throwable);
        });
        String finalOrderId = orderId;
        int finalPosition = position;
        eventClick(tv_ok, 2000).subscribe(o -> {
            EventBus.getDefault().post(new Event.cancelOrder(finalOrderId, finalPosition));

            dismiss();
        }, throwable -> {
            Logger.getInstance().error(throwable);
        });
    }

    //合并代码 1.0有此方法，2.0未有此方法
    private void tokenExpired() {
        initShowStyle(mContext.getResources().getDimensionPixelSize(R.dimen.x640), WindowManager.LayoutParams.WRAP_CONTENT, Gravity.CENTER, R.style.dialog_animation2);
        TextView tv_cancel = (TextView) findViewById(R.id.tv_cancel);
        TextView tv_ok = (TextView) findViewById(R.id.tv_ok);
        ImageView image = (ImageView) findViewById(R.id.image);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        eventClick(tv_cancel, 1000).subscribe(o -> {
            dismiss();
            EventBus.getDefault().post(new Event.exitApp());
        }, throwable -> {
            Logger.getInstance().error(throwable);
        });
        eventClick(tv_ok, 2000).subscribe(o -> {
            dismiss();
            EventBus.getDefault().post(new Event.exitApp());
            startActivity(LoginActivity.class);
        }, throwable -> {
            Logger.getInstance().error(throwable);
        });
    }
    //<!-- 退出确定 >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>-->

    /**
     * 版本更新
     */
    private void upVersion() {
        initShowStyle(mContext.getResources().getDimensionPixelSize(R.dimen.x640), WindowManager.LayoutParams.WRAP_CONTENT, Gravity.CENTER, R.style.dialog_animation2);
        Bundle bundle = getArguments();
        String title = null;
        if (null != bundle) {
            title = bundle.getString("title");
        }
        TextView tv_tip = (TextView) findViewById(R.id.tv_tip);
        TextView tv_cancel = (TextView) findViewById(R.id.tv_cancel);
        TextView tv_ok = (TextView) findViewById(R.id.tv_ok);
        tv_tip.setText(title);
        eventClick(tv_cancel, 1000).subscribe(o -> dismiss());
        eventClick(tv_ok, 2000).subscribe(o -> {
            EventBus.getDefault().post(new Event.clickSureUpVersioin());
            dismiss();
        }, throwable -> {
            Logger.getInstance().error(throwable);
        });
    }

    /**
     * 下单交易密码验证
     */
    private void start_trade() {
        initShowStyle(mContext.getResources().getDimensionPixelSize(R.dimen.x640), WindowManager.LayoutParams.WRAP_CONTENT, Gravity.CENTER, R.style.dialog_animation2);
        EditText et_trade_psd = (EditText) findViewById(R.id.et_trade_psd);
        TextView rtv_cancel = (TextView) findViewById(R.id.tv_cancel);
        TextView rtv_ok = (TextView) findViewById(R.id.tv_ok);
        ImageView image = (ImageView) findViewById(R.id.image);
        ImageView iv_close_psw = (ImageView) findViewById(R.id.iv_close_psw);
        ImageView iv_clear_psw = (ImageView) findViewById(R.id.iv_clear_psw);
        iv_clear_psw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_trade_psd.setText("");
            }
        });
        iv_close_psw.setTag("hide");
        iv_close_psw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (iv_close_psw.getTag().toString().equals("show")) {
                    et_trade_psd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    iv_close_psw.setImageResource(R.mipmap.ico_eyes_close);
                    iv_close_psw.setTag("hide");
                } else {
                    et_trade_psd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    iv_close_psw.setImageResource(R.mipmap.ico_eyes_open);
                    iv_close_psw.setTag("show");
                    et_trade_psd.setSelection(et_trade_psd.getText().toString().length());
                }
                et_trade_psd.setSelection(et_trade_psd.getText().toString().length());
            }
        });
        et_trade_psd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().isEmpty()) {
                    iv_clear_psw.setVisibility(View.GONE);
                    iv_close_psw.setVisibility(View.GONE);
                } else {
                    iv_clear_psw.setVisibility(View.VISIBLE);
                    iv_close_psw.setVisibility(View.VISIBLE);
                }
            }
        });
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        int type = getArguments().getInt("type"); // 0 买入 1卖出
        int type1 = getArguments().getInt("type1"); // 区分来源 2为杠杆输入交易密码
        boolean isETF = getArguments().getBoolean("isETF");
        eventClick(rtv_cancel, 2000).subscribe(o -> dismiss(), throwable -> {
            Logger.getInstance().error(throwable);
        });
        eventClick(rtv_ok, 3000).subscribe(o -> {
            String trade_psd = et_trade_psd.getText().toString().trim();
            if (TextUtils.isEmpty(trade_psd)) {
                showMessage(getString(R.string.cc30), 2);
                return;
            }
            if (type1 == 0) {
                EventBus.getDefault().post(new Event.getStart_Trade_psd(type, trade_psd, isETF));
            } else if (type1 == 1) {
                EventBus.getDefault().post(new Event.getStart_Trade_(type, trade_psd, isETF));
            }
            dismiss();
        }, throwable -> {
            Logger.getInstance().error(throwable);
        });
    }

    /**
     * Dialog设置
     */
    private void initShowStyle(int width, int height, int gravity, int style) {
        Window window = getDialog().getWindow();
        assert window != null;
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = width;
        lp.height = height;
        lp.gravity = gravity;
        window.setWindowAnimations(style); // 添加动画
        window.setAttributes(lp);
    }

    public void onCancel(DialogInterface dialog) {
        if (currentStyle == AppConstants.TIMER.TOKEN_EXPIRED) {//处理TOKEN失效
            dismiss();
            EventBus.getDefault().post(new Event.exitApp());
        }
    }

    public void setOnClick(OnClick onClick) {
        this.onitemClick = onClick;
    }

    public interface OnClick {
        void onItemClick();
    }

}
