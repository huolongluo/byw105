package huolongluo.byw.reform.mine.bean;

import java.util.List;

/**
 * Created by Administrator on 2019/3/7 0007.
 */

public class VipBean {

    private int code;
    private int vipsize;
    private int ffees;
    private boolean result;
    private String value;
    private String sendTime;
    public List<ViP> vipFees;
    private double ftotal;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getFfees() {
        return ffees;
    }

    public void setFfees(int ffees) {
        this.ffees = ffees;
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

    public List<ViP> getVipFees() {
        return vipFees;
    }

    public void setVipFees(List<ViP> vipFees) {
        this.vipFees = vipFees;
    }

    public int getVipsize() {
        return vipsize;
    }

    public void setVipsize(int vipsize) {
        this.vipsize = vipsize;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    public double getFtotal() {
        return ftotal;
    }

    public void setFtotal(double ftotal) {
        this.ftotal = ftotal;
    }


    public static class ViP {
        private String coinsCount;
        private String tradeFfees;
        private String takerFfees;
        private String makerFfees;
        private int id;

        public String getTakerFfees() {
            return takerFfees;
        }

        public void setTakerFfees(String takerFfees) {
            this.takerFfees = takerFfees;
        }

        public String getMakerFfees() {
            return makerFfees;
        }

        public void setMakerFfees(String makerFfees) {
            this.makerFfees = makerFfees;
        }

        public String getCoinsCount() {
            return coinsCount;
        }

        public void setCoinsCount(String coinsCount) {
            this.coinsCount = coinsCount;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getTradeFfees() {
            return tradeFfees;
        }

        public void setTradeFfees(String tradeFfees) {
            this.tradeFfees = tradeFfees;
        }
    }


}
