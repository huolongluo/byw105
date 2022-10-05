package huolongluo.bywx.helper;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewConfiguration;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.widget.PopupWindow;

import com.just.agentweb.AgentWeb;

import huolongluo.byw.log.Logger;
import huolongluo.byw.user.UserInfoManager;
import rx.Subscription;

public class AppHelper {
    public static void dismissDialog(Dialog dialog) {
        if (dialog == null) {
            return;
        }
        if (isDestroy(dialog.getContext())) {
            return;
        }
        if (dialog.isShowing()) {
            try {
                dialog.dismiss();
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

    //dismissDialog打印日志会不断执行，使用dismissDialog2取代
    public static void dismissDialog2(Dialog dialog) {
        if (dialog == null) {
            return;
        }
        if (isDestroy(dialog.getContext())) {
            return;
        }
        try {
            dialog.dismiss();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public static void dismissPopupWindow(PopupWindow popupWindow) {
        if (popupWindow == null) {
            return;
        }
        try {
            if (popupWindow.isShowing()) {
                popupWindow.dismiss();
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public static void clearCookies(Context context) {
        try {
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.removeAllCookie();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public static void distoryWebView(final WebView webView) {
        if (webView == null) {
            return;
        }
        try {
            clearCookies(webView.getContext());
            webView.getSettings().setBuiltInZoomControls(true);
            webView.getSettings().setDisplayZoomControls(false);
            webView.setVisibility(View.GONE);
            long timeout = ViewConfiguration.getZoomControlsTimeout();//timeout ==3000
            webView.postDelayed(() -> {
                try {
                    if (webView != null) {
                        webView.destroy();
                    }
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }, timeout);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public static void distoryWebView(final AgentWeb agentWeb) {
        if (agentWeb == null || agentWeb.getWebCreator() == null || agentWeb.getWebCreator().get() == null) {
            return;
        }
        try {
            clearCookies(agentWeb.getWebCreator().get().getContext());
            agentWeb.getWebCreator().get().setVisibility(View.GONE);
            agentWeb.getWebCreator().get().getSettings().setDisplayZoomControls(false);
            long timeout = ViewConfiguration.getZoomControlsTimeout();//timeout ==3000
            agentWeb.getWebCreator().get().postDelayed(() -> {
                try {
                    if (agentWeb != null) {
                        agentWeb.destroy();
                    }
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }, timeout);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    //判断Activity是否Destroy
    public static boolean isDestroy(Activity atv) {
        return atv == null || atv.isFinishing() || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && atv.isDestroyed());
    }

    public static boolean isDestroy(Context context) {
        if (context instanceof Activity) {
            return isDestroy((Activity) context);
        }
        return false;
    }

    public static void release(Handler handler) {
        if (handler == null) {
            return;
        }
        try {
            Logger.getInstance().debug("release handler!");
            handler.removeCallbacksAndMessages(null);
        } catch (Throwable t) {
            Logger.getInstance().error(t);
        }
    }

    public static boolean isNoWithdrawal() {//是否禁止提币或划转
        //JIRA:COIN-1721
        //业务控制条件：是否禁止提币 0.可以提币， 1.禁止提币
        String fisForbidWithdraw = UserInfoManager.getUserInfo().getFisForbidWithdraw();
        if (TextUtils.equals("1", fisForbidWithdraw)) {
            return false;
        }
        return true;
    }

    public static void setSafeBrowsingEnabled(WebView webView) {
        try {
            if (webView == null) {
                return;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                webView.getSettings().setSafeBrowsingEnabled(false);
            }
        } catch (Throwable t) {
            Logger.getInstance().error(t);
        }
    }

    public static void unsubscribe(Subscription subscription) {
        if (subscription == null) {
            return;
        }
        try {
            if (subscription.isUnsubscribed()) {
                return;
            }
            subscription.unsubscribe();
        } catch (Throwable t) {
            Logger.getInstance().error(t);
        }
    }
}
