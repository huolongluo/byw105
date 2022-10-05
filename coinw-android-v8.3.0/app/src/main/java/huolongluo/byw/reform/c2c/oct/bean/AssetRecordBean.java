package huolongluo.byw.reform.c2c.oct.bean;

import java.util.List;

/**
 * Created by dell on 2019/6/17.
 */

public class AssetRecordBean {
    /**
     * code : 0
     * data : {"beginPage":1,"count":0,"currentPage":1,"listData":[{"coinName":"CNYT","createTime":{"date":17,"day":1,"hours":20,"minutes":56,"month":5,"seconds":25,"time":1560776185000,"timezoneOffset":-480,"year":119},"createTime_s":"2019-06-17 20:56:25","id":165,"num":3,"type":"1"},{"coinName":"CNYT","createTime":{"date":17,"day":1,"hours":20,"minutes":56,"month":5,"seconds":17,"time":1560776177000,"timezoneOffset":-480,"year":119},"createTime_s":"2019-06-17 20:56:17","id":164,"num":1,"type":"2"},{"coinName":"CNYT","createTime":{"date":17,"day":1,"hours":20,"minutes":51,"month":5,"seconds":2,"time":1560775862000,"timezoneOffset":-480,"year":119},"createTime_s":"2019-06-17 20:51:02","id":163,"num":1,"type":"1"},{"coinName":"CNYT","createTime":{"date":17,"day":1,"hours":20,"minutes":50,"month":5,"seconds":56,"time":1560775856000,"timezoneOffset":-480,"year":119},"createTime_s":"2019-06-17 20:50:56","id":162,"num":1,"type":"2"},{"coinName":"CNYT","createTime":{"date":17,"day":1,"hours":20,"minutes":47,"month":5,"seconds":15,"time":1560775635000,"timezoneOffset":-480,"year":119},"createTime_s":"2019-06-17 20:47:15","id":161,"num":1,"type":"1"},{"coinName":"CNYT","createTime":{"date":17,"day":1,"hours":20,"minutes":47,"month":5,"seconds":9,"time":1560775629000,"timezoneOffset":-480,"year":119},"createTime_s":"2019-06-17 20:47:09","id":160,"num":1,"type":"1"},{"coinName":"CNYT","createTime":{"date":17,"day":1,"hours":20,"minutes":46,"month":5,"seconds":49,"time":1560775609000,"timezoneOffset":-480,"year":119},"createTime_s":"2019-06-17 20:46:49","id":159,"num":1,"type":"1"},{"coinName":"CNYT","createTime":{"date":17,"day":1,"hours":20,"minutes":45,"month":5,"seconds":45,"time":1560775545000,"timezoneOffset":-480,"year":119},"createTime_s":"2019-06-17 20:45:45","id":158,"num":1,"type":"1"},{"coinName":"CNYT","createTime":{"date":17,"day":1,"hours":20,"minutes":45,"month":5,"seconds":42,"time":1560775542000,"timezoneOffset":-480,"year":119},"createTime_s":"2019-06-17 20:45:42","id":157,"num":1,"type":"1"},{"coinName":"CNYT","createTime":{"date":17,"day":1,"hours":20,"minutes":45,"month":5,"seconds":36,"time":1560775536000,"timezoneOffset":-480,"year":119},"createTime_s":"2019-06-17 20:45:36","id":156,"num":1,"type":"1"}],"nextPage":2,"pageSize":10,"prePage":1,"totalCount":23,"totalPage":3}
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
         * beginPage : 1
         * count : 0
         * currentPage : 1
         * listData : [{"coinName":"CNYT","createTime":{"date":17,"day":1,"hours":20,"minutes":56,"month":5,"seconds":25,"time":1560776185000,"timezoneOffset":-480,"year":119},"createTime_s":"2019-06-17 20:56:25","id":165,"num":3,"type":"1"},{"coinName":"CNYT","createTime":{"date":17,"day":1,"hours":20,"minutes":56,"month":5,"seconds":17,"time":1560776177000,"timezoneOffset":-480,"year":119},"createTime_s":"2019-06-17 20:56:17","id":164,"num":1,"type":"2"},{"coinName":"CNYT","createTime":{"date":17,"day":1,"hours":20,"minutes":51,"month":5,"seconds":2,"time":1560775862000,"timezoneOffset":-480,"year":119},"createTime_s":"2019-06-17 20:51:02","id":163,"num":1,"type":"1"},{"coinName":"CNYT","createTime":{"date":17,"day":1,"hours":20,"minutes":50,"month":5,"seconds":56,"time":1560775856000,"timezoneOffset":-480,"year":119},"createTime_s":"2019-06-17 20:50:56","id":162,"num":1,"type":"2"},{"coinName":"CNYT","createTime":{"date":17,"day":1,"hours":20,"minutes":47,"month":5,"seconds":15,"time":1560775635000,"timezoneOffset":-480,"year":119},"createTime_s":"2019-06-17 20:47:15","id":161,"num":1,"type":"1"},{"coinName":"CNYT","createTime":{"date":17,"day":1,"hours":20,"minutes":47,"month":5,"seconds":9,"time":1560775629000,"timezoneOffset":-480,"year":119},"createTime_s":"2019-06-17 20:47:09","id":160,"num":1,"type":"1"},{"coinName":"CNYT","createTime":{"date":17,"day":1,"hours":20,"minutes":46,"month":5,"seconds":49,"time":1560775609000,"timezoneOffset":-480,"year":119},"createTime_s":"2019-06-17 20:46:49","id":159,"num":1,"type":"1"},{"coinName":"CNYT","createTime":{"date":17,"day":1,"hours":20,"minutes":45,"month":5,"seconds":45,"time":1560775545000,"timezoneOffset":-480,"year":119},"createTime_s":"2019-06-17 20:45:45","id":158,"num":1,"type":"1"},{"coinName":"CNYT","createTime":{"date":17,"day":1,"hours":20,"minutes":45,"month":5,"seconds":42,"time":1560775542000,"timezoneOffset":-480,"year":119},"createTime_s":"2019-06-17 20:45:42","id":157,"num":1,"type":"1"},{"coinName":"CNYT","createTime":{"date":17,"day":1,"hours":20,"minutes":45,"month":5,"seconds":36,"time":1560775536000,"timezoneOffset":-480,"year":119},"createTime_s":"2019-06-17 20:45:36","id":156,"num":1,"type":"1"}]
         * nextPage : 2
         * pageSize : 10
         * prePage : 1
         * totalCount : 23
         * totalPage : 3
         */

