package huolongluo.byw.byw.ui.fragment.maintab03.bean;

import android.os.Parcel;
import android.os.Parcelable;


import java.util.ArrayList;
import java.util.List;
/**
 * Created by hy on 2018/10/19 0019.
 */
public class AssetCoinsBean implements Parcelable {
    /**
     * "borrow": 0,
     * "cnName": "byc",
     * "curPrice": "200.0000",
     * "frozen": "0.0000",
     * "id": 5,
     * "isCoin": true,
     * "isRecharge": false,
     * "isWithDraw": true,
     * "iseos": false,
     * "logo": "https://btc018.oss-cn-shenzhen.aliyuncs.com/201709010458040_frL1C.png",
     * "shortName": "byc",
     * "total": "0.0000",
     * "tradeId": 4,
     * "withdrawDigit": 2,
     * "zhehe": "0.0000"
     */
    private String memoName;
    private int id;
    private String cnytIshide;//新加，cnyt是否隐藏按钮,0隐藏，1显示
    private double minimumService;//新加，最小手续费
    private String cnName;
    private String shortName;
    private String cnyName;
    private String total;
    private String frozen;
    private boolean isCoin;
    private String logo;
    private int borrow;
    private String zhehe;
    private String zheheExchange;
    private double curPrice;
    private boolean isWithDraw;
    private boolean isRecharge;
    private String ftrademappingId;
    private boolean iseos;
    private int selfselection;
    private int withdrawDigit;//控制提现小数点位数
    private boolean biyingbao;//是否活期理财
    private String biyingbaoUrl;//目标地址
    private ArrayList<WithdrawFeeBean> withdrawFees;

    private String mainNetworkWithdraw;//控制提现小数点位数
    private ArrayList<WithdrawLimitBean> withdrawQtyArr;//把最大最小提币数放入该list
    private int tradeId = 0;
    //
    private int areaType;//4为ETF币种
    private LockCoin lockCoin;

    public LockCoin getLockCoin() {
        return lockCoin;
    }

    public void setLockCoin(LockCoin lockCoin) {
        this.lockCoin = lockCoin;
    }

    public String getZheheExchange() {
        return zheheExchange;
    }

    public void setZheheExchange(String zheheExchange) {
        this.zheheExchange = zheheExchange;
    }

    //新加
    private boolean isHyperpayRecharge;
    private boolean isHyperpayWithDraw;
    private String hyperpayWithDrawMax;
    private String hyperpayWithDrawMin;
    private String mainNetworkSpecification = "0";
    private String withdrawlimitqty;//该币种24小时提币数量限制

    public String getWithdrawlimitqty() {
        return withdrawlimitqty;
    }

    public void setWithdrawlimitqty(String withdrawlimitqty) {
        this.withdrawlimitqty = withdrawlimitqty;
    }

    public ArrayList<WithdrawFeeBean> getWithdrawFees() {
        return withdrawFees;
    }

    public void setWithdrawFees(ArrayList<WithdrawFeeBean> withdrawFees) {
        this.withdrawFees = withdrawFees;
    }



    public ArrayList<WithdrawLimitBean> getWithdrawQtyArr() {
        return withdrawQtyArr;
    }

    public void setWithdrawQtyArr(ArrayList<WithdrawLimitBean> withdrawQtyArr) {
        this.withdrawQtyArr = withdrawQtyArr;
    }

    public String getMainNetworkWithdraw() {
        return mainNetworkWithdraw;
    }

    public void setMainNetworkWithdraw(String mainNetworkWithdraw) {
        this.mainNetworkWithdraw = mainNetworkWithdraw;
    }


    public AssetCoinsBean() {
    }

    public static class LockCoin implements Parcelable {
        private String coinNum;
        private String orderLimit;
        private String releaseNum;
        private String releaseLimit;

        protected LockCoin(Parcel in) {
            coinNum = in.readString();
            orderLimit = in.readString();
            releaseNum = in.readString();
            releaseLimit = in.readString();
        }

        public static final Creator<LockCoin> CREATOR = new Creator<LockCoin>() {
            @Override
            public LockCoin createFromParcel(Parcel in) {
                return new LockCoin(in);
            }

            @Override
            public LockCoin[] newArray(int size) {
                return new LockCoin[size];
            }
        };

        public String getCoinNum() {
            return coinNum;
        }

        public void setCoinNum(String coinNum) {
            this.coinNum = coinNum;
        }

        public String getOrderLimit() {
            return orderLimit;
        }

