package huolongluo.byw.model.result;

import huolongluo.byw.model.MarketResult;

import java.io.Serializable;
import java.util.List;

/**
 * 多条数据集合
 */
public class ListResult<T> implements Serializable {

    /**
     * 服务器返回的json数据
     */
    public String message;
    public String code;
    public SubListResult<T> data = new SubListResult<T>();

    public static class SubListResult<T> implements Serializable {

        public String value;
        public int code;
        public T data;
    }
}
