package huolongluo.byw.byw.ui.activity.cthistory;


import java.util.List;

/**
 * Created by LS on 2018/7/11.
 */
public class CTHistoryBean {
    private boolean result;
    private int totalPage;
    private String currentPage;
    private List<CTHistoryListBean> list;
    private int code;
    private List<CTHistoryListBean> airdropAwardList;
    private int totalPages;
    private int pageNo;
    private boolean isCoinList;
    private int symbol;

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

    public CTHistoryBean(){

    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }



    public String getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(String currentPage) {
        this.currentPage = currentPage;
    }

    public List<CTHistoryListBean> getList() {
        return list;
    }

    public void setList(List<CTHistoryListBean> list) {
        this.list = list;
    }

    public List<CTHistoryListBean> getAirdropAwardList() {
        return airdropAwardList;
    }

    public void setAirdropAwardList(List<CTHistoryListBean> airdropAwardList) {
        this.airdropAwardList = airdropAwardList;
    }


    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }
}
