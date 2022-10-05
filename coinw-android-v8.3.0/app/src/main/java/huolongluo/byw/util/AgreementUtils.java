package huolongluo.byw.util;
import android.content.Context;

import huolongluo.byw.byw.ui.activity.main.MainActivity;
import huolongluo.byw.user.UserInfoManager;
import huolongluo.byw.util.sp.SpUtils2;
//协议开通相关的工具类
public class AgreementUtils {
    /**
     * 记录开通状态
     */
    private static boolean IS_BDB_AGREEMENT_OPEN=false;
    private static boolean IS_HY_AGREEMENT_OPEN=false;//合约的协议因为还有冻结状态，每次都需要获取接口
    
    public static final String KEY_BDB="IS_BDB_AGREEMENT_OPEN";
    public static final String KEY_HY="IS_HY_AGREEMENT_OPEN";
    
    public static void initAgreementOpenStatus(Context context,String uid){//应用登录注册成功或者启动有用户信息都需要调用该方法初始化该用户的协议开通状态
        IS_BDB_AGREEMENT_OPEN= SpUtils2.getBoolean(context,KEY_BDB+"_"+uid,false);
        IS_HY_AGREEMENT_OPEN= SpUtils2.getBoolean(context,KEY_HY+"_"+uid,false);
    }
    public static boolean isBdbOpen(){
        return IS_BDB_AGREEMENT_OPEN;
    }
    public static boolean isHyOpen(){
        return IS_HY_AGREEMENT_OPEN;
    }
    public static void saveBdbOpen(Context context){
        if(IS_BDB_AGREEMENT_OPEN) return;
        IS_BDB_AGREEMENT_OPEN=true;
        SpUtils2.saveBoolean(context,KEY_BDB+"_"+ UserInfoManager.getUserInfo().getFid(),true);
    }
    public static void saveHyOpen(Context context){
        if(IS_HY_AGREEMENT_OPEN) return;
        IS_HY_AGREEMENT_OPEN=true;
        SpUtils2.saveBoolean(context,KEY_HY+"_"+ UserInfoManager.getUserInfo().getFid(),true);
    }
    public static void reset(){//注销需要还原（不还原也不会影响功能，所以不考虑其他注销的情况）
        MainActivity.self.resetAgreement();
        IS_BDB_AGREEMENT_OPEN= false;
        IS_HY_AGREEMENT_OPEN= false;
    }
}
