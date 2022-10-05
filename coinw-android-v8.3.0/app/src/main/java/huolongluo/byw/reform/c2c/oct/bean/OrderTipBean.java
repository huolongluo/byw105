package huolongluo.byw.reform.c2c.oct.bean;

/**
 * Created by dell on 2019/7/9.
 */

public class OrderTipBean {
    /**
     * code : 0
     * data : {"otcOrderVo":{"UId":400092,"account":"xyweixin@qq.com","adUserId":67669,"amount":100,"canComplaint":0,"cancelComplaint":0,"coinId":29,"coinName":"USDT","createTime":{"date":9,"day":2,"hours":18,"minutes":35,"month":6,"seconds":10,"time":1562668510000,"timezoneOffset":-480,"year":119},"createTime_s":"","dealStatus":1,"dealType":0,"dealUserNickname":"鱼商家","fee":0,"id":30126,"orderNo":"19070918353395","orderType":0,"otcLevel":2,"payLimit":5,"payType":2,"price":6,"status":1,"timeLimit":0,"totalAmount":600,"transReferNum":188723},"type":1}
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
        /**
         * otcOrderVo : {"UId":400092,"account":"xyweixin@qq.com","adUserId":67669,"amount":100,"canComplaint":0,"cancelComplaint":0,"coinId":29,"coinName":"USDT","createTime":{"date":9,"day":2,"hours":18,"minutes":35,"month":6,"seconds":10,"time":1562668510000,"timezoneOffset":-480,"year":119},"createTime_s":"","dealStatus":1,"dealType":0,"dealUserNickname":"鱼商家","fee":0,"id":30126,"orderNo":"19070918353395","orderType":0,"otcLevel":2,"payLimit":5,"payType":2,"price":6,"status":1,"timeLimit":0,"totalAmount":600,"transReferNum":188723}
         * type : 1
         */

        private OtcOrderVoBean otcOrderVo;
        private int type;

        public OtcOrderVoBean getOtcOrderVo() {
            return otcOrderVo;
        }

        public void setOtcOrderVo(OtcOrderVoBean otcOrderVo) {
            this.otcOrderVo = otcOrderVo;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public static class OtcOrderVoBean {
            /**
             * UId : 400092
             * account : xyweixin@qq.com
             * adUserId : 67669
             * amount : 100
             * canComplaint : 0
             * cancelComplaint : 0
             * coinId : 29
             * coinName : USDT
             * createTime : {"date":9,"day":2,"hours":18,"minutes":35,"month":6,"seconds":10,"time":1562668510000,"timezoneOffset":-480,"year":119}
             * createTime_s :
             * dealStatus : 1
             * dealType : 0
             * dealUserNickname : 鱼商家
             * fee : 0
             * id : 30126
             * orderNo : 19070918353395
             * orderType : 0
             * otcLevel : 2
             * payLimit : 5
             * payType : 2
             * price : 6
             * status : 1
             * timeLimit : 0
             * totalAmount : 600
             * transReferNum : 188723
             */

            private int UId;
            private String account;
            private int adUserId;
            private int amount;
            private int canComplaint;
            private int cancelComplaint;
            private int coinId;
            private String coinName;
            private CreateTimeBean createTime;
            private String createTime_s;
            private int dealStatus;
            private int dealType;
            private String dealUserNickname;
            private double fee;
            private int id;
            private String orderNo;
            private int orderType;
            private int otcLevel;
            private int payLimit;
            private int payType;
            private double price;
            private int status;
            private int timeLimit;
            private int totalAmount;
            private int transReferNum;

            public int getUId() {
                return UId;
            }

            public void setUId(int UId) {
                this.UId = UId;
            }

            public String getAccount() {
                return account;
            }

            public void setAccount(String account) {
                this.account = account;
            }

            public int getAdUserId() {
                return adUserId;
            }

            public void setAdUserId(int adUserId) {
                this.adUserId = adUserId;
            }

            public int getAmount() {
                return amount;
            }

            public void setAmount(int amount) {
                this.amount = amount;
            }

            public int getCanComplaint() {
                return canComplaint;
            }

            public void setCanComplaint(int canComplaint) {
                this.canComplaint = canComplaint;
            }

            public int getCancelComplaint() {
                return cancelComplaint;
            }

            public void setCancelComplaint(int cancelComplaint) {
                this.cancelComplaint = cancelComplaint;
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

            public CreateTimeBean getCreateTime() {
                return createTime;
            }

            public void setCreateTime(CreateTimeBean createTime) {
                this.createTime = createTime;
            }

            public String getCreateTime_s() {
                return createTime_s;
            }

            public void setCreateTime_s(String createTime_s) {
                this.createTime_s = createTime_s;
            }

            public int getDealStatus() {
                return dealStatus;
            }

            public void setDealStatus(int dealStatus) {
                this.dealStatus = dealStatus;
            }

            public int getDealType() {
                return dealType;
            }

            public void setDealType(int dealType) {
                this.dealType = dealType;
            }

            public String getDealUserNickname() {
                return dealUserNickname;
            }

            public void setDealUserNickname(String dealUserNickname) {
                this.dealUserNickname = dealUserNickname;
            }

            public double getFee() {
                return fee;
            }

            public void setFee(double fee) {
                this.fee = fee;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getOrderNo() {
                return orderNo;
            }

            public void setOrderNo(String orderNo) {
                this.orderNo = orderNo;
            }

            public int getOrderType() {
                return orderType;
            }

            public void setOrderType(int orderType) {
                this.orderType = orderType;
            }

            public int getOtcLevel() {
                return otcLevel;
            }

            public void setOtcLevel(int otcLevel) {
                this.otcLevel = otcLevel;
            }

            public int getPayLimit() {
                return payLimit;
            }

            public void setPayLimit(int payLimit) {
                this.payLimit = payLimit;
            }

            public int getPayType() {
                return payType;
            }

            public void setPayType(int payType) {
                this.payType = payType;
            }

            public double getPrice() {
                return price;
            }

            public void setPrice(double price) {
                this.price = price;
            }

            public int getStatus() {
                return status;
            }

            public void setStatus(int status) {
                this.status = status;
            }

            public int getTimeLimit() {
                return timeLimit;
            }

            public void setTimeLimit(int timeLimit) {
                this.timeLimit = timeLimit;
            }

            public int getTotalAmount() {
                return totalAmount;
            }

            public void setTotalAmount(int totalAmount) {
                this.totalAmount = totalAmount;
            }

            public int getTransReferNum() {
                return transReferNum;
            }

            public void setTransReferNum(int transReferNum) {
                this.transReferNum = transReferNum;
            }

            public static class CreateTimeBean {
                /**
                 * date : 9
                 * day : 2
                 * hours : 18
                 * minutes : 35
                 * month : 6
                 * seconds : 10
                 * time : 1562668510000
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
