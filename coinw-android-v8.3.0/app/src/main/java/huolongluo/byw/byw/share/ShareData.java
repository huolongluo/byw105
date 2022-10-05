package huolongluo.byw.byw.share;

import com.cocosw.favor.AllFavor;
import com.cocosw.favor.Commit;
import com.cocosw.favor.Default;
import com.cocosw.favor.Favor;

import huolongluo.byw.byw.net.UrlConstants;

/**
 * Created by 火龙裸先生 on 2017/8/11 0011.
 * <p>
 * 数据保存在sharepreference中
 */

@AllFavor
public interface ShareData {
    String BASE_URL = "baseUrl";

    String TOKEN = "token";

    String USER_NAME = "userName";
    String PASS_WORD = "passWord";
    String LOGINTOKEN = "loginToken";
    String UID = "uId";

    String IS_BIND_PHONE = "isBindPhone";
    String IS_BIND_GOOGLE = "isBindGoogle";

    String REALNAME = "realName";

    String TRADE_PASSWORD = "tradePassWord";

    String isTRADE_PASSWORD = "true";
    String IMEI = "imei";

    String VIP = "vip";

    String isFirstOpen = "isFirstOpen";//是否第一次打开app
    String isFirstOpenOtc = "isFirstOpenOtc";//是否第一次打开otc界面

    String firstXianShi = "firstXianShi";//第一次打开限时区

    @Commit
    @Favor(isFirstOpen)
    void setisFirstOpen(boolean isopenc2c);

    @Commit
    @Favor(isFirstOpen)
    boolean getisFirstOpen();

    @Favor(isFirstOpenOtc)
    void setisFirstOpenOtc(boolean isopenotc);
    @Commit
    @Favor(isFirstOpenOtc)
    boolean getisFirstOpenOtc();


    @Favor(firstXianShi)
    void setFirstXianShi(boolean isTradepassword);

    @Commit
    @Favor(firstXianShi)
    boolean getFirstXianShi();

    //@Commit
    //@Favor(BASE_URL)
    //void setBaseUrl(String baseUrl);

    //@Default(UrlConstants.DOMAIN)  //好像这个地方没有用到，直接默认线上接口，不能设置URLConstant的为final
    //  @Default("http://47.105.57.232/")
    //@Favor(BASE_URL)
    //String getBaseUrl();

    @Commit
    @Favor(TOKEN)
    void setToken(String token);

    @Default("e9d839537a3e4ee6836a0f286e74c8a5")
    @Favor(TOKEN)
    String getToken();


    @Commit
    @Favor(LOGINTOKEN)
    void setLogintoken(String loginToken);

    @Default("")
    @Favor(LOGINTOKEN)
    String getLogintoken();


}
