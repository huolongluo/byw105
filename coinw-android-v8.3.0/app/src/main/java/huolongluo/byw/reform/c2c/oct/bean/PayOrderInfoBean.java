package huolongluo.byw.reform.c2c.oct.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.List;

/**
 * Created by dell on 2019/6/5.
 */

public class PayOrderInfoBean implements Serializable {

    /**
     * code : 0
     * data : {"canComplaint":0,"day30":15,"loginUid":67351,"order":{"account":"","adId":30126,"adStatus":0,"adUserId":67353,"adUserName":"_哈哈霍霍_","amount":21,"coinId":2,"coinName":"CNYT","createTime":{"date":17,"day":1,"hours":13,"minutes":36,"month":5,"seconds":28,"time":1560749788000,"timezoneOffset":-480,"year":119},"createTime_s":"2019-06-17 13:36:28","endTime":"","fee":0,"id":30594,"payLimit":60,"payType":0,"price":1,"qrCode":"","remark":"","startTime":"","status":0,"totalAmount":21,"transReferNum":403334,"type":1,"uid":67351,"updateTime":{"date":17,"day":1,"hours":13,"minutes":36,"month":5,"seconds":28,"time":1560749788000,"timezoneOffset":-480,"year":119},"userName":"爱海洋","version":0},"otcUserinfo":{"advancedOrgAudit":false,"advancedOrgProtocol":false,"alipay":"","apply":false,"createTime":{"date":20,"day":1,"hours":18,"minutes":58,"month":4,"seconds":42,"time":1558349922000,"timezoneOffset":-480,"year":119},"email":"474744140@qq.com","id":69,"nickname":"_哈哈霍霍_","otcLevel":2,"otcLevelName":"初级商家","platformAccount":0,"qq":"","skype":"","telegram":"","telephone":"13881724497","textMessage":false,"uid":67353,"updateTime":{"date":17,"day":1,"hours":10,"minutes":39,"month":5,"seconds":21,"time":1560739161000,"timezoneOffset":-480,"year":119},"wechat":"","whasapp":""},"payMent":{"alipay":{"account":"bbbbb","createTime":{"date":20,"day":1,"hours":19,"minutes":1,"month":4,"seconds":33,"time":1558350093000,"timezoneOffset":-480,"year":119},"id":38,"qrcode":"https://btc018.oss-cn-shenzhen.aliyuncs.com/201905201900053_vsodu.png","realName":"廖广强","type":1,"uid":67353,"updateTime":{"date":6,"day":4,"hours":16,"minutes":13,"month":5,"seconds":16,"time":1559808796000,"timezoneOffset":-480,"year":119}},"bank":{"bankAdress":"南开支行","bankCity":"南开区","bankName":"工商银行","bankNum":"62220231000357159","bankProvince":"天津","createTime":{"date":20,"day":1,"hours":19,"minutes":0,"month":4,"seconds":17,"time":1558350017000,"timezoneOffset":-480,"year":119},"id":18,"realName":"廖广强","uid":67353,"updateTime":{"date":23,"day":4,"hours":16,"minutes":31,"month":4,"seconds":32,"time":1558600292000,"timezoneOffset":-480,"year":119}},"wechat":{"account":"_我的微信_","createTime":{"date":20,"day":1,"hours":19,"minutes":1,"month":4,"seconds":8,"time":1558350068000,"timezoneOffset":-480,"year":119},"id":37,"qrcode":"https://btc018.oss-cn-shenzhen.aliyuncs.com/201905201900053_vsodu.png","realName":"廖广强","type":2,"uid":67353,"updateTime":{"date":20,"day":1,"hours":19,"minutes":1,"month":4,"seconds":8,"time":1558350068000,"timezoneOffset":-480,"year":119}}},"priorityPay":0,"startTime":1560749788,"timeLimit":3419,"timeout":0,"timestamp":1560749969}
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
         * canComplaint : 0
         * day30 : 15
         * loginUid : 67351
         * order : {"account":"","adId":30126,"adStatus":0,"adUserId":67353,"adUserName":"_哈哈霍霍_","amount":21,"coinId":2,"coinName":"CNYT","createTime":{"date":17,"day":1,"hours":13,"minutes":36,"month":5,"seconds":28,"time":1560749788000,"timezoneOffset":-480,"year":119},"createTime_s":"2019-06-17 13:36:28","endTime":"","fee":0,"id":30594,"payLimit":60,"payType":0,"price":1,"qrCode":"","remark":"","startTime":"","status":0,"totalAmount":21,"transReferNum":403334,"type":1,"uid":67351,"updateTime":{"date":17,"day":1,"hours":13,"minutes":36,"month":5,"seconds":28,"time":1560749788000,"timezoneOffset":-480,"year":119},"userName":"爱海洋","version":0}
         * otcUserinfo : {"advancedOrgAudit":false,"advancedOrgProtocol":false,"alipay":"","apply":false,"createTime":{"date":20,"day":1,"hours":18,"minutes":58,"month":4,"seconds":42,"time":1558349922000,"timezoneOffset":-480,"year":119},"email":"474744140@qq.com","id":69,"nickname":"_哈哈霍霍_","otcLevel":2,"otcLevelName":"初级商家","platformAccount":0,"qq":"","skype":"","telegram":"","telephone":"13881724497","textMessage":false,"uid":67353,"updateTime":{"date":17,"day":1,"hours":10,"minutes":39,"month":5,"seconds":21,"time":1560739161000,"timezoneOffset":-480,"year":119},"wechat":"","whasapp":""}
         * payMent : {"alipay":{"account":"bbbbb","createTime":{"date":20,"day":1,"hours":19,"minutes":1,"month":4,"seconds":33,"time":1558350093000,"timezoneOffset":-480,"year":119},"id":38,"qrcode":"https://btc018.oss-cn-shenzhen.aliyuncs.com/201905201900053_vsodu.png","realName":"廖广强","type":1,"uid":67353,"updateTime":{"date":6,"day":4,"hours":16,"minutes":13,"month":5,"seconds":16,"time":1559808796000,"timezoneOffset":-480,"year":119}},"bank":{"bankAdress":"南开支行","bankCity":"南开区","bankName":"工商银行","bankNum":"62220231000357159","bankProvince":"天津","createTime":{"date":20,"day":1,"hours":19,"minutes":0,"month":4,"seconds":17,"time":1558350017000,"timezoneOffset":-480,"year":119},"id":18,"realName":"廖广强","uid":67353,"updateTime":{"date":23,"day":4,"hours":16,"minutes":31,"month":4,"seconds":32,"time":1558600292000,"timezoneOffset":-480,"year":119}},"wechat":{"account":"_我的微信_","createTime":{"date":20,"day":1,"hours":19,"minutes":1,"month":4,"seconds":8,"time":1558350068000,"timezoneOffset":-480,"year":119},"id":37,"qrcode":"https://btc018.oss-cn-shenzhen.aliyuncs.com/201905201900053_vsodu.png","realName":"廖广强","type":2,"uid":67353,"updateTime":{"date":20,"day":1,"hours":19,"minutes":1,"month":4,"seconds":8,"time":1558350068000,"timezoneOffset":-480,"year":119}}}
         * priorityPay : 0
         * startTime : 1560749788
         * timeLimit : 3419
         * timeout : 0
         * timestamp : 1560749969
         */
        private String coinIdLogo;

