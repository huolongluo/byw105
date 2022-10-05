package huolongluo.byw.byw.bean;
import android.text.TextUtils;


import java.io.Serializable;

import huolongluo.byw.log.Logger;
import huolongluo.byw.util.RSACipher;
/**
 * <p>
 * Created by 火龙裸 on 2017/12/30 0030.
 */
public class UserInfoBean implements Serializable, Cloneable {
    /**
     * {
     * "code": 0,
     * "result": true,
     * "userInfo": {
     * "fid": 67351,
     * "hasC2Validate": true,
     * "idDeductible": false,
     * "identityNo": "341282199103017610",
     * "identityType": 0,
     * "isBindMobil": true,
     * "isGoogleBind": true,
     * "isHasTradePWD": 1,
     * "isSecondValidate": false,
     * "loginName": "18655056179",
     * "nickName": "18655056179",
     * "postC2Validate": true,
     * "realName": "王海洋",
     * "tel": "18655056179",
     * "vip": 1
     * }
     * }
     * <p>
     * loginName;//新加/用于网页远程登录
     */
    private String loginToken;
    private int code;
    private String value;
    private int vip;
    private String nickName;
    private boolean isGoogleBind;
    private boolean isSecondValidate;
    private int fid;
    private String realName;
    private boolean isBindMobil;
    private boolean isBindEmail;
    private String tel;
    private int isHasTradePWD;
    private boolean idDeductible;
    private boolean postC2Validate;
    private boolean hasC2Validate;
    private boolean hasC3Validate;//高级认证
    private String identityNo;
    private int identityType;//证件类型(0 身份证 1 军官证 2 护照 3 台湾 4 港澳 5 海外ID
    private int nationality;//国籍，中国1，外国0
    private String ksTokenUrl;//旷视地址
    private String fisForbidWithdraw;//是否禁止提币 0.可以提币， 1.禁止提币
    private boolean isHBTPartner;//是否是HBT合伙人

    public int getC3status() {
        return c3status;
    }

    public void setC3status(int c3status) {
        this.c3status = c3status;
    }

    private int c3status;//0null-未提交，1 - 已提交(审核中)  2表示审核不通过
    private String loginName;//新加/用于网页远程登录
    private boolean isNewComer;

    public UserInfoBean() {
    }

    public UserInfoBean(UserInfoBean userInfoBean) {
        this.loginToken = userInfoBean.getLoginToken();
        this.code = userInfoBean.getCode();
        this.value = userInfoBean.getValue();
        this.vip = userInfoBean.getVip();
        this.nickName = userInfoBean.getNickName();
        this.isGoogleBind = userInfoBean.isGoogleBind();
        this.isSecondValidate = userInfoBean.isSecondValidate();
        this.fid = userInfoBean.getFid();
        this.realName = userInfoBean.getRealName();
        this.isBindMobil = userInfoBean.isBindMobil();
        this.isBindEmail = userInfoBean.isBindEmail();
        this.tel = userInfoBean.tel;
        this.isHasTradePWD = userInfoBean.isHasTradePWD;
        this.idDeductible = userInfoBean.isIdDeductible();
        this.postC2Validate = userInfoBean.isPostC2Validate();
        this.hasC2Validate = userInfoBean.isHasC2Validate();
        this.identityNo = userInfoBean.getIdentityNo();
        this.identityType = userInfoBean.getIdentityType();
        this.loginName = userInfoBean.getLoginName();
        this.hasC3Validate = userInfoBean.isHasC3Validate();
        this.c3status = userInfoBean.getC3status();
        this.ksTokenUrl = userInfoBean.getKsTokenUrl();
        this.fisForbidWithdraw = userInfoBean.getFisForbidWithdraw();
        this.isHBTPartner = userInfoBean.getIsHBTPartner();
        this.isNewComer = userInfoBean.isNewComer();
    }

    public String getLoginToken() {
        return loginToken;
    }

