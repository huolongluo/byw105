package huolongluo.byw.reform.c2c.oct.bean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dell on 2019/6/5.
 */

public class OtcUserInfoBean {

    /**
     * code : 0
     * data : {"otcUser":{"advancedOrgAudit":false,"advancedOrgProtocol":false,"alipay":"","apply":false,"createTime":{"date":5,"day":3,"hours":10,"minutes":4,"month":5,"seconds":41,"time":1559700281000,"timezoneOffset":-480,"year":119},"email":"","id":85,"nickname":"爱海洋","otcLevel":1,"otcLevelName":"otc_用户","platformAccount":0,"qq":"","skype":"","telegram":"","telephone":"18655056179","textMessage":false,"uid":67351,"updateTime":{"date":10,"day":1,"hours":11,"minutes":48,"month":5,"seconds":38,"time":1560138518000,"timezoneOffset":-480,"year":119},"wechat":"","whasapp":""},"otcUserAsset":[{"coinId":2,"freezeCoinsMoney":212,"freezeCoinsMoney_s":"","freezeLegalTenderMoney":103.53,"freezeLegalTenderMoney_s":"","legalTenderAbbreviation":"CNYT","legalTenderIconUrl":"https://btc018.oss-cn-shenzhen.aliyuncs.com/static/front/images/cnyt.png","usableCoinsMoney":1.872395097589406E7,"usableCoinsMoney_s":"","usableLegalTenderMoney":2210.47,"usableLegalTenderMoney_s":"2210.47"},{"coinId":29,"freezeCoinsMoney":0,"freezeCoinsMoney_s":"","freezeLegalTenderMoney":0,"freezeLegalTenderMoney_s":"","legalTenderAbbreviation":"USDT","legalTenderIconUrl":"https://btc018.oss-cn-shenzhen.aliyuncs.com/201711250157013_QSgzZ.png","usableCoinsMoney":22,"usableCoinsMoney_s":"","usableLegalTenderMoney":0,"usableLegalTenderMoney_s":"0"},{"coinId":16,"freezeCoinsMoney":0,"freezeCoinsMoney_s":"","freezeLegalTenderMoney":0,"freezeLegalTenderMoney_s":"","legalTenderAbbreviation":"ETH","legalTenderIconUrl":"https://btc018.oss-cn-shenzhen.aliyuncs.com/201711020114014_nIiOH.png","usableCoinsMoney":92447.41419621,"usableCoinsMoney_s":"","usableLegalTenderMoney":0,"usableLegalTenderMoney_s":"0"}],"otcUserLevel":4,"realValidate":true,"telephone":"18655056179","tradePassword":true,"userPayment":[{"account":"1234567890","accountQrcode":"","bankAdress":"","bankName":"","id":37,"realName":"王洋","type":"1"},{"account":"624382450@qq.com","accountQrcode":"https://btc018.oss-cn-shenzhen.aliyuncs.com/201906191712054_UTHp7.zhifubao.png","bankAdress":"","bankName":"","id":81,"realName":"王洋","type":"3"},{"account":"18655056178","accountQrcode":"https://btc018.oss-cn-shenzhen.aliyuncs.com/201906191717005_cPOny.zhifubao.png","bankAdress":"","bankName":"","id":82,"realName":"王洋","type":"2"}]}
     * result : true
     * value : 操作成功
     */

    private int code;
    private DataBean data;
    private boolean result;
    private String value;


    private Map<Integer, DataBean.OtcUserAssetBean> assetBeanMap = new HashMap<>();

    public void refreshAsset() {

        assetBeanMap.clear();

        if (data != null && data.getOtcUserAsset() != null) {
            for (DataBean.OtcUserAssetBean bean : data.getOtcUserAsset()) {
                assetBeanMap.put(bean.getCoinId(), bean);
            }
        }


    }


