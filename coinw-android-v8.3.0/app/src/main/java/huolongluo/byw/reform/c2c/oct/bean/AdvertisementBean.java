package huolongluo.byw.reform.c2c.oct.bean;

import java.util.List;

/**
 * Created by Administrator on 2019/7/24 0024.
 */

public class AdvertisementBean {
    /**
     * code : 0
     * data : {"buy":[{"alipayId":4,"amount":2000,"amount_s":"","bankId":2,"cancelAmount":0,"cancelAmount_s":"","coinId":63,"coinName":"USDT","coinUrl":"","createTime":{"date":22,"day":1,"hours":14,"minutes":56,"month":6,"seconds":14,"time":1563778574000,"timezoneOffset":-480,"year":119},"fee":20,"fee_s":"","id":10,"leftAmount":1400,"leftAmount_s":"","orderMax":200,"orderMax_s":"","orderMin":100,"orderMin_s":"","payLimit":60,"price":0,"priceFloat":0,"priceType":2,"price_s":"","realAmount":600,"realAmount_s":"","realFee":6,"realFee_s":"","remark":"123","sort":0,"status":2,"type":2,"uid":646859,"updateTime":{"date":22,"day":1,"hours":16,"minutes":22,"month":6,"seconds":50,"time":1563783770000,"timezoneOffset":-480,"year":119},"userName":"测试2","version":6,"wechatId":3}],"sell":[{"alipayId":4,"amount":2000,"amount_s":"","bankId":2,"cancelAmount":0,"cancelAmount_s":"","coinId":63,"coinName":"USDT","coinUrl":"","createTime":{"date":22,"day":1,"hours":14,"minutes":56,"month":6,"seconds":14,"time":1563778574000,"timezoneOffset":-480,"year":119},"fee":20,"fee_s":"","id":10,"leftAmount":1400,"leftAmount_s":"","orderMax":200,"orderMax_s":"","orderMin":100,"orderMin_s":"","payLimit":60,"price":0,"priceFloat":0,"priceType":2,"price_s":"","realAmount":600,"realAmount_s":"","realFee":6,"realFee_s":"","remark":"123","sort":0,"status":2,"type":2,"uid":646859,"updateTime":{"date":22,"day":1,"hours":16,"minutes":22,"month":6,"seconds":50,"time":1563783770000,"timezoneOffset":-480,"year":119},"userName":"测试2","version":6,"wechatId":3},{"alipayId":4,"amount":2000,"amount_s":"","bankId":2,"cancelAmount":0,"cancelAmount_s":"","coinId":2,"coinName":"CNYT","coinUrl":"","createTime":{"date":22,"day":1,"hours":15,"minutes":2,"month":6,"seconds":19,"time":1563778939000,"timezoneOffset":-480,"year":119},"fee":20,"fee_s":"","id":11,"leftAmount":1800,"leftAmount_s":"","orderMax":200,"orderMax_s":"","orderMin":100,"orderMin_s":"","payLimit":50,"price":1,"priceFloat":0,"priceType":1,"price_s":"","realAmount":200,"realAmount_s":"","realFee":2,"realFee_s":"","remark":"1122","sort":0,"status":2,"type":2,"uid":646859,"updateTime":{"date":23,"day":2,"hours":18,"minutes":17,"month":6,"seconds":33,"time":1563877053000,"timezoneOffset":-480,"year":119},"userName":"测试2","version":8,"wechatId":3}]}
     * result : true
     * value : 操作成功
     */

