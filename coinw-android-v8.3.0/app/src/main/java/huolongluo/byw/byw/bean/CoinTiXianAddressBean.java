package huolongluo.byw.byw.bean;


import java.io.Serializable;
import java.util.List;

/**
 * Created by 火龙裸 on 2018/1/3.
 */
public class CoinTiXianAddressBean implements Serializable {
    /**
     * result : true
     * list : [{"id":82394,"remark":"zheshi beizhu","address":"3ewq41"}]
     * fee_list : [{"level":1,"fee":0.003},{"level":2,"fee":0.003},{"level":3,"fee":0.003},{"level":4,"fee":0.003},{"level":5,"fee":0.003},{"level":6,
     * "fee":0.003},{"level":7,"fee":0.003},{"level":8,"fee":0.003},{"level":9,"fee":0.003},{"level":10,"fee":0.003}]
     */
    private boolean hasSecond;
    private boolean result;
    private String tokenName;
    private String tokenName2;
    private String tokenName3;
    private List<WithdrawChainBean> list;
    private List<FeeListBean> fee_list;
    private int code;

    public String getTokenName() {
        return tokenName;
    }

    public void setTokenName(String tokenName) {
        this.tokenName = tokenName;
    }

    public String getTokenName2() {
        return tokenName2;
    }

    public void setTokenName2(String tokenName2) {
        this.tokenName2 = tokenName2;
    }

    public void setTokenName3(String tokenName3) {
        this.tokenName3 = tokenName3;
    }

    public String getTokenName3() {
        return tokenName3;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public boolean isHasSecond() {
        return hasSecond;
    }

    public void setHasSecond(boolean hasSecond) {
        this.hasSecond = hasSecond;
    }

    public List<WithdrawChainBean> getList() {
        return list;
    }

    public void setList(List<WithdrawChainBean> list) {
        this.list = list;
    }

    public List<FeeListBean> getFee_list() {
        return fee_list;
    }

    public void setFee_list(List<FeeListBean> fee_list) {
        this.fee_list = fee_list;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "CoinTiXianAddressBean{" + "result=" + result + ", list=" + list + ", fee_list=" + fee_list + ", code=" + code + '}';
    }
}
