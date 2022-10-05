package huolongluo.byw.reform.base;


import java.util.List;

/**
 * Created by Administrator on 2019/1/7 0007.
 */

public class ByperpayBean {

public  int code;

public List<LicaiBean> data;

public String msg;
public int  status;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }



    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<LicaiBean> getData() {
        return data;
    }

    public void setData(List<LicaiBean> data) {
        this.data = data;
    }

    public static  class LicaiBean{
             private String info;
             private String ico_url;
             private String id;
             private String rate;
             private String title;
             private String type;
             private String keywords;

             private String start_in;

        public String getInfo() {
            return info;
        }

        public void setInfo(String info) {
            this.info = info;
        }

        public String getIco_url() {
            return ico_url;
        }

        public void setIco_url(String ico_url) {
            this.ico_url = ico_url;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getRate() {
            return rate;
        }

        public void setRate(String rate) {
            this.rate = rate;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getKeywords() {
            return keywords;
        }

        public void setKeywords(String keywords) {
            this.keywords = keywords;
        }

        public String getStart_in() {
            return start_in;
        }

        public void setStart_in(String start_in) {
            this.start_in = start_in;
        }
    }
}
