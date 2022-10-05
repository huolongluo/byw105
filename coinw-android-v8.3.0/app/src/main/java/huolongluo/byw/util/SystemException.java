package huolongluo.byw.util;

/**
 * Created by Administrator on 2018/12/25 0025.
 */

public class SystemException extends Exception {
    static final long serialVersionUID = 7818375828146090157L;
    public SystemException(String message) {
        super(message);
    }

    public SystemException(String message, Throwable cause) {
        super(message, cause);
    }

    public SystemException(Throwable cause) {
        super(cause);
    }
}
