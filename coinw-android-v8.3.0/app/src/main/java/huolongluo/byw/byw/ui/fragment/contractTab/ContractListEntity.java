package huolongluo.byw.byw.ui.fragment.contractTab;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class ContractListEntity {

    /**
     * result : true
     * code : 0
     * value : 操作成功
     * data : {"cnytPrice":"0.00","cnytPriceExchange":"0.00","coinAndQuota":[{"coinId":29,"coinName":"USDT","quota":1000000,"status":1},{"coinId":16,"coinName":"ETH","quota":0,"status":1},{"coinId":28,"coinName":"EOS","quota":0,"status":1}],"detail":[{"availableVol":"0.0000","cashVol":"0.0000","coinId":29,"coinName":"USDT","coinStatus":0,"coinUrl":"https://btc018.oss-cn-shenzhen.aliyuncs.com/201711250157013_QSgzZ.png","depositVol":"0.0000","earningsVol":"0.0000","freezeVol":"0.0000","quota":"","realisedVol":"0.0000"},{"availableVol":"0.0000","cashVol":"0.0000","coinId":16,"coinName":"ETH","coinStatus":0,"coinUrl":"https://btc018.oss-cn-shenzhen.aliyuncs.com/201711020114014_nIiOH.png","depositVol":"0.0000","earningsVol":"0.0000","freezeVol":"0.0000","quota":"","realisedVol":"0.0000"},{"availableVol":"0.0000","cashVol":"0.0000","coinId":28,"coinName":"EOS","coinStatus":0,"coinUrl":"https://btc018.oss-cn-shenzhen.aliyuncs.com/201711250157013_QSgzZ.png","depositVol":"0.0000","earningsVol":"0.0000","freezeVol":"0.0000","quota":"","realisedVol":"0.0000"}]}
     */

    private boolean result;
    private int code;
    private String value;
    private DataBean data;

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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * cnytPrice : 0.00
         * cnytPriceExchange : 0.00
         * coinAndQuota : [{"coinId":29,"coinName":"USDT","quota":1000000,"status":1},{"coinId":16,"coinName":"ETH","quota":0,"status":1},{"coinId":28,"coinName":"EOS","quota":0,"status":1}]
         * detail : [{"availableVol":"0.0000","cashVol":"0.0000","coinId":29,"coinName":"USDT","coinStatus":0,"coinUrl":"https://btc018.oss-cn-shenzhen.aliyuncs.com/201711250157013_QSgzZ.png","depositVol":"0.0000","earningsVol":"0.0000","freezeVol":"0.0000","quota":"","realisedVol":"0.0000"},{"availableVol":"0.0000","cashVol":"0.0000","coinId":16,"coinName":"ETH","coinStatus":0,"coinUrl":"https://btc018.oss-cn-shenzhen.aliyuncs.com/201711020114014_nIiOH.png","depositVol":"0.0000","earningsVol":"0.0000","freezeVol":"0.0000","quota":"","realisedVol":"0.0000"},{"availableVol":"0.0000","cashVol":"0.0000","coinId":28,"coinName":"EOS","coinStatus":0,"coinUrl":"https://btc018.oss-cn-shenzhen.aliyuncs.com/201711250157013_QSgzZ.png","depositVol":"0.0000","earningsVol":"0.0000","freezeVol":"0.0000","quota":"","realisedVol":"0.0000"}]
         */

        private String cnytPrice;
        private String cnytPriceExchange;
        private List<CoinAndQuotaBean> coinAndQuota;
        private List<DetailBean> detail;

        public String getCnytPrice() {
            return cnytPrice;
        }

        public void setCnytPrice(String cnytPrice) {
            this.cnytPrice = cnytPrice;
        }

        public String getCnytPriceExchange() {
            return cnytPriceExchange;
        }

        public void setCnytPriceExchange(String cnytPriceExchange) {
            this.cnytPriceExchange = cnytPriceExchange;
        }

        public List<CoinAndQuotaBean> getCoinAndQuota() {
            return coinAndQuota;
        }

        public void setCoinAndQuota(List<CoinAndQuotaBean> coinAndQuota) {
            this.coinAndQuota = coinAndQuota;
        }

        public List<DetailBean> getDetail() {
            return detail;
        }

        public void setDetail(List<DetailBean> detail) {
            this.detail = detail;
        }

        public static class CoinAndQuotaBean {
            /**
             * coinId : 29
             * coinName : USDT
             * quota : 1000000
             * status : 1
             */

            private int coinId;
            private String coinName;
            private double quota;
            private int status;

            public int getCoinId() {
                return coinId;
            }

            public void setCoinId(int coinId) {
                this.coinId = coinId;
            }

            public String getCoinName() {
                return coinName;
            }

            public void setCoinName(String coinName) {
                this.coinName = coinName;
            }

            public double getQuota() {
                return quota;
            }

            public void setQuota(double quota) {
                this.quota = quota;
            }

            public int getStatus() {
                return status;
            }

            public void setStatus(int status) {
                this.status = status;
            }
        }

        public static class DetailBean implements Parcelable {
            /**
             * availableVol : 0.0000
             * cashVol : 0.0000
             * coinId : 29
             * coinName : USDT
             * coinStatus : 0
             * coinUrl : https://btc018.oss-cn-shenzhen.aliyuncs.com/201711250157013_QSgzZ.png
             * depositVol : 0.0000
             * earningsVol : 0.0000
             * freezeVol : 0.0000
             * quota :
             * realisedVol : 0.0000
             */

            private String availableVol;
            private String cashVol;
            private int coinId;
            private String coinName;
            private int coinStatus;
            private String coinUrl;
            private String depositVol;
            private String earningsVol;
            private String freezeVol;
            private String quota;
            private String realisedVol;

            //新接口要求
            private String availableBalance;//可用保证金、可用余额
            private String totalVol;//总资产
            private String totalIm;//仓位保证金

            public String getTotalVol() {
                return totalVol;
            }

            public void setTotalVol(String totalVol) {
                this.totalVol = totalVol;
            }

            public String getTotalIm() {
                return totalIm;
            }

            public void setTotalIm(String totalIm) {
                this.totalIm = totalIm;
            }

            public String getAvailableBalance() {
                return availableBalance;
            }

            public void setAvailableBalance(String availableBalance) {
                this.availableBalance = availableBalance;
            }

            public String getAvailableVol() {
                return availableVol;
            }

            public void setAvailableVol(String availableVol) {
                this.availableVol = availableVol;
            }

            public String getCashVol() {
                return cashVol;
            }

            public void setCashVol(String cashVol) {
                this.cashVol = cashVol;
            }

            public int getCoinId() {
                return coinId;
            }

            public void setCoinId(int coinId) {
                this.coinId = coinId;
            }

            public String getCoinName() {
                return coinName;
            }

            public void setCoinName(String coinName) {
                this.coinName = coinName;
            }

            public int getCoinStatus() {
                return coinStatus;
            }

            public void setCoinStatus(int coinStatus) {
                this.coinStatus = coinStatus;
            }

            public String getCoinUrl() {
                return coinUrl;
            }

            public void setCoinUrl(String coinUrl) {
                this.coinUrl = coinUrl;
            }

            public String getDepositVol() {
                return depositVol;
            }

            public void setDepositVol(String depositVol) {
                this.depositVol = depositVol;
            }

            public String getEarningsVol() {
                return earningsVol;
            }

            public void setEarningsVol(String earningsVol) {
                this.earningsVol = earningsVol;
            }

            public String getFreezeVol() {
                return freezeVol;
            }

            public void setFreezeVol(String freezeVol) {
                this.freezeVol = freezeVol;
            }

            public String getQuota() {
                return quota;
            }

            public void setQuota(String quota) {
                this.quota = quota;
            }

            public String getRealisedVol() {
                return realisedVol;
            }

            public void setRealisedVol(String realisedVol) {
                this.realisedVol = realisedVol;
            }

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
                dest.writeString(this.availableVol);
                dest.writeString(this.cashVol);
                dest.writeInt(this.coinId);
                dest.writeString(this.coinName);
                dest.writeInt(this.coinStatus);
                dest.writeString(this.coinUrl);
                dest.writeString(this.depositVol);
                dest.writeString(this.earningsVol);
                dest.writeString(this.freezeVol);
                dest.writeString(this.quota);
                dest.writeString(this.realisedVol);
                dest.writeString(this.availableBalance);
                dest.writeString(this.totalVol);
                dest.writeString(this.totalIm);
            }

            public DetailBean() {
            }

            protected DetailBean(Parcel in) {
                this.availableVol = in.readString();
                this.cashVol = in.readString();
                this.coinId = in.readInt();
                this.coinName = in.readString();
                this.coinStatus = in.readInt();
                this.coinUrl = in.readString();
                this.depositVol = in.readString();
                this.earningsVol = in.readString();
                this.freezeVol = in.readString();
                this.quota = in.readString();
                this.realisedVol = in.readString();
                this.availableBalance = in.readString();
                this.totalVol = in.readString();
                this.totalIm = in.readString();
            }

            public static final Parcelable.Creator<DetailBean> CREATOR = new Parcelable.Creator<DetailBean>() {
                @Override
                public DetailBean createFromParcel(Parcel source) {
                    return new DetailBean(source);
                }

                @Override
                public DetailBean[] newArray(int size) {
                    return new DetailBean[size];
                }
            };
        }
    }
}
