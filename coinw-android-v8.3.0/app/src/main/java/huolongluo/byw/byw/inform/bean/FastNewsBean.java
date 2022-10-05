package huolongluo.byw.byw.inform.bean;


import java.io.Serializable;

/**
 * Created by Administrator on 2019/1/21 0021.
 */

public class FastNewsBean  implements Serializable{

    public String coin;
    public String content;
    public String id;
    public String source;
    public String time;
    public String title;
    public long timestamp;

    @Override
    public String toString() {
        return "FastNewsBean{" +
                "coin='" + coin + '\'' +
                ", content='" + content + '\'' +
                ", id='" + id + '\'' +
                ", source='" + source + '\'' +
                ", time='" + time + '\'' +
                ", title='" + title + '\'' +
                '}';
    }

    public String getCoin() {
        return coin;
    }

    public void setCoin(String coin) {
        this.coin = coin;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
