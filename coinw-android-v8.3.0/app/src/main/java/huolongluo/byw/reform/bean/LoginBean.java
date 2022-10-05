package huolongluo.byw.reform.bean;


import java.io.Serializable;

import huolongluo.byw.byw.bean.UserInfoBean;
import huolongluo.byw.user.UserInfoManager;

/**
 * Created by Administrator on 2019/1/9 0009.
 */
public class LoginBean implements Serializable {

    private int code;

    private  String loginToken;
    private    boolean result;
    String value;

    private String random;

    UserInfoBean userInfo;
    boolean area_available=true;//区域不可用显示提示弹窗

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getLoginToken() {
        return loginToken;
    }

    public boolean isArea_available() {
        return area_available;
    }

    public void setArea_available(boolean area_available) {
        this.area_available = area_available;
    }

    public void setLoginToken(String loginToken) {
        this.loginToken = loginToken;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public UserInfoBean getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfoBean userInfo) {
        this.userInfo = userInfo;
    }

    public String getRandom() {
        return random;
    }

    public void setRandom(String random) {
        this.random = random;
    }
}
