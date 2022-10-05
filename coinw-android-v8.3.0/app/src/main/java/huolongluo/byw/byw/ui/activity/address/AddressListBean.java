package huolongluo.byw.byw.ui.activity.address;


import java.util.List;

/**
 * Created by LS on 2018/7/11.
 */
public class AddressListBean {
    // 显示数据拼音的首字母
    public String sortLetters;
    private List<AddressList1Bean> coinAddress;
    private String cnName;
    private String shortName;
    private int id;




    public List<AddressList1Bean> getCoinAddress() {
        return coinAddress;
    }

    public void setCoinAddress(List<AddressList1Bean> coinAddress) {
        this.coinAddress = coinAddress;
    }

    public String getCnName() {
        return cnName;
    }

    public void setCnName(String cnName) {
        this.cnName = cnName;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSortLetters() {
        return sortLetters;
    }

    public void setSortLetters(String sortLetters) {
        this.sortLetters = sortLetters;
    }

    @Override
    public String toString() {
        return "AddressListBean{" +

                ", coinAddress=" + coinAddress +
                ", cnName='" + cnName + '\'' +
                ", shortName='" + shortName + '\'' +
                ", id=" + id +
                '}';
    }
}