    public String getAsset(int id) {
        DataBean.OtcUserAssetBean bean = assetBeanMap.get(id);
        if (bean != null) {
            return bean.getUsableLegalTenderMoney_s();
        } else {
            return null;
        }
    }


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
         * otcUser : {"advancedOrgAudit":false,"advancedOrgProtocol":false,"alipay":"","apply":false,"createTime":{"date":5,"day":3,"hours":10,"minutes":4,"month":5,"seconds":41,"time":1559700281000,"timezoneOffset":-480,"year":119},"email":"","id":85,"nickname":"爱海洋","otcLevel":1,"otcLevelName":"otc_用户","platformAccount":0,"qq":"","skype":"","telegram":"","telephone":"18655056179","textMessage":false,"uid":67351,"updateTime":{"date":10,"day":1,"hours":11,"minutes":48,"month":5,"seconds":38,"time":1560138518000,"timezoneOffset":-480,"year":119},"wechat":"","whasapp":""}
         * otcUserAsset : [{"coinId":2,"freezeCoinsMoney":212,"freezeCoinsMoney_s":"","freezeLegalTenderMoney":103.53,"freezeLegalTenderMoney_s":"","legalTenderAbbreviation":"CNYT","legalTenderIconUrl":"https://btc018.oss-cn-shenzhen.aliyuncs.com/static/front/images/cnyt.png","usableCoinsMoney":1.872395097589406E7,"usableCoinsMoney_s":"","usableLegalTenderMoney":2210.47,"usableLegalTenderMoney_s":"2210.47"},{"coinId":29,"freezeCoinsMoney":0,"freezeCoinsMoney_s":"","freezeLegalTenderMoney":0,"freezeLegalTenderMoney_s":"","legalTenderAbbreviation":"USDT","legalTenderIconUrl":"https://btc018.oss-cn-shenzhen.aliyuncs.com/201711250157013_QSgzZ.png","usableCoinsMoney":22,"usableCoinsMoney_s":"","usableLegalTenderMoney":0,"usableLegalTenderMoney_s":"0"},{"coinId":16,"freezeCoinsMoney":0,"freezeCoinsMoney_s":"","freezeLegalTenderMoney":0,"freezeLegalTenderMoney_s":"","legalTenderAbbreviation":"ETH","legalTenderIconUrl":"https://btc018.oss-cn-shenzhen.aliyuncs.com/201711020114014_nIiOH.png","usableCoinsMoney":92447.41419621,"usableCoinsMoney_s":"","usableLegalTenderMoney":0,"usableLegalTenderMoney_s":"0"}]
         * otcUserLevel : 4
         * realValidate : true
         * telephone : 18655056179
         * tradePassword : true
         * userPayment : [{"account":"1234567890","accountQrcode":"","bankAdress":"","bankName":"","id":37,"realName":"王洋","type":"1"},{"account":"624382450@qq.com","accountQrcode":"https://btc018.oss-cn-shenzhen.aliyuncs.com/201906191712054_UTHp7.zhifubao.png","bankAdress":"","bankName":"","id":81,"realName":"王洋","type":"3"},{"account":"18655056178","accountQrcode":"https://btc018.oss-cn-shenzhen.aliyuncs.com/201906191717005_cPOny.zhifubao.png","bankAdress":"","bankName":"","id":82,"realName":"王洋","type":"2"}]
         */

        private boolean isMerch;//是否是商户
        private List<Integer> payAccountTypes;
        private boolean isConvertMerchant;

        public boolean isConvertMerchant() {
            return isConvertMerchant;
        }

        public void setConvertMerchant(boolean convertMerchant) {
            isConvertMerchant = convertMerchant;
        }

        public List<Integer> getPayAccountTypes() {
            return payAccountTypes;
        }

        public void setPayAccountTypes(List<Integer> payAccountTypes) {
            this.payAccountTypes = payAccountTypes;
        }

        //是否绑定的三三种支付账号
        private boolean bankId;
        private boolean wechatId;
        private boolean alipayId;

        private boolean isOpenLimit;

        private OtcUserBean otcUser;
        private int otcUserLevel;
        private boolean realValidate;
        private String telephone;
        private boolean tradePassword;
        private List<OtcUserAssetBean> otcUserAsset;
        private List<UserPaymentBean> userPayment;
        private String exchangeQuota;

        public String getExchangeQuota() {
            return exchangeQuota;
        }

        public void setExchangeQuota(String exchangeQuota) {
            this.exchangeQuota = exchangeQuota;
        }

