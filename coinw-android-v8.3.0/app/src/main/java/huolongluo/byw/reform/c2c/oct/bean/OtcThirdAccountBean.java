package huolongluo.byw.reform.c2c.oct.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by dell on 2019/6/6.
 */

public class OtcThirdAccountBean {
    /**
     * code : 0
     * data : {"otcUserAlipay":{"account":"xialaini@163.com","createTime":{"date":6,"day":4,"hours":16,"minutes":0,"month":5,"seconds":38,"time":1559808038000,"timezoneOffset":-480,"year":119},"id":56,"qrcode":"https://image.baidu.com/search/detail?ct=50331648","realName":"王洋","type":1,"uid":67351,"updateTime":{"date":6,"day":4,"hours":16,"minutes":0,"month":5,"seconds":38,"time":1559808038000,"timezoneOffset":-480,"year":119}},"otcUserBank":{"bankAdress":"安徽省合肥市庐阳区","bankCity":"","bankName":"农业银行","bankNum":"255665555","bankProvince":"","createTime":{"date":6,"day":4,"hours":14,"minutes":40,"month":5,"seconds":13,"time":1559803213000,"timezoneOffset":-480,"year":119},"id":26,"realName":"王洋","uid":67351,"updateTime":{"date":6,"day":4,"hours":14,"minutes":40,"month":5,"seconds":13,"time":1559803213000,"timezoneOffset":-480,"year":119}},"otcUserWechat":{"account":"18655056179","createTime":{"date":6,"day":4,"hours":16,"minutes":14,"month":5,"seconds":12,"time":1559808852000,"timezoneOffset":-480,"year":119},"id":57,"qrcode":"https://image.baidu.com/search/detail?ct=50331648","realName":"王洋","type":2,"uid":67351,"updateTime":{"date":6,"day":4,"hours":16,"minutes":14,"month":5,"seconds":12,"time":1559808852000,"timezoneOffset":-480,"year":119}}}
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
         * otcUserAlipay : {"account":"xialaini@163.com","createTime":{"date":6,"day":4,"hours":16,"minutes":0,"month":5,"seconds":38,"time":1559808038000,"timezoneOffset":-480,"year":119},"id":56,"qrcode":"https://image.baidu.com/search/detail?ct=50331648","realName":"王洋","type":1,"uid":67351,"updateTime":{"date":6,"day":4,"hours":16,"minutes":0,"month":5,"seconds":38,"time":1559808038000,"timezoneOffset":-480,"year":119}}
         * otcUserBank : {"bankAdress":"安徽省合肥市庐阳区","bankCity":"","bankName":"农业银行","bankNum":"255665555","bankProvince":"","createTime":{"date":6,"day":4,"hours":14,"minutes":40,"month":5,"seconds":13,"time":1559803213000,"timezoneOffset":-480,"year":119},"id":26,"realName":"王洋","uid":67351,"updateTime":{"date":6,"day":4,"hours":14,"minutes":40,"month":5,"seconds":13,"time":1559803213000,"timezoneOffset":-480,"year":119}}
         * otcUserWechat : {"account":"18655056179","createTime":{"date":6,"day":4,"hours":16,"minutes":14,"month":5,"seconds":12,"time":1559808852000,"timezoneOffset":-480,"year":119},"id":57,"qrcode":"https://image.baidu.com/search/detail?ct=50331648","realName":"王洋","type":2,"uid":67351,"updateTime":{"date":6,"day":4,"hours":16,"minutes":14,"month":5,"seconds":12,"time":1559808852000,"timezoneOffset":-480,"year":119}}
         */

        private OtcUserAlipayBean otcUserAlipay;
        private OtcUserBankBean otcUserBank;
        private OtcUserWechatBean otcUserWechat;

        public OtcUserAlipayBean getOtcUserAlipay() {
            return otcUserAlipay;
        }

        public void setOtcUserAlipay(OtcUserAlipayBean otcUserAlipay) {
            this.otcUserAlipay = otcUserAlipay;
        }

        public OtcUserBankBean getOtcUserBank() {
            return otcUserBank;
        }

        public void setOtcUserBank(OtcUserBankBean otcUserBank) {
            this.otcUserBank = otcUserBank;
        }

        public OtcUserWechatBean getOtcUserWechat() {
            return otcUserWechat;
        }

        public void setOtcUserWechat(OtcUserWechatBean otcUserWechat) {
            this.otcUserWechat = otcUserWechat;
        }

        public static class OtcUserAlipayBean  implements Parcelable{
            /**
             * account : xialaini@163.com
             * createTime : {"date":6,"day":4,"hours":16,"minutes":0,"month":5,"seconds":38,"time":1559808038000,"timezoneOffset":-480,"year":119}
             * id : 56
             * qrcode : https://image.baidu.com/search/detail?ct=50331648
             * realName : 王洋
             * type : 1
             * uid : 67351
             * updateTime : {"date":6,"day":4,"hours":16,"minutes":0,"month":5,"seconds":38,"time":1559808038000,"timezoneOffset":-480,"year":119}
             */

            private String account;
            private CreateTimeBean createTime;
            private int id;
            private String qrcode;
            private String realName;
            private int type;
            private int uid;
            private UpdateTimeBean updateTime;

