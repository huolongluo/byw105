package huolongluo.byw.byw.ui.oneClickBuy;

import java.util.List;

import huolongluo.byw.reform.c2c.oct.bean.OtcCoinBean;
import huolongluo.byw.reform.c2c.oct.bean.PayOrderInfoBean;

public class C2cStatus {
    //由于服务器的定义值（true or false）代表意义不同，故采用此方式来处理
    public static boolean isShow = true;
    public static boolean isShowFast = false;

    public static List<OtcCoinBean.DataBean> oneClickCoinsId;
    public static PayOrderInfoBean payOrderInfoBean;
}
