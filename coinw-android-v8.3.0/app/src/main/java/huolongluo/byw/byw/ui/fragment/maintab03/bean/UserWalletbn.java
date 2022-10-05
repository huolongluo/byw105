package huolongluo.byw.byw.ui.fragment.maintab03.bean;


import java.util.List;

/**
 * Created by hy on 2018/10/19 0019.
 */
public class UserWalletbn {

      public  int code;
      public String value;
      public double totalAsset;
    public String totalAssetExchange;
      public List<AssetCoinsBean> userWallet;

    public String getTotalAssetExchange() {
        return totalAssetExchange;
    }

    public void setTotalAssetExchange(String totalAssetExchange) {
        this.totalAssetExchange = totalAssetExchange;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public double getTotalAsset() {
        return totalAsset;
    }

    public void setTotalAsset(double totalAsset) {
        this.totalAsset = totalAsset;
    }

    public List<AssetCoinsBean> getUserWallet() {
        return userWallet;
    }

    public void setUserWallet(List<AssetCoinsBean> userWallet) {
        this.userWallet = userWallet;
    }



    @Override
    public String toString() {
        return "UserWalletbn{" +
                "code=" + code +
                ", value='" + value + '\'' +
                ", totalAsset=" + totalAsset +
                ", userWallet=" + userWallet +
                '}';
    }
}
