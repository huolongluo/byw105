package huolongluo.byw.byw.ui.activity.cthistory;


import java.util.List;

/**
 * Created by LS on 2018/7/11.
 */
public class CTHistoryAirdropBean {
    private int totalPages;
    private int pageNo;
    private List<AirdropBean> airdropAwardList;
    private boolean isCoinList;
    private int symbol;
    private int code;

    public CTHistoryAirdropBean() {
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<AirdropBean> getAirdropAwardList() {
        return airdropAwardList;
    }

    public void setAirdropAwardList(List<AirdropBean> airdropAwardList) {
        this.airdropAwardList = airdropAwardList;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public boolean isCoinList() {
        return isCoinList;
    }

    public void setCoinList(boolean coinList) {
        isCoinList = coinList;
    }

    public int getSymbol() {
        return symbol;
    }

    public void setSymbol(int symbol) {
        this.symbol = symbol;
    }

    public static class AirdropBean {
        //    "coinName": "HAIC",
        //    "fremark": "HAIC空投",
        //    "number": "5.00000000",
        //    "status": "审核成功",
        //    "createTime": "2019-09-25 22:35:14"
        public String coinName;
        public String fremark;
        public String number;
        public String status;
        public String createTime;
    }
}
