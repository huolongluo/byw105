package huolongluo.byw.model;

import huolongluo.byw.byw.bean.MarketListBean;
import huolongluo.byw.byw.ui.fragment.maintab01.bean.RiseFallBean;

import java.util.List;

public class RiseFallResult {

    public String message;
    public String code;
    public SubRiseFallResult<RiseFallBean> data;

    public static class SubRiseFallResult<RiseFall> {

        public String value;
        public int code;
        public RiseFallBean data;
    }

//    public class RiseFall {
//
////        public int type;
////        public boolean result;
////        public String lastUpdateTime;
////        public String title;
//        public List<RiseFallBean> fallList;
//    }
}
