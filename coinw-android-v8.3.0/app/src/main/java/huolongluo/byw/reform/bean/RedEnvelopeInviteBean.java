package huolongluo.byw.reform.bean;

import java.io.Serializable;

public class RedEnvelopeInviteBean implements Serializable {
    public String url;
    public InviteIMG img;

    public static class InviteIMG {
        public int id;
        public String createTime;
        public String i18nEnUs;
        public String i18nKey;
        public String i18nKoKr;
        public String i18nZhCn;
        public String i18nZhTw;
        public String updateTime;
    }
}
