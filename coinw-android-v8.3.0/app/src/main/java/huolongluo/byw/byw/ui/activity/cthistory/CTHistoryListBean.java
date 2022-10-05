package huolongluo.byw.byw.ui.activity.cthistory;


/**
 * Created by LS on 2018/7/11.
 */
public class CTHistoryListBean {
    private String id;
    private String fvi_id;
    private String fcreateTime;
    private String flastUpdateTime;
    private String famount;
    private String ffees;
    private String ftype;//1充值，2提现
    private String fstatus;
    private String recharge_virtual_address; // 充值显示的地址
    private String withdraw_virtual_address; // 提现地址
    private String fshortName;
    private String txid;
    private String coinsCount;
    private String logo;
    private String blockUrl;//判断是否需要跳转

    //空投字段

    private  String coinName;
    private  String createTime;
    private  String fremark;
    private  String status;
    private  int number;


    public String getTxid() {
        return txid;
    }

    public void setTxid(String txid) {
        this.txid = txid;
    }

    public String getCoinsCount() {
        return coinsCount;
    }

    public void setCoinsCount(String coinsCount) {
        this.coinsCount = coinsCount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFvi_id() {
        return fvi_id;
    }

    public void setFvi_id(String fvi_id) {
        this.fvi_id = fvi_id;
    }

    public String getFcreateTime() {
        return fcreateTime;
    }

    public void setFcreateTime(String fcreateTime) {
        this.fcreateTime = fcreateTime;
    }

    public String getFlastUpdateTime() {
        return flastUpdateTime;
    }

    public void setFlastUpdateTime(String flastUpdateTime) {
        this.flastUpdateTime = flastUpdateTime;
    }

    public String getFamount() {
        return famount;
    }

    public void setFamount(String famount) {
        this.famount = famount;
    }

    public String getFfees() {
        return ffees;
    }

    public void setFfees(String ffees) {
        this.ffees = ffees;
    }

    public String getFtype() {
        return ftype;
    }

    public void setFtype(String ftype) {
        this.ftype = ftype;
    }

    public String getFstatus() {
        return fstatus;
    }

    public void setFstatus(String fstatus) {
        this.fstatus = fstatus;
    }

    public String getRecharge_virtual_address() {
        return recharge_virtual_address;
    }

    public void setRecharge_virtual_address(String recharge_virtual_address) {
        this.recharge_virtual_address = recharge_virtual_address;
    }

    public String getFshortName() {
        return fshortName;
    }

    public void setFshortName(String fshortName) {
        this.fshortName = fshortName;
    }


    public String getWithdraw_virtual_address() {
        return withdraw_virtual_address;
    }

    public void setWithdraw_virtual_address(String withdraw_virtual_address) {
        this.withdraw_virtual_address = withdraw_virtual_address;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getBlockUrl() {
        return blockUrl;
    }

    public void setBlockUrl(String blockUrl) {
        this.blockUrl = blockUrl;
    }

    @Override
    public String toString() {
        return "CTHistoryListBean{" +
                "id='" + id + '\'' +
                ", fvi_id='" + fvi_id + '\'' +
                ", fcreateTime='" + fcreateTime + '\'' +
                ", flastUpdateTime='" + flastUpdateTime + '\'' +
                ", famount='" + famount + '\'' +
                ", ffees='" + ffees + '\'' +
                ", ftype='" + ftype + '\'' +
                ", fstatus='" + fstatus + '\'' +
                ", recharge_virtual_address='" + recharge_virtual_address + '\'' +
                ", withdraw_virtual_address='" + withdraw_virtual_address + '\'' +
                ", fshortName='" + fshortName + '\'' +
                ", txid='" + txid + '\'' +
                ", coinsCount='" + coinsCount + '\'' +
                ", logo='" + logo + '\'' +
                ", blockUrl='" + blockUrl + '\'' +
                '}';
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

    public String getFremark() {
        return fremark;
    }

    public void setFremark(String fremark) {
        this.fremark = fremark;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
