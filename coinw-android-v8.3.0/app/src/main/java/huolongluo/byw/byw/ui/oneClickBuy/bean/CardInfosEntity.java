package huolongluo.byw.byw.ui.oneClickBuy.bean;

import java.util.List;

public class CardInfosEntity {

    /**
     * result : true
     * code : 0
     * value : 操作成功
     * data : [{"account":"6532563257854521","bankAdress":"萨大苏打","bankCity":"资阳市","bankName":"工商银行","bankProvince":"四川省","createTime":{"date":16,"day":6,"hours":2,"minutes":21,"month":10,"seconds":6,"time":1573842066000,"timezoneOffset":-480,"year":119},"id":1,"qrcode":"","realName":"真实姓名76","status":2,"type":1,"uid":400150,"updateTime":{"date":16,"day":6,"hours":2,"minutes":21,"month":10,"seconds":6,"time":1573842066000,"timezoneOffset":-480,"year":119},"version":0},{"account":"wx123215","bankAdress":"","bankCity":"","bankName":"","bankProvince":"","createTime":{"date":16,"day":6,"hours":2,"minutes":21,"month":10,"seconds":33,"time":1573842093000,"timezoneOffset":-480,"year":119},"id":2,"qrcode":"http://192.168.3.227/upload/images/201911211604023_6Fpcc.png","realName":"真实姓名76","status":2,"type":2,"uid":400150,"updateTime":{"date":16,"day":6,"hours":2,"minutes":21,"month":10,"seconds":33,"time":1573842093000,"timezoneOffset":-480,"year":119},"version":0},{"account":"zfb123456","bankAdress":"","bankCity":"","bankName":"","bankProvince":"","createTime":{"date":16,"day":6,"hours":2,"minutes":21,"month":10,"seconds":51,"time":1573842111000,"timezoneOffset":-480,"year":119},"id":3,"qrcode":"http://192.168.3.227/upload/images/201911211604023_6Fpcc.png","realName":"真实姓名76","status":2,"type":3,"uid":400150,"updateTime":{"date":16,"day":6,"hours":2,"minutes":21,"month":10,"seconds":51,"time":1573842111000,"timezoneOffset":-480,"year":119},"version":0}]
     */

    private boolean result;
    private int code;
    private String value;
    private List<DataBean> data;

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

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * account : 6532563257854521
         * bankAdress : 萨大苏打
         * bankCity : 资阳市
         * bankName : 工商银行
         * bankProvince : 四川省
         * createTime : {"date":16,"day":6,"hours":2,"minutes":21,"month":10,"seconds":6,"time":1573842066000,"timezoneOffset":-480,"year":119}
         * id : 1
         * qrcode :
         * realName : 真实姓名76
         * status : 2
         * type : 1
         * uid : 400150
         * updateTime : {"date":16,"day":6,"hours":2,"minutes":21,"month":10,"seconds":6,"time":1573842066000,"timezoneOffset":-480,"year":119}
         * version : 0
         */

        private String account;
        private String bankAdress;
        private String bankCity;
        private String bankName;
        private String bankProvince;
        private CreateTimeBean createTime;
        private int id;
        private String qrcode;
        private String realName;
        private int status;
        private int type;
        private int uid;
        private UpdateTimeBean updateTime;
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

        public CreateTimeBean getCreateTime() {
            return createTime;
        }

        public void setCreateTime(CreateTimeBean createTime) {
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

        public UpdateTimeBean getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(UpdateTimeBean updateTime) {
            this.updateTime = updateTime;
        }

        public int getVersion() {
            return version;
        }

        public void setVersion(int version) {
            this.version = version;
        }

        public static class CreateTimeBean {
            /**
             * date : 16
             * day : 6
             * hours : 2
             * minutes : 21
             * month : 10
             * seconds : 6
             * time : 1573842066000
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
             * date : 16
             * day : 6
             * hours : 2
             * minutes : 21
             * month : 10
             * seconds : 6
             * time : 1573842066000
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
