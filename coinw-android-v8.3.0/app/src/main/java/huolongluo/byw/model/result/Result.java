package huolongluo.byw.model.result;

import java.io.Serializable;

/**
 * 多条数据集合
 */
public class Result implements Serializable {

    /**
     * 服务器返回的json数据
     */
    public String message;
    public String value;
    public String code;
    public int forceUpdate;
    public String msg;
    public boolean success;
    public boolean retry;
}
