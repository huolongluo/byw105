package huolongluo.byw.byw.ui.fragment.maintab01.home;
import android.content.Context;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import cn.baymax.android.keyboard.Utils;
import huolongluo.byw.R;
import huolongluo.byw.log.Logger;
import huolongluo.byw.model.HomeDynamicResult;
import huolongluo.byw.util.Constant;

public class HomeDynamicAdapter extends RecyclerView.Adapter<HomeDynamicAdapter.ViewHoler> {
    private List<HomeDynamicResult.HomeDynamic> dataList = new LinkedList<>();
    private IDynamicTarget target;
    private Context context;

    public HomeDynamicAdapter(Context context, List<HomeDynamicResult.HomeDynamic> dataList, IDynamicTarget target) {
        this.dataList.clear();
        this.dataList.addAll(dataList);
        this.context = context;
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        this.target = target;
    }

    public void update(List<HomeDynamicResult.HomeDynamic> dataList) {
        if (dataList != null) {
            this.dataList.clear();
            this.dataList.addAll(dataList);
            notifyDataSetChanged();
        }
    }

    public HomeDynamicResult.HomeDynamic getItem(int position) {
        if (position < 0 || dataList == null || dataList.isEmpty()) {
            return null;
        }
        if (dataList.size() > position) {
            return dataList.get(position);
        }
        return null;
    }

    @Override
    public ViewHoler onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_dynamic_item, parent, false);
        ViewHoler ViewHoler = new ViewHoler(view);
        return ViewHoler;
    }

    @Override
    public void onBindViewHolder(ViewHoler holder, int position) {
        final HomeDynamicResult.HomeDynamic hd = dataList.get(position);
        holder.itemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hd == null) {
                    return;
                }
                if (target == null) {
                    return;
                }
                MobclickAgent.onEvent(context, Constant.UMENG_EVENT_PRE_BG_2 + (position+1), hd.title);

                target.gotoTarget(hd);
            }
        });
        //图片
        if (hd.isLocal) {
            try {
                holder.imgView.setImageResource(hd.imgResId);
            } catch (Throwable t) {
                Logger.getInstance().error(t);
            }
        } else {
            //
            RequestOptions ro = new RequestOptions();
            ro.error(R.mipmap.rmblogo);
            ro.centerCrop();
            Glide.with(context).load(hd.imgUrl).apply(ro).into((ImageView) holder.imgView);
        }
        Logger.getInstance().debug("HomeDynamicAdapter", "title: " + hd.title);
        holder.titleTxt.setText(TextUtils.isEmpty(hd.title) ? "" : hd.title);
    }

    @Override
    public int getItemCount() {
        return this.dataList == null ? 0 : this.dataList.size();
    }

    public static class ViewHoler extends RecyclerView.ViewHolder {
        public RelativeLayout itemLayout;
        public ImageView imgView;
        public TextView titleTxt;

        public ViewHoler(View itemView) {
            super(itemView);
            this.itemLayout = itemView.findViewById(R.id.ll_dynamic_item);
            this.imgView = itemView.findViewById(R.id.iv_dynamic_item);
            this.titleTxt = itemView.findViewById(R.id.tv_dynamic_item_title);
        }
    }
}