            protected OtcUserAlipayBean(Parcel in) {
                account = in.readString();
                id = in.readInt();
                qrcode = in.readString();
                realName = in.readString();
                type = in.readInt();
                uid = in.readInt();
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

            @Override
            public int describeContents() {
                return 0;
            }

            public static final Creator<OtcUserAlipayBean> CREATOR = new Creator<OtcUserAlipayBean>() {
                @Override
                public OtcUserAlipayBean createFromParcel(Parcel in) {
                    return new OtcUserAlipayBean(in);
                }

                @Override
                public OtcUserAlipayBean[] newArray(int size) {
                    return new OtcUserAlipayBean[size];
                }
            };

            public String getAccount() {
                return account;
            }

            public void setAccount(String account) {
                this.account = account;
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

            public static class CreateTimeBean {
                /**
                 * date : 6
                 * day : 4
                 * hours : 16
                 * minutes : 0
                 * month : 5
                 * seconds : 38
                 * time : 1559808038000
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
                 * date : 6
                 * day : 4
                 * hours : 16
                 * minutes : 0
                 * month : 5
                 * seconds : 38
                 * time : 1559808038000
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

        public static class OtcUserBankBean  implements Parcelable{
            /**
             * bankAdress : 安徽省合肥市庐阳区
             * bankCity :
             * bankName : 农业银行
             * bankNum : 255665555
             * bankProvince :
             * createTime : {"date":6,"day":4,"hours":14,"minutes":40,"month":5,"seconds":13,"time":1559803213000,"timezoneOffset":-480,"year":119}
             * id : 26
             * realName : 王洋
             * uid : 67351
             * updateTime : {"date":6,"day":4,"hours":14,"minutes":40,"month":5,"seconds":13,"time":1559803213000,"timezoneOffset":-480,"year":119}
             */

            private String bankAdress;
            private String bankCity;
            private String bankName;
            private String bankNum;
            private String bankProvince;
            private CreateTimeBeanX createTime;
            private int id;
            private String realName;
            private int uid;
            private UpdateTimeBeanX updateTime;

            protected OtcUserBankBean(Parcel in) {
                bankAdress = in.readString();
                bankCity = in.readString();
                bankName = in.readString();
                bankNum = in.readString();
                bankProvince = in.readString();
                id = in.readInt();
                realName = in.readString();
                uid = in.readInt();
            }

            public static final Creator<OtcUserBankBean> CREATOR = new Creator<OtcUserBankBean>() {
                @Override
                public OtcUserBankBean createFromParcel(Parcel in) {
                    return new OtcUserBankBean(in);
                }

                @Override
                public OtcUserBankBean[] newArray(int size) {
                    return new OtcUserBankBean[size];
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

            public CreateTimeBeanX getCreateTime() {
                return createTime;
            }

            public void setCreateTime(CreateTimeBeanX createTime) {
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

            public UpdateTimeBeanX getUpdateTime() {
                return updateTime;
            }

            public void setUpdateTime(UpdateTimeBeanX updateTime) {
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

            public static class CreateTimeBeanX {
                /**
                 * date : 6
                 * day : 4
                 * hours : 14
                 * minutes : 40
                 * month : 5
                 * seconds : 13
                 * time : 1559803213000
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
                 * date : 6
                 * day : 4
                 * hours : 14
                 * minutes : 40
                 * month : 5
                 * seconds : 13
                 * time : 1559803213000
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

        public static class OtcUserWechatBean  implements Parcelable{
            /**
             * account : 18655056179
             * createTime : {"date":6,"day":4,"hours":16,"minutes":14,"month":5,"seconds":12,"time":1559808852000,"timezoneOffset":-480,"year":119}
             * id : 57
             * qrcode : https://image.baidu.com/search/detail?ct=50331648
             * realName : 王洋
             * type : 2
             * uid : 67351
             * updateTime : {"date":6,"day":4,"hours":16,"minutes":14,"month":5,"seconds":12,"time":1559808852000,"timezoneOffset":-480,"year":119}
             */

            private String account;
            private CreateTimeBeanXX createTime;
            private int id;
            private String qrcode;
            private String realName;
            private int type;
            private int uid;
            private UpdateTimeBeanXX updateTime;

            protected OtcUserWechatBean(Parcel in) {
                account = in.readString();
                id = in.readInt();
                qrcode = in.readString();
                realName = in.readString();
                type = in.readInt();
                uid = in.readInt();
            }

            public static final Creator<OtcUserWechatBean> CREATOR = new Creator<OtcUserWechatBean>() {
                @Override
                public OtcUserWechatBean createFromParcel(Parcel in) {
                    return new OtcUserWechatBean(in);
                }

                @Override
                public OtcUserWechatBean[] newArray(int size) {
                    return new OtcUserWechatBean[size];
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
                 * date : 6
                 * day : 4
                 * hours : 16
                 * minutes : 14
                 * month : 5
                 * seconds : 12
                 * time : 1559808852000
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
                 * minutes : 14
                 * month : 5
                 * seconds : 12
                 * time : 1559808852000
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
