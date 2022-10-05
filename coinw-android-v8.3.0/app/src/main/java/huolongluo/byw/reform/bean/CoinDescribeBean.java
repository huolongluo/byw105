package huolongluo.byw.reform.bean;


/**
 * Created by haiyang on 2018/12/16.
 */
public class CoinDescribeBean {

    /*{
        "code": "200",
            "data": {
        "blockQuery": "none",
                "circulationTotal": "90625万",
                "coinCode": "LTC",
                "coinId": 6,
                "coinImg": null,
                "coinName": "莱特币",
                "createTime": "2018-10-10 16:30:01",
                "creator": "dev",
                "id": 6,
                "introduction": "\t\t\t\t\t\t<p><strong><span style=\"font-family:宋体;font-size:10.5000pt;\">一．</span><span style=\"font-family:宋体;font-size:10.5000pt;\">LTC<span style=\"font-family:宋体;\">是什么<img alt=\"吐舌头\" src=\"/static/ssadmin/js/xheditor/xheditor_emot/default/tongue.gif\" /></span></span></strong></p><p><span style=\"font-family:宋体;font-size:10.5000pt;\">Enterprise Operation System</span><span style=\"font-family:宋体;font-size:10.5000pt;\">(</span><span style=\"font-family:宋体;font-size:10.5000pt;\"><span style=\"font-family:宋体;\">简称</span>EOS</span><span style=\"font-family:宋体;font-size:10.5000pt;\"><span style=\"font-family:宋体;\">，</span></span><span style=\"font-family:宋体;font-size:10.5000pt;\"><span style=\"font-family:宋体;\">被称为柚子</span></span><span style=\"font-family:宋体;font-size:10.5000pt;\"><span style=\"font-family:宋体;\">）</span></span><span style=\"font-family:宋体;color:rgb(51, 51, 51);font-size:10.5pt;\"><span style=\"font-family:宋体;\">为</span>Daniel Larimer领导开发的类似操作系统的区块链架构平台，旨在实现分布式应用的性能扩展。EOS 提供帐户，身份验证，数据库，异步通信以及在数以百计的CPU或群集上的程序调度。该技术的最终形式是一个类似操作系统的支撑分布式应用程序的区块链体系架构，该区块链每秒可以支持数百万个交易，同时普通用户无需支付使用费用。</span></p><p><strong><span style=\"font-family:宋体;font-size:10.5000pt;\">二．</span><span style=\"font-family:宋体;font-size:10.5000pt;\">LTC的特点<img alt=\"得意\" src=\"/static/ssadmin/js/xheditor/xheditor_emot/default/proud.gif\" /></span></strong></p><p><span style=\"font-family:宋体;font-size:10.5000pt;\">LTC<span style=\"font-family:宋体;\">代币是基于以太坊的代币，</span><span style=\"font-family:Calibri;\">EOS</span><span style=\"font-family:宋体;\">主网上线后会将</span><span style=\"font-family:Calibri;\">ERC20</span><span style=\"font-family:宋体;\">代币</span><span style=\"font-family:Calibri;\">EOS</span><span style=\"font-family:宋体;\">转换为其主链代币。可理解为：</span></span></p><p><span style=\"font-family:宋体;font-size:10.5000pt;\">1/<span style=\"font-family:宋体;\">接收方支付：客户在该业务中购买特定产品，这些产品销售收入即可支付业务成本，避免客户直接为使用区块链付费，也不妨碍企业产品的货币化策略。</span></span></p><p><span style=\"font-family:宋体;font-size:10.5000pt;\">2/<span style=\"font-family:宋体;\">授权能力：</span></span><span style=\"font-family:宋体;font-size:10.5000pt;\"><span style=\"font-family:宋体;\">如果一个区块链是基于</span> EOS 软件系统开发，而其代币是由一个代币持有者所持有，他可能不需要立即消耗全部或部分可用带宽，这样的代币持有者可以选择将未消耗的带宽给予或租给他人。</span></p><p><span style=\"font-family:宋体;font-size:10.5000pt;\">3/交易成本与代币价值分开：如果应用程序所有者持有相应数量的代币，那么应用程序可以在固定的状态和带宽使用中持续运行。开发人员和用户不会受到代币市场价格波动的影响，因此不会依赖于价格。</span></p>",
                "language": "zh_CN",
                "status": 0,
                "time": "2017-07-02",
                "total": "100624万",
                "tradeId": null,
                "updateTime": "2018-10-10 16:59:43",
                "updator": "dev",
                "website": "https://eos.io/"
    },
        "message": "执行成功"
    }*/


    int code;
    String message;




    String blockQuery;
    String circulationTotal;
    String coinCode;
    int coinId;
    String coinImg;
    String coinName;
    String createTime;
    String creator;
    int id;
    String introduction;
    String language;
    int status;
    String time;
    String total;
    String tradeId;
    String updateTime;
    String updator;
    String website;

    String introductionApp;


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }



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

    public String getCoinCode() {
        return coinCode;
    }

    public void setCoinCode(String coinCode) {
        this.coinCode = coinCode;
    }

    public int getCoinId() {
        return coinId;
    }

    public void setCoinId(int coinId) {
        this.coinId = coinId;
    }

    public String getCoinImg() {
        return coinImg;
    }

    public void setCoinImg(String coinImg) {
        this.coinImg = coinImg;
    }

    public String getCoinName() {
        return coinName;
    }

    public void setCoinName(String coinName) {
        this.coinName = coinName;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getTradeId() {
        return tradeId;
    }

    public void setTradeId(String tradeId) {
        this.tradeId = tradeId;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getUpdator() {
        return updator;
    }

    public void setUpdator(String updator) {
        this.updator = updator;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getIntroductionApp() {
        return introductionApp;
    }

    public void setIntroductionApp(String introductionApp) {
        this.introductionApp = introductionApp;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "CoinDescribeBean{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", blockQuery='" + blockQuery + '\'' +
                ", circulationTotal='" + circulationTotal + '\'' +
                ", coinCode='" + coinCode + '\'' +
                ", coinId=" + coinId +
                ", coinImg='" + coinImg + '\'' +
                ", coinName='" + coinName + '\'' +
                ", createTime='" + createTime + '\'' +
                ", creator='" + creator + '\'' +
                ", id=" + id +
                ", introduction='" + introduction + '\'' +
                ", language='" + language + '\'' +
                ", status=" + status +
                ", time='" + time + '\'' +
                ", total='" + total + '\'' +
                ", tradeId='" + tradeId + '\'' +
                ", updateTime='" + updateTime + '\'' +
                ", updator='" + updator + '\'' +
                ", website='" + website + '\'' +
                ", introductionApp='" + introductionApp + '\'' +
                '}';
    }
}
