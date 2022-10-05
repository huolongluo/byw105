package huolongluo.byw.byw.bean;

import java.io.Serializable;

/**
 * <p>
 * Created by 火龙裸 on 2017/12/16 0016.
 */
public class RegisterBean implements Serializable
{

    /**
     * result : true
     * code : 0
     * value : 注册成功
     * loginToken : 52C805CB7B17EACAC3302BD4959567D9_1513438709663_97296
     * postRealValidate : false
     * Fid : 97296
     */

    private boolean result;
    private int code;
    private String value;
    private String loginToken;
    private boolean postRealValidate;
    private long Fid;

     private  UserInfoBean userInfo;

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

    public long getFid()
    {
        return Fid;
    }

    public void setFid(long Fid)
    {
        this.Fid = Fid;
    }

    public UserInfoBean getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfoBean userInfo) {
        this.userInfo = userInfo;
    }

    @Override
    public String toString() {
        return "RegisterBean{" +
                "result=" + result +
                ", code=" + code +
                ", value='" + value + '\'' +
                ", loginToken='" + loginToken + '\'' +
                ", postRealValidate=" + postRealValidate +
                ", Fid=" + Fid +
                ", userInfo=" + userInfo +
                '}';
    }
}
