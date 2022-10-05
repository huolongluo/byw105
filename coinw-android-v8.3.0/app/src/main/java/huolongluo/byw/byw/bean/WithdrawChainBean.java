package huolongluo.byw.byw.bean;


import java.io.Serializable;

/**
 * <p>
 * Created by 火龙裸 on 2018/1/4 0004.
 */
public class WithdrawChainBean implements Serializable {
    /**
     * id : 82394
     * remark : zheshi beizhu
     * address : 3ewq41
     */

    private int id;
    private String remark;
    private String address;
    private String chainName;

    public boolean getIsInternalAddress() {
        return isInternalAddress;
    }

    public void setIsInternalAddress(boolean internalAddress) {
        isInternalAddress = internalAddress;
    }

    private boolean isInternalAddress;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getChainName() {
        return chainName;
    }

    public void setChainName(String chainName) {
        this.chainName = chainName;
    }
}
