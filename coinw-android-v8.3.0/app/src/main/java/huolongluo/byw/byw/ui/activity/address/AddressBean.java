package huolongluo.byw.byw.ui.activity.address;


import java.util.List;

/**
 * Created by LS on 2018/7/11.
 */
public class AddressBean {
    private boolean result;
    private int code;
    private List<AddressListBean> fvirtualcointype;

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


    public List<AddressListBean> getFvirtualcointype() {
        return fvirtualcointype;
    }

    public void setFvirtualcointype(List<AddressListBean> fvirtualcointype) {
        this.fvirtualcointype = fvirtualcointype;
    }

    @Override
    public String toString() {
        return "AddressBean{" +
                "result=" + result +
                ", code='" + code + '\'' +
                ", fvirtualcointype=" + fvirtualcointype +
                '}';
    }
}
