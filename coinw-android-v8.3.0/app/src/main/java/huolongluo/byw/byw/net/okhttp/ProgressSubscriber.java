package huolongluo.byw.byw.net.okhttp;

import android.content.Context;

import com.android.tu.loadingdialog.LoadingDailog;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

import huolongluo.byw.R;
import huolongluo.byw.byw.manager.DialogManager;
import huolongluo.byw.byw.net.UrlConstants;
import huolongluo.byw.byw.share.Share;
import huolongluo.byw.util.L;
//import retrofit2.adapter.rxjava.HttpException;
import rx.Subscriber;

/**
 * <p>
 * Created by 火龙裸 on 2017/8/21.
 */

public class ProgressSubscriber<T> extends Subscriber<T>
{
    //    回调接口  
    private HttpOnNextListener mSubscriberOnNextListener;
    private HttpOnNextListener2 mSubscriberOnNextListener2;
    private Context mContext;
    private boolean isShowDialog;

    public ProgressSubscriber(HttpOnNextListener mSubscriberOnNextListener, Context mContext, boolean isShowDialog)
    {
        this.mSubscriberOnNextListener = mSubscriberOnNextListener;
        this.mContext = mContext;
        this.isShowDialog = isShowDialog;
    }

    public ProgressSubscriber(HttpOnNextListener2 mSubscriberOnNextListener2, Context mContext, boolean isShowDialog)
    {
        this.mSubscriberOnNextListener2 = mSubscriberOnNextListener2;
        this.mContext = mContext;
        this.isShowDialog = isShowDialog;
    }
    
    @Override
    public void onCompleted()
    {
        DialogManager.INSTANCE.dismiss();
        L.d("--- onCompleted");
//        LoadingDailog.Builder loadBuilder=new LoadingDailog.Builder(mContext)
//                .setMessage("加载中...")
//                .setCancelable(true)
//                .setCancelOutside(true);
//        LoadingDailog dialog=loadBuilder.create();
//        dialog.dismiss();
    }

    @Override
    public void onError(Throwable e)
    {
        //      Share.get().setBaseUrl(UrlConstants.DOMAIN_VICE); // 切换到副域名
       //Share.get().setBaseUrl(UrlConstants.DOMAIN); // 切换到副域名
//        if (e instanceof HttpException)
//        {
////            ToastSimple.show("服务繁忙，请稍后重试", 2);
//            L.e("服务繁忙，请稍后重试");
//        }
//        else
            if (e instanceof SocketTimeoutException)
        {
//            ToastSimple.show("网络出问题了，请稍后重试", 2);
            L.e("网络出问题了，请稍后重试");
        }
        else if (e instanceof ConnectException)
        {
//            ToastSimple.show("连接出问题了，请稍后重试", 2);
            L.e("连接出问题了，请稍后重试");
        }
        else
        {
            L.e("错误: " + e.getMessage());
        }

        if (null != mSubscriberOnNextListener2)
        {
            mSubscriberOnNextListener2.onError(e);
        }
        DialogManager.INSTANCE.dismiss();
//        LoadingDailog.Builder loadBuilder=new LoadingDailog.Builder(mContext)
//                .setMessage("加载中...")
//                .setCancelable(true)
//                .setCancelOutside(true);
//        LoadingDailog dialog=loadBuilder.create();
//        dialog.dismiss();
    }

    @Override
    public void onStart()
    {
        if (isShowDialog)
        {
            DialogManager.INSTANCE.showProgressDialog(mContext, mContext.getString(R.string.j1));
//            LoadingDailog.Builder loadBuilder=new LoadingDailog.Builder(mContext)
//                    .setMessage("加载中...")
//                    .setCancelable(true)
//                    .setCancelOutside(true);
//            LoadingDailog dialog=loadBuilder.create();
//            dialog.show();
//            DialogManager.INSTANCE.showLoading(mContext,"");
        }
    }

    @Override
    public void onNext(T t)
    {
//        Share.get().setBaseUrl(UrlConstants.DOMAIN); // 切换到主域名
        L.d("66666666666666666666666666666666666666"+t.toString());
//        L.e("================啪啪啪============" + t.toString());
        mSubscriberOnNextListener.onNext(t);
    }

}
