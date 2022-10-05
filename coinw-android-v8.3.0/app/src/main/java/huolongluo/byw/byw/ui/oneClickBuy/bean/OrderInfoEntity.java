package huolongluo.byw.byw.ui.oneClickBuy.bean;

import java.util.List;

import huolongluo.byw.reform.c2c.oct.bean.PayOrderInfoBean;

public class OrderInfoEntity {

    /**
     * result : true
     * code : 0
     * value : 操作成功
     * data : {"adId":116,"adStatus":0,"adUserId":400185,"adUserName":"一键买币3","alreadyPayTime":null,"amount":123.45,"buyerSelectedSellerPayType":{"account":"45645121","bankAdress":"","bankCity":"","bankName":"","bankProvince":"","id":2440,"orders":null,"payAccount":{"account":"","bankAdress":"","bankCity":"","bankName":"","bankProvince":"","createTime":null,"id":66,"qrcode":"","realName":"","status":0,"type":3,"uid":0,"updateTime":null,"version":0},"qrcode":"https://btc018.oss-cn-shenzhen.aliyuncs.com/201912091521050_mRhPx.jpg","realName":"买币3","type":3},"c30":34,"canComplaint":1,"canComplaintMint":1,"cancelCounts":160,"cashDeposit":20000,"cashDepositName":"USDT","channel":"coinW","coinId":2,"coinLogoUrl":"https://btc018.oss-cn-shenzhen.aliyuncs.com/static/front/images/cnyt.png","coinName":"CNYT","complaintCompleteAfter":4,"complaintCompleteBefore":1,"complaintCreateTimestamp":0,"complaintPayed":1,"complaintPayedAfter":20,"complaintUid":0,"completeDealTime":null,"createTime":{"date":10,"day":5,"hours":16,"minutes":35,"month":0,"seconds":25,"time":1578645325000,"timezoneOffset":-480,"year":120},"createTime_s":"","endTime":"","fee":0,"id":1847,"isBuyerView":true,"isOneKey":true,"isOnline":false,"isSellerSelectedPayType":true,"loggerRealName":"真实姓名76","loginUid":400150,"orderNo":"20011016357025","ou":{"advancedOrgAudit":true,"advancedOrgProtocol":false,"alipay":"","allowOtc":true,"apply":false,"areaCode":"","cashDeposit":[],"cashOutMerchant":false,"createTime":{"date":9,"day":1,"hours":15,"minutes":5,"month":11,"seconds":45,"time":1575875145000,"timezoneOffset":-480,"year":119},"email":"","id":105,"isNewUser":false,"nickname":"一键买币3","oneKeyMatchingMerchant":false,"otcBond":0,"otcLevel":3,"otcLevelName":"认证商家","otherComplaintCount":0,"platformAccount":0,"qq":"","realName":"买币3","skype":"","telegram":"","telephone":"18507512677","textMessage":true,"uid":400185,"unrealCompleteOrders":0,"unrealCompleteOrdersSell":0,"unrealOrders":0,"updateTime":{"date":24,"day":2,"hours":10,"minutes":34,"month":11,"seconds":12,"time":1577154852000,"timezoneOffset":-480,"year":119},"userComplaintCount":0,"wechat":"","whasapp":"","yxAccount":""},"payLimit":3,"price":0.81,"randomColor":0,"realName":"买币3","remark":"","residueTime":-1,"residueTimeAfter":-1,"sellerPayTypes":[{"account":"4578154654745121","bankAdress":"火车南站","bankCity":"成都市","bankName":"工商银行","bankProvince":"四川省","id":2438,"orders":{"adId":0,"adStatus":0,"adUserId":0,"adUserName":"","alreadyPayTime":null,"amount":0,"buyerSelectedSellerPayType":null,"channel":"","coinId":0,"coinName":"","completeDealTime":null,"createTime":null,"createTime_s":"","endTime":"","fee":0,"id":1847,"isOneKey":false,"orderNo":"","payLimit":0,"price":0,"remark":"","sellerPayTypes":[],"startTime":"","status":0,"statusArray":[],"terminal":"","totalAmount":0,"transReferNum":0,"type":0,"uid":0,"updateTime":null,"userName":"","version":0},"payAccount":{"account":"","bankAdress":"","bankCity":"","bankName":"","bankProvince":"","createTime":null,"id":64,"qrcode":"","realName":"","status":0,"type":0,"uid":0,"updateTime":null,"version":0},"qrcode":"","realName":"买币3","type":1},{"account":"4512564","bankAdress":"","bankCity":"","bankName":"","bankProvince":"","id":2439,"orders":{"adId":0,"adStatus":0,"adUserId":0,"adUserName":"","alreadyPayTime":null,"amount":0,"buyerSelectedSellerPayType":null,"channel":"","coinId":0,"coinName":"","completeDealTime":null,"createTime":null,"createTime_s":"","endTime":"","fee":0,"id":1847,"isOneKey":false,"orderNo":"","payLimit":0,"price":0,"remark":"","sellerPayTypes":[],"startTime":"","status":0,"statusArray":[],"terminal":"","totalAmount":0,"transReferNum":0,"type":0,"uid":0,"updateTime":null,"userName":"","version":0},"payAccount":{"account":"","bankAdress":"","bankCity":"","bankName":"","bankProvince":"","createTime":null,"id":65,"qrcode":"","realName":"","status":0,"type":0,"uid":0,"updateTime":null,"version":0},"qrcode":"https://btc018.oss-cn-shenzhen.aliyuncs.com/201912091520038_CMbDN.jpg","realName":"买币3","type":2},{"account":"45645121","bankAdress":"","bankCity":"","bankName":"","bankProvince":"","id":2440,"orders":{"adId":0,"adStatus":0,"adUserId":0,"adUserName":"","alreadyPayTime":null,"amount":0,"buyerSelectedSellerPayType":null,"channel":"","coinId":0,"coinName":"","completeDealTime":null,"createTime":null,"createTime_s":"","endTime":"","fee":0,"id":1847,"isOneKey":false,"orderNo":"","payLimit":0,"price":0,"remark":"","sellerPayTypes":[],"startTime":"","status":0,"statusArray":[],"terminal":"","totalAmount":0,"transReferNum":0,"type":0,"uid":0,"updateTime":null,"userName":"","version":0},"payAccount":{"account":"","bankAdress":"","bankCity":"","bankName":"","bankProvince":"","createTime":null,"id":66,"qrcode":"","realName":"","status":0,"type":0,"uid":0,"updateTime":null,"version":0},"qrcode":"https://btc018.oss-cn-shenzhen.aliyuncs.com/201912091521050_mRhPx.jpg","realName":"买币3","type":3}],"startTime":"","startTimeSecond":1578645325,"status":0,"statusArray":[],"terminal":"APP","timeout":0,"timestamp":1578645342,"toAccount":"5dedf24a2d34dc5ad1108f10","totalAmount":100,"transReferNum":443205,"type":1,"uid":400150,"updateTime":{"date":10,"day":5,"hours":16,"minutes":35,"month":0,"seconds":24,"time":1578645324000,"timezoneOffset":-480,"year":120},"userName":"商家76","version":1}
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
         * adId : 116
         * adStatus : 0
         * adUserId : 400185
         * adUserName : 一键买币3
         * alreadyPayTime : null
         * amount : 123.45
         * buyerSelectedSellerPayType : {"account":"45645121","bankAdress":"","bankCity":"","bankName":"","bankProvince":"","id":2440,"orders":null,"payAccount":{"account":"","bankAdress":"","bankCity":"","bankName":"","bankProvince":"","createTime":null,"id":66,"qrcode":"","realName":"","status":0,"type":3,"uid":0,"updateTime":null,"version":0},"qrcode":"https://btc018.oss-cn-shenzhen.aliyuncs.com/201912091521050_mRhPx.jpg","realName":"买币3","type":3}
         * c30 : 34
         * canComplaint : 1
         * canComplaintMint : 1
         * cancelCounts : 160
         * cashDeposit : 20000
         * cashDepositName : USDT
         * channel : coinW
         * coinId : 2
         * coinLogoUrl : https://btc018.oss-cn-shenzhen.aliyuncs.com/static/front/images/cnyt.png
         * coinName : CNYT
         * complaintCompleteAfter : 4
         * complaintCompleteBefore : 1
         * complaintCreateTimestamp : 0
         * complaintPayed : 1
         * complaintPayedAfter : 20
         * complaintUid : 0
         * completeDealTime : null
         * createTime : {"date":10,"day":5,"hours":16,"minutes":35,"month":0,"seconds":25,"time":1578645325000,"timezoneOffset":-480,"year":120}
         * createTime_s :
         * endTime :
         * fee : 0
         * id : 1847
         * isBuyerView : true
         * isOneKey : true
         * isOnline : false
         * isSellerSelectedPayType : true
         * loggerRealName : 真实姓名76
         * loginUid : 400150
         * orderNo : 20011016357025
         * ou : {"advancedOrgAudit":true,"advancedOrgProtocol":false,"alipay":"","allowOtc":true,"apply":false,"areaCode":"","cashDeposit":[],"cashOutMerchant":false,"createTime":{"date":9,"day":1,"hours":15,"minutes":5,"month":11,"seconds":45,"time":1575875145000,"timezoneOffset":-480,"year":119},"email":"","id":105,"isNewUser":false,"nickname":"一键买币3","oneKeyMatchingMerchant":false,"otcBond":0,"otcLevel":3,"otcLevelName":"认证商家","otherComplaintCount":0,"platformAccount":0,"qq":"","realName":"买币3","skype":"","telegram":"","telephone":"18507512677","textMessage":true,"uid":400185,"unrealCompleteOrders":0,"unrealCompleteOrdersSell":0,"unrealOrders":0,"updateTime":{"date":24,"day":2,"hours":10,"minutes":34,"month":11,"seconds":12,"time":1577154852000,"timezoneOffset":-480,"year":119},"userComplaintCount":0,"wechat":"","whasapp":"","yxAccount":""}
         * payLimit : 3
         * price : 0.81
         * randomColor : 0
         * realName : 买币3
         * remark :
         * residueTime : -1
         * residueTimeAfter : -1
         * sellerPayTypes : [{"account":"4578154654745121","bankAdress":"火车南站","bankCity":"成都市","bankName":"工商银行","bankProvince":"四川省","id":2438,"orders":{"adId":0,"adStatus":0,"adUserId":0,"adUserName":"","alreadyPayTime":null,"amount":0,"buyerSelectedSellerPayType":null,"channel":"","coinId":0,"coinName":"","completeDealTime":null,"createTime":null,"createTime_s":"","endTime":"","fee":0,"id":1847,"isOneKey":false,"orderNo":"","payLimit":0,"price":0,"remark":"","sellerPayTypes":[],"startTime":"","status":0,"statusArray":[],"terminal":"","totalAmount":0,"transReferNum":0,"type":0,"uid":0,"updateTime":null,"userName":"","version":0},"payAccount":{"account":"","bankAdress":"","bankCity":"","bankName":"","bankProvince":"","createTime":null,"id":64,"qrcode":"","realName":"","status":0,"type":0,"uid":0,"updateTime":null,"version":0},"qrcode":"","realName":"买币3","type":1},{"account":"4512564","bankAdress":"","bankCity":"","bankName":"","bankProvince":"","id":2439,"orders":{"adId":0,"adStatus":0,"adUserId":0,"adUserName":"","alreadyPayTime":null,"amount":0,"buyerSelectedSellerPayType":null,"channel":"","coinId":0,"coinName":"","completeDealTime":null,"createTime":null,"createTime_s":"","endTime":"","fee":0,"id":1847,"isOneKey":false,"orderNo":"","payLimit":0,"price":0,"remark":"","sellerPayTypes":[],"startTime":"","status":0,"statusArray":[],"terminal":"","totalAmount":0,"transReferNum":0,"type":0,"uid":0,"updateTime":null,"userName":"","version":0},"payAccount":{"account":"","bankAdress":"","bankCity":"","bankName":"","bankProvince":"","createTime":null,"id":65,"qrcode":"","realName":"","status":0,"type":0,"uid":0,"updateTime":null,"version":0},"qrcode":"https://btc018.oss-cn-shenzhen.aliyuncs.com/201912091520038_CMbDN.jpg","realName":"买币3","type":2},{"account":"45645121","bankAdress":"","bankCity":"","bankName":"","bankProvince":"","id":2440,"orders":{"adId":0,"adStatus":0,"adUserId":0,"adUserName":"","alreadyPayTime":null,"amount":0,"buyerSelectedSellerPayType":null,"channel":"","coinId":0,"coinName":"","completeDealTime":null,"createTime":null,"createTime_s":"","endTime":"","fee":0,"id":1847,"isOneKey":false,"orderNo":"","payLimit":0,"price":0,"remark":"","sellerPayTypes":[],"startTime":"","status":0,"statusArray":[],"terminal":"","totalAmount":0,"transReferNum":0,"type":0,"uid":0,"updateTime":null,"userName":"","version":0},"payAccount":{"account":"","bankAdress":"","bankCity":"","bankName":"","bankProvince":"","createTime":null,"id":66,"qrcode":"","realName":"","status":0,"type":0,"uid":0,"updateTime":null,"version":0},"qrcode":"https://btc018.oss-cn-shenzhen.aliyuncs.com/201912091521050_mRhPx.jpg","realName":"买币3","type":3}]
         * startTime :
         * startTimeSecond : 1578645325
         * status : 0
         * statusArray : []
         * terminal : APP
         * timeout : 0
         * timestamp : 1578645342
         * toAccount : 5dedf24a2d34dc5ad1108f10
         * totalAmount : 100
         * transReferNum : 443205
         * type : 1
         * uid : 400150
         * updateTime : {"date":10,"day":5,"hours":16,"minutes":35,"month":0,"seconds":24,"time":1578645324000,"timezoneOffset":-480,"year":120}
         * userName : 商家76
         * version : 1
         */

