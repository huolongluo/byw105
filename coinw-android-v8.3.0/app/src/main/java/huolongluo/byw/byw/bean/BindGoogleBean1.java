package huolongluo.byw.byw.bean;


import java.io.Serializable;

/**
 * Created by 火龙裸先生 on 2018/2/1.
 */
public class BindGoogleBean1 implements Serializable
{

    /**
     * device_name : COINW:2277497550@qq.com
     * code : 0
     * totpKey : PAIQXS4IRSQEGB7R
     */

    private String device_name;
    private int code;
    private String totpKey;
    private String value;

    public String getDevice_name()
    {
        return device_name;
    }

    public void setDevice_name(String device_name)
    {
        this.device_name = device_name;
    }

    public int getCode()
    {
        return code;
    }

    public void setCode(int code)
    {
        this.code = code;
    }

    public String getTotpKey()
    {
        return totpKey;
    }

    public void setTotpKey(String totpKey)
    {
        this.totpKey = totpKey;
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
