package huolongluo.byw.reform.c2c.oct.bean;

/**
 * Created by dell on 2019/6/17.
 */

public class AssetInfoBean  {
    /**
     * code : 0
     * data : {"fvirtualwallet":{"FShortName":"","falreadylendbtc":0,"fborrowbtc":0,"fcanlendbtc":0,"ffrozen":212,"ffrozen_s":"212","ffrozenlendbtc":0,"fhaveappointborrowbtc":0,"fid":2280139,"flastupdatetime":{"date":17,"day":1,"hours":16,"minutes":57,"month":5,"seconds":23,"time":1560761843000,"timezoneOffset":-480,"year":119},"ftotal":1.872400197589406E7,"ftotal_s":"18724001.97589406","fuid":67351,"fviFid":2,"otcBond":0,"otcfrozen":0,"otcfrozen_s":"0","otctotal":2349,"otctotal_s":"2349","version":410}}
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
         * fvirtualwallet : {"FShortName":"","falreadylendbtc":0,"fborrowbtc":0,"fcanlendbtc":0,"ffrozen":212,"ffrozen_s":"212","ffrozenlendbtc":0,"fhaveappointborrowbtc":0,"fid":2280139,"flastupdatetime":{"date":17,"day":1,"hours":16,"minutes":57,"month":5,"seconds":23,"time":1560761843000,"timezoneOffset":-480,"year":119},"ftotal":1.872400197589406E7,"ftotal_s":"18724001.97589406","fuid":67351,"fviFid":2,"otcBond":0,"otcfrozen":0,"otcfrozen_s":"0","otctotal":2349,"otctotal_s":"2349","version":410}
         */

        private FvirtualwalletBean fvirtualwallet;

        public FvirtualwalletBean getFvirtualwallet() {
            return fvirtualwallet;
        }

        public void setFvirtualwallet(FvirtualwalletBean fvirtualwallet) {
            this.fvirtualwallet = fvirtualwallet;
        }

        public static class FvirtualwalletBean {
            /**
             * FShortName :
             * falreadylendbtc : 0.0
             * fborrowbtc : 0.0
             * fcanlendbtc : 0.0
             * ffrozen : 212
             * ffrozen_s : 212
             * ffrozenlendbtc : 0.0
             * fhaveappointborrowbtc : 0.0
             * fid : 2280139
             * flastupdatetime : {"date":17,"day":1,"hours":16,"minutes":57,"month":5,"seconds":23,"time":1560761843000,"timezoneOffset":-480,"year":119}
             * ftotal : 1.872400197589406E7
             * ftotal_s : 18724001.97589406
             * fuid : 67351
             * fviFid : 2
             * otcBond : 0.0
             * otcfrozen : 0.0
             * otcfrozen_s : 0
             * otctotal : 2349
             * otctotal_s : 2349
             * version : 410
             */

            private String FShortName;
            private double falreadylendbtc;
            private double fborrowbtc;
            private double fcanlendbtc;
            private double ffrozen;
            private String ffrozen_s;
            private double ffrozenlendbtc;
            private double fhaveappointborrowbtc;
            private int fid;
            private FlastupdatetimeBean flastupdatetime;
            private double ftotal;
            private String ftotal_s;
            private int fuid;
            private int fviFid;
            private double otcBond;
            private double otcfrozen;
            private String otcfrozen_s;
            private double otctotal;
            private String otctotal_s;
            private int version;

            public String getFShortName() {
                return FShortName;
            }

            public void setFShortName(String FShortName) {
                this.FShortName = FShortName;
            }

            public double getFalreadylendbtc() {
                return falreadylendbtc;
            }

            public void setFalreadylendbtc(double falreadylendbtc) {
                this.falreadylendbtc = falreadylendbtc;
            }

            public double getFborrowbtc() {
                return fborrowbtc;
            }

            public void setFborrowbtc(double fborrowbtc) {
                this.fborrowbtc = fborrowbtc;
            }

            public double getFcanlendbtc() {
                return fcanlendbtc;
            }

            public void setFcanlendbtc(double fcanlendbtc) {
                this.fcanlendbtc = fcanlendbtc;
            }

            public double getFfrozen() {
                return ffrozen;
            }

            public void setFfrozen(double ffrozen) {
                this.ffrozen = ffrozen;
            }

            public String getFfrozen_s() {
                return ffrozen_s;
            }

            public void setFfrozen_s(String ffrozen_s) {
                this.ffrozen_s = ffrozen_s;
            }

            public double getFfrozenlendbtc() {
                return ffrozenlendbtc;
            }

            public void setFfrozenlendbtc(double ffrozenlendbtc) {
                this.ffrozenlendbtc = ffrozenlendbtc;
            }

            public double getFhaveappointborrowbtc() {
                return fhaveappointborrowbtc;
            }

            public void setFhaveappointborrowbtc(double fhaveappointborrowbtc) {
                this.fhaveappointborrowbtc = fhaveappointborrowbtc;
            }

            public int getFid() {
                return fid;
            }

            public void setFid(int fid) {
                this.fid = fid;
            }

            public FlastupdatetimeBean getFlastupdatetime() {
                return flastupdatetime;
            }

            public void setFlastupdatetime(FlastupdatetimeBean flastupdatetime) {
                this.flastupdatetime = flastupdatetime;
            }

            public double getFtotal() {
                return ftotal;
            }

            public void setFtotal(double ftotal) {
                this.ftotal = ftotal;
            }

            public String getFtotal_s() {
                return ftotal_s;
            }

            public void setFtotal_s(String ftotal_s) {
                this.ftotal_s = ftotal_s;
            }

            public int getFuid() {
                return fuid;
            }

            public void setFuid(int fuid) {
                this.fuid = fuid;
            }

            public int getFviFid() {
                return fviFid;
            }

            public void setFviFid(int fviFid) {
                this.fviFid = fviFid;
            }

            public double getOtcBond() {
                return otcBond;
            }

            public void setOtcBond(double otcBond) {
                this.otcBond = otcBond;
            }

            public double getOtcfrozen() {
                return otcfrozen;
            }

            public void setOtcfrozen(double otcfrozen) {
                this.otcfrozen = otcfrozen;
            }

            public String getOtcfrozen_s() {
                return otcfrozen_s;
            }

            public void setOtcfrozen_s(String otcfrozen_s) {
                this.otcfrozen_s = otcfrozen_s;
            }

            public double getOtctotal() {
                return otctotal;
            }

            public void setOtctotal(double otctotal) {
                this.otctotal = otctotal;
            }

            public String getOtctotal_s() {
                return otctotal_s;
            }

            public void setOtctotal_s(String otctotal_s) {
                this.otctotal_s = otctotal_s;
            }

            public int getVersion() {
                return version;
            }

            public void setVersion(int version) {
                this.version = version;
            }

            public static class FlastupdatetimeBean {
                /**
                 * date : 17
                 * day : 1
                 * hours : 16
                 * minutes : 57
                 * month : 5
                 * seconds : 23
                 * time : 1560761843000
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
