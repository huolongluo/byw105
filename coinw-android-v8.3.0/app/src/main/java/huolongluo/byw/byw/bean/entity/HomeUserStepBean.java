package huolongluo.byw.byw.bean.entity;

public class HomeUserStepBean {
    private boolean result;
    private int code;
    private int status;

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "HomeUserStepBean{" +
                "result=" + result +
                ", code='" + code + '\'' +
                ", status=" + status +
                '}';
    }
}
