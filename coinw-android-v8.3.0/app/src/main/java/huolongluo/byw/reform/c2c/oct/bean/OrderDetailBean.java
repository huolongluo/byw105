package huolongluo.byw.reform.c2c.oct.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by dell on 2019/6/11.
 */

public class OrderDetailBean {

    /**
     * code : 0
     * data : {"information":"13881724497","orders":{"account":"","adId":30126,"adStatus":3,"adUserId":67353,"adUserName":"_哈哈霍霍_","amount":21,"coinId":2,"coinName":"CNYT","createTime":{"date":17,"day":1,"hours":13,"minutes":36,"month":5,"seconds":28,"time":1560749788000,"timezoneOffset":-480,"year":119},"createTime_s":"","endTime":"","fee":0,"id":30594,"payLimit":60,"payType":0,"price":1,"qrCode":"","remark":"","startTime":"","status":3,"totalAmount":21,"transReferNum":403334,"type":1,"uid":67351,"updateTime":{"date":17,"day":1,"hours":14,"minutes":16,"month":5,"seconds":31,"time":1560752191000,"timezoneOffset":-480,"year":119},"userName":"爱海洋","version":1},"payWay":{"account":"","accountQrcode":"","bankAdress":"","bankName":"","id":0,"realName":"","type":""},"timeLimit":1197}
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
         * information : 13881724497
         * orders : {"account":"","adId":30126,"adStatus":3,"adUserId":67353,"adUserName":"_哈哈霍霍_","amount":21,"coinId":2,"coinName":"CNYT","createTime":{"date":17,"day":1,"hours":13,"minutes":36,"month":5,"seconds":28,"time":1560749788000,"timezoneOffset":-480,"year":119},"createTime_s":"","endTime":"","fee":0,"id":30594,"payLimit":60,"payType":0,"price":1,"qrCode":"","remark":"","startTime":"","status":3,"totalAmount":21,"transReferNum":403334,"type":1,"uid":67351,"updateTime":{"date":17,"day":1,"hours":14,"minutes":16,"month":5,"seconds":31,"time":1560752191000,"timezoneOffset":-480,"year":119},"userName":"爱海洋","version":1}
         * payWay : {"account":"","accountQrcode":"","bankAdress":"","bankName":"","id":0,"realName":"","type":""}
         * timeLimit : 1197
         */

        private boolean isCanComplaint;//是否可申诉
        private int complaintResidueTime;//申诉倒计时
        private String oppositeUserName;//对方姓名

        private String complaintPayedTips;//提示
        private String complaintCompleteTips;//提示
        private String information;//是否可申诉，0可以，1不可以
        private String cancelComplaint;//申诉方 0 , 对方发起申诉 , 1, 我方发起申诉,
        private OrdersBean orders;


        public ComplaintInfoBean getComplaintInfo() {
            return complaintInfo;
        }

        public void setComplaintInfo(ComplaintInfoBean complaintInfo) {
            this.complaintInfo = complaintInfo;
        }

        private ComplaintInfoBean complaintInfo;
        private PayWayBean payWay;
        private long timeLimit;
        private int canComplaint;//申诉方 0 , 对方发起申诉 , 1, 我方发起申诉,
        private int complainTimeLimit;//可申诉倒计时
        private List<PayOrderInfoBean.PayAccountsBean> payAccounts;

        public List<PayOrderInfoBean.PayAccountsBean> getPayAccounts() {
            return payAccounts;
        }

        public void setPayAccounts(List<PayOrderInfoBean.PayAccountsBean> payAccounts) {
            this.payAccounts = payAccounts;
        }

        public String getInformation() {
            return information;
        }

        private int dealType;

        public int getDealType() {
            return dealType;
        }

        public void setDealType(int dealType) {
            this.dealType = dealType;
        }

        public void setInformation(String information) {
            this.information = information;
        }

        //coinw的广告：true：coinw
        public OrdersBean getOrders() {
            return orders;
        }

        public void setOrders(OrdersBean orders) {
            this.orders = orders;
        }

        public PayWayBean getPayWay() {
            return payWay;
        }

        public void setPayWay(PayWayBean payWay) {
            this.payWay = payWay;
        }

        public long getTimeLimit() {
            return timeLimit;
        }

        public void setTimeLimit(long timeLimit) {
            this.timeLimit = timeLimit;
        }

        public int getCanComplaint() {
            return canComplaint;
        }

        public void setCanComplaint(int canComplaint) {
            this.canComplaint = canComplaint;
        }

        public String getCancelComplaint() {
            return cancelComplaint;
        }

