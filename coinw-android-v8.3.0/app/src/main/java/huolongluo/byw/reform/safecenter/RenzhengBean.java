package huolongluo.byw.reform.safecenter;


/**
 * Created by Administrator on 2019/1/3 0003.
 */
public class RenzhengBean {
  /*  {
        "code": 0,
            "data": {
        "biz_id": "1546504162,34a7c4a7-3d45-49bd-b3f2-7f78dcb46318",
                "expired_time": 1546507762,
                "request_id": "1546504161,7119660d-74b6-4dc2-b0e4-5ed94448a210",
                "time_used": 157,
                "token": "88166fe00432193c28d5bed3adf2f182"
    }
    }*/

    public int code;

    public RenzhengBean data;
    public String biz_id;
    public long expired_time;
    public String request_id;
    public int time_used;
    public String token;
    public String value;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public RenzhengBean getData() {
        return data;
    }

    public void setData(RenzhengBean data) {
        this.data = data;
    }

    public String getBiz_id() {
        return biz_id;
    }

    public void setBiz_id(String biz_id) {
        this.biz_id = biz_id;
    }

    public long getExpired_time() {
        return expired_time;
    }

    public void setExpired_time(long expired_time) {
        this.expired_time = expired_time;
    }

    public String getRequest_id() {
        return request_id;
    }

    public void setRequest_id(String request_id) {
        this.request_id = request_id;
    }

    public int getTime_used() {
        return time_used;
    }

    public void setTime_used(int time_used) {
        this.time_used = time_used;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "RenzhengBean{" +
                "code=" + code +
                ", data=" + data +
                ", biz_id='" + biz_id + '\'' +
                ", expired_time=" + expired_time +
                ", request_id='" + request_id + '\'' +
                ", time_used=" + time_used +
                ", token='" + token + '\'' +
                ", value='" + value + '\'' +
                '}';
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
