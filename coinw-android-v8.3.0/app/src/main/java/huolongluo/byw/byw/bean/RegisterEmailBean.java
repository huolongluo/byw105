package huolongluo.byw.byw.bean;


import java.io.Serializable;

/**
 * <p>
 * Created by 火龙裸 on 2017/12/16 0016.
 */
public class RegisterEmailBean implements Serializable
{

    /**
     * result : 0
     * value : 验证码已经发送，请查收
     */

    private int result;
    private String value;

    public int getResult()
    {
        return result;
    }

    public void setResult(int result)
    {
        this.result = result;
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
        return "RegisterEmailBean{" +
                "result=" + result +
                ", value='" + value + '\'' +
                '}';
    }
}