        public String getCoinIdLogo() {
            return coinIdLogo;
        }

        public void setCoinIdLogo(String coinIdLogo) {
            this.coinIdLogo = coinIdLogo;
        }

        private String adRemark;//商户备注

        private String complaintPayedTips;//时间描述

        private boolean isCanComplaint;
        private int complaintResidueTime;//申诉倒计时

        private String sellPayTimeLimit;//是否可申诉
        private String oppositeUserName;//对方姓名
        private String information;//对方电话
        private int canComplaint;
        private int day30;
        private int loginUid;
        private OrderBean order;
        private OtcUserinfoBean otcUserinfo;
        private PayMentBean payMent;
        private int priorityPay;
        private long startTime;
        private int payLimit;
        private int timeout;
        private long timestamp;
        private int dealType;

        private List<PayAccountsBean> payAccounts;

        public List<PayAccountsBean> getPayAccounts() {
            return payAccounts;
        }

        public void setPayAccounts(List<PayAccountsBean> payAccounts) {
            this.payAccounts = payAccounts;
        }

        public int getDealType() {
            return dealType;
        }

        public void setDealType(int dealType) {
            this.dealType = dealType;
        }

        public int getCanComplaint() {
            return canComplaint;
        }

        public void setCanComplaint(int canComplaint) {
            this.canComplaint = canComplaint;
        }

