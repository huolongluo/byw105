package huolongluo.byw.reform.mine.bean;

import java.util.List;

/**
 * Created by Administrator on 2019/3/7 0007.
 */

public class VipPurchaseBean {

    private int code;
    private int currentPage;
    private int totalPage;
    private boolean result;
    private String value;
       private List<VipPurchase> vipList;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
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

    public List<VipPurchase> getVipList() {
        return vipList;
    }

    public void setVipList(List<VipPurchase> vipList) {
        this.vipList = vipList;
    }

    public static class VipPurchase{
        private double coinsCount;
        private String createTime;

        private  int ffees;
           private  int id;
           private  int status;
           private  int uid;
           private  String sendTime;
           private  String statusName;

        public double getCoinsCount() {
            return coinsCount;
        }

        public void setCoinsCount(double coinsCount) {
            this.coinsCount = coinsCount;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public int getFfees() {
            return ffees;
        }

        public void setFfees(int ffees) {
            this.ffees = ffees;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public int getUid() {
            return uid;
        }

        public void setUid(int uid) {
            this.uid = uid;
        }

        public String getSendTime() {
            return sendTime;
        }

        public void setSendTime(String sendTime) {
            this.sendTime = sendTime;
        }

        public String getStatusName() {
            return statusName;
        }

        public void setStatusName(String statusName) {
            this.statusName = statusName;
        }
    }

}
