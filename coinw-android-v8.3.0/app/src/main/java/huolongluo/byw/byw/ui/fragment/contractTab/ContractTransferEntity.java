package huolongluo.byw.byw.ui.fragment.contractTab;

public class ContractTransferEntity {
    /**
     * code : -4
     * data : null
     * i18nMsgCode :
     * msg : 币种不支持
     * success : false
     */
    private String code;
    private Object data;
    private String i18nMsgCode;
    private String msg;
    private boolean success;
    private String stillNeedTransferQuota;
    private String availableMudQuota;
    private String netTransferQuota;
    private boolean isGainMudAll;
    private boolean isGainMud;
    private int mudStatus;
    private String mudGetQuota;
    private String mudLeverQuota;
    private boolean isAchieve;
    private int tipsType;

    public int getTipsType() {
        return tipsType;
    }

    public void setTipsType(int tipsType) {
        this.tipsType = tipsType;
    }

    public boolean getIsAchieve() {
        return isAchieve;
    }

    public void setIsAchieve(boolean isAchieve) {
        this.isAchieve = isAchieve;
    }

    public String getMudLeverQuota() {
        return mudLeverQuota;
    }

    public void setMudLeverQuota(String mudLeverQuota) {
        this.mudLeverQuota = mudLeverQuota;
    }

    public String getStillNeedTransferQuota() {
        return stillNeedTransferQuota;
    }

    public void setStillNeedTransferQuota(String stillNeedTransferQuota) {
        this.stillNeedTransferQuota = stillNeedTransferQuota;
    }

    public String getAvailableMudQuota() {
        return availableMudQuota;
    }

    public void setAvailableMudQuota(String availableMudQuota) {
        this.availableMudQuota = availableMudQuota;
    }

    public String getNetTransferQuota() {
        return netTransferQuota;
    }

    public void setNetTransferQuota(String netTransferQuota) {
        this.netTransferQuota = netTransferQuota;
    }

    public boolean isGainMudAll() {
        return isGainMudAll;
    }

    public void setGainMudAll(boolean gainMudAll) {
        isGainMudAll = gainMudAll;
    }

    public boolean isGainMud() {
        return isGainMud;
    }

    public void setGainMud(boolean gainMud) {
        isGainMud = gainMud;
    }

    public int getMudStatus() {
        return mudStatus;
    }

    public void setMudStatus(int mudStatus) {
        this.mudStatus = mudStatus;
    }

    public String getMudGetQuota() {
        return mudGetQuota;
    }

    public void setMudGetQuota(String mudGetQuota) {
        this.mudGetQuota = mudGetQuota;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getI18nMsgCode() {
        return i18nMsgCode;
    }

    public void setI18nMsgCode(String i18nMsgCode) {
        this.i18nMsgCode = i18nMsgCode;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
