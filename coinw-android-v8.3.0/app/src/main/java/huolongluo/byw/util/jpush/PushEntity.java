package huolongluo.byw.util.jpush;

public class PushEntity {

    /**
     * h5 : http://www.baidu.com
     */

    private String h5;
    private String app;
    private String left;//左币名称，需要切换币对才有，否则为null
    private String right;

    public String getLeft() {
        return left;
    }

    public void setLeft(String left) {
        this.left = left;
    }

    public String getRight() {
        return right;
    }

    public void setRight(String right) {
        this.right = right;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public String getH5() {
        return h5;
    }

    public void setH5(String h5) {
        this.h5 = h5;
    }
}
