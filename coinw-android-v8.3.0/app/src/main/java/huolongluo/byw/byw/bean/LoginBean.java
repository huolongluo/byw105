package huolongluo.byw.byw.bean;


import java.io.Serializable;

/**
 * <p>
 * Created by 火龙裸 on 2017/12/15 0015.
 */
public class LoginBean implements Serializable
{
    /**
     * {
     "Fid": 223726,
     "code": 0,
     "isBindEmail": false,
     "isBindGoogle": true,
     "isBindTelephone": true,
     "isHasTradePWD": 1,
     "isLoginPassword": true,
     "isTradePassword": true,
     "loginToken": "A51F92BCB45D0323C165C1B1A778584D_1539246445601_223726",
     "postRealValidate": true,
     "result": true,
     "value": "登陆成功",
     "vip": 1
     }
     */
    private boolean result;
    private int code;
    private String value;
    private String loginToken;
    private boolean postRealValidate; // 是否提交身份验证(true, false)
    private int isHasTradePWD;
    private boolean isBindGoogle;
    private boolean isBindTelephone;
    private boolean isBindEmail;
    private boolean isTradePassword;
    private boolean isLoginPassword;
    private int vip;
    private long Fid;

    public boolean isResult()
    {
        return result;
    }

    public void setResult(boolean result)
    {
        this.result = result;
    }

    public int getCode()
    {
        return code;
    }

    public void setCode(int code)
    {
        this.code = code;
    }

    public String getValue()
    {
        return value;
    }

    public void setValue(String value)
    {
        this.value = value;
    }

    public String getLoginToken()
    {
        return loginToken;
    }

    public void setLoginToken(String loginToken)
    {
        this.loginToken = loginToken;
    }

    public boolean isPostRealValidate()
    {
        return postRealValidate;
    }

    public void setPostRealValidate(boolean postRealValidate)
    {
        this.postRealValidate = postRealValidate;
    }

    public int getIsHasTradePWD()
    {
        return isHasTradePWD;
    }

    public void setIsHasTradePWD(int isHasTradePWD)
    {
        this.isHasTradePWD = isHasTradePWD;
    }

    public boolean isIsBindGoogle()
    {
        return isBindGoogle;
    }

    public void setIsBindGoogle(boolean isBindGoogle)
    {
        this.isBindGoogle = isBindGoogle;
    }

    public boolean isIsBindTelephone()
    {
        return isBindTelephone;
    }

    public void setIsBindTelephone(boolean isBindTelephone)
    {
        this.isBindTelephone = isBindTelephone;
    }

    public boolean isIsBindEmail()
    {
        return isBindEmail;
    }

    public void setIsBindEmail(boolean isBindEmail)
    {
        this.isBindEmail = isBindEmail;
    }

    public boolean isIsTradePassword()
    {
        return isTradePassword;
    }

    public void setIsTradePassword(boolean isTradePassword)
    {
        this.isTradePassword = isTradePassword;
    }

    public boolean isIsLoginPassword()
    {
        return isLoginPassword;
    }

    public void setIsLoginPassword(boolean isLoginPassword)
    {
        this.isLoginPassword = isLoginPassword;
    }

    public int getVip()
    {
        return vip;
    }

    public void setVip(int vip)
    {
        this.vip = vip;
    }

    public long getFid()
    {
        return Fid;
    }

    public void setFid(long Fid)
    {
        this.Fid = Fid;
    }

    @Override
    public String toString() {
        return "LoginBean{" +
                "result=" + result +
                ", code=" + code +
                ", value='" + value + '\'' +
                ", loginToken='" + loginToken + '\'' +
                ", postRealValidate=" + postRealValidate +
                ", isHasTradePWD=" + isHasTradePWD +
                ", isBindGoogle=" + isBindGoogle +
                ", isBindTelephone=" + isBindTelephone +
                ", isBindEmail=" + isBindEmail +
                ", isTradePassword=" + isTradePassword +
                ", isLoginPassword=" + isLoginPassword +
                ", vip=" + vip +
                ", Fid=" + Fid +
                '}';
    }
}
