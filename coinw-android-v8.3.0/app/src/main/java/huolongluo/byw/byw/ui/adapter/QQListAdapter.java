package huolongluo.byw.byw.ui.adapter;
import android.content.Context;

import com.jakewharton.rxbinding.view.RxView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.concurrent.TimeUnit;

import huolongluo.byw.R;
import huolongluo.byw.byw.bean.QQBean;
import huolongluo.byw.byw.share.Event;
import huolongluo.byw.log.Logger;
import huolongluo.byw.superAdapter.recycler.BaseViewHolder;
import huolongluo.byw.superAdapter.recycler.SuperAdapter;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
/**
 * Created by 火龙裸 on 2018/1/3.
 */
public class QQListAdapter extends SuperAdapter<QQBean.ListBean> {
    public QQListAdapter(Context context, List<QQBean.ListBean> list, int layoutResId) {
        super(context, list, layoutResId);
    }

    @Override
    public void onBind(int viewType, BaseViewHolder holder, int position, QQBean.ListBean item) {
        holder.setText(R.id.tv_shanghu_name, item.getFname());
        holder.setText(R.id.tv_qq_num, item.getFqq());
        holder.setText(R.id.tv_shanghu_introduce, item.getFremark());
        // 点击自配送
        RxView.clicks(holder.getView(R.id.tv_qq_num)).throttleFirst(500, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread()).onErrorReturn(throwable -> {
            Logger.getInstance().errorLog(throwable);
            return null;
        }).subscribe(v -> {
            EventBus.getDefault().post(new Event.clickQQService(item.getFqq()));
        }, throwable -> {
            Logger.getInstance().error(throwable);
        });
    }
}
