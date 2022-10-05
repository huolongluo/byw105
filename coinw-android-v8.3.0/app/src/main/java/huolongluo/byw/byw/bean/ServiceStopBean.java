package huolongluo.byw.byw.bean;

import java.io.Serializable;

public class ServiceStopBean {

    /**
     * code : 400
     * data : {"codeUrl":"https://btc018.oss-cn-shenzhen.aliyuncs.com/202005141520059_mL1Id.jpg","wechatNum":"CoinW-2023","officialDomain":"www.coinw.to","remainTime":"05:42:27","countDown":20547,"title":"System maintaining and upgrading","quickDomain":"www.coinw.py","content":"In order to bring you a better experience, CoinW is undergoing system maintenance and upgrades. We apologize for any inconvenience caused by this. Thank you for your understanding and support!"}
     */

    private int code;
    private DataBean data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean implements Serializable {
        /**
         * codeUrl : https://btc018.oss-cn-shenzhen.aliyuncs.com/202005141520059_mL1Id.jpg
         * wechatNum : CoinW-2023
         * officialDomain : www.coinw.to
         * remainTime : 05:42:27
         * countDown : 20547
         * title : System maintaining and upgrading
         * quickDomain : www.coinw.py
         * content : In order to bring you a better experience, CoinW is undergoing system maintenance and upgrades. We apologize for any inconvenience caused by this. Thank you for your understanding and support!
         */

        private String codeUrl;
        private String wechatNum;
        private String officialDomain;
        private String remainTime;
        private long countDown;
        private String title;
        private String quickDomain;
        private String content;
        private String outerContent;

        public String getOuterContent() {
            return outerContent;
        }

        public void setOuterContent(String outerContent) {
            this.outerContent = outerContent;
        }

        public String getCodeUrl() {
            return codeUrl;
        }

        public void setCodeUrl(String codeUrl) {
            this.codeUrl = codeUrl;
        }

        public String getWechatNum() {
            return wechatNum;
        }

        public void setWechatNum(String wechatNum) {
            this.wechatNum = wechatNum;
        }

        public String getOfficialDomain() {
            return officialDomain;
        }

        public void setOfficialDomain(String officialDomain) {
            this.officialDomain = officialDomain;
        }

        public String getRemainTime() {
            return remainTime;
        }

        public void setRemainTime(String remainTime) {
            this.remainTime = remainTime;
        }

        public long getCountDown() {
            return countDown;
        }

        public void setCountDown(long countDown) {
            this.countDown = countDown;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getQuickDomain() {
            return quickDomain;
        }

        public void setQuickDomain(String quickDomain) {
            this.quickDomain = quickDomain;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}