        public void setCancelComplaint(String cancelComplaint) {
            this.cancelComplaint = cancelComplaint;
        }

        public int getComplainTimeLimit() {
            return complainTimeLimit;
        }

        public void setComplainTimeLimit(int complainTimeLimit) {
            this.complainTimeLimit = complainTimeLimit;
        }

        public String getComplaintPayedTips() {
            return complaintPayedTips;
        }

        public void setComplaintPayedTips(String complaintPayedTips) {
            this.complaintPayedTips = complaintPayedTips;
        }

        public String getOppositeUserName() {
            return oppositeUserName;
        }

        public void setOppositeUserName(String oppositeUserName) {
            this.oppositeUserName = oppositeUserName;
        }

        public boolean isCanComplaint() {
            return isCanComplaint;
        }

        public void setCanComplaint(boolean canComplaint) {
            isCanComplaint = canComplaint;
        }

        public int getComplaintResidueTime() {
            return complaintResidueTime;
        }

        public void setComplaintResidueTime(int complaintResidueTime) {
            this.complaintResidueTime = complaintResidueTime;
        }

        public String getComplaintCompleteTips() {
            return complaintCompleteTips;
        }

        public void setComplaintCompleteTips(String complaintCompleteTips) {
            this.complaintCompleteTips = complaintCompleteTips;
        }

        public static class ComplaintInfoBean {
            private String id;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public int getUid() {
                return uid;
            }

            public void setUid(int uid) {
                this.uid = uid;
            }

            public String getUname() {
                return uname;
            }

            public void setUname(String uname) {
                this.uname = uname;
            }

            public String getComplaintUid() {
                return complaintUid;
            }

            public void setComplaintUid(String complaintUid) {
                this.complaintUid = complaintUid;
            }

            public String getComplaintUname() {
                return complaintUname;
            }

            public void setComplaintUname(String complaintUname) {
                this.complaintUname = complaintUname;
            }

            public String getReasonUrl() {
                return reasonUrl;
            }

            public void setReasonUrl(String reasonUrl) {
                this.reasonUrl = reasonUrl;
            }

            public int getStatus() {
                return status;
            }

            public void setStatus(int status) {
                this.status = status;
            }

            public int getOrderStatus() {
                return orderStatus;
            }

            public void setOrderStatus(int orderStatus) {
                this.orderStatus = orderStatus;
            }

            public int getOrderStatusAfter() {
                return orderStatusAfter;
            }

            public void setOrderStatusAfter(int orderStatusAfter) {
                this.orderStatusAfter = orderStatusAfter;
            }

            public String getReason() {
                return reason;
            }

            public void setReason(String reason) {
                this.reason = reason;
            }

            private int uid;
            private String uname;
            private String complaintUid;
            private String complaintUname;
            private String reasonUrl;
            private int status;
            private int orderStatus;
            private int orderStatusAfter;
            private String reason;


        }

        private long payLimit;

        public long getPayLimit() {
            return payLimit;
        }

        public void setPayLimit(long payLimit) {
            this.payLimit = payLimit;
        }

        public static class OrdersBean implements Serializable {
            /**
             * account :
             * adId : 30126
             * adStatus : 3
             * adUserId : 67353
             * adUserName : _哈哈霍霍_
             * amount : 21
             * coinId : 2
             * coinName : CNYT
             * createTime : {"date":17,"day":1,"hours":13,"minutes":36,"month":5,"seconds":28,"time":1560749788000,"timezoneOffset":-480,"year":119}
             * createTime_s :
             * endTime :
             * fee : 0
             * id : 30594
             * payLimit : 60
             * payType : 0
             * price : 1
             * qrCode :
             * remark :
             * startTime :
             * status : 3
             * totalAmount : 21
             * transReferNum : 403334
             * type : 1
             * uid : 67351
             * updateTime : {"date":17,"day":1,"hours":14,"minutes":16,"month":5,"seconds":31,"time":1560752191000,"timezoneOffset":-480,"year":119}
             * userName : 爱海洋
             * version : 1
             */

            private List<PayOrderInfoBean.PayAccountsBean> sellerPayTypes;

            public List<PayOrderInfoBean.PayAccountsBean> getSellerPayTypes() {
                return sellerPayTypes;
            }

            public void setSellerPayTypes(List<PayOrderInfoBean.PayAccountsBean> sellerPayTypes) {
                this.sellerPayTypes = sellerPayTypes;
            }

            private boolean isCoinWAd;

            public boolean isCoinWAd() {
                return isCoinWAd;
            }

            public void setCoinWAd(boolean coinWAd) {
                isCoinWAd = coinWAd;
            }