        public int getDay30() {
            return day30;
        }

        public void setDay30(int day30) {
            this.day30 = day30;
        }

        public int getLoginUid() {
            return loginUid;
        }

        public void setLoginUid(int loginUid) {
            this.loginUid = loginUid;
        }

        public OrderBean getOrder() {
            return order;
        }

        public String getOppositeUserName() {
            return oppositeUserName;
        }

        public void setOppositeUserName(String oppositeUserName) {
            this.oppositeUserName = oppositeUserName;
        }

        public void setOrder(OrderBean order) {
            this.order = order;
        }

        public OtcUserinfoBean getOtcUserinfo() {
            return otcUserinfo;
        }

        public void setOtcUserinfo(OtcUserinfoBean otcUserinfo) {
            this.otcUserinfo = otcUserinfo;
        }

        public PayMentBean getPayMent() {
            return payMent;
        }

        public void setPayMent(PayMentBean payMent) {
            this.payMent = payMent;
        }

        public int getPriorityPay() {
            return priorityPay;
        }

        public void setPriorityPay(int priorityPay) {
            this.priorityPay = priorityPay;
        }

        public long getStartTime() {
            return startTime;
        }

        public void setStartTime(long startTime) {
            this.startTime = startTime;
        }

        public int getTimeout() {
            return timeout;
        }

        public void setTimeout(int timeout) {
            this.timeout = timeout;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }

        public String getInformation() {
            return information;
        }

        public void setInformation(String information) {
            this.information = information;
        }

        public String getSellPayTimeLimit() {
            return sellPayTimeLimit;
        }

        public void setSellPayTimeLimit(String sellPayTimeLimit) {
            this.sellPayTimeLimit = sellPayTimeLimit;
        }

        public int getComplaintResidueTime() {
            return complaintResidueTime;
        }

        public void setComplaintResidueTime(int complaintResidueTime) {
            this.complaintResidueTime = complaintResidueTime;
        }

        public boolean isCanComplaint() {
            return isCanComplaint;
        }

        public void setCanComplaint(boolean canComplaint) {
            isCanComplaint = canComplaint;
        }

        public int getPayLimit() {
            return payLimit;
        }

        public void setPayLimit(int payLimit) {
            this.payLimit = payLimit;
        }

        public String getComplaintPayedTips() {
            return complaintPayedTips;
        }

        public void setComplaintPayedTips(String complaintPayedTips) {
            this.complaintPayedTips = complaintPayedTips;
        }

        public String getAdRemark() {
            return adRemark;
        }

        public void setAdRemark(String adRemark) {
            this.adRemark = adRemark;
        }

        public static class OrderBean {
            /**
             * account :
             * adId : 30126
             * adStatus : 0
             * adUserId : 67353
             * adUserName : _哈哈霍霍_
             * amount : 21
             * coinId : 2
             * coinName : CNYT
             * createTime : {"date":17,"day":1,"hours":13,"minutes":36,"month":5,"seconds":28,"time":1560749788000,"timezoneOffset":-480,"year":119}
             * createTime_s : 2019-06-17 13:36:28
             * endTime :
             * fee : 0
             * id : 30594
             * payLimit : 60
             * payType : 0
             * price : 1
             * qrCode :
             * remark :
             * startTime :
             * status : 0
             * totalAmount : 21
             * transReferNum : 403334
             * type : 1
             * uid : 67351
             * updateTime : {"date":17,"day":1,"hours":13,"minutes":36,"month":5,"seconds":28,"time":1560749788000,"timezoneOffset":-480,"year":119}
             * userName : 爱海洋
             * version : 0
             */