        public void setOtcUser(OtcUserBean otcUser) {
            this.otcUser = otcUser;
        }

        public OtcUserBean getOtcUser() {
            return otcUser;
        }

        public int getOtcUserLevel() {
            return otcUserLevel;
        }

        public void setOtcUserLevel(int otcUserLevel) {
            this.otcUserLevel = otcUserLevel;
        }

        public boolean isRealValidate() {
            return realValidate;
        }

        public void setRealValidate(boolean realValidate) {
            this.realValidate = realValidate;
        }

        public String getTelephone() {
            return telephone;
        }

        public void setTelephone(String telephone) {
            this.telephone = telephone;
        }

        public boolean isTradePassword() {
            return tradePassword;
        }

        public void setTradePassword(boolean tradePassword) {
            this.tradePassword = tradePassword;
        }

        public List<OtcUserAssetBean> getOtcUserAsset() {
            return otcUserAsset;
        }

        public void setOtcUserAsset(List<OtcUserAssetBean> otcUserAsset) {
            this.otcUserAsset = otcUserAsset;
        }

        public List<UserPaymentBean> getUserPayment() {
            return userPayment;
        }

        public void setUserPayment(List<UserPaymentBean> userPayment) {
            this.userPayment = userPayment;
        }

        public boolean isBankId() {
            return bankId;
        }

        public void setBankId(boolean bankId) {
            this.bankId = bankId;
        }

        public boolean isWechatId() {
            return wechatId;
        }

        public void setWechatId(boolean wechatId) {
            this.wechatId = wechatId;
        }

        public boolean isAlipayId() {
            return alipayId;
        }

        public void setAlipayId(boolean alipayId) {
            this.alipayId = alipayId;
        }

        public boolean isOpenLimit() {
            return isOpenLimit;
        }

        public void setOpenLimit(boolean openLimit) {
            isOpenLimit = openLimit;
        }

        public boolean isMerch() {
            return isMerch;
        }

        public void setMerch(boolean merch) {
            isMerch = merch;
        }

        public static class OtcUserBean {
            /**
             * advancedOrgAudit : false
             * advancedOrgProtocol : false
             * alipay :
             * apply : false
             * createTime : {"date":5,"day":3,"hours":10,"minutes":4,"month":5,"seconds":41,"time":1559700281000,"timezoneOffset":-480,"year":119}
             * email :
             * id : 85
             * nickname : 爱海洋
             * otcLevel : 1
             * otcLevelName : otc_用户
             * platformAccount : 0
             * qq :
             * skype :
             * telegram :
             * telephone : 18655056179
             * textMessage : false
             * uid : 67351
             * updateTime : {"date":10,"day":1,"hours":11,"minutes":48,"month":5,"seconds":38,"time":1560138518000,"timezoneOffset":-480,"year":119}
             * wechat :
             * whasapp :
             */

            private boolean allowOtc;
            private boolean isNewUser;


            private boolean advancedOrgAudit;
            private boolean advancedOrgProtocol;
            private String alipay;
            private boolean apply;
            private CreateTimeBean createTime;
            private String email;
            private int id;
            private String nickname;
            private int otcLevel; // 1, 普通商家 2出初级商家， 3认证商家
            private String otcLevelName;
            private int platformAccount;
            private String qq;
            private String skype;
            private String telegram;
            private String telephone;
            private boolean textMessage;
            private int uid;
            private UpdateTimeBean updateTime;
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

            public CreateTimeBean getCreateTime() {
                return createTime;
            }