            private String orderNo;//新加订单号，用于显示
            private String account;
            private int adId;
            private int adStatus;
            private int adUserId;
            private String adUserName;
            private double amount;
            private int coinId;
            private String coinName;
            private CreateTimeBean createTime;
            private String createTime_s;
            private String endTime;
            private double fee;
            private int id;
            private long payLimit;
            private int payType;
            private double price;
            private String qrCode;
            private String remark;
            private String startTime;
            private int status;
            private double totalAmount;
            private int transReferNum;
            private int type;
            private int uid;
            private UpdateTimeBean updateTime;
            private String userName;
            private int version;


            private PayOrderInfoBean.PayAccountsBean buyerSelectedSellerPayType;

            public PayOrderInfoBean.PayAccountsBean getBuyerSelectedSellerPayType() {
                return buyerSelectedSellerPayType;
            }

            public void setBuyerSelectedSellerPayType(PayOrderInfoBean.PayAccountsBean buyerSelectedSellerPayType) {
                this.buyerSelectedSellerPayType = buyerSelectedSellerPayType;
            }

            public String getAccount() {
                return account;
            }

            public void setAccount(String account) {
                this.account = account;
            }

            public int getAdId() {
                return adId;
            }

            public void setAdId(int adId) {
                this.adId = adId;
            }

            public int getAdStatus() {
                return adStatus;
            }

            public void setAdStatus(int adStatus) {
                this.adStatus = adStatus;
            }

            public int getAdUserId() {
                return adUserId;
            }

            public void setAdUserId(int adUserId) {
                this.adUserId = adUserId;
            }

            public String getAdUserName() {
                return adUserName;
            }

            public void setAdUserName(String adUserName) {
                this.adUserName = adUserName;
            }

            public double getAmount() {
                return amount;
            }

            public void setAmount(double amount) {
                this.amount = amount;
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

            public String getEndTime() {
                return endTime;
            }

            public void setEndTime(String endTime) {
                this.endTime = endTime;
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

            public long getPayLimit() {
                return payLimit;
            }

            public void setPayLimit(long payLimit) {
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

            public String getQrCode() {
                return qrCode;
            }

            public void setQrCode(String qrCode) {
                this.qrCode = qrCode;
            }

            public String getRemark() {
                return remark;
            }

            public void setRemark(String remark) {
                this.remark = remark;
            }

            public String getStartTime() {
                return startTime;
            }

            public void setStartTime(String startTime) {
                this.startTime = startTime;
            }

            public int getStatus() {
                return status;
            }

            public void setStatus(int status) {
                this.status = status;
            }

            public double getTotalAmount() {
                return totalAmount;
            }

            public void setTotalAmount(double totalAmount) {
                this.totalAmount = totalAmount;
            }

            public int getTransReferNum() {
                return transReferNum;
            }

            public void setTransReferNum(int transReferNum) {
                this.transReferNum = transReferNum;
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

            public UpdateTimeBean getUpdateTime() {
                return updateTime;
            }

            public void setUpdateTime(UpdateTimeBean updateTime) {
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

            public String getOrderNo() {
                return "#" + orderNo;
            }

            public void setOrderNo(String orderNo) {
                this.orderNo = orderNo;
            }

            public static class CreateTimeBean implements Serializable {
                /**
                 * date : 17
                 * day : 1
                 * hours : 13
                 * minutes : 36
                 * month : 5
                 * seconds : 28
                 * time : 1560749788000
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

            public static class UpdateTimeBean implements Serializable {
                /**
                 * date : 17
                 * day : 1
                 * hours : 14
                 * minutes : 16
                 * month : 5
                 * seconds : 31
                 * time : 1560752191000
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

        public static class PayWayBean {
            /**
             * account :
             * accountQrcode :
             * bankAdress :
             * bankName :
             * id : 0
             * realName :
             * type :
             */

            private String account;
            private String accountQrcode;
            private String bankAdress;
            private String bankName;
            private int id;
            private String realName;
            private String type;

            public String getAccount() {
                return account;
            }

            public void setAccount(String account) {
                this.account = account;
            }

            public String getAccountQrcode() {
                return accountQrcode;
            }

            public void setAccountQrcode(String accountQrcode) {
                this.accountQrcode = accountQrcode;
            }

            public String getBankAdress() {
                return bankAdress;
            }

            public void setBankAdress(String bankAdress) {
                this.bankAdress = bankAdress;
            }

            public String getBankName() {
                return bankName;
            }

            public void setBankName(String bankName) {
                this.bankName = bankName;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getRealName() {
                return realName;
            }

            public void setRealName(String realName) {
                this.realName = realName;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }
        }
    }
}