    private int code;
    private DataBean data;
    private boolean result;
    private String value;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public static class DataBean {
        private List<SellBean> buy;
        private List<SellBean> sell;

        public List<SellBean> getBuy() {
            return buy;
        }

        public void setBuy(List<SellBean> buy) {
            this.buy = buy;
        }

        public List<SellBean> getSell() {
            return sell;
        }

        public void setSell(List<SellBean> sell) {
            this.sell = sell;
        }


        public static class SellBean {
            /**
             * alipayId : 4
             * amount : 2000
             * amount_s :
             * bankId : 2
             * cancelAmount : 0
             * cancelAmount_s :
             * coinId : 63
             * coinName : USDT
             * coinUrl :
             * createTime : {"date":22,"day":1,"hours":14,"minutes":56,"month":6,"seconds":14,"time":1563778574000,"timezoneOffset":-480,"year":119}
             * fee : 20
             * fee_s :
             * id : 10
             * leftAmount : 1400
             * leftAmount_s :
             * orderMax : 200
             * orderMax_s :
             * orderMin : 100
             * orderMin_s :
             * payLimit : 60
             * price : 0.0
             * priceFloat : 0
             * priceType : 2
             * price_s :
             * realAmount : 600
             * realAmount_s :
             * realFee : 6
             * realFee_s :
             * remark : 123
             * sort : 0
             * status : 2
             * type : 2
             * uid : 646859
             * updateTime : {"date":22,"day":1,"hours":16,"minutes":22,"month":6,"seconds":50,"time":1563783770000,"timezoneOffset":-480,"year":119}
             * userName : 测试2
             * version : 6
             * wechatId : 3
             */


              int viewType=0;



            private int alipayId;
            private double amount;
            private String amount_s;
            private int bankId;
            private double cancelAmount;
            private String cancelAmount_s;
            private int coinId;
            private String coinName;
            private String coinUrl;
            private CreateTimeBeanX createTime;
            private double fee;
            private String fee_s;
            private int id;
            private double leftAmount;
            private String leftAmount_s;
            private double orderMax;
            private String orderMax_s;
            private double orderMin;
            private String orderMin_s;
            private double payLimit;
            private double price;
            private double priceFloat;
            private int priceType;
            private String price_s;
            private double realAmount;
            private String realAmount_s;
            private double realFee;
            private String realFee_s;
            private String remark;
            private int sort;
            private int status;
            private int type;
            private int uid;
            private UpdateTimeBeanX updateTime;
            private String userName;
            private int version;
            private int wechatId;
            private boolean top;
            public SellBean() {

            }
            public boolean isTop() {
                return top;
            }

            public void setTop(boolean top) {
                this.top = top;
            }

            private List<Integer> payAccountTypes;

            public List<Integer> getPayAccountTypes() {
                return payAccountTypes;
            }

            public void setPayAccountTypes(List<Integer> payAccountTypes) {
                this.payAccountTypes = payAccountTypes;
            }

            public SellBean(int viewType) {
                this.viewType = viewType;
            }

            public int getAlipayId() {
                return alipayId;
            }

            public void setAlipayId(int alipayId) {
                this.alipayId = alipayId;
            }

            public double getAmount() {
                return amount;
            }

            public void setAmount(double amount) {
                this.amount = amount;
            }

            public String getAmount_s() {
                return amount_s;
            }

            public void setAmount_s(String amount_s) {
                this.amount_s = amount_s;
            }

            public int getBankId() {
                return bankId;
            }

            public void setBankId(int bankId) {
                this.bankId = bankId;
            }

            public double getCancelAmount() {
                return cancelAmount;
            }

            public void setCancelAmount(double cancelAmount) {
                this.cancelAmount = cancelAmount;
            }

            public String getCancelAmount_s() {
                return cancelAmount_s;
            }

            public void setCancelAmount_s(String cancelAmount_s) {
                this.cancelAmount_s = cancelAmount_s;
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

            public String getCoinUrl() {
                return coinUrl;
            }

            public void setCoinUrl(String coinUrl) {
                this.coinUrl = coinUrl;
            }

            public CreateTimeBeanX getCreateTime() {
                return createTime;
            }

            public void setCreateTime(CreateTimeBeanX createTime) {
                this.createTime = createTime;
            }

            public double getFee() {
                return fee;
            }

            public void setFee(double fee) {
                this.fee = fee;
            }

            public String getFee_s() {
                return fee_s;
            }

            public void setFee_s(String fee_s) {
                this.fee_s = fee_s;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public double getLeftAmount() {
                return leftAmount;
            }

            public void setLeftAmount(double leftAmount) {
                this.leftAmount = leftAmount;
            }

            public String getLeftAmount_s() {
                return leftAmount_s;
            }

            public void setLeftAmount_s(String leftAmount_s) {
                this.leftAmount_s = leftAmount_s;
            }

            public double getOrderMax() {
                return orderMax;
            }

            public void setOrderMax(double orderMax) {
                this.orderMax = orderMax;
            }

            public String getOrderMax_s() {
                return orderMax_s;
            }

            public void setOrderMax_s(String orderMax_s) {
                this.orderMax_s = orderMax_s;
            }

            public double getOrderMin() {
                return orderMin;
            }

            public void setOrderMin(double orderMin) {
                this.orderMin = orderMin;
            }

            public String getOrderMin_s() {
                return orderMin_s;
            }

            public void setOrderMin_s(String orderMin_s) {
                this.orderMin_s = orderMin_s;
            }

            public double getPayLimit() {
                return payLimit;
            }

            public void setPayLimit(double payLimit) {
                this.payLimit = payLimit;
            }

            public double getPrice() {
                return price;
            }

            public void setPrice(double price) {
                this.price = price;
            }

            public double getPriceFloat() {
                return priceFloat;
            }

            public void setPriceFloat(double priceFloat) {
                this.priceFloat = priceFloat;
            }

            public int getPriceType() {
                return priceType;
            }

            public void setPriceType(int priceType) {
                this.priceType = priceType;
            }

            public String getPrice_s() {
                return price_s;
            }

            public void setPrice_s(String price_s) {
                this.price_s = price_s;
            }

            public double getRealAmount() {
                return realAmount;
            }

            public void setRealAmount(double realAmount) {
                this.realAmount = realAmount;
            }

            public String getRealAmount_s() {
                return realAmount_s;
            }

            public void setRealAmount_s(String realAmount_s) {
                this.realAmount_s = realAmount_s;
            }

            public double getRealFee() {
                return realFee;
            }

            public void setRealFee(double realFee) {
                this.realFee = realFee;
            }

            public String getRealFee_s() {
                return realFee_s;
            }

            public void setRealFee_s(String realFee_s) {
                this.realFee_s = realFee_s;
            }

            public String getRemark() {
                return remark;
            }

            public void setRemark(String remark) {
                this.remark = remark;
            }

            public int getSort() {
                return sort;
            }

            public void setSort(int sort) {
                this.sort = sort;
            }

            public int getStatus() {
                return status;
            }

            public void setStatus(int status) {
                this.status = status;
            }

            public int getType() {
                return type;
            }

            public void setType(int type) {
                this.type = type;
            }

            public int getUid() {
                return uid;
            }

            public void setUid(int uid) {
                this.uid = uid;
            }

            public UpdateTimeBeanX getUpdateTime() {
                return updateTime;
            }

            public void setUpdateTime(UpdateTimeBeanX updateTime) {
                this.updateTime = updateTime;
            }

            public String getUserName() {
                return userName;
            }

            public void setUserName(String userName) {
                this.userName = userName;
            }

            public int getVersion() {
                return version;
            }

            public void setVersion(int version) {
                this.version = version;
            }

            public int getWechatId() {
                return wechatId;
            }

            public void setWechatId(int wechatId) {
                this.wechatId = wechatId;
            }

            public int getViewType() {
                return viewType;
            }

            public void setViewType(int viewType) {
                this.viewType = viewType;
            }

            public static class CreateTimeBeanX {
                /**
                 * date : 22
                 * day : 1
                 * hours : 14
                 * minutes : 56
                 * month : 6
                 * seconds : 14
                 * time : 1563778574000
                 * timezoneOffset : -480
                 * year : 119
                 */

                private int date;
                private int day;
                private int hours;
                private int minutes;
                private int month;
                private int seconds;
                private long time;
                private int timezoneOffset;
                private int year;

                public int getDate() {
                    return date;
                }

                public void setDate(int date) {
                    this.date = date;
                }

                public int getDay() {
                    return day;
                }

                public void setDay(int day) {
                    this.day = day;
                }

                public int getHours() {
                    return hours;
                }

                public void setHours(int hours) {
                    this.hours = hours;
                }

                public int getMinutes() {
                    return minutes;
                }

                public void setMinutes(int minutes) {
                    this.minutes = minutes;
                }

                public int getMonth() {
                    return month;
                }

                public void setMonth(int month) {
                    this.month = month;
                }

                public int getSeconds() {
                    return seconds;
                }

                public void setSeconds(int seconds) {
                    this.seconds = seconds;
                }

                public long getTime() {
                    return time;
                }

                public void setTime(long time) {
                    this.time = time;
                }

                public int getTimezoneOffset() {
                    return timezoneOffset;
                }

                public void setTimezoneOffset(int timezoneOffset) {
                    this.timezoneOffset = timezoneOffset;
                }

                public int getYear() {
                    return year;
                }

                public void setYear(int year) {
                    this.year = year;
                }
            }

            public static class UpdateTimeBeanX {
                /**
                 * date : 22
                 * day : 1
                 * hours : 16
                 * minutes : 22
                 * month : 6
                 * seconds : 50
                 * time : 1563783770000
                 * timezoneOffset : -480
                 * year : 119
                 */

                private int date;
                private int day;
                private int hours;
                private int minutes;
                private int month;
                private int seconds;
                private long time;
                private int timezoneOffset;
                private int year;

                public int getDate() {
                    return date;
                }

                public void setDate(int date) {
                    this.date = date;
                }

                public int getDay() {
                    return day;
                }

                public void setDay(int day) {
                    this.day = day;
                }

                public int getHours() {
                    return hours;
                }

                public void setHours(int hours) {
                    this.hours = hours;
                }

                public int getMinutes() {
                    return minutes;
                }

                public void setMinutes(int minutes) {
                    this.minutes = minutes;
                }

                public int getMonth() {
                    return month;
                }

                public void setMonth(int month) {
                    this.month = month;
                }

                public int getSeconds() {
                    return seconds;
                }

                public void setSeconds(int seconds) {
                    this.seconds = seconds;
                }

                public long getTime() {
                    return time;
                }

                public void setTime(long time) {
                    this.time = time;
                }

                public int getTimezoneOffset() {
                    return timezoneOffset;
                }

                public void setTimezoneOffset(int timezoneOffset) {
                    this.timezoneOffset = timezoneOffset;
                }

                public int getYear() {
                    return year;
                }

                public void setYear(int year) {
                    this.year = year;
                }
            }
        }
    }
}
