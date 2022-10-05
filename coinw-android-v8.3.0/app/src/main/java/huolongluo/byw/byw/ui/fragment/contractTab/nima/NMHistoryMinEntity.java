package huolongluo.byw.byw.ui.fragment.contractTab.nima;

import java.util.List;

public class NMHistoryMinEntity {

    /**
     * result : true
     * code : 0
     * value : 操作成功
     * data : {"beginPage":1,"count":0,"currentPage":1,"listData":[{"activityId":0,"auditStatus":1,"coinId":63,"coinName":"USDT","createAt":{"date":24,"day":5,"hours":19,"minutes":7,"month":3,"seconds":54,"time":1587726474000,"timezoneOffset":-480,"year":120},"createAt_str":"2020-04-24 19:07:54","externalNo":"","grantDt":{"date":24,"day":5,"hours":19,"minutes":7,"month":3,"seconds":54,"time":1587726474000,"timezoneOffset":-480,"year":120},"grantDt_str":"2020-04-24 19:07:54","grantType":2,"grantTypeDesc":"空投","id":353,"isDel":0,"isNotify":1,"mudLeveTransfer":0,"mudQuota":999,"outTradeNo":"","remark":"单个泥码新增","totalTransfer":0,"type":0,"updateAt":{"date":24,"day":5,"hours":19,"minutes":7,"month":3,"seconds":54,"time":1587726474000,"timezoneOffset":-480,"year":120},"updateAt_str":"2020-04-24 19:07:54","uuid":1115420},{"activityId":0,"auditStatus":1,"coinId":63,"coinName":"USDT","createAt":{"date":24,"day":5,"hours":19,"minutes":7,"month":3,"seconds":54,"time":1587726474000,"timezoneOffset":-480,"year":120},"createAt_str":"2020-04-24 19:07:54","externalNo":"","grantDt":{"date":24,"day":5,"hours":19,"minutes":7,"month":3,"seconds":54,"time":1587726474000,"timezoneOffset":-480,"year":120},"grantDt_str":"2020-04-24 19:07:54","grantType":2,"grantTypeDesc":"空投","id":356,"isDel":0,"isNotify":1,"mudLeveTransfer":0,"mudQuota":999,"outTradeNo":"","remark":"单个泥码新增","totalTransfer":0,"type":0,"updateAt":{"date":24,"day":5,"hours":19,"minutes":7,"month":3,"seconds":54,"time":1587726474000,"timezoneOffset":-480,"year":120},"updateAt_str":"2020-04-24 19:07:54","uuid":1115420},{"activityId":0,"auditStatus":1,"coinId":63,"coinName":"USDT","createAt":{"date":24,"day":5,"hours":19,"minutes":7,"month":3,"seconds":54,"time":1587726474000,"timezoneOffset":-480,"year":120},"createAt_str":"2020-04-24 19:07:54","externalNo":"","grantDt":{"date":24,"day":5,"hours":19,"minutes":7,"month":3,"seconds":54,"time":1587726474000,"timezoneOffset":-480,"year":120},"grantDt_str":"2020-04-24 19:07:54","grantType":2,"grantTypeDesc":"空投","id":354,"isDel":0,"isNotify":1,"mudLeveTransfer":0,"mudQuota":999,"outTradeNo":"","remark":"单个泥码新增","totalTransfer":0,"type":0,"updateAt":{"date":24,"day":5,"hours":19,"minutes":7,"month":3,"seconds":54,"time":1587726474000,"timezoneOffset":-480,"year":120},"updateAt_str":"2020-04-24 19:07:54","uuid":1115420},{"activityId":0,"auditStatus":1,"coinId":63,"coinName":"USDT","createAt":{"date":24,"day":5,"hours":19,"minutes":7,"month":3,"seconds":54,"time":1587726474000,"timezoneOffset":-480,"year":120},"createAt_str":"2020-04-24 19:07:54","externalNo":"","grantDt":{"date":24,"day":5,"hours":19,"minutes":7,"month":3,"seconds":54,"time":1587726474000,"timezoneOffset":-480,"year":120},"grantDt_str":"2020-04-24 19:07:54","grantType":2,"grantTypeDesc":"空投","id":355,"isDel":0,"isNotify":1,"mudLeveTransfer":0,"mudQuota":999,"outTradeNo":"","remark":"单个泥码新增","totalTransfer":0,"type":0,"updateAt":{"date":24,"day":5,"hours":19,"minutes":7,"month":3,"seconds":54,"time":1587726474000,"timezoneOffset":-480,"year":120},"updateAt_str":"2020-04-24 19:07:54","uuid":1115420}],"nextPage":1,"pageSize":10,"prePage":1,"totalCount":4,"totalPage":1}
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
         * beginPage : 1
         * count : 0
         * currentPage : 1
         * listData : [{"activityId":0,"auditStatus":1,"coinId":63,"coinName":"USDT","createAt":{"date":24,"day":5,"hours":19,"minutes":7,"month":3,"seconds":54,"time":1587726474000,"timezoneOffset":-480,"year":120},"createAt_str":"2020-04-24 19:07:54","externalNo":"","grantDt":{"date":24,"day":5,"hours":19,"minutes":7,"month":3,"seconds":54,"time":1587726474000,"timezoneOffset":-480,"year":120},"grantDt_str":"2020-04-24 19:07:54","grantType":2,"grantTypeDesc":"空投","id":353,"isDel":0,"isNotify":1,"mudLeveTransfer":0,"mudQuota":999,"outTradeNo":"","remark":"单个泥码新增","totalTransfer":0,"type":0,"updateAt":{"date":24,"day":5,"hours":19,"minutes":7,"month":3,"seconds":54,"time":1587726474000,"timezoneOffset":-480,"year":120},"updateAt_str":"2020-04-24 19:07:54","uuid":1115420},{"activityId":0,"auditStatus":1,"coinId":63,"coinName":"USDT","createAt":{"date":24,"day":5,"hours":19,"minutes":7,"month":3,"seconds":54,"time":1587726474000,"timezoneOffset":-480,"year":120},"createAt_str":"2020-04-24 19:07:54","externalNo":"","grantDt":{"date":24,"day":5,"hours":19,"minutes":7,"month":3,"seconds":54,"time":1587726474000,"timezoneOffset":-480,"year":120},"grantDt_str":"2020-04-24 19:07:54","grantType":2,"grantTypeDesc":"空投","id":356,"isDel":0,"isNotify":1,"mudLeveTransfer":0,"mudQuota":999,"outTradeNo":"","remark":"单个泥码新增","totalTransfer":0,"type":0,"updateAt":{"date":24,"day":5,"hours":19,"minutes":7,"month":3,"seconds":54,"time":1587726474000,"timezoneOffset":-480,"year":120},"updateAt_str":"2020-04-24 19:07:54","uuid":1115420},{"activityId":0,"auditStatus":1,"coinId":63,"coinName":"USDT","createAt":{"date":24,"day":5,"hours":19,"minutes":7,"month":3,"seconds":54,"time":1587726474000,"timezoneOffset":-480,"year":120},"createAt_str":"2020-04-24 19:07:54","externalNo":"","grantDt":{"date":24,"day":5,"hours":19,"minutes":7,"month":3,"seconds":54,"time":1587726474000,"timezoneOffset":-480,"year":120},"grantDt_str":"2020-04-24 19:07:54","grantType":2,"grantTypeDesc":"空投","id":354,"isDel":0,"isNotify":1,"mudLeveTransfer":0,"mudQuota":999,"outTradeNo":"","remark":"单个泥码新增","totalTransfer":0,"type":0,"updateAt":{"date":24,"day":5,"hours":19,"minutes":7,"month":3,"seconds":54,"time":1587726474000,"timezoneOffset":-480,"year":120},"updateAt_str":"2020-04-24 19:07:54","uuid":1115420},{"activityId":0,"auditStatus":1,"coinId":63,"coinName":"USDT","createAt":{"date":24,"day":5,"hours":19,"minutes":7,"month":3,"seconds":54,"time":1587726474000,"timezoneOffset":-480,"year":120},"createAt_str":"2020-04-24 19:07:54","externalNo":"","grantDt":{"date":24,"day":5,"hours":19,"minutes":7,"month":3,"seconds":54,"time":1587726474000,"timezoneOffset":-480,"year":120},"grantDt_str":"2020-04-24 19:07:54","grantType":2,"grantTypeDesc":"空投","id":355,"isDel":0,"isNotify":1,"mudLeveTransfer":0,"mudQuota":999,"outTradeNo":"","remark":"单个泥码新增","totalTransfer":0,"type":0,"updateAt":{"date":24,"day":5,"hours":19,"minutes":7,"month":3,"seconds":54,"time":1587726474000,"timezoneOffset":-480,"year":120},"updateAt_str":"2020-04-24 19:07:54","uuid":1115420}]
         * nextPage : 1
         * pageSize : 10
         * prePage : 1
         * totalCount : 4
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
             * activityId : 0
             * auditStatus : 1
             * coinId : 63
             * coinName : USDT
             * createAt : {"date":24,"day":5,"hours":19,"minutes":7,"month":3,"seconds":54,"time":1587726474000,"timezoneOffset":-480,"year":120}
             * createAt_str : 2020-04-24 19:07:54
             * externalNo :
             * grantDt : {"date":24,"day":5,"hours":19,"minutes":7,"month":3,"seconds":54,"time":1587726474000,"timezoneOffset":-480,"year":120}
             * grantDt_str : 2020-04-24 19:07:54
             * grantType : 2
             * grantTypeDesc : 空投
             * id : 353
             * isDel : 0
             * isNotify : 1
             * mudLeveTransfer : 0
             * mudQuota : 999
             * outTradeNo :
             * remark : 单个泥码新增
             * totalTransfer : 0
             * type : 0
             * updateAt : {"date":24,"day":5,"hours":19,"minutes":7,"month":3,"seconds":54,"time":1587726474000,"timezoneOffset":-480,"year":120}
             * updateAt_str : 2020-04-24 19:07:54
             * uuid : 1115420
             */

