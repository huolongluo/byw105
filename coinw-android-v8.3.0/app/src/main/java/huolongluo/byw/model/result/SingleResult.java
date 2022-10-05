package huolongluo.byw.model.result;

/**
 * 单条数据集合
 */
public class SingleResult<T>extends Result {

    /**
     * 服务器返回的json数据
     */
    public T data;
}
