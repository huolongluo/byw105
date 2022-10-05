package huolongluo.byw.reform.bean;


/**
 * Created by Administrator on 2018/12/12 0012.
 */
public class UserFinancalBean {

    String time;
    String total;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    @Override
    public String toString() {
        return "UserFinancalBean{" +
                "time='" + time + '\'' +
                ", total='" + total + '\'' +
                '}';
    }
}
