package huolongluo.byw.reform.mine.bean;

import java.util.List;

/**
 * Created by Administrator on 2019/3/8 0008.
 */

public class BounsBean {
    private int code;
    private boolean result;
    private double settlement;
    private String spreadLink;
    private String proportion;
    private int total;
    private String value;

    private List<TreeMap> treeMap;
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

    public double getSettlement() {
        return settlement;
    }

    public void setSettlement(double settlement) {
        this.settlement = settlement;
    }

    public String getSpreadLink() {
        return spreadLink;
    }

    public void setSpreadLink(String spreadLink) {
        this.spreadLink = spreadLink;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public List<TreeMap> getTreeMap() {
        return treeMap;
    }

    public void setTreeMap(List<TreeMap> treeMap) {
        this.treeMap = treeMap;
    }

    public String getProportion() {
        return proportion;
    }

    public void setProportion(String proportion) {
        this.proportion = proportion;
    }


    public static  class TreeMap{
        public String nameString;
        public String qty;

        public String getNameString() {
            return nameString;
        }

        public void setNameString(String nameString) {
            this.nameString = nameString;
        }

        public String getQty() {
            return qty;
        }

        public void setQty(String qty) {
            this.qty = qty;
        }
    }
}
