package huolongluo.byw.byw.ui.redEnvelope;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.android.coinw.biz.event.BizEvent;
import com.android.legend.socketio.SocketIOClient;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;

import huolongluo.byw.io.AppConstants;
import huolongluo.byw.log.Logger;

public class RedEnvelopeService extends Service {
    protected String TAG = this.getClass().getSimpleName() + "-x";

    public RedEnvelopeService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Logger.getInstance().debug(TAG, "onCreate");
//        init();
        registerEvent();
    }

//    /**
//     * 初始化
//     */
//    private void init() {
//        //注册ws订阅回调接口
//        ReqData rd = new ReqData();
//        rd.tag = this.getClass().getName();
//        //SOCKET
//        Type socketType = new TypeToken<RedEnvelopeEntity>() {
//        }.getType();
//        rd.api = AppConstants.SOCKET.redenvelope;
//        rd.socketType = socketType;
//        rd.interval = 3000L;
//        rd.socketCallback = onRecvDataCallback;
//        AppClient.getInstance().addListener(rd, true);
//        //TODO 测试专用
//        AppClient.getInstance().apiRedEnvelope(AppConstants.SOCKET.OPEN, "-1");
//    }

    private void registerEvent() {
        SocketIOClient.subscribe(TAG, AppConstants.SOCKET.SPOT_RED_ENVELOPE, new TypeToken<RedEnvelopeEntity>() {
        }, bean -> {
            EventBus.getDefault().post(new BizEvent.ShakeRedEnvelope(bean.isHaveNew == 1));
            return null;
        });
    }

    private void unregisterEvent() {
        SocketIOClient.unsubscribe(TAG, AppConstants.SOCKET.SPOT_RED_ENVELOPE);
    }

//    //socket返回数据
//    protected OnResultCallback onRecvDataCallback = new OnResultCallback<RedEnvelopeEntity>() {
//        @Override
//        public void onResult(RedEnvelopeEntity obj, String[] params) {
//            EventBus.getDefault().post(new BizEvent.ShakeRedEnvelope(obj.isHaveNew == 1));
//        }
//
//        @Override
//        public void onFail() {
//            Log.i(getClass().toString(), "------------2");
//            Logger.getInstance().debug(TAG, "error", new Exception());
//            //TODO 处理异常情况
//        }
//    };

    @Override
    public void onDestroy() {
        super.onDestroy();
//        AppClient.getInstance().apiRedEnvelope(AppConstants.SOCKET.CLOSE);
        unregisterEvent();
    }
}
