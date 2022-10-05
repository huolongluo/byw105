package huolongluo.byw.byw.ui.fragment.contractTab;

public class NMInfoEntity {

    /**
     * result : true
     * code : 0
     * value : 操作成功
     * data : {"beginDt":{"date":20,"day":5,"hours":14,"minutes":5,"month":2,"seconds":19,"time":1584684319000,"timezoneOffset":-480,"year":120},"coinId":63,"endDt":{"date":21,"day":6,"hours":14,"minutes":5,"month":2,"seconds":21,"time":1584770721000,"timezoneOffset":-480,"year":120},"isEnable":1,"oneLevelMud":"7","oneLevelTransfer":"10","remainMudQuota":"200","status":1,"totalMudQuota":"200","twoLevelMud":"10","twoLevelTransfer":"20"}
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
         * beginDt : {"date":20,"day":5,"hours":14,"minutes":5,"month":2,"seconds":19,"time":1584684319000,"timezoneOffset":-480,"year":120}
         * coinId : 63
         * endDt : {"date":21,"day":6,"hours":14,"minutes":5,"month":2,"seconds":21,"time":1584770721000,"timezoneOffset":-480,"year":120}
         * isEnable : 1
         * oneLevelMud : 7
         * oneLevelTransfer : 10
         * remainMudQuota : 200
         * status : 1
         * totalMudQuota : 200
         * twoLevelMud : 10
         * twoLevelTransfer : 20
         */

        private BeginDtBean beginDt;
        private int coinId;
        private EndDtBean endDt;
        private int isEnable;
        private String oneLevelMud;
        private String oneLevelTransfer;
        private String remainMudQuota;
        private int status;
        private String totalMudQuota;
        private String twoLevelMud;
        private String twoLevelTransfer;
        private String url;//活动地址

        public BeginDtBean getBeginDt() {
            return beginDt;
        }

        public void setBeginDt(BeginDtBean beginDt) {
            this.beginDt = beginDt;
        }

        public int getCoinId() {
            return coinId;
        }

        public void setCoinId(int coinId) {
            this.coinId = coinId;
        }

        public EndDtBean getEndDt() {
            return endDt;
        }

        public void setEndDt(EndDtBean endDt) {
            this.endDt = endDt;
        }

        public int getIsEnable() {
            return isEnable;
        }

        public void setIsEnable(int isEnable) {
            this.isEnable = isEnable;
        }

        public String getOneLevelMud() {
            return oneLevelMud;
        }

        public void setOneLevelMud(String oneLevelMud) {
            this.oneLevelMud = oneLevelMud;
        }

        public String getOneLevelTransfer() {
            return oneLevelTransfer;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getUrl() {
            return url;
        }

        public void setOneLevelTransfer(String oneLevelTransfer) {
            this.oneLevelTransfer = oneLevelTransfer;
        }

        public String getRemainMudQuota() {
            return remainMudQuota;
        }

        public void setRemainMudQuota(String remainMudQuota) {
            this.remainMudQuota = remainMudQuota;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getTotalMudQuota() {
            return totalMudQuota;
        }

        public void setTotalMudQuota(String totalMudQuota) {
            this.totalMudQuota = totalMudQuota;
        }

        public String getTwoLevelMud() {
            return twoLevelMud;
        }

        public void setTwoLevelMud(String twoLevelMud) {
            this.twoLevelMud = twoLevelMud;
        }

        public String getTwoLevelTransfer() {
            return twoLevelTransfer;
        }

        public void setTwoLevelTransfer(String twoLevelTransfer) {
            this.twoLevelTransfer = twoLevelTransfer;
        }

        public static class BeginDtBean {
            /**
             * date : 20
             * day : 5
             * hours : 14
             * minutes : 5
             * month : 2
             * seconds : 19
             * time : 1584684319000
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

        public static class EndDtBean {
            /**
             * date : 21
             * day : 6
             * hours : 14
             * minutes : 5
             * month : 2
             * seconds : 21
             * time : 1584770721000
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
    }
}
