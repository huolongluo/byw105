package huolongluo.byw.model;
//领取红包返回的数据
public class RedEnvelope {
    private int status;//1成功 其他失败
    private String context;//失败信息 失败才有
    private String title;
    private String currency;//红包币种
    private String itemMoney;//红包领取的数量

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getItemMoney() {
        return itemMoney;
    }

    public void setItemMoney(String itemMoney) {
        this.itemMoney = itemMoney;
    }
}
