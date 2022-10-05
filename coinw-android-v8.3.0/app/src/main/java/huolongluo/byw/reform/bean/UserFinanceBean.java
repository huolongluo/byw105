package huolongluo.byw.reform.bean;


import java.util.List;

/**
 * Created by Administrator on 2018/12/10 0010.
 */
public class UserFinanceBean {

     String code;



   List<FinanceBean> data;

    @Override
    public String toString() {
        return "UserFinanceBean{" +
                "code='" + code + '\'' +
                ", data=" + data +
                '}';
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<FinanceBean> getData() {
        return data;
    }

    public void setData(List<FinanceBean> data) {
        this.data = data;
    }

    public static  class FinanceBean{
        String total;
        String time;

        public String getTotal() {
            return total;
        }

        public void setTotal(String total) {
            this.total = total;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        @Override
        public String toString() {
            return "financeBean{" +
                    "total='" + total + '\'' +
                    ", time='" + time + '\'' +
                    '}';
        }
    }
}