            public void setCreateTime(CreateTimeBean createTime) {
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

            public UpdateTimeBean getUpdateTime() {
                return updateTime;
            }

            public void setUpdateTime(UpdateTimeBean updateTime) {
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

            public boolean isAllowOtc() {
                return allowOtc;
            }

            public void setAllowOtc(boolean allowOtc) {
                this.allowOtc = allowOtc;
            }

            public boolean isNewUser() {
                return isNewUser;
            }

            public void setNewUser(boolean newUser) {
                isNewUser = newUser;
            }

            public static class CreateTimeBean {
                /**
                 * date : 5
                 * day : 3
                 * hours : 10
                 * minutes : 4
                 * month : 5
                 * seconds : 41
                 * time : 1559700281000
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
                 * date : 10
                 * day : 1
                 * hours : 11
                 * minutes : 48
                 * month : 5
                 * seconds : 38
                 * time : 1560138518000
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

        public static class OtcUserAssetBean {
            /**
             * coinId : 2
             * freezeCoinsMoney : 212
             * freezeCoinsMoney_s :
             * freezeLegalTenderMoney : 103.53
             * freezeLegalTenderMoney_s :
             * legalTenderAbbreviation : CNYT
             * legalTenderIconUrl : https://btc018.oss-cn-shenzhen.aliyuncs.com/static/front/images/cnyt.png
             * usableCoinsMoney : 1.872395097589406E7
             * usableCoinsMoney_s :
             * usableLegalTenderMoney : 2210.47
             * usableLegalTenderMoney_s : 2210.47
             */

            private int coinId;
            private double freezeCoinsMoney;
            private String freezeCoinsMoney_s;
            private double freezeLegalTenderMoney;
            private String freezeLegalTenderMoney_s;
            private String legalTenderAbbreviation;
            private String legalTenderIconUrl;
            private double usableCoinsMoney;
            private String usableCoinsMoney_s;
            private double usableLegalTenderMoney;
            private String usableLegalTenderMoney_s;

            public int getCoinId() {
                return coinId;
            }

            public void setCoinId(int coinId) {
                this.coinId = coinId;
            }

            public double getFreezeCoinsMoney() {
                return freezeCoinsMoney;
            }

            public void setFreezeCoinsMoney(double freezeCoinsMoney) {
                this.freezeCoinsMoney = freezeCoinsMoney;
            }

            public String getFreezeCoinsMoney_s() {
                return freezeCoinsMoney_s;
            }

            public void setFreezeCoinsMoney_s(String freezeCoinsMoney_s) {
                this.freezeCoinsMoney_s = freezeCoinsMoney_s;
            }

            public double getFreezeLegalTenderMoney() {
                return freezeLegalTenderMoney;
            }

            public void setFreezeLegalTenderMoney(double freezeLegalTenderMoney) {
                this.freezeLegalTenderMoney = freezeLegalTenderMoney;
            }

            public String getFreezeLegalTenderMoney_s() {
                return freezeLegalTenderMoney_s;
            }

            public void setFreezeLegalTenderMoney_s(String freezeLegalTenderMoney_s) {
                this.freezeLegalTenderMoney_s = freezeLegalTenderMoney_s;
            }

            public String getLegalTenderAbbreviation() {
                return legalTenderAbbreviation;
            }

            public void setLegalTenderAbbreviation(String legalTenderAbbreviation) {
                this.legalTenderAbbreviation = legalTenderAbbreviation;
            }

            public String getLegalTenderIconUrl() {
                return legalTenderIconUrl;
            }

            public void setLegalTenderIconUrl(String legalTenderIconUrl) {
                this.legalTenderIconUrl = legalTenderIconUrl;
            }

            public double getUsableCoinsMoney() {
                return usableCoinsMoney;
            }

            public void setUsableCoinsMoney(double usableCoinsMoney) {
                this.usableCoinsMoney = usableCoinsMoney;
            }

            public String getUsableCoinsMoney_s() {
                return usableCoinsMoney_s;
            }

            public void setUsableCoinsMoney_s(String usableCoinsMoney_s) {
                this.usableCoinsMoney_s = usableCoinsMoney_s;
            }

            public double getUsableLegalTenderMoney() {
                return usableLegalTenderMoney;
            }

            public void setUsableLegalTenderMoney(double usableLegalTenderMoney) {
                this.usableLegalTenderMoney = usableLegalTenderMoney;
            }

            public String getUsableLegalTenderMoney_s() {
                return usableLegalTenderMoney_s;
            }

            public void setUsableLegalTenderMoney_s(String usableLegalTenderMoney_s) {
                this.usableLegalTenderMoney_s = usableLegalTenderMoney_s;
            }
        }

        public static class UserPaymentBean {
            /**
             * account : 1234567890
             * accountQrcode :
             * bankAdress :
             * bankName :
             * id : 37
             * realName : 王洋
             * type : 1
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
