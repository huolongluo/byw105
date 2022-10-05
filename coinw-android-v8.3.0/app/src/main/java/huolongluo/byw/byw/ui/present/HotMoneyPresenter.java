package huolongluo.byw.byw.ui.present;

import android.annotation.TargetApi;
import android.os.Build;
import android.util.Log;

import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import huolongluo.byw.byw.net.UrlConstants;
import huolongluo.byw.io.AppConstants;
import huolongluo.byw.util.OkhttpManager;
import huolongluo.byw.util.RSACipher;
import okhttp3.Request;

/**
 * Created by hy on 2018/9/6 0006.
 * 收集热点数据
 */

public class HotMoneyPresenter {


    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static void collectHotData(String type, String tmid){
        Map<String,String> params=new HashMap<>();
     //   params.put("type","1");
     //   params.put("tmid",tmid);


        try {


            RSACipher rsaCipher = new RSACipher();
            String body = "type="+ URLEncoder.encode("1")+"&tmid="+URLEncoder.encode(tmid);


            String body1 = rsaCipher.encrypt(body, AppConstants.KEY.PUBLIC_KEY);
            params.put("body", body1);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }



        OkhttpManager.postAsync(UrlConstants.DOMAIN + UrlConstants.SAVE_HOT_SEARCH ,
                params, new OkhttpManager.DataCallBack() {
                    @Override
                    public void requestFailure(Request request, Exception e, String errorMsg) {
                        Log.d("热点收集","Failure=  "+ request.body().toString());
                        e.printStackTrace();
                    }

                    @Override
                    public void requestSuccess(String result) {
                        Log.d("热点收集","result=  "+ result);


                    }
                });
    }

  //  public static void
}