        public void setOrderLimit(String orderLimit) {
            this.orderLimit = orderLimit;
        }

        public String getReleaseNum() {
            return releaseNum;
        }

        public void setReleaseNum(String releaseNum) {
            this.releaseNum = releaseNum;
        }

        public String getReleaseLimit() {
            return releaseLimit;
        }

        public void setReleaseLimit(String releaseLimit) {
            this.releaseLimit = releaseLimit;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(coinNum);
            dest.writeString(orderLimit);
            dest.writeString(releaseNum);
            dest.writeString(releaseLimit);
        }
    }

    public boolean isIseos() {
        return iseos;
    }

    public void setIseos(boolean iseos) {
        this.iseos = iseos;
    }

    public String getFtrademappingId() {
        return ftrademappingId;
    }

    public void setFtrademappingId(String ftrademappingId) {
        this.ftrademappingId = ftrademappingId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public boolean isIsCoin() {
        return isCoin;
    }

    public void setIsCoin(boolean isCoin) {
        this.isCoin = isCoin;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public int getBorrow() {
        return borrow;
    }

    public void setBorrow(int borrow) {
        this.borrow = borrow;
    }

    public String getZhehe() {
        return zhehe;
    }

    public void setZhehe(String zhehe) {
        this.zhehe = zhehe;
    }

    public double getCurPrice() {
        return curPrice;
    }

    public void setCurPrice(double curPrice) {
        this.curPrice = curPrice;
    }

    public boolean isIsWithDraw() {
        return isWithDraw;
    }

    public void setIsWithDraw(boolean isWithDraw) {
        this.isWithDraw = isWithDraw;
    }

    public boolean isIsRecharge() {
        return isRecharge;
    }

    public void setIsRecharge(boolean isRecharge) {
        this.isRecharge = isRecharge;
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

    public int getTradeId() {
        return tradeId;
    }

    public void setTradeId(int tradeId) {
        this.tradeId = tradeId;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getFrozen() {
        return frozen;
    }

    public void setFrozen(String frozen) {
        this.frozen = frozen;
    }

    public String getCnyName() {
        return cnyName;
    }

    public void setCnyName(String cnyName) {
        this.cnyName = cnyName;
    }

    public int getSelfselection() {
        return selfselection;
    }

    public void setSelfselection(int selfselection) {
        this.selfselection = selfselection;
    }

    public double getMinimumService() {
        return minimumService;
    }

    public void setMinimumService(double minimumService) {
        this.minimumService = minimumService;
    }

    public String getCnytIshide() {
        return cnytIshide;
    }

    public void setCnytIshide(String cnytIshide) {
        this.cnytIshide = cnytIshide;
    }

    public boolean getBiyingbao() {
        return biyingbao;
    }

    public void setBiyingbao(boolean biyingbao) {
        this.biyingbao = biyingbao;
    }

    public String getBiyingbaoUrl() {
        return biyingbaoUrl;
    }

    public void setBiyingbaoUrl(String biyingbaoUrl) {
        this.biyingbaoUrl = biyingbaoUrl;
    }

    public int getAreaType() {
        return areaType;
    }

    public void setAreaType(int areaType) {
        this.areaType = areaType;
    }


    public boolean isIsHyperpayRecharge() {
        return isHyperpayRecharge;
    }

    public void setIsHyperpayRecharge(boolean hyperpayRecharge) {
        isHyperpayRecharge = hyperpayRecharge;
    }

    public boolean isIsHyperpayWithDraw() {
        return isHyperpayWithDraw;
    }

    public void setIsHyperpayWithDraw(boolean hyperpayWithDraw) {
        isHyperpayWithDraw = hyperpayWithDraw;
    }

    public String getHyperpayWithDrawMax() {
        return hyperpayWithDrawMax;
    }

    public void setHyperpayWithDrawMax(String hyperpayWithDrawMax) {
        this.hyperpayWithDrawMax = hyperpayWithDrawMax;
    }

    public String getHyperpayWithDrawMin() {
        return hyperpayWithDrawMin;
    }

    public void setHyperpayWithDrawMin(String hyperpayWithDrawMin) {
        this.hyperpayWithDrawMin = hyperpayWithDrawMin;
    }

    public static Creator<AssetCoinsBean> getCREATOR() {
        return CREATOR;
    }

    public boolean isHyperpayRecharge() {
        return isHyperpayRecharge;
    }

    public void setHyperpayRecharge(boolean hyperpayRecharge) {
        isHyperpayRecharge = hyperpayRecharge;
    }

    public boolean isHyperpayWithDraw() {
        return isHyperpayWithDraw;
    }

    public void setHyperpayWithDraw(boolean hyperpayWithDraw) {
        isHyperpayWithDraw = hyperpayWithDraw;
    }

    public String getMemoName() {
        return memoName;
    }

    public void setMemoName(String memoName) {
        this.memoName = memoName;
    }

    public String getMainNetworkSpecification() {
        return mainNetworkSpecification;
    }

    public void setMainNetworkSpecification(String mainNetworkSpecification) {
        this.mainNetworkSpecification = mainNetworkSpecification;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.memoName);
        dest.writeInt(this.id);
        dest.writeString(this.cnytIshide);
        dest.writeDouble(this.minimumService);
        dest.writeString(this.cnName);
        dest.writeString(this.shortName);
        dest.writeString(this.cnyName);
        dest.writeString(this.total);
        dest.writeString(this.frozen);
        dest.writeByte(this.isCoin ? (byte) 1 : (byte) 0);
        dest.writeString(this.logo);
        dest.writeInt(this.borrow);
        dest.writeString(this.zhehe);
        dest.writeString(this.zheheExchange);
        dest.writeDouble(this.curPrice);
        dest.writeByte(this.isWithDraw ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isRecharge ? (byte) 1 : (byte) 0);
        dest.writeString(this.ftrademappingId);
        dest.writeByte(this.iseos ? (byte) 1 : (byte) 0);
        dest.writeInt(this.selfselection);
        dest.writeInt(this.withdrawDigit);
        dest.writeByte(this.biyingbao ? (byte) 1 : (byte) 0);
        dest.writeString(this.biyingbaoUrl);
        dest.writeString(this.mainNetworkWithdraw);
        dest.writeList(this.withdrawQtyArr);
        dest.writeList(this.withdrawFees);
        dest.writeInt(this.tradeId);
        dest.writeInt(this.areaType);
        dest.writeParcelable(this.lockCoin, flags);
        dest.writeByte(this.isHyperpayRecharge ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isHyperpayWithDraw ? (byte) 1 : (byte) 0);
        dest.writeString(this.hyperpayWithDrawMax);
        dest.writeString(this.withdrawlimitqty);
        dest.writeString(this.hyperpayWithDrawMin);
        dest.writeString(this.mainNetworkSpecification);
    }

    protected AssetCoinsBean(Parcel in) {
        this.memoName = in.readString();
        this.id = in.readInt();
        this.cnytIshide = in.readString();
        this.minimumService = in.readDouble();
        this.cnName = in.readString();
        this.shortName = in.readString();
        this.cnyName = in.readString();
        this.total = in.readString();
        this.frozen = in.readString();
        this.isCoin = in.readByte() != 0;
        this.logo = in.readString();
        this.borrow = in.readInt();
        this.zhehe = in.readString();
        this.zheheExchange = in.readString();
        this.curPrice = in.readDouble();
        this.isWithDraw = in.readByte() != 0;
        this.isRecharge = in.readByte() != 0;
        this.ftrademappingId = in.readString();
        this.iseos = in.readByte() != 0;
        this.selfselection = in.readInt();
        this.withdrawDigit = in.readInt();
        this.biyingbao = in.readByte() != 0;
        this.biyingbaoUrl = in.readString();
        this.mainNetworkWithdraw = in.readString();
        this.withdrawQtyArr = new ArrayList<WithdrawLimitBean>();
        in.readList(this.withdrawQtyArr, WithdrawLimitBean.class.getClassLoader());
        this.withdrawFees = new ArrayList<WithdrawFeeBean>();
        in.readList(this.withdrawFees, WithdrawFeeBean.class.getClassLoader());
        this.tradeId = in.readInt();
        this.areaType = in.readInt();
        this.lockCoin = in.readParcelable(LockCoin.class.getClassLoader());
        this.isHyperpayRecharge = in.readByte() != 0;
        this.isHyperpayWithDraw = in.readByte() != 0;
        this.hyperpayWithDrawMax = in.readString();
        this.withdrawlimitqty=in.readString();
        this.hyperpayWithDrawMin = in.readString();
        this.mainNetworkSpecification = in.readString();
    }

    public static final Creator<AssetCoinsBean> CREATOR = new Creator<AssetCoinsBean>() {
        @Override
        public AssetCoinsBean createFromParcel(Parcel source) {
            return new AssetCoinsBean(source);
        }

        @Override
        public AssetCoinsBean[] newArray(int size) {
            return new AssetCoinsBean[size];
        }
    };
}
