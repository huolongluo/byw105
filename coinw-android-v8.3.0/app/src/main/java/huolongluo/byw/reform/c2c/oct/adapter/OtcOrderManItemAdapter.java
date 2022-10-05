package huolongluo.byw.reform.c2c.oct.adapter;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcodes.utils.TimeUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

import huolongluo.byw.R;
import huolongluo.byw.log.Logger;
import huolongluo.byw.reform.c2c.oct.activity.OtcCancleAppealActivity;
import huolongluo.byw.reform.c2c.oct.bean.OtcGetOrderBean;
import huolongluo.byw.util.Util;
import huolongluo.bywx.helper.AppHelper;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
/**
 * Created by Administrator on 2019/5/15 0015.
 */
public class OtcOrderManItemAdapter extends BaseAdapter {
    private Context context;
    private int realStatus;
    List<OtcGetOrderBean.DataBean.ListDataBean> listDataBeans;
    ClickListener clickListener;
    Subscription subscription;

    public static interface ClickListener {
        void onClick(int orderId, int status);
    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public OtcOrderManItemAdapter(Context context, int status, List<OtcGetOrderBean.DataBean.ListDataBean> listDataBeans) {
        this.context = context;
        realStatus = status;
        this.listDataBeans = listDataBeans;
    }

    @Override
    public int getCount() {
        return listDataBeans.size();
    }

