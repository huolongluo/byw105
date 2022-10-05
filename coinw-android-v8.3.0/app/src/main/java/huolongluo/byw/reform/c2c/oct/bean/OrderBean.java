package huolongluo.byw.reform.c2c.oct.bean;

import android.os.Parcel;
import android.os.Parcelable;

//暂时未把以前替换成该类，待修改并测试验证
public class OrderBean implements Parcelable {
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
    private double payLimit;
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

    protected OrderBean(Parcel in) {
        orderNo = in.readString();
        account = in.readString();
        adId = in.readInt();
        adStatus = in.readInt();
        adUserId = in.readInt();
        adUserName = in.readString();
        amount = in.readDouble();
        coinId = in.readInt();
        coinName = in.readString();
        createTime = in.readParcelable(CreateTimeBean.class.getClassLoader());
        createTime_s = in.readString();
        endTime = in.readString();
        fee = in.readDouble();
        id = in.readInt();
        payLimit = in.readDouble();
        payType = in.readInt();
        price = in.readDouble();
        qrCode = in.readString();
        remark = in.readString();
        startTime = in.readString();
        status = in.readInt();
        totalAmount = in.readDouble();
        transReferNum = in.readInt();
        type = in.readInt();
        uid = in.readInt();
        updateTime = in.readParcelable(UpdateTimeBean.class.getClassLoader());
        userName = in.readString();
        version = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(orderNo);
        dest.writeString(account);
        dest.writeInt(adId);
        dest.writeInt(adStatus);
        dest.writeInt(adUserId);
        dest.writeString(adUserName);
        dest.writeDouble(amount);
        dest.writeInt(coinId);
        dest.writeString(coinName);
        dest.writeParcelable(createTime, flags);
        dest.writeString(createTime_s);
        dest.writeString(endTime);
        dest.writeDouble(fee);
        dest.writeInt(id);
        dest.writeDouble(payLimit);
        dest.writeInt(payType);
        dest.writeDouble(price);
        dest.writeString(qrCode);
        dest.writeString(remark);
        dest.writeString(startTime);
        dest.writeInt(status);
        dest.writeDouble(totalAmount);
        dest.writeInt(transReferNum);
        dest.writeInt(type);
        dest.writeInt(uid);
        dest.writeParcelable(updateTime, flags);
        dest.writeString(userName);
        dest.writeInt(version);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<OrderBean> CREATOR = new Creator<OrderBean>() {
        @Override
        public OrderBean createFromParcel(Parcel in) {
            return new OrderBean(in);
        }

        @Override
        public OrderBean[] newArray(int size) {
            return new OrderBean[size];
        }
    };

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

    public double getPayLimit() {
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

    public static class CreateTimeBean implements Parcelable {
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
