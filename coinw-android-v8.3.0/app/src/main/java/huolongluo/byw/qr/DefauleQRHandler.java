package huolongluo.byw.qr;
import android.text.TextUtils;
public class DefauleQRHandler implements IQRHandler {
    @Override
    public void handle(String data) {
        //
        if (TextUtils.isEmpty(data)) {
            //异常情况
            return;
        }
        //face验证业务
        if (data.indexOf("/app/faceIdVerifyToken") != -1) {
            //
            post(data);
        }
    }

    private void post(String url){

    }
}