        private int adId;
        private int adStatus;
        private int adUserId;
        private String adUserName;
        private Object alreadyPayTime;
        private double amount;
        private BuyerSelectedSellerPayTypeBean buyerSelectedSellerPayType;
        private int c30;
        private int canComplaint;
        private int canComplaintMint;
        private int cancelCounts;
        private int cashDeposit;
        private String cashDepositName;
        private String channel;
        private int coinId;
        private String coinLogoUrl;
        private String coinName;
        private int complaintCompleteAfter;
        private int complaintCompleteBefore;
        private int complaintCreateTimestamp;
        private int complaintPayed;
        private int complaintPayedAfter;
        private int complaintUid;
        private Object completeDealTime;
        private CreateTimeBean createTime;
        private String createTime_s;
        private String endTime;
        private int fee;
        private int id;
        private boolean isBuyerView;
        private boolean isOneKey;
        private boolean isOnline;
        private boolean isSellerSelectedPayType;
        private String loggerRealName;
        private int loginUid;
        private String orderNo;
        private OuBean ou;
        private int payLimit;
        private double price;
        private int randomColor;
        private String realName;
        private String remark;
        private int residueTime;
        private int residueTimeAfter;
        private String startTime;
        private int startTimeSecond;
        private int status;
        private String terminal;
        private int timeout;
        private int timestamp;
        private String toAccount;
        private int totalAmount;
        private int transReferNum;
        private int type;
        private int uid;
        private UpdateTimeBeanX updateTime;
        private String userName;
        private int version;
        private List<SellerPayTypesBean> sellerPayTypes;
        private List<?> statusArray;

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

