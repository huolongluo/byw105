package huolongluo.byw.reform.home.activity.kline2.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import com.android.coinw.api.kx.model.XLatestDeal;
import com.android.coinw.biz.trade.model.OrderSide;
import com.legend.common.util.ThemeUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import huolongluo.byw.R;
import huolongluo.byw.byw.base.BaseApp;
import huolongluo.byw.log.Logger;
import huolongluo.byw.util.Constant;
import huolongluo.byw.util.DateUtils;
import huolongluo.byw.util.pricing.PricingMethodUtil;
/**
 * Created by hy on 2018/11/14 0014.
 */
public class KLineTurnoverViewAdapter extends RecyclerView.Adapter<KLineTurnoverViewAdapter.ViewHolder> {
    private static final String TAG = "KLineTurnoverViewAdapte";
    List<XLatestDeal> dataList;
    private long seq=0;//序号类似id

    public KLineTurnoverViewAdapter(List<XLatestDeal> latestList) {
        this.dataList = latestList;
    }

    public int getCount() {
        return dataList == null ? 0 : dataList.size();
    }

    public XLatestDeal getItem(int position) {
        if (position < 0 || dataList == null || dataList.isEmpty()) {
            return null;
        }
        return dataList.get(position);
    }

    public void clear() {
        if (dataList != null) {
            this.dataList.clear();
        }
        notifyDataSetChanged();
    }

    public void updates(List<XLatestDeal> latestList) {
        if (latestList == null) {
            if (dataList != null) {
                this.dataList.clear();
            }
            notifyDataSetChanged();
            return;
        }
        //TODO 检查数据是否已经更新
        //
        if (this.dataList != null) {
            this.dataList.clear();
            this.dataList.addAll(latestList);
        } else {
            this.dataList = latestList;
        }
        sort();
        checkList();
        notifyDataSetChanged();
    }
    public void updateSocket(List<XLatestDeal> list) {
        if (list == null) {
            return;
        }
        if(list.get(0).getSeq() <seq){//socket推来的数据小于记录的时间直接忽略
            return;
        }
        if (this.dataList != null) {
            this.dataList.addAll(0,list);
        } else {
            this.dataList = new ArrayList<>();
            this.dataList.addAll(0,list);
        }
        checkList();
        notifyDataSetChanged();
    }
    //只显示最多20条数据
    private void checkList(){
        if(dataList.size()> Constant.MAX_SIZE){
            dataList=dataList.subList(0,Constant.MAX_SIZE);
        }
        seq= dataList.get(0).getSeq();
    }
    //http拿到数据因为考虑异常情况不是最新的数据，需要添加进list做排序
    private void sort(){
        Collections.sort(dataList, new TimeComparator());
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_lastorder_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        try {
            XLatestDeal bean = getItem(position);
            holder.number_tv.setText(PricingMethodUtil.getLargePrice(bean.getSize(),3));
            holder.price_tv.setText(bean.getPrice());
            holder.time_tv.setText(DateUtils.format(bean.getTime(),DateUtils.FORMAT1));

            if (TextUtils.equals(bean.getSide(), OrderSide.BUY.getSide())) {
                holder.price_tv.setTextColor(ThemeUtil.INSTANCE.getUpColor(holder.price_tv.getContext()));
            } else {
                holder.price_tv.setTextColor(ThemeUtil.INSTANCE.getDropColor(holder.price_tv.getContext()));
            }
        }catch (Exception e){
            e.printStackTrace();
            Logger.getInstance().debug(TAG,"adapter解析数据出错");
        }

    }

    @Override
    public int getItemCount() {
        return getCount();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView time_tv;
        public TextView number_tv;
        public TextView price_tv;

        public ViewHolder(View itemView) {
            super(itemView);
            time_tv = itemView.findViewById(R.id.time_tv);
            price_tv = itemView.findViewById(R.id.price_tv);
            number_tv = itemView.findViewById(R.id.number_tv);
        }
    }

    static class TimeComparator implements Comparator<XLatestDeal> {
        @Override
        public int compare(XLatestDeal entry1, XLatestDeal entry2) {
            float diff = entry1.getTime() - entry2.getTime();

            if (diff == 0f) return 0;
            else {
                if (diff > 0f) return -1;
                else return 1;
            }
        }
    }
}
