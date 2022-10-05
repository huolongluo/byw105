package huolongluo.byw.byw.inform.bean;

/**
 * Created by Administrator on 2018/9/17 0017.
 */

public class FarticleBean {

      /* "fid": 79,
       "furl": "https://btc018.oss-cn-shenzhen.aliyuncs.com/static/front/images/service/news.png",
       "ftitle": "Coinw币赢网11月18日0点停机维护公告4",
       "fcontent": "<p><span style=\"font-size:18px;\">尊敬的币赢网用户您好：<br />为了让您更流畅的体验交易，我们将在11月18日0点停机维护，维护时间大约为2小时，维护内容如下：<br />1.APP新版本发布<br />2.交易服务器集群负载<\/span><\/p><p><span style=\"font-size:18px;\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Coinw币赢网运营团队<\/span><\/p>",
       "fcontent_m": "尊敬的币赢网用户您好：为了让您更流畅的体验交易，我们将在11月18日0点停机维护，维护时间大约为2小时，维护内容如下：1.APP新版本发布2.交易服务器集群负载&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;...",
       "fcreateTime": "2017-11-17 20:30:25"
    */

    public int fid;
    public String furl;
    public String ftitle;
    public String fcontent;
    public String fcontent_m;
    public String fcreateTime;
    public boolean isReaded;
    public boolean goOut;
    public String outUrl;

    public String getOutUrl() {
        return outUrl;
    }

    public void setOutUrl(String outUrl) {
        this.outUrl = outUrl;
    }

    public boolean isGoOut() {
        return goOut;
    }

    public void setGoOut(boolean goOut) {
        this.goOut = goOut;
    }

    public int getFid() {
        return fid;
    }

    public void setFid(int fid) {
        this.fid = fid;
    }

    public String getFurl() {
        return furl;
    }

    public void setFurl(String furl) {
        this.furl = furl;
    }

    public String getFtitle() {
        return ftitle;
    }

    public void setFtitle(String ftitle) {
        this.ftitle = ftitle;
    }

    public String getFcontent() {
        return fcontent;
    }

    public void setFcontent(String fcontent) {
        this.fcontent = fcontent;
    }

    public String getFcontent_m() {
        return fcontent_m;
    }

    public void setFcontent_m(String fcontent_m) {
        this.fcontent_m = fcontent_m;
    }

    public String getFcreateTime() {
        return fcreateTime;
    }

    public void setFcreateTime(String fcreateTime) {
        this.fcreateTime = fcreateTime;
    }

    public boolean isReaded() {
        return isReaded;
    }

    public void setReaded(boolean readed) {
        isReaded = readed;
    }

    @Override
    public String toString() {
        return "FarticleBean{" +
                "fid=" + fid +
                ", furl='" + furl + '\'' +
                ", ftitle='" + ftitle + '\'' +
                ", fcontent='" + fcontent + '\'' +
                ", fcontent_m='" + fcontent_m + '\'' +
                ", fcreateTime='" + fcreateTime + '\'' +
                ", isReaded=" + isReaded +
                '}';
    }
}