    public void setLoginToken(String loginToken) {
        this.loginToken = loginToken;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getVip() {
        return vip;
    }

    public void setVip(int vip) {
        this.vip = vip;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public boolean isGoogleBind() {
        return isGoogleBind;
    }

    public void setGoogleBind(boolean googleBind) {
        isGoogleBind = googleBind;
    }

    public boolean isSecondValidate() {
        return isSecondValidate;
    }

    public void setSecondValidate(boolean secondValidate) {
        isSecondValidate = secondValidate;
    }

    public int getFid() {
        return fid;
    }

    public void setFid(int fid) {
        this.fid = fid;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public boolean isBindMobil() {
        return isBindMobil;
    }

    public void setBindMobil(boolean bindMobil) {
        isBindMobil = bindMobil;
    }

    public boolean isBindEmail() {
        return isBindEmail;
    }

    public void setBindEmail(boolean bindEmail) {
        isBindEmail = bindEmail;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public int getIsHasTradePWD() {
        return isHasTradePWD;
    }

    public void setIsHasTradePWD(int isHasTradePWD) {
        this.isHasTradePWD = isHasTradePWD;
    }

    public boolean isIdDeductible() {
        return idDeductible;
    }

    public void setIdDeductible(boolean idDeductible) {
        this.idDeductible = idDeductible;
    }

    public boolean isPostC2Validate() {
        return postC2Validate;
    }

    public void setPostC2Validate(boolean postC2Validate) {
        this.postC2Validate = postC2Validate;
    }

    public boolean isHasC2Validate() {
        return hasC2Validate;
    }

    public void setHasC2Validate(boolean hasC2Validate) {
        this.hasC2Validate = hasC2Validate;
    }

    public String getIdentityNo() {
//        return identityNo;
        if (TextUtils.isEmpty(identityNo)) {
            return "";
        }
        try {
            //说明为密文
            if (identityNo.length() > 18) {
                return new RSACipher().decryptfd(identityNo);
            }
        } catch (Throwable t) {
            Logger.getInstance().error(t);
        }
        return identityNo;
    }

    public void setIdentityNo(String identityNo) {
        this.identityNo = identityNo;
    }

    public int getIdentityType() {
        return identityType;
    }

    public void setIdentityType(int identityType) {
        this.identityType = identityType;
    }

    public String getLoginName() {
//        try {
//            return new RSACipher().decryptfd(loginName);
//        } catch (Throwable t) {
//            Logger.getInstance().error(t);
//        }
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getKsTokenUrl() {
        return ksTokenUrl;
    }

    public void setKsTokenUrl(String ksTokenUrl) {
        this.ksTokenUrl = ksTokenUrl;
    }

    public String getFisForbidWithdraw() {
        return fisForbidWithdraw;
    }

    public void setFisForbidWithdraw(String fisForbidWithdraw) {
        this.fisForbidWithdraw = fisForbidWithdraw;
    }

    public boolean getIsHBTPartner() {
        return isHBTPartner;
    }

    public void setIsHBTPartner(boolean isHBTPartner) {
        this.isHBTPartner = isHBTPartner;
    }

    @Override
    public String toString() {
        return "UserInfoBean{" + "loginToken='" + loginToken + '\'' + ", code=" + code + ", value='" + value + '\'' + ", vip=" + vip + ", nickName='" + nickName + '\'' + ", isGoogleBind=" + isGoogleBind + ", isSecondValidate=" + isSecondValidate + ", fid=" + fid + ", realName='" + realName + '\'' + ", isBindMobil=" + isBindMobil + ", isBindEmail=" + isBindEmail + ", tel='" + tel + '\'' + ", isHasTradePWD=" + isHasTradePWD + ", idDeductible=" + idDeductible + ", postC2Validate=" + postC2Validate + ", hasC2Validate=" + hasC2Validate + ", identityNo='" + identityNo + '\'' + ", identityType=" + identityType + ", loginName='" + loginName + '\'' + ", isNewComer='" + isNewComer +  '}';
    }

    public boolean isHasC3Validate() {
        return hasC3Validate;
    }

    public void setHasC3Validate(boolean hasC3Validate) {
        this.hasC3Validate = hasC3Validate;
    }

    public int getNationality() {
        return nationality;
    }

    public void setNationality(int nationality) {
        this.nationality = nationality;
    }

    @Override
    public Object clone() {
        UserInfoBean uib = null;
        try {
            uib = (UserInfoBean) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        if (uib == null) {
            uib = new UserInfoBean();
        }
        return uib;
    }

    public boolean isNewComer() {
        return isNewComer;
    }

    public void setNewComer(boolean newComer) {
        isNewComer = newComer;
    }
}
