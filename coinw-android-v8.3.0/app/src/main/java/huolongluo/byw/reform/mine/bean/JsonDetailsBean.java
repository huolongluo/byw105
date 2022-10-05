package huolongluo.byw.reform.mine.bean;

import java.util.List;

/**
 * Created by Administrator on 2019/3/11 0011.
 */

public class JsonDetailsBean {

    private int code;
    private int currentPage;
    private int totalPage;
 private String value;
private List<JsonDetail> fusers;
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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public List<JsonDetail> getFusers() {
        return fusers;
    }

    public void setFusers(List<JsonDetail> fusers) {
        this.fusers = fusers;
    }

    public static  class JsonDetail{

    private int fid;
    private String fregisterTime;

    public int getFid() {
        return fid;
    }

    public void setFid(int fid) {
        this.fid = fid;
    }

    public String getFregisterTime() {
        return fregisterTime;
    }

    public void setFregisterTime(String fregisterTime) {
        this.fregisterTime = fregisterTime;
    }
}
}
