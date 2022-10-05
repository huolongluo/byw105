package huolongluo.byw.reform.c2c.oct.bean;

import java.util.List;

/**
 * Created by dell on 2019/6/10.
 */

public class OtcGetOrderBean {
    /**
     * code : 0
     * data : {"beginPage":1,"count":0,"currentPage":1,"listData":[{"UId":67351,"adUserId":400076,"amount":51,"canComplaint":0,"coinId":2,"coinName":"CNYT","createTime":{"date":10,"day":1,"hours":14,"minutes":43,"month":5,"seconds":34,"time":1560149014000,"timezoneOffset":-480,"year":119},"dealStatus":0,"dealUserNickname":"user1006","fee":0,"id":30268,"orderType":0,"price":1,"status":0,"totalAmount":51,"transReferNum":505803}],"nextPage":1,"pageSize":10,"prePage":1,"totalCount":1,"totalPage":1}
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
         * listData : [{"UId":67351,"adUserId":400076,"amount":51,"canComplaint":0,"coinId":2,"coinName":"CNYT","createTime":{"date":10,"day":1,"hours":14,"minutes":43,"month":5,"seconds":34,"time":1560149014000,"timezoneOffset":-480,"year":119},"dealStatus":0,"dealUserNickname":"user1006","fee":0,"id":30268,"orderType":0,"price":1,"status":0,"totalAmount":51,"transReferNum":505803}]
         * nextPage : 1
         * pageSize : 10
         * prePage : 1
         * totalCount : 1
         * totalPage : 1
         */

        private int beginPage;
        private int count;
        private int currentPage;
        private int nextPage;
        private int pageSize;
        private int prePage;
        private int totalCount;
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

        public int getTotalCount() {
            return totalCount;
        }

        public void setTotalCount(int totalCount) {
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
             * UId : 67351
             * adUserId : 400076
             * amount : 51
             * canComplaint : 0
             * coinId : 2
             * coinName : CNYT
             * createTime : {"date":10,"day":1,"hours":14,"minutes":43,"month":5,"seconds":34,"time":1560149014000,"timezoneOffset":-480,"year":119}
             * dealStatus : 0
             * dealUserNickname : user1006
             * fee : 0
             * id : 30268
             * orderType : 0
             * price : 1
             * status : 0
             * totalAmount : 51
             * transReferNum : 505803
             */

            private String createTime_s;
            private int payLimit;
            private int otcLevel;
            private int UId;
            private int adUserId;
            private double amount;
            private int canComplaint;
            private int coinId;
            private String coinName;
            private CreateTimeBean createTime;
            private int dealStatus;
            private String dealUserNickname;
            private double fee;
            private int id;
            private int orderType;
            private int payType;//出售的时候是否设置了付款方式，0是未设置
            private double price;
            private int status;
            private int timeLimit;//倒计时
            private double totalAmount;
            private double transReferNum;

            private boolean isOneKey;

            public boolean isOneKey() {
                return isOneKey;
            }

            public void setOneKey(boolean oneKey) {
                isOneKey = oneKey;
            }

            public int getUId() {
                return UId;
            }

            public void setUId(int UId) {
                this.UId = UId;
            }

            public int getAdUserId() {
                return adUserId;
            }

            public void setAdUserId(int adUserId) {
                this.adUserId = adUserId;
            }

            public double getAmount() {
                return amount;
            }

            public void setAmount(double amount) {
                this.amount = amount;
            }

            public int getCanComplaint() {
                return canComplaint;
            }

            public void setCanComplaint(int canComplaint) {
                this.canComplaint = canComplaint;
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

            public int getDealStatus() {
                return dealStatus;
            }

            public void setDealStatus(int dealStatus) {
                this.dealStatus = dealStatus;
            }

            public String getDealUserNickname() {
                return dealUserNickname;
            }

            public void setDealUserNickname(String dealUserNickname) {
                this.dealUserNickname = dealUserNickname;
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

            public int getOrderType() {
                return orderType;
            }

            public void setOrderType(int orderType) {
                this.orderType = orderType;
            }

            public double getPrice() {
                return price;
            }

            public void setPrice(double price) {
                this.price = price;
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

            public double getTransReferNum() {
                return transReferNum;
            }

            public void setTransReferNum(double transReferNum) {
                this.transReferNum = transReferNum;
            }

            public int getPayType() {
                return payType;
            }

            public void setPayType(int payType) {
                this.payType = payType;
            }

            public int getTimeLimit() {
                return timeLimit;
            }

            public void setTimeLimit(int timeLimit) {
                this.timeLimit = timeLimit;
            }

            public int getOtcLevel() {
                return otcLevel;
            }

            public void setOtcLevel(int otcLevel) {
                this.otcLevel = otcLevel;
            }

            public int getPayLimit() {
                return payLimit;
            }

            public void setPayLimit(int payLimit) {
                this.payLimit = payLimit;
            }

            public String getCreateTime_s() {
                return createTime_s;
            }

            public void setCreateTime_s(String createTime_s) {
                this.createTime_s = createTime_s;
            }

            public static class CreateTimeBean {
                /**
                 * date : 10
                 * day : 1
                 * hours : 14
                 * minutes : 43
                 * month : 5
                 * seconds : 34
                 * time : 1560149014000
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
