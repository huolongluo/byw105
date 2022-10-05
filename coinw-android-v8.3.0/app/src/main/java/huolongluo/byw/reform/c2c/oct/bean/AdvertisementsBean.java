package huolongluo.byw.reform.c2c.oct.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by Administrator on 2019/5/31 0031.
 */

public class AdvertisementsBean {
    /**
     * code : 0
     * data : {"data":[{"alipayId":26,"amount":500,"bankId":17,"cancelAmount":0,"coinId":2,"coinName":"CNYT","coinUrl":"","completeOrderNum":0,"completeOrderRate":"0.00","createTime":{"date":29,"day":3,"hours":16,"minutes":26,"month":4,"seconds":2,"time":1559118362000,"timezoneOffset":-480,"year":119},"fee":5,"id":30003,"isOnline":false,"leftAmount":500,"levelCode":2,"orderMax":200,"orderMin":100,"payLimit":10,"price":1,"realAmount":0,"realFee":0,"remark":"","sort":0,"status":0,"type":2,"uid":67353,"updateTime":{"date":29,"day":3,"hours":16,"minutes":26,"month":4,"seconds":2,"time":1559118362000,"timezoneOffset":-480,"year":119},"userName":"_以梦为马_","version":0,"wechatId":25},{"alipayId":0,"amount":400,"bankId":17,"cancelAmount":0,"coinId":2,"coinName":"CNYT","coinUrl":"","completeOrderNum":0,"completeOrderRate":"0.00","createTime":{"date":29,"day":3,"hours":16,"minutes":26,"month":4,"seconds":57,"time":1559118417000,"timezoneOffset":-480,"year":119},"fee":4,"id":30004,"isOnline":false,"leftAmount":400,"levelCode":2,"orderMax":200,"orderMin":100,"payLimit":10,"price":1,"realAmount":0,"realFee":0,"remark":"仅银行卡","sort":0,"status":0,"type":2,"uid":67353,"updateTime":{"date":29,"day":3,"hours":16,"minutes":26,"month":4,"seconds":57,"time":1559118417000,"timezoneOffset":-480,"year":119},"userName":"_以梦为马_","version":0,"wechatId":0},{"alipayId":27,"amount":2000,"bankId":18,"cancelAmount":0,"coinId":2,"coinName":"CNYT","coinUrl":"","completeOrderNum":0,"completeOrderRate":"0","createTime":{"date":29,"day":3,"hours":18,"minutes":26,"month":4,"seconds":19,"time":1559125579000,"timezoneOffset":-480,"year":119},"fee":20,"id":30007,"isOnline":false,"leftAmount":2000,"levelCode":2,"orderMax":500,"orderMin":100,"payLimit":10,"price":1,"realAmount":0,"realFee":0,"remark":"测试备注","sort":0,"status":0,"type":2,"uid":67369,"updateTime":{"date":29,"day":3,"hours":18,"minutes":45,"month":4,"seconds":59,"time":1559126759000,"timezoneOffset":-480,"year":119},"userName":"_寥寥_","version":4,"wechatId":0}],"pageNo":1,"pageSize":10,"total":3}
     * result : true
     * value : 操作成功
     */

    private int code;
    private DataBeanX data;
    private boolean result;
    private String value;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public DataBeanX getData() {
        return data;
    }

