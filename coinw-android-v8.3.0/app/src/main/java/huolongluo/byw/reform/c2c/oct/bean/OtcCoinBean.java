package huolongluo.byw.reform.c2c.oct.bean;

import java.util.List;

/**
 * Created by Administrator on 2019/5/30 0030.
 */

public class OtcCoinBean {
    /**
     * code : 0
     * data : [{"buyFee":0.01,"coinId":2,"coinName":"CNYT","createTime":{"date":26,"day":5,"hours":9,"minutes":50,"month":3,"seconds":45,"time":1556243445000,"timezoneOffset":-480,"year":119},"id":12,"price":1,"sellFee":0.01,"status":1,"updateTime":{"date":26,"day":5,"hours":9,"minutes":50,"month":3,"seconds":45,"time":1556243445000,"timezoneOffset":-480,"year":119}},{"buyFee":0.02,"coinId":29,"coinName":"USDT","createTime":{"date":30,"day":2,"hours":14,"minutes":4,"month":3,"seconds":34,"time":1556604274000,"timezoneOffset":-480,"year":119},"id":13,"price":6.81,"sellFee":0.01,"status":1,"updateTime":{"date":30,"day":2,"hours":14,"minutes":4,"month":3,"seconds":34,"time":1556604274000,"timezoneOffset":-480,"year":119}}]
     * result : true
     * value : 操作成功
     */

    private int code;
    private boolean result;
    private String value;
    private List<DataBean> data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
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

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public List<DataBean> getData() {
        return data;
    }

    public static class DataBean {
        /**
         * buyFee : 0.01
         * coinId : 2
         * coinName : CNYT
         * createTime : {"date":26,"day":5,"hours":9,"minutes":50,"month":3,"seconds":45,"time":1556243445000,"timezoneOffset":-480,"year":119}
         * id : 12
         * price : 1
         * sellFee : 0.01
         * status : 1
         * updateTime : {"date":26,"day":5,"hours":9,"minutes":50,"month":3,"seconds":45,"time":1556243445000,"timezoneOffset":-480,"year":119}
         */

        private double buyFee;
        private int coinId;
        private String coinName;
        private CreateTimeBean createTime;
        private int id;
        private double price;
        private double sellFee;
        private int status;
        private UpdateTimeBean updateTime;
        private int quantityPrecision;
        private int pricePrecision;

        public int getQuantityPrecision() {
            return quantityPrecision;
        }

        public void setQuantityPrecision(int quantityPrecision) {
            this.quantityPrecision = quantityPrecision;
        }

        public int getPricePrecision() {
            return pricePrecision;
        }

        public void setPricePrecision(int pricePrecision) {
            this.pricePrecision = pricePrecision;
        }

        public double getBuyFee() {
            return buyFee;
        }

        public void setBuyFee(double buyFee) {
            this.buyFee = buyFee;
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

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        public double getSellFee() {
            return sellFee;
        }

        public void setSellFee(double sellFee) {
            this.sellFee = sellFee;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public UpdateTimeBean getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(UpdateTimeBean updateTime) {
            this.updateTime = updateTime;
        }

        public static class CreateTimeBean {
            /**
             * date : 26
             * day : 5
             * hours : 9
             * minutes : 50
             * month : 3
             * seconds : 45
             * time : 1556243445000
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
             * date : 26
             * day : 5
             * hours : 9
             * minutes : 50
             * month : 3
             * seconds : 45
             * time : 1556243445000
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
