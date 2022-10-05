package huolongluo.byw.reform.base;


import java.util.List;

/**
 * Created by Administrator on 2019/1/7 0007.
 */
public class hyperpayBannerBean {

    public int code;

    public String msg;

    public int status;

    public List<BannerBean> banners;
    public String down_url;

    @Override
    public String toString() {
        return "hyperpayBannerBean{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", status=" + status +
                '}';
    }

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

    public List<BannerBean> getBanners() {
        return banners;
    }

    public void setBanners(List<BannerBean> banners) {
        this.banners = banners;
    }

    public String getDown_url() {
        return down_url;
    }

    public void setDown_url(String down_url) {
        this.down_url = down_url;
    }

    public static class BannerBean {

        public String id;
        public String imageUrl;


        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }
    }
}
