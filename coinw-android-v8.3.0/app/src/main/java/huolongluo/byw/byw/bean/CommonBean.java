package huolongluo.byw.byw.bean;


import java.io.Serializable;

/**
 * Created by 火龙裸 on 2018/1/4.
 */
public class CommonBean implements Serializable
{

    /**
     * result : true
     * code : 0
     * value : 操作成功
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

    @Override
    public String toString()
    {
        return "CommonBean{" + "result=" + result + ", code=" + code + ", value='" + value + '\'' + '}';
    }
}