        public Object getAlreadyPayTime() {
            return alreadyPayTime;
        }

        public void setAlreadyPayTime(Object alreadyPayTime) {
            this.alreadyPayTime = alreadyPayTime;
        }

        public double getAmount() {
            return amount;
        }

        public void setAmount(double amount) {
            this.amount = amount;
        }

        public BuyerSelectedSellerPayTypeBean getBuyerSelectedSellerPayType() {
            return buyerSelectedSellerPayType;
        }

        public void setBuyerSelectedSellerPayType(BuyerSelectedSellerPayTypeBean buyerSelectedSellerPayType) {
            this.buyerSelectedSellerPayType = buyerSelectedSellerPayType;
        }

        public int getC30() {
            return c30;
        }

        public void setC30(int c30) {
            this.c30 = c30;
        }

        public int getCanComplaint() {
            return canComplaint;
        }

        public void setCanComplaint(int canComplaint) {
            this.canComplaint = canComplaint;
        }

        public int getCanComplaintMint() {
            return canComplaintMint;
        }

        public void setCanComplaintMint(int canComplaintMint) {
            this.canComplaintMint = canComplaintMint;
        }

        public int getCancelCounts() {
            return cancelCounts;
        }

        public void setCancelCounts(int cancelCounts) {
            this.cancelCounts = cancelCounts;
        }

