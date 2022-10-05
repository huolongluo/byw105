package huolongluo.byw.byw.bean;

import android.os.Parcel;
import android.os.Parcelable;


import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * Created by 火龙裸 on 2017/12/30 0030.
 */
public class TradeAccountBean implements Serializable
{
    private String totalAsset;
    private String netAsset;
    private List<TradeAccountBean.CoinsBean> userWallet;

    public String getTotalAsset()
    {
        return totalAsset;
    }

    public void setTotalAsset(String totalAsset)
    {
        this.totalAsset = totalAsset;
    }

    public String getNetAsset()
    {
        return netAsset;
    }

    public void setNetAsset(String netAsset)
    {
        this.netAsset = netAsset;
    }

    public List<TradeAccountBean.CoinsBean> getCoins()
    {
        return userWallet;
    }

    public void setCoins(List<TradeAccountBean.CoinsBean> coins)
    {
        this.userWallet = coins;
    }

    @Override
    public String toString() {
        return "TradeAccountBean{" +
                "totalAsset='" + totalAsset + '\'' +
                ", netAsset='" + netAsset + '\'' +
                ", userWallet=" + userWallet +
                '}';
    }

    public static class CoinsBean implements Parcelable
    {
        /**
         * id : 0
         * cnName : 人民币
         * shortName : CNY
         * total : 0
         * frozen : 0
         * isCoin : false
         * logo : https://btc018.oss-cn-shenzhen.aliyuncs.com/201709010330040_9rOWN.png
         * borrow : 0
         * zhehe : 0
         * curPrice : 0.09
         * isWithDraw : true
         * isRecharge : true
         */

        private int id;
        private String cnName;
        private String shortName;
        private Double total;
        private Double frozen;
        private boolean isCoin;
        private String logo;
        private int borrow;
        private String zhehe;
        private double curPrice;
        private boolean isWithDraw;
        private boolean isRecharge;
        private String ftrademappingId;
        private boolean iseos;

        private int withdrawDigit;//控制提现小数点位数

        public CoinsBean (){

        }

        public boolean isIseos() {
            return iseos;
        }

        public void setIseos(boolean iseos) {
            this.iseos = iseos;
        }


        protected CoinsBean(Parcel in) {
            id = in.readInt();
            cnName = in.readString();
            shortName = in.readString();
            if (in.readByte() == 0) {
                total = null;
            } else {
                total = in.readDouble();
            }
            if (in.readByte() == 0) {
                frozen = null;
            } else {
                frozen = in.readDouble();
            }
            isCoin = in.readByte() != 0;
            logo = in.readString();
            borrow = in.readInt();
            zhehe = in.readString();
            curPrice = in.readDouble();
            isWithDraw = in.readByte() != 0;
            isRecharge = in.readByte() != 0;
            ftrademappingId = in.readString();
        }

        public static final Creator<CoinsBean> CREATOR = new Creator<CoinsBean>() {
            @Override
            public CoinsBean createFromParcel(Parcel in) {
                return new CoinsBean(in);
            }

            @Override
            public CoinsBean[] newArray(int size) {
                return new CoinsBean[size];
            }
        };

        public String getFtrademappingId() {
            return ftrademappingId;
        }

        public void setFtrademappingId(String ftrademappingId) {
            this.ftrademappingId = ftrademappingId;
        }

        public int getId()
        {
            return id;
        }

        public void setId(int id)
        {
            this.id = id;
        }

        public String getCnName()
        {
            return cnName;
        }

        public void setCnName(String cnName)
        {
            this.cnName = cnName;
        }

        public String getShortName()
        {
            return shortName;
        }

        public void setShortName(String shortName)
        {
            this.shortName = shortName;
        }

        public Double getTotal()
        {
            return total;
        }

        public void setTotal(Double total)
        {
            this.total = total;
        }

        public Double getFrozen()
        {
            return frozen;
        }

        public void setFrozen(Double frozen)
        {
            this.frozen = frozen;
        }

        public boolean isIsCoin()
        {
            return isCoin;
        }

        public void setIsCoin(boolean isCoin)
        {
            this.isCoin = isCoin;
        }

        public String getLogo()
        {
            return logo;
        }

        public void setLogo(String logo)
        {
            this.logo = logo;
        }

        public int getBorrow()
        {
            return borrow;
        }

        public void setBorrow(int borrow)
        {
            this.borrow = borrow;
        }

        public String getZhehe()
        {
            return zhehe;
        }

        public void setZhehe(String zhehe)
        {
            this.zhehe = zhehe;
        }

        public double getCurPrice()
        {
            return curPrice;
        }

        public void setCurPrice(double curPrice)
        {
            this.curPrice = curPrice;
        }

        public boolean isIsWithDraw()
        {
            return isWithDraw;
        }

        public void setIsWithDraw(boolean isWithDraw)
        {
            this.isWithDraw = isWithDraw;
        }

        public boolean isIsRecharge()
        {
            return isRecharge;
        }

        public void setIsRecharge(boolean isRecharge)
        {
            this.isRecharge = isRecharge;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeInt(id);
            parcel.writeString(cnName);
            parcel.writeString(shortName);
            if (total == null) {
                parcel.writeByte((byte) 0);
            } else {
                parcel.writeByte((byte) 1);
                parcel.writeDouble(total);
            }
            if (frozen == null) {
                parcel.writeByte((byte) 0);
            } else {
                parcel.writeByte((byte) 1);
                parcel.writeDouble(frozen);
            }
            parcel.writeByte((byte) (isCoin ? 1 : 0));
            parcel.writeString(logo);
            parcel.writeInt(borrow);
            parcel.writeString(zhehe);
            parcel.writeDouble(curPrice);
            parcel.writeByte((byte) (isWithDraw ? 1 : 0));
            parcel.writeByte((byte) (isRecharge ? 1 : 0));
            parcel.writeString(ftrademappingId);
        }

        public boolean isCoin() {
            return isCoin;
        }

        public void setCoin(boolean coin) {
            isCoin = coin;
        }

        public boolean isWithDraw() {
            return isWithDraw;
        }

        public void setWithDraw(boolean withDraw) {
            isWithDraw = withDraw;
        }

        public boolean isRecharge() {
            return isRecharge;
        }

        public void setRecharge(boolean recharge) {
            isRecharge = recharge;
        }

        public int getWithdrawDigit() {
            return withdrawDigit;
        }

        public void setWithdrawDigit(int withdrawDigit) {
            this.withdrawDigit = withdrawDigit;
        }

        @Override
        public String toString() {
            return "CoinsBean{" +
                    "id=" + id +
                    ", cnName='" + cnName + '\'' +
                    ", shortName='" + shortName + '\'' +
                    ", total=" + total +
                    ", frozen=" + frozen +
                    ", isCoin=" + isCoin +
                    ", logo='" + logo + '\'' +
                    ", borrow=" + borrow +
                    ", zhehe='" + zhehe + '\'' +
                    ", curPrice=" + curPrice +
                    ", isWithDraw=" + isWithDraw +
                    ", isRecharge=" + isRecharge +
                    ", ftrademappingId='" + ftrademappingId + '\'' +
                    ", iseos=" + iseos +
                    ", withdrawDigit=" + withdrawDigit +
                    '}';
        }
    }
}
