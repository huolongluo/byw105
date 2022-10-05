package huolongluo.byw.model.result;

import java.util.List;

/**
 * 多条数据集合
 */
public class MultiResult<T>extends Result {

    /**
     * 服务器返回的json数据
     */
    public List<T> data;
}
