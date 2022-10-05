package huolongluo.byw.byw.inform.bean;
public class MailBean {
//       "content":"测试站内信内容",
//               "createDate":"2022-02-10 23:25:55",
//               "fId":20,
//               "farticleId":110,
//               "pushRange":1,
//               "status":2,
//               "title":"테스트 사이트 편지 제목",
//               "uid":231
    public String fContent;
    public String fCreateDate;
    public long fId;
    public long fFarticleId;
    public int fPushRange;
    public int fStatus;//1未读  2已读
    public long fUid;
    public String fTitle;

    public String getfContent() {
        return fContent;
    }

    public void setfContent(String fContent) {
        this.fContent = fContent;
    }

    public String getfCreateDate() {
        return fCreateDate;
    }

    public void setfCreateDate(String fCreateDate) {
        this.fCreateDate = fCreateDate;
    }

    public long getfId() {
        return fId;
    }

    public void setfId(long fId) {
        this.fId = fId;
    }

    public long getfFarticleId() {
        return fFarticleId;
    }

    public void setfFarticleId(long fFarticleId) {
        this.fFarticleId = fFarticleId;
    }

    public int getfPushRange() {
        return fPushRange;
    }

    public void setfPushRange(int fPushRange) {
        this.fPushRange = fPushRange;
    }

    public int getfStatus() {
        return fStatus;
    }

    public void setfStatus(int fStatus) {
        this.fStatus = fStatus;
    }

    public long getfUid() {
        return fUid;
    }

    public void setfUid(long fUid) {
        this.fUid = fUid;
    }

    public String getfTitle() {
        return fTitle;
    }

    public void setfTitle(String fTitle) {
        this.fTitle = fTitle;
    }
}
