package huolongluo.byw.byw.ui.fragment.maintab01.bean;
import java.io.Serializable;
import java.util.List;
public class TitleEntity implements Serializable {
    /**
     * fbs : [{"showRisk":true,"active":true,"areaName":"主区","areaCoins":[{"coinName":"CNYT","fid":2},{"coinName":"USDT","fid":29}],"type":1,"areaCoinsStr":"CNYT_USDT"},{"showRisk":true,"active":true,"areaName":"创新区","areaCoins":[{"coinName":"CNYT","fid":2}],"type":0,"areaCoinsStr":"CNYT"},{"showRisk":true,"active":true,"areaName":"潜力区","areaCoins":[],"type":2,"areaCoinsStr":""},{"showRisk":true,"active":true,"areaName":"低流通区","areaCoins":[],"type":3,"areaCoinsStr":""}]
     * value : 操作成功
     * code : 0
     */

    private String value;
    private int code;
    private List<FbsBean> fbs;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<FbsBean> getFbs() {
        return fbs;
    }

    public void setFbs(List<FbsBean> fbs) {
        this.fbs = fbs;
    }

    public static class FbsBean implements Serializable {
        /**
         * showRisk : true
         * active : true
         * areaName : 主区
         * areaCoins : [{"coinName":"CNYT","fid":2},{"coinName":"USDT","fid":29}]
         * type : 1
         * areaCoinsStr : CNYT_USDT
         */

        private boolean showRisk;
        private boolean active;
        private String areaName;
        private int type;
        private String areaCoinsStr;
        private List<AreaCoinsBean> areaCoins;

        public boolean isShowRisk() {
            return showRisk;
        }

        public void setShowRisk(boolean showRisk) {
            this.showRisk = showRisk;
        }

        public boolean isActive() {
            return active;
        }

        public void setActive(boolean active) {
            this.active = active;
        }

        public String getAreaName() {
            return areaName;
        }

        public void setAreaName(String areaName) {
            this.areaName = areaName;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getAreaCoinsStr() {
            return areaCoinsStr;
        }

        public void setAreaCoinsStr(String areaCoinsStr) {
            this.areaCoinsStr = areaCoinsStr;
        }

        public List<AreaCoinsBean> getAreaCoins() {
            return areaCoins;
        }

        public void setAreaCoins(List<AreaCoinsBean> areaCoins) {
            this.areaCoins = areaCoins;
        }

        public static class AreaCoinsBean implements Serializable {
            /**
             * coinName : CNYT
             * fid : 2
             */

            private String coinName;
            private int fid;

            public String getCoinName() {
                return coinName;
            }

            public void setCoinName(String coinName) {
                this.coinName = coinName;
            }

            public int getFid() {
                return fid;
            }

            public void setFid(int fid) {
                this.fid = fid;
            }
        }
    }
}