            private String orderNo;//新加，用于显示订单号
            private boolean isSellerSelectedPayType;

            public boolean isSellerSelectedPayType() {
                return isSellerSelectedPayType;
            }

            public void setSellerSelectedPayType(boolean sellerSelectedPayType) {
                isSellerSelectedPayType = sellerSelectedPayType;
            }

            private boolean isCoinWAd;

            public boolean isCoinWAd() {
                return isCoinWAd;
            }

            public void setCoinWAd(boolean coinWAd) {
                isCoinWAd = coinWAd;
            }

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
            private int payLimit;
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
            private String confirmReceiptMsg;
            private boolean isOneKey;

            public boolean isOneKey() {
                return isOneKey;
            }

            public void setOneKey(boolean oneKey) {
                isOneKey = oneKey;
            }

            public String getConfirmReceiptMsg() {
                return confirmReceiptMsg;
            }

            public void setConfirmReceiptMsg(String confirmReceiptMsg) {
                this.confirmReceiptMsg = confirmReceiptMsg;
            }

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

            private List<MessageListBean> messageList;

            public List<MessageListBean> getMessageList() {
                return messageList;
            }

            public void setMessageList(List<MessageListBean> messageList) {
                this.messageList = messageList;
            }

            public static class MessageListBean {
                /**
                 * result : true
                 * code : -1
                 * value : 请尽快选择收款方式，60秒后未选择，系统会自动取消订单；每日取消200次将会限制当天交易。
                 * error : 业务异常
                 */

                private boolean result;
                private String code;
                private String value;
                private String error;

                public boolean isResult() {
                    return result;
                }

                public void setResult(boolean result) {
                    this.result = result;
                }

                public String getCode() {
                    return code;
                }

                public void setCode(String code) {
                    this.code = code;
                }

                public String getValue() {
                    return value;
                }

                public void setValue(String value) {
                    this.value = value;
                }

                public String getError() {
                    return error;
                }

                public void setError(String error) {
                    this.error = error;
                }
            }
            public static class CreateTimeBean {
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

            public static class UpdateTimeBean {
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
        }

        public static class OtcUserinfoBean {
            /**
             * advancedOrgAudit : false
             * advancedOrgProtocol : false
             * alipay :
             * apply : false
             * createTime : {"date":20,"day":1,"hours":18,"minutes":58,"month":4,"seconds":42,"time":1558349922000,"timezoneOffset":-480,"year":119}
             * email : 474744140@qq.com
             * id : 69
             * nickname : _哈哈霍霍_
             * otcLevel : 2
             * otcLevelName : 初级商家
             * platformAccount : 0
             * qq :
             * skype :
             * telegram :
             * telephone : 13881724497
             * textMessage : false
             * uid : 67353
             * updateTime : {"date":17,"day":1,"hours":10,"minutes":39,"month":5,"seconds":21,"time":1560739161000,"timezoneOffset":-480,"year":119}
             * wechat :
             * whasapp :
             */

            private boolean advancedOrgAudit;
            private boolean advancedOrgProtocol;
            private String alipay;
            private boolean apply;
            private CreateTimeBeanX createTime;
            private String email;
            private int id;
            private String nickname;
            private int otcLevel;
            private String otcLevelName;
            private int platformAccount;
            private String qq;
            private String skype;
            private String telegram;
            private String telephone;
            private boolean textMessage;
            private int uid;
            private UpdateTimeBeanX updateTime;
            private String wechat;
            private String whasapp;

            public boolean isAdvancedOrgAudit() {
                return advancedOrgAudit;
            }

            public void setAdvancedOrgAudit(boolean advancedOrgAudit) {
                this.advancedOrgAudit = advancedOrgAudit;
            }

            public boolean isAdvancedOrgProtocol() {
                return advancedOrgProtocol;
            }

            public void setAdvancedOrgProtocol(boolean advancedOrgProtocol) {
                this.advancedOrgProtocol = advancedOrgProtocol;
            }

            public String getAlipay() {
                return alipay;
            }

            public void setAlipay(String alipay) {
                this.alipay = alipay;
            }

            public boolean isApply() {
                return apply;
            }

            public void setApply(boolean apply) {
                this.apply = apply;
            }

