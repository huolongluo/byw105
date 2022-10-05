package huolongluo.byw.byw.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/1/30.
 */
public class ShiMingInfoBean implements Serializable
{

    /**
     * result : true
     * code : 0
     * realName : 马圣群
     * identityNo : 320100199309240936
     * postRealValidate : true
     * postRealValidateTime : 2017-12-04 17:56:57
     */

    private boolean result;
    private int code;
    private String realName;
    private String identityNo;
    private boolean postRealValidate; // 实名认证是否通过
    private String postRealValidateTime;
    private String value;

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

    public String getRealName()
    {
        return realName;
    }

    public void setRealName(String realName)
    {
        this.realName = realName;
    }

    public String getIdentityNo()
    {
        return identityNo;
    }

    public void setIdentityNo(String identityNo)
    {
        this.identityNo = identityNo;
    }

    public boolean isPostRealValidate()
    {
        return postRealValidate;
    }

    public void setPostRealValidate(boolean postRealValidate)
    {
        this.postRealValidate = postRealValidate;
    }

    public String getPostRealValidateTime()
    {
        return postRealValidateTime;
    }

    public void setPostRealValidateTime(String postRealValidateTime)
    {
        this.postRealValidateTime = postRealValidateTime;
    }

    public String getValue()
    {
        return value;
    }

    public void setValue(String value)
    {
        this.value = value;
    }
}
