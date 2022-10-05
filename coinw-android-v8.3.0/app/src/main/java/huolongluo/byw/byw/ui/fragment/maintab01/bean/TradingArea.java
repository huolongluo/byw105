package huolongluo.byw.byw.ui.fragment.maintab01.bean;


import java.util.List;

/**
 * Created by hy on 2018/8/27 0027.
 */
public class TradingArea {

    int code;
    String value;
       public List<TradItem> fbs;

    public TradingArea() {
        this.code = code;
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

    public List<TradItem> getTradItemList() {
        return fbs;
    }

    public void setTradItemList(List<TradItem> fbs) {
        this.fbs = fbs;
    }

    @Override
    public String toString() {
        return "tradingArea{" +
                "code=" + code +
                ", value='" + value + '\'' +
                ", tradItemList=" + fbs +
                '}';
    }

    public static class  TradItem{

        int fid;
        String fShortName;
        String fname;

        public TradItem() {

        }

        public TradItem(int fid, String fShortName, String fname) {
            this.fid = fid;
            this.fShortName = fShortName;
            this.fname = fname;
        }

        public int getFid() {
            return fid;
        }

        public void setFid(int fid) {
            this.fid = fid;
        }

        public String getfShortName() {
            return fShortName;
        }

        public void setfShortName(String fShortName) {
            this.fShortName = fShortName;
        }

        public String getFname() {
            return fname;
        }

        public void setFname(String fname) {
            this.fname = fname;
        }

        @Override
        public String toString() {
            return "TradItem{" +
                    "fid=" + fid +
                    ", fShortName='" + fShortName + '\'' +
                    ", fname='" + fname + '\'' +
                    '}';
        }
    }

}