        public int getCashDeposit() {
            return cashDeposit;
        }

        public void setCashDeposit(int cashDeposit) {
            this.cashDeposit = cashDeposit;
        }

        public String getCashDepositName() {
            return cashDepositName;
        }

        public void setCashDepositName(String cashDepositName) {
            this.cashDepositName = cashDepositName;
        }

        public String getChannel() {
            return channel;
        }

        public void setChannel(String channel) {
            this.channel = channel;
        }

        public int getCoinId() {
            return coinId;
        }

        public void setCoinId(int coinId) {
            this.coinId = coinId;
        }

        public String getCoinLogoUrl() {
            return coinLogoUrl;
        }

        public void setCoinLogoUrl(String coinLogoUrl) {
            this.coinLogoUrl = coinLogoUrl;
        }

        public String getCoinName() {
            return coinName;
        }

        public void setCoinName(String coinName) {
            this.coinName = coinName;
        }

        public int getComplaintCompleteAfter() {
            return complaintCompleteAfter;
        }

        public void setComplaintCompleteAfter(int complaintCompleteAfter) {
            this.complaintCompleteAfter = complaintCompleteAfter;
        }

        public int getComplaintCompleteBefore() {
            return complaintCompleteBefore;
        }

        public void setComplaintCompleteBefore(int complaintCompleteBefore) {
            this.complaintCompleteBefore = complaintCompleteBefore;
        }

        public int getComplaintCreateTimestamp() {
            return complaintCreateTimestamp;
        }

        public void setComplaintCreateTimestamp(int complaintCreateTimestamp) {
            this.complaintCreateTimestamp = complaintCreateTimestamp;
        }

        public int getComplaintPayed() {
            return complaintPayed;
        }

        public void setComplaintPayed(int complaintPayed) {
            this.complaintPayed = complaintPayed;
        }

        public int getComplaintPayedAfter() {
            return complaintPayedAfter;
        }

        public void setComplaintPayedAfter(int complaintPayedAfter) {
            this.complaintPayedAfter = complaintPayedAfter;
        }

        public int getComplaintUid() {
            return complaintUid;
        }

        public void setComplaintUid(int complaintUid) {
            this.complaintUid = complaintUid;
        }

        public Object getCompleteDealTime() {
            return completeDealTime;
        }

