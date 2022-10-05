package huolongluo.byw.byw.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/1/30.
 */
public class SafeCentreBean implements Serializable
{

    /**
     * googleTXT : 未绑定
     * isBindGoogle : false
     * isBindTelephone : true
     * telNumberTXT : 180187****
     * isBindEmail : true
     * emailTXT : 2277497****qq.com
     * isTradePassword : true
     * tradePaswordTXT : ******
     * isLoginPassword : true
     * loginPaswordTXT : ******
     */

    private String googleTXT;
    private boolean isBindGoogle;
    private boolean isBindTelephone;
    private String telNumberTXT;
    private boolean isBindEmail;
    private String emailTXT;
    private boolean isTradePassword;
    private String tradePaswordTXT;
    private boolean isLoginPassword;
    private String loginPaswordTXT;
    
    private boolean result;
    private int code;
    private String value;

    public String getGoogleTXT()
    {
        return googleTXT;
    }

    public void setGoogleTXT(String googleTXT)
    {
        this.googleTXT = googleTXT;
    }

    public boolean getIsBindGoogle()
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

    public String getTelNumberTXT()
    {
        return telNumberTXT;
    }

    public void setTelNumberTXT(String telNumberTXT)
    {
        this.telNumberTXT = telNumberTXT;
    }

    public boolean isIsBindEmail()
    {
        return isBindEmail;
    }

    public void setIsBindEmail(boolean isBindEmail)
    {
        this.isBindEmail = isBindEmail;
    }

    public String getEmailTXT()
    {
        return emailTXT;
    }

    public void setEmailTXT(String emailTXT)
    {
        this.emailTXT = emailTXT;
    }

    public boolean isIsTradePassword()
    {
        return isTradePassword;
    }

    public void setIsTradePassword(boolean isTradePassword)
    {
        this.isTradePassword = isTradePassword;
    }

    public String getTradePaswordTXT()
    {
        return tradePaswordTXT;
    }

    public void setTradePaswordTXT(String tradePaswordTXT)
    {
        this.tradePaswordTXT = tradePaswordTXT;
    }

    public boolean isIsLoginPassword()
    {
        return isLoginPassword;
    }

    public void setIsLoginPassword(boolean isLoginPassword)
    {
        this.isLoginPassword = isLoginPassword;
    }

    public String getLoginPaswordTXT()
    {
        return loginPaswordTXT;
    }

    public void setLoginPaswordTXT(String loginPaswordTXT)
    {
        this.loginPaswordTXT = loginPaswordTXT;
    }

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

    @Override
    public String toString() {
        return "SafeCentreBean{" +
                "googleTXT='" + googleTXT + '\'' +
                ", isBindGoogle=" + isBindGoogle +
                ", isBindTelephone=" + isBindTelephone +
                ", telNumberTXT='" + telNumberTXT + '\'' +
                ", isBindEmail=" + isBindEmail +
                ", emailTXT='" + emailTXT + '\'' +
                ", isTradePassword=" + isTradePassword +
                ", tradePaswordTXT='" + tradePaswordTXT + '\'' +
                ", isLoginPassword=" + isLoginPassword +
                ", loginPaswordTXT='" + loginPaswordTXT + '\'' +
                ", result=" + result +
                ", code=" + code +
                ", value='" + value + '\'' +
                '}';
    }
}
