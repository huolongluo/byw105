package huolongluo.byw.reform.mine.bean;

/**
 * Created by Administrator on 2019/2/21 0021.
 */

public class HppRecord {

      private double amount;

      private int id;
         private String openId;
         private String orderNo;
         private String fShortName;
         private int status;
         private String status_s;
         private  int type;
          private FcreateTime fcreateTime;
          private CoinType coinType;

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getStatus_s() {
        return status_s;
    }

    public void setStatus_s(String status_s) {
        this.status_s = status_s;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public FcreateTime getFcreateTime() {
        return fcreateTime;
    }

    public void setFcreateTime(FcreateTime fcreateTime) {
        this.fcreateTime = fcreateTime;
    }

    public String getfShortName() {
        return fShortName;
    }

    public void setfShortName(String fShortName) {
        this.fShortName = fShortName;
    }

    public CoinType getCoinType() {
        return coinType;
    }

    public void setCoinType(CoinType coinType) {
        this.coinType = coinType;
    }

    public static class FcreateTime{
           private   long time;

             public long getTime() {
                 return time;
             }

             public void setTime(long time) {
                 this.time = time;
             }
         }

         public  static  class  CoinType{
        private String fShortName;

             public String getfShortName() {
                 return fShortName;
             }

             public void setfShortName(String fShortName) {
                 this.fShortName = fShortName;
             }
         }
}