    @Override
    public OtcGetOrderBean.DataBean.ListDataBean getItem(int position) {
        //java.lang.ArrayIndexOutOfBoundsException: length=10; index=-1
        //    at java.util.ArrayList.get(ArrayList.java:439)
        //    at g.a.j.a.b.b.p.getItem(OtcOrderManItemAdapter.java:2)
        if (position < 0 && listDataBeans.size() <= 0) {
            return null;
        }
        if (position < 0 && listDataBeans.size() > 0) {
            return listDataBeans.get(0);
        } else if (position == listDataBeans.size() && position > 0) {
            return listDataBeans.get(position - 1);
        }
        return listDataBeans.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHold viewHold;
        OtcGetOrderBean.DataBean.ListDataBean bean = listDataBeans.get(position);
        //java.lang.ArrayIndexOutOfBoundsException: length=10; index=-1
        //    at java.util.ArrayList.get(ArrayList.java:439)
        //    at g.a.j.b.b.b.p.getItem(OtcOrderManItemAdapter.java:4)
        //    at g.a.j.b.b.c.xa.onItemClick(OtcorderManItemFragment.java:1)
        if (bean == null) {
            return convertView;
        }
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_otcordermanitem_item, parent, false);
            viewHold = new ViewHold();
            viewHold.state_tv = convertView.findViewById(R.id.state_tv);
            viewHold.nopay_ll = convertView.findViewById(R.id.nopay_ll);
            viewHold.coinName_tv = convertView.findViewById(R.id.coinName_tv);
            viewHold.amount_tv = convertView.findViewById(R.id.amount_tv);
            viewHold.nickName_tv = convertView.findViewById(R.id.nickName_tv);
            viewHold.time_tv = convertView.findViewById(R.id.time_tv);
            viewHold.timeLimit_tv = convertView.findViewById(R.id.timeLimit_tv);
            viewHold.namelog_tv = convertView.findViewById(R.id.namelog_tv);
            viewHold.isVip_iv = convertView.findViewById(R.id.isVip_iv);
            viewHold.tradeType_tv = convertView.findViewById(R.id.tradeType_tv);
            if (bean.getDealUserNickname().length() > 0) {
                viewHold.namelog_tv.setText(bean.getDealUserNickname().charAt(0) + "");
                viewHold.namelog_tv.setBackgroundResource(Util.getRadom());
            }
            convertView.setTag(viewHold);
        } else {
            viewHold = (ViewHold) convertView.getTag();
        }
        viewHold.coinName_tv.setText(bean.getCoinName());
        // viewHold.amount_tv.setText(bean.getAmount() + " " + bean.getCoinName());
        viewHold.amount_tv.setText(bean.getTotalAmount() + " CNY");
        viewHold.nickName_tv.setText(bean.getDealUserNickname());
        viewHold.time_tv.setText(bean.getCreateTime_s() + "");
        if (bean.getOtcLevel() == 3) {
            viewHold.isVip_iv.setVisibility(View.VISIBLE);
        } else {
            viewHold.isVip_iv.setVisibility(View.GONE);
        }
        //    viewHold.state_tv.setOnClickListener(null);
        if (realStatus == 0) {
            int timeLimt = bean.getTimeLimit();
            if (timeLimt > 1) {
                subscription = Observable.interval(0, 1, TimeUnit.SECONDS).limit(timeLimt + 1).map(aLong -> timeLimt - aLong).observeOn(AndroidSchedulers.mainThread()).doOnNext(aLong -> {
                    if (viewHold == null || viewHold.timeLimit_tv == null) {
                        return;
                    }
                    if (aLong == 1) {
                        viewHold.timeLimit_tv.setVisibility(View.INVISIBLE);
                    } else {
                        viewHold.timeLimit_tv.setText(TimeUtils.millis2String(aLong * 1000, "mm:ss"));
                    }
                }).onErrorReturn(throwable -> {
                    Logger.getInstance().errorLog(throwable);
                    return null;
                }).doOnError(throwable -> Logger.getInstance().error(throwable)).subscribe();
            } else {
                viewHold.timeLimit_tv.setVisibility(View.INVISIBLE);
            }
            viewHold.tradeType_tv.setText(R.string.qa28);
            viewHold.state_tv.setText(realStatus + "");
            viewHold.nopay_ll.setVisibility(View.VISIBLE);
            viewHold.state_tv.setVisibility(View.GONE);
        } else {
            viewHold.nopay_ll.setVisibility(View.GONE);
            viewHold.state_tv.setVisibility(View.VISIBLE);
            if (realStatus == 1) {
                viewHold.state_tv.setText(R.string.qa29);
                viewHold.state_tv.setBackgroundResource(R.drawable.otcorder_bg1);
                viewHold.state_tv.setTextColor(context.getResources().getColor(R.color.ff6f6699));
            } else if (realStatus == 2) {
                viewHold.state_tv.setBackgroundResource(R.drawable.otcorder_bg2);
                viewHold.state_tv.setTextColor(context.getResources().getColor(R.color.ff31ac5e));
                viewHold.state_tv.setText(R.string.qa30);
            } else if (realStatus == 3 || realStatus == 4) {
                viewHold.state_tv.setBackgroundResource(R.drawable.otcorder_bg1);
                viewHold.state_tv.setTextColor(context.getResources().getColor(R.color.ff6f6699));
                viewHold.state_tv.setText(R.string.qa31);
            } else if (realStatus == 5) {
                if (bean.getStatus() == 5) {
                    viewHold.state_tv.setBackgroundResource(R.drawable.otcorder_bg1);
                    viewHold.state_tv.setTextColor(context.getResources().getColor(R.color.ff6f6699));
                    viewHold.state_tv.setText(R.string.qa32);
                    viewHold.state_tv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (context != null) {
                                Intent intent = new Intent(context, OtcCancleAppealActivity.class);
                                intent.putExtra("orderId", bean.getId());
                                context.startActivity(intent);
                            }
                        }
                    });
                } else if (bean.getStatus() == 6) {
                    viewHold.state_tv.setOnClickListener(null);
                    viewHold.state_tv.setBackgroundResource(R.drawable.otcorder_bg1);
                    viewHold.state_tv.setTextColor(context.getResources().getColor(R.color.ff6f6699));
                    viewHold.state_tv.setText(R.string.qa33);
                } else {
                    viewHold.state_tv.setOnClickListener(null);
                    viewHold.state_tv.setBackgroundResource(R.drawable.otcorder_bg1);
                    viewHold.state_tv.setTextColor(context.getResources().getColor(R.color.ff6f6699));
                    viewHold.state_tv.setText(R.string.qa34);
                }
            }
        }
        return convertView;
    }

    public void release() {
        AppHelper.unsubscribe(subscription);
    }

    public static class ViewHold {
        public ImageView isVip_iv;
        public TextView namelog_tv;
        public TextView tradeType_tv;
        public TextView state_tv;
        public TextView coinName_tv;
        public TextView amount_tv;
        public TextView nickName_tv;
        public TextView time_tv;
        public TextView timeLimit_tv;
        public LinearLayout nopay_ll;
    }
}
