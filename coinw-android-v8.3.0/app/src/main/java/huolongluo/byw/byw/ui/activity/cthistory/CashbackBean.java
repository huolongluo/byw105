package huolongluo.byw.byw.ui.activity.cthistory;

import java.util.List;

public class CashbackBean {

    /**
     * code : 0
     * totalPage : 2
     * pageNo : 1
     * list : [{"cashbackCount":"0.000025","cashbackTime":"2020-03-18 14:56:52","cionType":"CNYT","ftrademapping":"CWT/CNYT"},{"cashbackCount":"0.058","cashbackTime":"2020-03-17 17:32:54","cionType":"CWT","ftrademapping":"CWT/CNYT"},{"cashbackCount":"0.2","cashbackTime":"2020-03-17 17:32:54","cionType":"CWT","ftrademapping":"CWT/CNYT"},{"cashbackCount":"0.2","cashbackTime":"2020-03-17 17:32:53","cionType":"CWT","ftrademapping":"CWT/CNYT"},{"cashbackCount":"0.2","cashbackTime":"2020-03-17 17:32:52","cionType":"CWT","ftrademapping":"CWT/CNYT"},{"cashbackCount":"0.2","cashbackTime":"2020-03-17 17:32:52","cionType":"CWT","ftrademapping":"CWT/CNYT"},{"cashbackCount":"0.2","cashbackTime":"2020-03-17 17:32:52","cionType":"CWT","ftrademapping":"CWT/CNYT"},{"cashbackCount":"0.2","cashbackTime":"2020-03-17 17:32:51","cionType":"CWT","ftrademapping":"CWT/CNYT"},{"cashbackCount":"0.2","cashbackTime":"2020-03-17 17:32:51","cionType":"CWT","ftrademapping":"CWT/CNYT"},{"cashbackCount":"0.2","cashbackTime":"2020-03-17 17:32:51","cionType":"CWT","ftrademapping":"CWT/CNYT"},{"cashbackCount":"0.2","cashbackTime":"2020-03-17 17:32:49","cionType":"CWT","ftrademapping":"CWT/CNYT"},{"cashbackCount":"0.2","cashbackTime":"2020-03-17 17:32:49","cionType":"CWT","ftrademapping":"CWT/CNYT"},{"cashbackCount":"0.2","cashbackTime":"2020-03-17 17:32:48","cionType":"CWT","ftrademapping":"CWT/CNYT"},{"cashbackCount":"0.2","cashbackTime":"2020-03-17 17:32:47","cionType":"CWT","ftrademapping":"CWT/CNYT"},{"cashbackCount":"0.2","cashbackTime":"2020-03-17 17:32:47","cionType":"CWT","ftrademapping":"CWT/CNYT"},{"cashbackCount":"0.2","cashbackTime":"2020-03-17 17:32:46","cionType":"CWT","ftrademapping":"CWT/CNYT"},{"cashbackCount":"0.2","cashbackTime":"2020-03-17 17:32:46","cionType":"CWT","ftrademapping":"CWT/CNYT"},{"cashbackCount":"0.2","cashbackTime":"2020-03-17 17:32:46","cionType":"CWT","ftrademapping":"CWT/CNYT"},{"cashbackCount":"0.2","cashbackTime":"2020-03-17 17:32:45","cionType":"CWT","ftrademapping":"CWT/CNYT"},{"cashbackCount":"0.2","cashbackTime":"2020-03-17 17:32:45","cionType":"CWT","ftrademapping":"CWT/CNYT"}]
     */

    private int code;
    private int totalPage;
    private int pageNo;
    private List<ListBean> list;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    public static class ListBean {
        /**
         * cashbackCount : 0.000025
         * cashbackTime : 2020-03-18 14:56:52
         * cionType : CNYT
         * ftrademapping : CWT/CNYT
         */

        private String cashbackCount;
        private String cashbackTime;
        private String cionType;
        private String ftrademapping;

        public String getCashbackCount() {
            return cashbackCount;
        }

        public void setCashbackCount(String cashbackCount) {
            this.cashbackCount = cashbackCount;
        }

        public String getCashbackTime() {
            return cashbackTime;
        }

        public void setCashbackTime(String cashbackTime) {
            this.cashbackTime = cashbackTime;
        }

        public String getCionType() {
            return cionType;
        }

        public void setCionType(String cionType) {
            this.cionType = cionType;
        }

        public String getFtrademapping() {
            return ftrademapping;
        }

        public void setFtrademapping(String ftrademapping) {
            this.ftrademapping = ftrademapping;
        }
    }
}