        public void setCompleteDealTime(Object completeDealTime) {
            this.completeDealTime = completeDealTime;
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

        public int getFee() {
            return fee;
        }

        public void setFee(int fee) {
            this.fee = fee;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public boolean isIsBuyerView() {
            return isBuyerView;
        }

        public void setIsBuyerView(boolean isBuyerView) {
            this.isBuyerView = isBuyerView;
        }

        public boolean isIsOneKey() {
            return isOneKey;
        }

        public void setIsOneKey(boolean isOneKey) {
            this.isOneKey = isOneKey;
        }

        public boolean isIsOnline() {
            return isOnline;
        }

        public void setIsOnline(boolean isOnline) {
            this.isOnline = isOnline;
        }

        public boolean isIsSellerSelectedPayType() {
            return isSellerSelectedPayType;
        }

        public void setIsSellerSelectedPayType(boolean isSellerSelectedPayType) {
            this.isSellerSelectedPayType = isSellerSelectedPayType;
        }

        public String getLoggerRealName() {
            return loggerRealName;
        }

        public void setLoggerRealName(String loggerRealName) {
            this.loggerRealName = loggerRealName;
        }

        public int getLoginUid() {
            return loginUid;
        }

        public void setLoginUid(int loginUid) {
            this.loginUid = loginUid;
        }

        public String getOrderNo() {
            return orderNo;
        }

        public void setOrderNo(String orderNo) {
            this.orderNo = orderNo;
        }

        public OuBean getOu() {
            return ou;
        }

        public void setOu(OuBean ou) {
            this.ou = ou;
        }

        public int getPayLimit() {
            return payLimit;
        }

        public void setPayLimit(int payLimit) {
            this.payLimit = payLimit;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        public int getRandomColor() {
            return randomColor;
        }

        public void setRandomColor(int randomColor) {
            this.randomColor = randomColor;
        }

        public String getRealName() {
            return realName;
        }

        public void setRealName(String realName) {
            this.realName = realName;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public int getResidueTime() {
            return residueTime;
        }

        public void setResidueTime(int residueTime) {
            this.residueTime = residueTime;
        }

        public int getResidueTimeAfter() {
            return residueTimeAfter;
        }

        public void setResidueTimeAfter(int residueTimeAfter) {
            this.residueTimeAfter = residueTimeAfter;
        }

        public String getStartTime() {
            return startTime;
        }

        public void setStartTime(String startTime) {
            this.startTime = startTime;
        }

        public int getStartTimeSecond() {
            return startTimeSecond;
        }

        public void setStartTimeSecond(int startTimeSecond) {
            this.startTimeSecond = startTimeSecond;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getTerminal() {
            return terminal;
        }

        public void setTerminal(String terminal) {
            this.terminal = terminal;
        }

        public int getTimeout() {
            return timeout;
        }

        public void setTimeout(int timeout) {
            this.timeout = timeout;
        }

        public int getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(int timestamp) {
            this.timestamp = timestamp;
        }

        public String getToAccount() {
            return toAccount;
        }

        public void setToAccount(String toAccount) {
            this.toAccount = toAccount;
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

        public List<SellerPayTypesBean> getSellerPayTypes() {
            return sellerPayTypes;
        }

        public void setSellerPayTypes(List<SellerPayTypesBean> sellerPayTypes) {
            this.sellerPayTypes = sellerPayTypes;
        }

        public List<?> getStatusArray() {
            return statusArray;
        }

        public void setStatusArray(List<?> statusArray) {
            this.statusArray = statusArray;
        }

        public static class BuyerSelectedSellerPayTypeBean {
            /**
             * account : 45645121
             * bankAdress :
             * bankCity :
             * bankName :
             * bankProvince :
             * id : 2440
             * orders : null
             * payAccount : {"account":"","bankAdress":"","bankCity":"","bankName":"","bankProvince":"","createTime":null,"id":66,"qrcode":"","realName":"","status":0,"type":3,"uid":0,"updateTime":null,"version":0}
             * qrcode : https://btc018.oss-cn-shenzhen.aliyuncs.com/201912091521050_mRhPx.jpg
             * realName : 买币3
             * type : 3
             */

            private String account;
            private String bankAdress;
            private String bankCity;
            private String bankName;
            private String bankProvince;
            private int id;
            private Object orders;
            private PayOrderInfoBean.PayAccountsBean payAccount;
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

            public Object getOrders() {
                return orders;
            }

            public void setOrders(Object orders) {
                this.orders = orders;
            }

            public PayOrderInfoBean.PayAccountsBean getPayAccount() {
                return payAccount;
            }

            public void setPayAccount(PayOrderInfoBean.PayAccountsBean payAccount) {
                this.payAccount = payAccount;
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

//            public static class PayAccountBean {
//                /**
//                 * account :
//                 * bankAdress :
//                 * bankCity :
//                 * bankName :
//                 * bankProvince :
//                 * createTime : null
//                 * id : 66
//                 * qrcode :
//                 * realName :
//                 * status : 0
//                 * type : 3
//                 * uid : 0
//                 * updateTime : null
//                 * version : 0
//                 */
//
//                private String account;
//                private String bankAdress;
//                private String bankCity;
//                private String bankName;
//                private String bankProvince;
//                private Object createTime;
//                private int id;
//                private String qrcode;
//                private String realName;
//                private int status;
//                private int type;
//                private int uid;
//                private Object updateTime;
//                private int version;
//
//                public String getAccount() {
//                    return account;
//                }
//
//                public void setAccount(String account) {
//                    this.account = account;
//                }
//
//                public String getBankAdress() {
//                    return bankAdress;
//                }
//
//                public void setBankAdress(String bankAdress) {
//                    this.bankAdress = bankAdress;
//                }
//
//                public String getBankCity() {
//                    return bankCity;
//                }
//
//                public void setBankCity(String bankCity) {
//                    this.bankCity = bankCity;
//                }
//
//                public String getBankName() {
//                    return bankName;
//                }
//
//                public void setBankName(String bankName) {
//                    this.bankName = bankName;
//                }
//
//                public String getBankProvince() {
//                    return bankProvince;
//                }
//
//                public void setBankProvince(String bankProvince) {
//                    this.bankProvince = bankProvince;
//                }
//
//                public Object getCreateTime() {
//                    return createTime;
//                }
//
//                public void setCreateTime(Object createTime) {
//                    this.createTime = createTime;
//                }
//
//                public int getId() {
//                    return id;
//                }
//
//                public void setId(int id) {
//                    this.id = id;
//                }
//
//                public String getQrcode() {
//                    return qrcode;
//                }
//
//                public void setQrcode(String qrcode) {
//                    this.qrcode = qrcode;
//                }
//
//                public String getRealName() {
//                    return realName;
//                }
//
//                public void setRealName(String realName) {
//                    this.realName = realName;
//                }
//
//                public int getStatus() {
//                    return status;
//                }
//
//                public void setStatus(int status) {
//                    this.status = status;
//                }
//
//                public int getType() {
//                    return type;
//                }
//
//                public void setType(int type) {
//                    this.type = type;
//                }
//
//                public int getUid() {
//                    return uid;
//                }
//
//                public void setUid(int uid) {
//                    this.uid = uid;
//                }
//
//                public Object getUpdateTime() {
//                    return updateTime;
//                }
//
//                public void setUpdateTime(Object updateTime) {
//                    this.updateTime = updateTime;
//                }
//
//                public int getVersion() {
//                    return version;
//                }
//
//                public void setVersion(int version) {
//                    this.version = version;
//                }
//            }
        }

        public static class CreateTimeBean {
            /**
             * date : 10
             * day : 5
             * hours : 16
             * minutes : 35
             * month : 0
             * seconds : 25
             * time : 1578645325000
             * timezoneOffset : -480
             * year : 120
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

        public static class OuBean {
            /**
             * advancedOrgAudit : true
             * advancedOrgProtocol : false
             * alipay :
             * allowOtc : true
             * apply : false
             * areaCode :
             * cashDeposit : []
             * cashOutMerchant : false
             * createTime : {"date":9,"day":1,"hours":15,"minutes":5,"month":11,"seconds":45,"time":1575875145000,"timezoneOffset":-480,"year":119}
             * email :
             * id : 105
             * isNewUser : false
             * nickname : 一键买币3
             * oneKeyMatchingMerchant : false
             * otcBond : 0
             * otcLevel : 3
             * otcLevelName : 认证商家
             * otherComplaintCount : 0
             * platformAccount : 0
             * qq :
             * realName : 买币3
             * skype :
             * telegram :
             * telephone : 18507512677
             * textMessage : true
             * uid : 400185
             * unrealCompleteOrders : 0
             * unrealCompleteOrdersSell : 0
             * unrealOrders : 0
             * updateTime : {"date":24,"day":2,"hours":10,"minutes":34,"month":11,"seconds":12,"time":1577154852000,"timezoneOffset":-480,"year":119}
             * userComplaintCount : 0
             * wechat :
             * whasapp :
             * yxAccount :
             */

            private boolean advancedOrgAudit;
            private boolean advancedOrgProtocol;
            private String alipay;
            private boolean allowOtc;
            private boolean apply;
            private String areaCode;
            private boolean cashOutMerchant;
            private CreateTimeBeanX createTime;
            private String email;
            private int id;
            private boolean isNewUser;
            private String nickname;
            private boolean oneKeyMatchingMerchant;
            private int otcBond;
            private int otcLevel;
            private String otcLevelName;
            private int otherComplaintCount;
            private int platformAccount;
            private String qq;
            private String realName;
            private String skype;
            private String telegram;
            private String telephone;
            private boolean textMessage;
            private int uid;
            private int unrealCompleteOrders;
            private int unrealCompleteOrdersSell;
            private int unrealOrders;
            private UpdateTimeBean updateTime;
            private int userComplaintCount;
            private String wechat;
            private String whasapp;
            private String yxAccount;
            private List<?> cashDeposit;

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

            public boolean isAllowOtc() {
                return allowOtc;
            }

            public void setAllowOtc(boolean allowOtc) {
                this.allowOtc = allowOtc;
            }

            public boolean isApply() {
                return apply;
            }

            public void setApply(boolean apply) {
                this.apply = apply;
            }

            public String getAreaCode() {
                return areaCode;
            }

            public void setAreaCode(String areaCode) {
                this.areaCode = areaCode;
            }

            public boolean isCashOutMerchant() {
                return cashOutMerchant;
            }

            public void setCashOutMerchant(boolean cashOutMerchant) {
                this.cashOutMerchant = cashOutMerchant;
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

            public boolean isIsNewUser() {
                return isNewUser;
            }

            public void setIsNewUser(boolean isNewUser) {
                this.isNewUser = isNewUser;
            }

            public String getNickname() {
                return nickname;
            }

            public void setNickname(String nickname) {
                this.nickname = nickname;
            }

            public boolean isOneKeyMatchingMerchant() {
                return oneKeyMatchingMerchant;
            }

            public void setOneKeyMatchingMerchant(boolean oneKeyMatchingMerchant) {
                this.oneKeyMatchingMerchant = oneKeyMatchingMerchant;
            }

            public int getOtcBond() {
                return otcBond;
            }

            public void setOtcBond(int otcBond) {
                this.otcBond = otcBond;
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

            public int getOtherComplaintCount() {
                return otherComplaintCount;
            }

            public void setOtherComplaintCount(int otherComplaintCount) {
                this.otherComplaintCount = otherComplaintCount;
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

            public String getRealName() {
                return realName;
            }

            public void setRealName(String realName) {
                this.realName = realName;
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

            public int getUnrealCompleteOrders() {
                return unrealCompleteOrders;
            }

            public void setUnrealCompleteOrders(int unrealCompleteOrders) {
                this.unrealCompleteOrders = unrealCompleteOrders;
            }

            public int getUnrealCompleteOrdersSell() {
                return unrealCompleteOrdersSell;
            }

            public void setUnrealCompleteOrdersSell(int unrealCompleteOrdersSell) {
                this.unrealCompleteOrdersSell = unrealCompleteOrdersSell;
            }

            public int getUnrealOrders() {
                return unrealOrders;
            }

            public void setUnrealOrders(int unrealOrders) {
                this.unrealOrders = unrealOrders;
            }

            public UpdateTimeBean getUpdateTime() {
                return updateTime;
            }

            public void setUpdateTime(UpdateTimeBean updateTime) {
                this.updateTime = updateTime;
            }

            public int getUserComplaintCount() {
                return userComplaintCount;
            }

            public void setUserComplaintCount(int userComplaintCount) {
                this.userComplaintCount = userComplaintCount;
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

            public String getYxAccount() {
                return yxAccount;
            }

            public void setYxAccount(String yxAccount) {
                this.yxAccount = yxAccount;
            }

            public List<?> getCashDeposit() {
                return cashDeposit;
            }

            public void setCashDeposit(List<?> cashDeposit) {
                this.cashDeposit = cashDeposit;
            }

            public static class CreateTimeBeanX {
                /**
                 * date : 9
                 * day : 1
                 * hours : 15
                 * minutes : 5
                 * month : 11
                 * seconds : 45
                 * time : 1575875145000
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
                 * date : 24
                 * day : 2
                 * hours : 10
                 * minutes : 34
                 * month : 11
                 * seconds : 12
                 * time : 1577154852000
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

        public static class UpdateTimeBeanX {
            /**
             * date : 10
             * day : 5
             * hours : 16
             * minutes : 35
             * month : 0
             * seconds : 24
             * time : 1578645324000
             * timezoneOffset : -480
             * year : 120
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

        public static class SellerPayTypesBean {
            /**
             * account : 4578154654745121
             * bankAdress : 火车南站
             * bankCity : 成都市
             * bankName : 工商银行
             * bankProvince : 四川省
             * id : 2438
             * orders : {"adId":0,"adStatus":0,"adUserId":0,"adUserName":"","alreadyPayTime":null,"amount":0,"buyerSelectedSellerPayType":null,"channel":"","coinId":0,"coinName":"","completeDealTime":null,"createTime":null,"createTime_s":"","endTime":"","fee":0,"id":1847,"isOneKey":false,"orderNo":"","payLimit":0,"price":0,"remark":"","sellerPayTypes":[],"startTime":"","status":0,"statusArray":[],"terminal":"","totalAmount":0,"transReferNum":0,"type":0,"uid":0,"updateTime":null,"userName":"","version":0}
             * payAccount : {"account":"","bankAdress":"","bankCity":"","bankName":"","bankProvince":"","createTime":null,"id":64,"qrcode":"","realName":"","status":0,"type":0,"uid":0,"updateTime":null,"version":0}
             * qrcode :
             * realName : 买币3
             * type : 1
             */

            private String account;
            private String bankAdress;
            private String bankCity;
            private String bankName;
            private String bankProvince;
            private int id;
            private OrdersBean orders;
            private PayOrderInfoBean.PayAccountsBean payAccount;
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

            public OrdersBean getOrders() {
                return orders;
            }

            public void setOrders(OrdersBean orders) {
                this.orders = orders;
            }

            public PayOrderInfoBean.PayAccountsBean getPayAccount() {
                return payAccount;
            }

            public void setPayAccount(PayOrderInfoBean.PayAccountsBean payAccount) {
                this.payAccount = payAccount;
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

            public static class OrdersBean {
                /**
                 * adId : 0
                 * adStatus : 0
                 * adUserId : 0
                 * adUserName :
                 * alreadyPayTime : null
                 * amount : 0
                 * buyerSelectedSellerPayType : null
                 * channel :
                 * coinId : 0
                 * coinName :
                 * completeDealTime : null
                 * createTime : null
                 * createTime_s :
                 * endTime :
                 * fee : 0
                 * id : 1847
                 * isOneKey : false
                 * orderNo :
                 * payLimit : 0
                 * price : 0
                 * remark :
                 * sellerPayTypes : []
                 * startTime :
                 * status : 0
                 * statusArray : []
                 * terminal :
                 * totalAmount : 0
                 * transReferNum : 0
                 * type : 0
                 * uid : 0
                 * updateTime : null
                 * userName :
                 * version : 0
                 */

                private int adId;
                private int adStatus;
                private int adUserId;
                private String adUserName;
                private Object alreadyPayTime;
                private int amount;
                private Object buyerSelectedSellerPayType;
                private String channel;
                private int coinId;
                private String coinName;
                private Object completeDealTime;
                private Object createTime;
                private String createTime_s;
                private String endTime;
                private int fee;
                private int id;
                private boolean isOneKey;
                private String orderNo;
                private int payLimit;
                private int price;
                private String remark;
                private String startTime;
                private int status;
                private String terminal;
                private int totalAmount;
                private int transReferNum;
                private int type;
                private int uid;
                private Object updateTime;
                private String userName;
                private int version;
                private List<?> sellerPayTypes;
                private List<?> statusArray;

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

                public Object getAlreadyPayTime() {
                    return alreadyPayTime;
                }

                public void setAlreadyPayTime(Object alreadyPayTime) {
                    this.alreadyPayTime = alreadyPayTime;
                }

                public int getAmount() {
                    return amount;
                }

                public void setAmount(int amount) {
                    this.amount = amount;
                }

                public Object getBuyerSelectedSellerPayType() {
                    return buyerSelectedSellerPayType;
                }

                public void setBuyerSelectedSellerPayType(Object buyerSelectedSellerPayType) {
                    this.buyerSelectedSellerPayType = buyerSelectedSellerPayType;
                }

                public String getChannel() {
                    return channel;
                }

                public void setChannel(String channel) {
                    this.channel = channel;
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

                public Object getCompleteDealTime() {
                    return completeDealTime;
                }

                public void setCompleteDealTime(Object completeDealTime) {
                    this.completeDealTime = completeDealTime;
                }

                public Object getCreateTime() {
                    return createTime;
                }

                public void setCreateTime(Object createTime) {
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

                public int getFee() {
                    return fee;
                }

                public void setFee(int fee) {
                    this.fee = fee;
                }

                public int getId() {
                    return id;
                }

                public void setId(int id) {
                    this.id = id;
                }

                public boolean isIsOneKey() {
                    return isOneKey;
                }

                public void setIsOneKey(boolean isOneKey) {
                    this.isOneKey = isOneKey;
                }

                public String getOrderNo() {
                    return orderNo;
                }

                public void setOrderNo(String orderNo) {
                    this.orderNo = orderNo;
                }

                public int getPayLimit() {
                    return payLimit;
                }

                public void setPayLimit(int payLimit) {
                    this.payLimit = payLimit;
                }

                public int getPrice() {
                    return price;
                }

                public void setPrice(int price) {
                    this.price = price;
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

                public String getTerminal() {
                    return terminal;
                }

                public void setTerminal(String terminal) {
                    this.terminal = terminal;
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

                public Object getUpdateTime() {
                    return updateTime;
                }

                public void setUpdateTime(Object updateTime) {
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

                public List<?> getSellerPayTypes() {
                    return sellerPayTypes;
                }

                public void setSellerPayTypes(List<?> sellerPayTypes) {
                    this.sellerPayTypes = sellerPayTypes;
                }

                public List<?> getStatusArray() {
                    return statusArray;
                }

                public void setStatusArray(List<?> statusArray) {
                    this.statusArray = statusArray;
                }
            }

            public static class PayAccountBeanX {
                /**
                 * account :
                 * bankAdress :
                 * bankCity :
                 * bankName :
                 * bankProvince :
                 * createTime : null
                 * id : 64
                 * qrcode :
                 * realName :
                 * status : 0
                 * type : 0
                 * uid : 0
                 * updateTime : null
                 * version : 0
                 */

                private String account;
                private String bankAdress;
                private String bankCity;
                private String bankName;
                private String bankProvince;
                private Object createTime;
                private int id;
                private String qrcode;
                private String realName;
                private int status;
                private int type;
                private int uid;
                private Object updateTime;
                private int version;

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

                public Object getCreateTime() {
                    return createTime;
                }

                public void setCreateTime(Object createTime) {
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

                public Object getUpdateTime() {
                    return updateTime;
                }

                public void setUpdateTime(Object updateTime) {
                    this.updateTime = updateTime;
                }

                public int getVersion() {
                    return version;
                }

                public void setVersion(int version) {
                    this.version = version;
                }
            }
        }
    }
}