            public CreateTimeBeanX getCreateTime() {
                return createTime;
            }

            public void setCreateTime(CreateTimeBeanX createTime) {
                this.createTime = createTime;
            }

            public String getEmail() {
                return email;
            }

            public void setEmail(String email) {
                this.email = email;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getNickname() {
                return nickname;
            }

            public void setNickname(String nickname) {
                this.nickname = nickname;
            }

            public int getOtcLevel() {
                return otcLevel;
            }

            public void setOtcLevel(int otcLevel) {
                this.otcLevel = otcLevel;
            }

            public String getOtcLevelName() {
                return otcLevelName;
            }

            public void setOtcLevelName(String otcLevelName) {
                this.otcLevelName = otcLevelName;
            }

            public int getPlatformAccount() {
                return platformAccount;
            }

            public void setPlatformAccount(int platformAccount) {
                this.platformAccount = platformAccount;
            }

            public String getQq() {
                return qq;
            }

            public void setQq(String qq) {
                this.qq = qq;
            }

            public String getSkype() {
                return skype;
            }

            public void setSkype(String skype) {
                this.skype = skype;
            }

            public String getTelegram() {
                return telegram;
            }

            public void setTelegram(String telegram) {
                this.telegram = telegram;
            }

            public String getTelephone() {
                return telephone;
            }

            public void setTelephone(String telephone) {
                this.telephone = telephone;
            }

            public boolean isTextMessage() {
                return textMessage;
            }

            public void setTextMessage(boolean textMessage) {
                this.textMessage = textMessage;
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

            public String getWechat() {
                return wechat;
            }

            public void setWechat(String wechat) {
                this.wechat = wechat;
            }

            public String getWhasapp() {
                return whasapp;
            }

            public void setWhasapp(String whasapp) {
                this.whasapp = whasapp;
            }

            public static class CreateTimeBeanX {
                /**
                 * date : 20
                 * day : 1
                 * hours : 18
                 * minutes : 58
                 * month : 4
                 * seconds : 42
                 * time : 1558349922000
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
                 * date : 17
                 * day : 1
                 * hours : 10
                 * minutes : 39
                 * month : 5
                 * seconds : 21
                 * time : 1560739161000
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

        public static class PayMentBean implements Parcelable {
            /**
             * alipay : {"account":"bbbbb","createTime":{"date":20,"day":1,"hours":19,"minutes":1,"month":4,"seconds":33,"time":1558350093000,"timezoneOffset":-480,"year":119},"id":38,"qrcode":"https://btc018.oss-cn-shenzhen.aliyuncs.com/201905201900053_vsodu.png","realName":"廖广强","type":1,"uid":67353,"updateTime":{"date":6,"day":4,"hours":16,"minutes":13,"month":5,"seconds":16,"time":1559808796000,"timezoneOffset":-480,"year":119}}
             * bank : {"bankAdress":"南开支行","bankCity":"南开区","bankName":"工商银行","bankNum":"62220231000357159","bankProvince":"天津","createTime":{"date":20,"day":1,"hours":19,"minutes":0,"month":4,"seconds":17,"time":1558350017000,"timezoneOffset":-480,"year":119},"id":18,"realName":"廖广强","uid":67353,"updateTime":{"date":23,"day":4,"hours":16,"minutes":31,"month":4,"seconds":32,"time":1558600292000,"timezoneOffset":-480,"year":119}}
             * wechat : {"account":"_我的微信_","createTime":{"date":20,"day":1,"hours":19,"minutes":1,"month":4,"seconds":8,"time":1558350068000,"timezoneOffset":-480,"year":119},"id":37,"qrcode":"https://btc018.oss-cn-shenzhen.aliyuncs.com/201905201900053_vsodu.png","realName":"廖广强","type":2,"uid":67353,"updateTime":{"date":20,"day":1,"hours":19,"minutes":1,"month":4,"seconds":8,"time":1558350068000,"timezoneOffset":-480,"year":119}}
             */

            private AlipayBean alipay;
            private BankBean bank;
            private WechatBean wechat;

            protected PayMentBean(Parcel in) {
                alipay = in.readParcelable(AlipayBean.class.getClassLoader());
                bank = in.readParcelable(BankBean.class.getClassLoader());
                wechat = in.readParcelable(WechatBean.class.getClassLoader());
            }

            public static final Creator<PayMentBean> CREATOR = new Creator<PayMentBean>() {
                @Override
                public PayMentBean createFromParcel(Parcel in) {
                    return new PayMentBean(in);
                }

                @Override
                public PayMentBean[] newArray(int size) {
                    return new PayMentBean[size];
                }
            };

            public AlipayBean getAlipay() {
                return alipay;
            }

            public void setAlipay(AlipayBean alipay) {
                this.alipay = alipay;
            }

            public BankBean getBank() {
                return bank;
            }

            public void setBank(BankBean bank) {
                this.bank = bank;
            }

            public WechatBean getWechat() {
                return wechat;
            }

            public void setWechat(WechatBean wechat) {
                this.wechat = wechat;
            }

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
                dest.writeParcelable(alipay, flags);
                dest.writeParcelable(bank, flags);
                dest.writeParcelable(wechat, flags);
            }


            public static class AlipayBean implements Parcelable {
                /**
                 * account : bbbbb
                 * createTime : {"date":20,"day":1,"hours":19,"minutes":1,"month":4,"seconds":33,"time":1558350093000,"timezoneOffset":-480,"year":119}
                 * id : 38
                 * qrcode : https://btc018.oss-cn-shenzhen.aliyuncs.com/201905201900053_vsodu.png
                 * realName : 廖广强
                 * type : 1
                 * uid : 67353
                 * updateTime : {"date":6,"day":4,"hours":16,"minutes":13,"month":5,"seconds":16,"time":1559808796000,"timezoneOffset":-480,"year":119}
                 */

                private String account;
                private CreateTimeBeanXX createTime;
                private int id;
                private String qrcode;
                private String realName;
                private int type;
                private int uid;
                private UpdateTimeBeanXX updateTime;

                protected AlipayBean(Parcel in) {
                    account = in.readString();
                    id = in.readInt();
                    qrcode = in.readString();
                    realName = in.readString();
                    type = in.readInt();
                    uid = in.readInt();
                }

                public static final Creator<AlipayBean> CREATOR = new Creator<AlipayBean>() {
                    @Override
                    public AlipayBean createFromParcel(Parcel in) {
                        return new AlipayBean(in);
                    }

                    @Override
                    public AlipayBean[] newArray(int size) {
                        return new AlipayBean[size];
                    }
                };

                public String getAccount() {
                    return account;
                }

                public void setAccount(String account) {
                    this.account = account;
                }

                public CreateTimeBeanXX getCreateTime() {
                    return createTime;
                }

                public void setCreateTime(CreateTimeBeanXX createTime) {
                    this.createTime = createTime;
                }

                public int getId() {
                    return id;
                }

                public void setId(int id) {
                    this.id = id;
                }

                public String getQrcode() {
                    return qrcode;
                }

                public void setQrcode(String qrcode) {
                    this.qrcode = qrcode;
                }

                public String getRealName() {
                    return realName;
                }

                public void setRealName(String realName) {
                    this.realName = realName;
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

                public UpdateTimeBeanXX getUpdateTime() {
                    return updateTime;
                }

                public void setUpdateTime(UpdateTimeBeanXX updateTime) {
                    this.updateTime = updateTime;
                }

                @Override
                public int describeContents() {
                    return 0;
                }

                @Override
                public void writeToParcel(Parcel dest, int flags) {
                    dest.writeString(account);
                    dest.writeInt(id);
                    dest.writeString(qrcode);
                    dest.writeString(realName);
                    dest.writeInt(type);
                    dest.writeInt(uid);
                }

                public static class CreateTimeBeanXX {
                    /**
                     * date : 20
                     * day : 1
                     * hours : 19
                     * minutes : 1
                     * month : 4
                     * seconds : 33
                     * time : 1558350093000
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

                public static class UpdateTimeBeanXX {
                    /**
                     * date : 6
                     * day : 4
                     * hours : 16
                     * minutes : 13
                     * month : 5
                     * seconds : 16
                     * time : 1559808796000
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

            public static class BankBean implements Parcelable {
                /**
                 * bankAdress : 南开支行
                 * bankCity : 南开区
                 * bankName : 工商银行
                 * bankNum : 62220231000357159
                 * bankProvince : 天津
                 * createTime : {"date":20,"day":1,"hours":19,"minutes":0,"month":4,"seconds":17,"time":1558350017000,"timezoneOffset":-480,"year":119}
                 * id : 18
                 * realName : 廖广强
                 * uid : 67353
                 * updateTime : {"date":23,"day":4,"hours":16,"minutes":31,"month":4,"seconds":32,"time":1558600292000,"timezoneOffset":-480,"year":119}
                 */

                private String bankAdress;
                private String bankCity;
                private String bankName;
                private String bankNum;
                private String bankProvince;
                private CreateTimeBeanXXX createTime;
                private int id;
                private String realName;
                private int uid;
                private UpdateTimeBeanXXX updateTime;

                protected BankBean(Parcel in) {
                    bankAdress = in.readString();
                    bankCity = in.readString();
                    bankName = in.readString();
                    bankNum = in.readString();
                    bankProvince = in.readString();
                    id = in.readInt();
                    realName = in.readString();
                    uid = in.readInt();
                }

                public static final Creator<BankBean> CREATOR = new Creator<BankBean>() {
                    @Override
                    public BankBean createFromParcel(Parcel in) {
                        return new BankBean(in);
                    }

                    @Override
                    public BankBean[] newArray(int size) {
                        return new BankBean[size];
                    }
                };

                public String getBankAdress() {
                    return bankAdress;
                }

                public void setBankAdress(String bankAdress) {
                    this.bankAdress = bankAdress;
                }

                public String getBankCity() {
                    return bankCity;
                }

                public void setBankCity(String bankCity) {
                    this.bankCity = bankCity;
                }

                public String getBankName() {
                    return bankName;
                }

                public void setBankName(String bankName) {
                    this.bankName = bankName;
                }

                public String getBankNum() {
                    return bankNum;
                }

                public void setBankNum(String bankNum) {
                    this.bankNum = bankNum;
                }

                public String getBankProvince() {
                    return bankProvince;
                }

                public void setBankProvince(String bankProvince) {
                    this.bankProvince = bankProvince;
                }

                public CreateTimeBeanXXX getCreateTime() {
                    return createTime;
                }

                public void setCreateTime(CreateTimeBeanXXX createTime) {
                    this.createTime = createTime;
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

                public int getUid() {
                    return uid;
                }

                public void setUid(int uid) {
                    this.uid = uid;
                }

                public UpdateTimeBeanXXX getUpdateTime() {
                    return updateTime;
                }

                public void setUpdateTime(UpdateTimeBeanXXX updateTime) {
                    this.updateTime = updateTime;
                }

                @Override
                public int describeContents() {
                    return 0;
                }

                @Override
                public void writeToParcel(Parcel dest, int flags) {
                    dest.writeString(bankAdress);
                    dest.writeString(bankCity);
                    dest.writeString(bankName);
                    dest.writeString(bankNum);
                    dest.writeString(bankProvince);
                    dest.writeInt(id);
                    dest.writeString(realName);
                    dest.writeInt(uid);
                }

                public static class CreateTimeBeanXXX {
                    /**
                     * date : 20
                     * day : 1
                     * hours : 19
                     * minutes : 0
                     * month : 4
                     * seconds : 17
                     * time : 1558350017000
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

                public static class UpdateTimeBeanXXX {
                    /**
                     * date : 23
                     * day : 4
                     * hours : 16
                     * minutes : 31
                     * month : 4
                     * seconds : 32
                     * time : 1558600292000
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

            public static class WechatBean implements Parcelable {
                /**
                 * account : _我的微信_
                 * createTime : {"date":20,"day":1,"hours":19,"minutes":1,"month":4,"seconds":8,"time":1558350068000,"timezoneOffset":-480,"year":119}
                 * id : 37
                 * qrcode : https://btc018.oss-cn-shenzhen.aliyuncs.com/201905201900053_vsodu.png
                 * realName : 廖广强
                 * type : 2
                 * uid : 67353
                 * updateTime : {"date":20,"day":1,"hours":19,"minutes":1,"month":4,"seconds":8,"time":1558350068000,"timezoneOffset":-480,"year":119}
                 */

                private String account;
                private CreateTimeBeanXXXX createTime;
                private int id;
                private String qrcode;
                private String realName;
                private int type;
                private int uid;
                private UpdateTimeBeanXXXX updateTime;

                protected WechatBean(Parcel in) {
                    account = in.readString();
                    id = in.readInt();
                    qrcode = in.readString();
                    realName = in.readString();
                    type = in.readInt();
                    uid = in.readInt();
                }

                public static final Creator<WechatBean> CREATOR = new Creator<WechatBean>() {
                    @Override
                    public WechatBean createFromParcel(Parcel in) {
                        return new WechatBean(in);
                    }

                    @Override
                    public WechatBean[] newArray(int size) {
                        return new WechatBean[size];
                    }
                };

                public String getAccount() {
                    return account;
                }

                public void setAccount(String account) {
                    this.account = account;
                }

                public CreateTimeBeanXXXX getCreateTime() {
                    return createTime;
                }

                public void setCreateTime(CreateTimeBeanXXXX createTime) {
                    this.createTime = createTime;
                }

                public int getId() {
                    return id;
                }

                public void setId(int id) {
                    this.id = id;
                }

                public String getQrcode() {
                    return qrcode;
                }

                public void setQrcode(String qrcode) {
                    this.qrcode = qrcode;
                }

                public String getRealName() {
                    return realName;
                }

                public void setRealName(String realName) {
                    this.realName = realName;
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

                public UpdateTimeBeanXXXX getUpdateTime() {
                    return updateTime;
                }

                public void setUpdateTime(UpdateTimeBeanXXXX updateTime) {
                    this.updateTime = updateTime;
                }

                @Override
                public int describeContents() {
                    return 0;
                }

                @Override
                public void writeToParcel(Parcel dest, int flags) {
                    dest.writeString(account);
                    dest.writeInt(id);
                    dest.writeString(qrcode);
                    dest.writeString(realName);
                    dest.writeInt(type);
                    dest.writeInt(uid);
                }

                public static class CreateTimeBeanXXXX {
                    /**
                     * date : 20
                     * day : 1
                     * hours : 19
                     * minutes : 1
                     * month : 4
                     * seconds : 8
                     * time : 1558350068000
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

                public static class UpdateTimeBeanXXXX {
                    /**
                     * date : 20
                     * day : 1
                     * hours : 19
                     * minutes : 1
                     * month : 4
                     * seconds : 8
                     * time : 1558350068000
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

    public static class PayAccountsBean implements Serializable {
        /**
         * account : alipay2
         * bankAdress :
         * bankCity :
         * bankName :
         * bankProvince :
         * id : 45
         * qrcode : /upload/images/201909291734048_D7si9.jpg
         * realName : MAX
         * type : 3
         */

        private String account;
        private String bankAdress;
        private String bankCity;
        private String bankName;
        private String bankProvince;
        private int id;
        private String qrcode;
        private String realName;
        private int type;

        public String getAccount() {
            return account;
        }

        public void setAccount(String account) {
            this.account = account;
        }

        public String getBankAdress() {
            return bankAdress;
        }

        public void setBankAdress(String bankAdress) {
            this.bankAdress = bankAdress;
        }

        public String getBankCity() {
            return bankCity;
        }

        public void setBankCity(String bankCity) {
            this.bankCity = bankCity;
        }

        public String getBankName() {
            return bankName;
        }

        public void setBankName(String bankName) {
            this.bankName = bankName;
        }

        public String getBankProvince() {
            return bankProvince;
        }

        public void setBankProvince(String bankProvince) {
            this.bankProvince = bankProvince;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getQrcode() {
            return qrcode;
        }

        public void setQrcode(String qrcode) {
            this.qrcode = qrcode;
        }

        public String getRealName() {
            return realName;
        }

        public void setRealName(String realName) {
            this.realName = realName;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }
    }

}
