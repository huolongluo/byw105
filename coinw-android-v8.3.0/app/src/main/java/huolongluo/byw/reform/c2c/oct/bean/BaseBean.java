package huolongluo.byw.reform.c2c.oct.bean;

/**
 * Created by dell on 2019/6/5.
 */

public class BaseBean {
    /**
     * code : 0
     * data : 设置成功
     * result : true
     * value : 操作成功
     */

    private int code;
    private String data;
    private boolean result;
    private String value;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
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
}