    public void setData(DataBeanX data) {
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

    public static class DataBeanX {
        /**
         * data : [{"alipayId":26,"amount":500,"bankId":17,"cancelAmount":0,"coinId":2,"coinName":"CNYT","coinUrl":"","completeOrderNum":0,"completeOrderRate":"0.00","createTime":{"date":29,"day":3,"hours":16,"minutes":26,"month":4,"seconds":2,"time":1559118362000,"timezoneOffset":-480,"year":119},"fee":5,"id":30003,"isOnline":false,"leftAmount":500,"levelCode":2,"orderMax":200,"orderMin":100,"payLimit":10,"price":1,"realAmount":0,"realFee":0,"remark":"","sort":0,"status":0,"type":2,"uid":67353,"updateTime":{"date":29,"day":3,"hours":16,"minutes":26,"month":4,"seconds":2,"time":1559118362000,"timezoneOffset":-480,"year":119},"userName":"_以梦为马_","version":0,"wechatId":25},{"alipayId":0,"amount":400,"bankId":17,"cancelAmount":0,"coinId":2,"coinName":"CNYT","coinUrl":"","completeOrderNum":0,"completeOrderRate":"0.00","createTime":{"date":29,"day":3,"hours":16,"minutes":26,"month":4,"seconds":57,"time":1559118417000,"timezoneOffset":-480,"year":119},"fee":4,"id":30004,"isOnline":false,"leftAmount":400,"levelCode":2,"orderMax":200,"orderMin":100,"payLimit":10,"price":1,"realAmount":0,"realFee":0,"remark":"仅银行卡","sort":0,"status":0,"type":2,"uid":67353,"updateTime":{"date":29,"day":3,"hours":16,"minutes":26,"month":4,"seconds":57,"time":1559118417000,"timezoneOffset":-480,"year":119},"userName":"_以梦为马_","version":0,"wechatId":0},{"alipayId":27,"amount":2000,"bankId":18,"cancelAmount":0,"coinId":2,"coinName":"CNYT","coinUrl":"","completeOrderNum":0,"completeOrderRate":"0","createTime":{"date":29,"day":3,"hours":18,"minutes":26,"month":4,"seconds":19,"time":1559125579000,"timezoneOffset":-480,"year":119},"fee":20,"id":30007,"isOnline":false,"leftAmount":2000,"levelCode":2,"orderMax":500,"orderMin":100,"payLimit":10,"price":1,"realAmount":0,"realFee":0,"remark":"测试备注","sort":0,"status":0,"type":2,"uid":67369,"updateTime":{"date":29,"day":3,"hours":18,"minutes":45,"month":4,"seconds":59,"time":1559126759000,"timezoneOffset":-480,"year":119},"userName":"_寥寥_","version":4,"wechatId":0}]
         * pageNo : 1
         * pageSize : 10
         * total : 3
         */

        private int totalPage;
        private int pageNo;
        private int pageSize;
        private int total;
        private List<DataBean> data;

        public int getPageNo() {
            return pageNo;
        }

        public void setPageNo(int pageNo) {
            this.pageNo = pageNo;
        }

        public int getPageSize() {
            return pageSize;
        }

        public void setPageSize(int pageSize) {
            this.pageSize = pageSize;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public List<DataBean> getData() {
            return data;
        }

        public void setData(List<DataBean> data) {
            this.data = data;
        }

        public int getTotalPage() {
            return totalPage;
        }

        public void setTotalPage(int totalPage) {
            this.totalPage = totalPage;
        }

        public static class DataBean implements Parcelable {
            /**
             * alipayId : 26
             * amount : 500
             * bankId : 17
             * cancelAmount : 0
             * coinId : 2
             * coinName : CNYT
             * coinUrl :
             * completeOrderNum : 0
             * completeOrderRate : 0.00
             * createTime : {"date":29,"day":3,"hours":16,"minutes":26,"month":4,"seconds":2,"time":1559118362000,"timezoneOffset":-480,"year":119}
             * fee : 5
             * id : 30003
             * isOnline : false
             * leftAmount : 500
             * levelCode : 2
             * orderMax : 200
             * orderMin : 100
             * payLimit : 10
             * price : 1
             * realAmount : 0.0
             * realFee : 0.0
             * remark :
             * sort : 0
             * status : 0
             * type : 2
             * uid : 67353
             * updateTime : {"date":29,"day":3,"hours":16,"minutes":26,"month":4,"seconds":2,"time":1559118362000,"timezoneOffset":-480,"year":119}
             * userName : _以梦为马_
             * version : 0
             * wechatId : 25
             */

            private String orderMax_s;

            protected DataBean(Parcel in) {
                orderMax_s = in.readString();
                orderMin_s = in.readString();
                price_s = in.readString();
                alipayId = in.readInt();
                amount = in.readDouble();
                bankId = in.readInt();
                cancelAmount = in.readInt();
                coinId = in.readInt();
                coinName = in.readString();
                coinUrl = in.readString();
                completeOrderNum = in.readInt();
                completeOrderRate = in.readString();
                fee = in.readDouble();
                id = in.readInt();
                isOnline = in.readByte() != 0;
                leftAmount = in.readDouble();
                leftAmount_s = in.readString();
                levelCode = in.readInt();
                orderMax = in.readDouble();
                orderMin = in.readDouble();
                payLimit = in.readInt();
                price = in.readDouble();
                realAmount = in.readDouble();
                realFee = in.readDouble();
                remark = in.readString();
                isExternal = in.readByte() != 0;
                externalChannel = in.readString();
                payAccountType = in.readString();
                sort = in.readInt();
                status = in.readInt();
                type = in.readInt();
                uid = in.readInt();
                userName = in.readString();
                version = in.readInt();
                wechatId = in.readInt();
                payAccountTypes = in.readArrayList(Integer.class.getClassLoader());
                top = in.readByte() != 0;
            }

            public static final Creator<DataBean> CREATOR = new Creator<DataBean>() {
                @Override
                public DataBean createFromParcel(Parcel in) {
                    return new DataBean(in);
                }

                @Override
                public DataBean[] newArray(int size) {
                    return new DataBean[size];
                }
            };

            public String getOrderMax_s() {
                return orderMax_s;
            }

            public void setOrderMax_s(String orderMax_s) {
                this.orderMax_s = orderMax_s;
            }

            public String getOrderMin_s() {
                return orderMin_s;
            }

            public void setOrderMin_s(String orderMin_s) {
                this.orderMin_s = orderMin_s;
            }

            public boolean isOnline() {
                return isOnline;
            }

            public void setOnline(boolean online) {
                isOnline = online;
            }

            private String orderMin_s;
            private String price_s;

            private int alipayId;
            private double amount;
            private int bankId;
            private int cancelAmount;
            private int coinId;
            private String coinName;
            private String coinUrl;
            private int completeOrderNum;
            private String completeOrderRate;
            private CreateTimeBean createTime;
            private double fee;
            private int id;
            private boolean isOnline;
            private double leftAmount;
            private String leftAmount_s;
            private int levelCode;
            private double orderMax;
            private double orderMin;
            private int payLimit;
            private double price;
            private double realAmount;
            private double realFee;
            private String remark;//备注
            private int sort;
            private int status;
            private int type;
            private int uid;
            private UpdateTimeBean updateTime;
            private String userName;
            private int version;
            private int wechatId;
            private List<Integer> payAccountTypes;
            private boolean isExternal;
            private String externalChannel;
            private String payAccountType;
            private boolean top;

            public boolean isTop() {
                return top;
            }

            public void setTop(boolean top) {
                this.top = top;
            }

            public String getPayAccountType() {
                return payAccountType;
            }

            public void setPayAccountType(String payAccountType) {
                this.payAccountType = payAccountType;
            }

            public boolean getIsExternal() {
                return isExternal;
            }

            public void setIsExternal(boolean isExternal) {
                this.isExternal = isExternal;
            }

            public String getExternalChannel() {
                return externalChannel;
            }

            public void setExternalChannel(String externalChannel) {
                this.externalChannel = externalChannel;
            }

            public List<Integer> getPayAccountTypes() {
                return payAccountTypes;
            }

            public void setPayAccountTypes(List<Integer> payAccountTypes) {
                this.payAccountTypes = payAccountTypes;
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

            public int getBankId() {
                return bankId;
            }

            public void setBankId(int bankId) {
                this.bankId = bankId;
            }

            public int getCancelAmount() {
                return cancelAmount;
            }

            public void setCancelAmount(int cancelAmount) {
                this.cancelAmount = cancelAmount;
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

            public int getCompleteOrderNum() {
                return completeOrderNum;
            }

            public void setCompleteOrderNum(int completeOrderNum) {
                this.completeOrderNum = completeOrderNum;
            }

            public String getCompleteOrderRate() {
                return completeOrderRate;
            }

            public void setCompleteOrderRate(String completeOrderRate) {
                this.completeOrderRate = completeOrderRate;
            }

            public CreateTimeBean getCreateTime() {
                return createTime;
            }

            public void setCreateTime(CreateTimeBean createTime) {
                this.createTime = createTime;
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

            public boolean isIsOnline() {
                return isOnline;
            }

            public void setIsOnline(boolean isOnline) {
                this.isOnline = isOnline;
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

            public int getLevelCode() {
                return levelCode;
            }

            public void setLevelCode(int levelCode) {
                this.levelCode = levelCode;
            }

            public double getOrderMax() {
                return orderMax;
            }

            public void setOrderMax(double orderMax) {
                this.orderMax = orderMax;
            }

            public double getOrderMin() {
                return orderMin;
            }

            public void setOrderMin(double orderMin) {
                this.orderMin = orderMin;
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

            public double getRealAmount() {
                return realAmount;
            }

            public void setRealAmount(double realAmount) {
                this.realAmount = realAmount;
            }

            public double getRealFee() {
                return realFee;
            }

            public void setRealFee(double realFee) {
                this.realFee = realFee;
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

            public int getWechatId() {
                return wechatId;
            }

            public void setWechatId(int wechatId) {
                this.wechatId = wechatId;
            }

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
                dest.writeString(orderMax_s);
                dest.writeString(orderMin_s);
                dest.writeString(price_s);
                dest.writeInt(alipayId);
                dest.writeDouble(amount);
                dest.writeInt(bankId);
                dest.writeInt(cancelAmount);
                dest.writeInt(coinId);
                dest.writeString(coinName);
                dest.writeString(coinUrl);
                dest.writeInt(completeOrderNum);
                dest.writeString(completeOrderRate);
                dest.writeDouble(fee);
                dest.writeInt(id);
                dest.writeByte((byte) (isOnline ? 1 : 0));
                dest.writeDouble(leftAmount);
                dest.writeString(leftAmount_s);
                dest.writeInt(levelCode);
                dest.writeDouble(orderMax);
                dest.writeDouble(orderMin);
                dest.writeInt(payLimit);
                dest.writeDouble(price);
                dest.writeDouble(realAmount);
                dest.writeDouble(realFee);
                dest.writeString(remark);
                dest.writeByte((byte) (isExternal ? 1 : 0));
                dest.writeString(externalChannel);
                dest.writeString(payAccountType);
                dest.writeInt(sort);
                dest.writeInt(status);
                dest.writeInt(type);
                dest.writeInt(uid);
                dest.writeString(userName);
                dest.writeInt(version);
                dest.writeInt(wechatId);
                dest.writeList(payAccountTypes);
                dest.writeByte((byte) (top ? 1 : 0));
            }

            public String getPrice_s() {
                return price_s;
            }

            public void setPrice_s(String price_s) {
                this.price_s = price_s;
            }

            public static class CreateTimeBean implements Parcelable {
                /**
                 * date : 29
                 * day : 3
                 * hours : 16
                 * minutes : 26
                 * month : 4
                 * seconds : 2
                 * time : 1559118362000
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

                protected CreateTimeBean(Parcel in) {
                    date = in.readInt();
                    day = in.readInt();
                    hours = in.readInt();
                    minutes = in.readInt();
                    month = in.readInt();
                    seconds = in.readInt();
                    time = in.readLong();
                    timezoneOffset = in.readInt();
                    year = in.readInt();
                }

                @Override
                public void writeToParcel(Parcel dest, int flags) {
                    dest.writeInt(date);
                    dest.writeInt(day);
                    dest.writeInt(hours);
                    dest.writeInt(minutes);
                    dest.writeInt(month);
                    dest.writeInt(seconds);
                    dest.writeLong(time);
                    dest.writeInt(timezoneOffset);
                    dest.writeInt(year);
                }

                @Override
                public int describeContents() {
                    return 0;
                }

                public static final Creator<CreateTimeBean> CREATOR = new Creator<CreateTimeBean>() {
                    @Override
                    public CreateTimeBean createFromParcel(Parcel in) {
                        return new CreateTimeBean(in);
                    }

                    @Override
                    public CreateTimeBean[] newArray(int size) {
                        return new CreateTimeBean[size];
                    }
                };

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

            public static class UpdateTimeBean implements Parcelable {
                /**
                 * date : 29
                 * day : 3
                 * hours : 16
                 * minutes : 26
                 * month : 4
                 * seconds : 2
                 * time : 1559118362000
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

                protected UpdateTimeBean(Parcel in) {
                    date = in.readInt();
                    day = in.readInt();
                    hours = in.readInt();
                    minutes = in.readInt();
                    month = in.readInt();
                    seconds = in.readInt();
                    time = in.readLong();
                    timezoneOffset = in.readInt();
                    year = in.readInt();
                }

                @Override
                public void writeToParcel(Parcel dest, int flags) {
                    dest.writeInt(date);
                    dest.writeInt(day);
                    dest.writeInt(hours);
                    dest.writeInt(minutes);
                    dest.writeInt(month);
                    dest.writeInt(seconds);
                    dest.writeLong(time);
                    dest.writeInt(timezoneOffset);
                    dest.writeInt(year);
                }

                @Override
                public int describeContents() {
                    return 0;
                }

                public static final Creator<UpdateTimeBean> CREATOR = new Creator<UpdateTimeBean>() {
                    @Override
                    public UpdateTimeBean createFromParcel(Parcel in) {
                        return new UpdateTimeBean(in);
                    }

                    @Override
                    public UpdateTimeBean[] newArray(int size) {
                        return new UpdateTimeBean[size];
                    }
                };

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
