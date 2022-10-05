package huolongluo.byw.byw.ui.fragment.maintab01.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2018/10/29 0029.
 */

public class BannerBean implements Serializable {

     /*     "content": "appisc",
          "fcreateDateYear": "2018-10-29",
          "id": 21,
          "img": "/upload/images/201810291627053_VCguM.jpg",
          "title": "webisc",
          "type": 9,
          "url": "/"*/
    private int code;
    private String msg;

  private   List<BannerBean> bannerList;
    private String content;
    private String fcreateDateYear;
    private String img;
    private String title;
    private String url;
    private String newid;
    private int whether;//0,app,1,网页
    private int id;
    private int type;

    public BannerBean(String img) {
       this.img=img;
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

    public List<BannerBean> getBannerList() {
        return bannerList;
    }

    public void setBannerList(List<BannerBean> bannerList) {
        this.bannerList = bannerList;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFcreateDateYear() {
        return fcreateDateYear;
    }

    public void setFcreateDateYear(String fcreateDateYear) {
        this.fcreateDateYear = fcreateDateYear;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "BannerBean{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", bannerList=" + bannerList +
                ", content='" + content + '\'' +
                ", fcreateDateYear='" + fcreateDateYear + '\'' +
                ", img='" + img + '\'' +
                ", title='" + title + '\'' +
                ", url='" + url + '\'' +
                ", id=" + id +
                ", type=" + type +
                '}';
    }

    public String getNewid() {
        return newid;
    }

    public void setNewid(String newid) {
        this.newid = newid;
    }

    public int getWhether() {
        return whether;
    }

    public void setWhether(int whether) {
        this.whether = whether;
    }
}
