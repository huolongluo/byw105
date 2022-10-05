package huolongluo.byw.reform.mine.bean;


import java.util.List;

/**
 * Created by Administrator on 2019/2/18 0018.
 */
public class BindHpyBean {

    private int pageNo;
        private int code=-1;
        private  String value;
        private boolean hasHyperpayBind;

        private String uniqueId;
        private String appId;

      public   List<HppRecord> hyperpayRecord;
     public    List<HppRecord> rechargeRecord;


    public boolean isHasHyperpayBind() {
        return hasHyperpayBind;
    }

    public void setHasHyperpayBind(boolean hasHyperpayBind) {
        this.hasHyperpayBind = hasHyperpayBind;
    }



    public List<HppRecord> getHyperpayRecord() {
        return hyperpayRecord;
    }

    public void setHyperpayRecord(List<HppRecord> hyperpayRecord) {
        this.hyperpayRecord = hyperpayRecord;
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

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }


    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public List<HppRecord> getRechargeRecord() {
        return rechargeRecord;
    }

    public void setRechargeRecord(List<HppRecord> rechargeRecord) {
        this.rechargeRecord = rechargeRecord;
    }
}
