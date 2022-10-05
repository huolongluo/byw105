package huolongluo.byw.byw.ui.fragment.maintab05.bean;

import java.util.List;

/**
 * Created by LS on 2018/7/19.
 */

public class ShanghuBean {
    private boolean result;
    private int unfinished;
    private int cancellations;
    private int code;
    private List<ShanghuBeanList> proxyrechargeList;
    private List<UserWalletBean> userWallet;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public int getUnfinished() {
        return unfinished;
    }

    public void setUnfinished(int unfinished) {
        this.unfinished = unfinished;
    }

    public int getCancellations() {
        return cancellations;
    }

    public void setCancellations(int cancellations) {
        this.cancellations = cancellations;
    }

    public List<ShanghuBeanList> getProxyrechargeList() {
        return proxyrechargeList;
    }

    public void setProxyrechargeList(List<ShanghuBeanList> proxyrechargeList) {
        this.proxyrechargeList = proxyrechargeList;
    }
//
    public List<UserWalletBean> getUserWallet() {
        return userWallet;
    }

    public void setUserWallet(List<UserWalletBean> userWallet) {
        this.userWallet = userWallet;
    }
}