            private int activityId;
            private int auditStatus;
            private int coinId;
            private String coinName;
            private CreateAtBean createAt;
            private String createAt_str;
            private String externalNo;
            private GrantDtBean grantDt;
            private String grantDt_str;
            private int grantType;
            private String grantTypeDesc;
            private int id;
            private int isDel;
            private int isNotify;
            private double mudLeveTransfer;
            private double mudQuota;
            private String outTradeNo;
            private String remark;
            private double totalTransfer;
            private int type;
            private UpdateAtBean updateAt;
            private String updateAt_str;
            private int uuid;

            public int getActivityId() {
                return activityId;
            }

            public void setActivityId(int activityId) {
                this.activityId = activityId;
            }

            public int getAuditStatus() {
                return auditStatus;
            }

            public void setAuditStatus(int auditStatus) {
                this.auditStatus = auditStatus;
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

            public CreateAtBean getCreateAt() {
                return createAt;
            }

            public void setCreateAt(CreateAtBean createAt) {
                this.createAt = createAt;
            }

            public String getCreateAt_str() {
                return createAt_str;
            }

            public void setCreateAt_str(String createAt_str) {
                this.createAt_str = createAt_str;
            }

            public String getExternalNo() {
                return externalNo;
            }

            public void setExternalNo(String externalNo) {
                this.externalNo = externalNo;
            }

            public GrantDtBean getGrantDt() {
                return grantDt;
            }

            public void setGrantDt(GrantDtBean grantDt) {
                this.grantDt = grantDt;
            }

            public String getGrantDt_str() {
                return grantDt_str;
            }

            public void setGrantDt_str(String grantDt_str) {
                this.grantDt_str = grantDt_str;
            }

            public int getGrantType() {
                return grantType;
            }

            public void setGrantType(int grantType) {
                this.grantType = grantType;
            }

            public String getGrantTypeDesc() {
                return grantTypeDesc;
            }

            public void setGrantTypeDesc(String grantTypeDesc) {
                this.grantTypeDesc = grantTypeDesc;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public int getIsDel() {
                return isDel;
            }

            public void setIsDel(int isDel) {
                this.isDel = isDel;
            }

            public int getIsNotify() {
                return isNotify;
            }

            public void setIsNotify(int isNotify) {
                this.isNotify = isNotify;
            }

            public double getMudLeveTransfer() {
                return mudLeveTransfer;
            }

            public void setMudLeveTransfer(int mudLeveTransfer) {
                this.mudLeveTransfer = mudLeveTransfer;
            }

            public double getMudQuota() {
                return mudQuota;
            }

            public void setMudQuota(int mudQuota) {
                this.mudQuota = mudQuota;
            }

            public String getOutTradeNo() {
                return outTradeNo;
            }

            public void setOutTradeNo(String outTradeNo) {
                this.outTradeNo = outTradeNo;
            }

            public String getRemark() {
                return remark;
            }

            public void setRemark(String remark) {
                this.remark = remark;
            }

            public double getTotalTransfer() {
                return totalTransfer;
            }

            public void setTotalTransfer(int totalTransfer) {
                this.totalTransfer = totalTransfer;
            }

            public int getType() {
                return type;
            }

            public void setType(int type) {
                this.type = type;
            }

            public UpdateAtBean getUpdateAt() {
                return updateAt;
            }

            public void setUpdateAt(UpdateAtBean updateAt) {
                this.updateAt = updateAt;
            }

            public String getUpdateAt_str() {
                return updateAt_str;
            }

            public void setUpdateAt_str(String updateAt_str) {
                this.updateAt_str = updateAt_str;
            }

            public int getUuid() {
                return uuid;
            }

            public void setUuid(int uuid) {
                this.uuid = uuid;
            }

            public static class CreateAtBean {
                /**
                 * date : 24
                 * day : 5
                 * hours : 19
                 * minutes : 7
                 * month : 3
                 * seconds : 54
                 * time : 1587726474000
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

            public static class GrantDtBean {
                /**
                 * date : 24
                 * day : 5
                 * hours : 19
                 * minutes : 7
                 * month : 3
                 * seconds : 54
                 * time : 1587726474000
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

            public static class UpdateAtBean {
                /**
                 * date : 24
                 * day : 5
                 * hours : 19
                 * minutes : 7
                 * month : 3
                 * seconds : 54
                 * time : 1587726474000
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
}
