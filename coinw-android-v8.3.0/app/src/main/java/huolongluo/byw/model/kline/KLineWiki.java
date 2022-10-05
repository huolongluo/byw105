package huolongluo.byw.model.kline;
//k线改版百科对象
public class KLineWiki {
    private String blockQuery;//区块查询
    private String circulationTotal;//流通总量
    private String time;//发行时间
    private String introduction;//简介
    private String website;//官网
    private String total;//发行总量
    private String coinName;
    private String coinCode;

    public String getBlockQuery() {
        return blockQuery;
    }

    public void setBlockQuery(String blockQuery) {
        this.blockQuery = blockQuery;
    }

    public String getCirculationTotal() {
        return circulationTotal;
    }

    public void setCirculationTotal(String circulationTotal) {
        this.circulationTotal = circulationTotal;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getCoinName() {
        return coinName;
    }

    public void setCoinName(String coinName) {
        this.coinName = coinName;
    }

    public String getCoinCode() {
        return coinCode;
    }

    public void setCoinCode(String coinCode) {
        this.coinCode = coinCode;
    }
}
