package huolongluo.byw.byw.bean;


import java.io.Serializable;

/**
 * <p>
 * Created by 火龙裸 on 2017/12/17 0017.
 */
public class ChangePwdBean implements Serializable
{

    /**
     * result : true
     * code : 0
     * value : 密码修改成功
     */

    private boolean result;
    private int code;
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

    public String getValue()
    {
        return value;
    }

    public void setValue(String value)
    {
        this.value = value;
    }
}