        private int beginPage;
        private int count;
        private int currentPage;
        private int nextPage;
        private int pageSize;
        private int prePage;
        private double totalCount;
        private int totalPage;
        private List<ListDataBean> listData;

        public int getBeginPage() {
            return beginPage;
        }

        public void setBeginPage(int beginPage) {
            this.beginPage = beginPage;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public int getCurrentPage() {
            return currentPage;
        }

        public void setCurrentPage(int currentPage) {
            this.currentPage = currentPage;
        }

        public int getNextPage() {
            return nextPage;
        }

        public void setNextPage(int nextPage) {
            this.nextPage = nextPage;
        }

        public int getPageSize() {
            return pageSize;
        }

        public void setPageSize(int pageSize) {
            this.pageSize = pageSize;
        }

        public int getPrePage() {
            return prePage;
        }

        public void setPrePage(int prePage) {
            this.prePage = prePage;
        }

        public double getTotalCount() {
            return totalCount;
        }

        public void setTotalCount(double totalCount) {
            this.totalCount = totalCount;
        }

        public int getTotalPage() {
            return totalPage;
        }

        public void setTotalPage(int totalPage) {
            this.totalPage = totalPage;
        }

        public List<ListDataBean> getListData() {
            return listData;
        }

        public void setListData(List<ListDataBean> listData) {
            this.listData = listData;
        }

        public static class ListDataBean {
            /**
             * coinName : CNYT
             * createTime : {"date":17,"day":1,"hours":20,"minutes":56,"month":5,"seconds":25,"time":1560776185000,"timezoneOffset":-480,"year":119}
             * createTime_s : 2019-06-17 20:56:25
             * id : 165
             * num : 3
             * type : 1
             */

            //根据服务器数据更新
//            {
//                "channel": 1,
//                    "coinName": "CNYT",
//                    "createTime": {
//                "date": 7,
//                        "day": 1,
//                        "hours": 17,
//                        "minutes": 22,
//                        "month": 8,
//                        "seconds": 21,
//                        "time": 1599470541000,
//                        "timezoneOffset": -480,
//                        "year": 120
//            },
//                "createTime_s": "2020-09-07 17:22:21",
//                    "id": 2087,
//                    "num": 200,
//                    "type": "1"
//            }
            //channel=2&&type=1 hyperpay->买币
            //channel=2&&type=2 买币->hyperpay
            private int channel;
            private String coinName;
            private CreateTimeBean createTime;
            private String createTime_s;
            private int id;
            private String num;
            private String type;

            public int getChannel() {
                return channel;
            }

            public void setChannel(int channel) {
                this.channel = channel;
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

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getNum() {
                return num;
            }

            public void setNum(String num) {
                this.num = num;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public static class CreateTimeBean {
                /**
                 * date : 17
                 * day : 1
                 * hours : 20
                 * minutes : 56
                 * month : 5
                 * seconds : 25
                 * time : 1560776185000
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
