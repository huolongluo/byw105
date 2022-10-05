package huolongluo.byw.byw.ui.fragment.maintab05.cnycthis;


import java.util.List;

/**
 * Created by LS on 2018/7/20.
 */
public class ChongzhiBean {
    private boolean result;
    private int code;
    private int totalPage;
    private int status;
    private List<ChongzhiListBean> fcapitaloperations;



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

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public List<ChongzhiListBean> getFcapitaloperations() {
        return fcapitaloperations;
    }

    public void setFcapitaloperations(List<ChongzhiListBean> fcapitaloperations) {
        this.fcapitaloperations = fcapitaloperations;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }



    @Override
    public String toString() {
        return "ChongzhiBean{" +
                "result=" + result +
                ", code=" + code +
                ", totalPage=" + totalPage +
                ", status=" + status +
                ", fcapitaloperations=" + fcapitaloperations +

                '}';
    }
}
