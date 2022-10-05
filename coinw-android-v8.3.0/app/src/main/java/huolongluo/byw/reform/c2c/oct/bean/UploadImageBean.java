package huolongluo.byw.reform.c2c.oct.bean;

/**
 * Created by dell on 2019/6/6.
 */

public class UploadImageBean {
    /**
     * code : 0
     * result : true
     * url : https://btc018.oss-cn-shenzhen.aliyuncs.com/201906061643043_Xg56G.zhifubao.png
     * value : 上传成功
     */

    private int code;
    private boolean result;
    private String url;
    private String value;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
