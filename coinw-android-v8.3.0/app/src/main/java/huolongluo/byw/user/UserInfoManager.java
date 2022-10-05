package huolongluo.byw.user;
import android.text.TextUtils;

import com.liuzhongjun.videorecorddemo.util.CameraApp;

import java.net.URLEncoder;

import huolongluo.byw.byw.base.BaseApp;
import huolongluo.byw.byw.bean.LoginBean;
import huolongluo.byw.byw.bean.SafeCentreBean;
import huolongluo.byw.byw.bean.UserInfoBean;
import huolongluo.byw.byw.net.DomainHelper;
import huolongluo.byw.byw.net.UrlConstants;
import huolongluo.byw.io.AppConstants;
import huolongluo.byw.reform.c2c.oct.bean.OtcUserInfoBean;
import huolongluo.byw.util.FingerprintUtil;
import huolongluo.byw.util.RSACipher;
import huolongluo.byw.util.SPUtils;
/**
 * Created by hy on 2018/9/30 0030.
 */
public class UserInfoManager {
    private UserInfoManager() {
    }

    public static UserInfoManager getDeaflt() {
        if (infoManager == null) {
            synchronized (UserInfoManager.class) {
                if (infoManager == null) {
                    infoManager = new UserInfoManager();
                }
            }
        }
        return infoManager;
    }

    private static UserInfoManager infoManager;
    private static LoginBean loginBean;//登录返回的信息
    private static UserInfoBean UserInfoBean = null; //获取用户信息接口返回的信息，比登录更齐全
    static SafeCentreBean safeCentreBean; //安全相关信息
    static String token; //loginToken
    private static OtcUserInfoBean otcUserInfoBean;//otc个人信息
    private static boolean enableExchangeMer = false; //是否允许买币兑换

    public static OtcUserInfoBean getOtcUserInfoBean() {
        return otcUserInfoBean;
    }

    public static void setOtcUserInfoBean(OtcUserInfoBean otcUserInfoBean) {
        UserInfoManager.otcUserInfoBean = otcUserInfoBean;
    }

    public static boolean isLogin() {
        if (TextUtils.isEmpty(token)) {
            setToken(SPUtils.getLoginToken());
        }
        return !TextUtils.isEmpty(getToken());
    }

    public static huolongluo.byw.byw.bean.UserInfoBean getUserInfo() {
        if (UserInfoBean != null) {
            return UserInfoBean;
        } else {
            return new UserInfoBean();
        }
    }

    public static void setUserInfoBean(huolongluo.byw.byw.bean.UserInfoBean userInfoBean) {
        UserInfoBean = (huolongluo.byw.byw.bean.UserInfoBean)userInfoBean.clone();
    }

    public SafeCentreBean getSafeCentreBean() {
        return safeCentreBean;
    }

    public void setSafeCentreBean(SafeCentreBean safeCentreBean) {
        UserInfoManager.safeCentreBean = safeCentreBean;
    }

    public static String getToken() {
        if (TextUtils.isEmpty(token)) {
            setToken(SPUtils.getLoginToken());
        }
        //优化数据处理
        CameraApp.token = token;
        CameraApp.deviceId = FingerprintUtil.getFingerprint(BaseApp.getSelf());
        CameraApp.body = getEnCodeToken(token);
        CameraApp.host = DomainHelper.getDomain().getHost();
        if (null != UserInfoBean) {
            CameraApp.identityNo = UserInfoBean.getIdentityNo();
            CameraApp.realName = UserInfoBean.getRealName();
        }
        return token;
    }

    public static String getEnCodeToken(String token) {
        String body1 = "";
        try {
            RSACipher rsaCipher = new RSACipher();
            String body = "message=" + URLEncoder.encode(token);
            body1 = rsaCipher.encrypt(body, AppConstants.KEY.PUBLIC_KEY);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return body1;
    }

    public static void setToken(String toke) {
        token = toke;
    }

    public static void clearUser() {
        token = null;
        UserInfoBean = null;
        loginBean = null;
        safeCentreBean = null;
    }

    public static boolean isEnableExchangeMer() {
        return enableExchangeMer;
    }

    public static void setEnableExchangeMer(boolean enableExchangeMer) {
        UserInfoManager.enableExchangeMer = enableExchangeMer;
    }
}
