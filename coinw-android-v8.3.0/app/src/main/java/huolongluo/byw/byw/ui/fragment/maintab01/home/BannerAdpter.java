package huolongluo.byw.byw.ui.fragment.maintab01.home;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import huolongluo.byw.R;
import huolongluo.byw.byw.inform.activity.NoticeDetailActivity;
import huolongluo.byw.byw.ui.fragment.maintab01.bean.BannerBean;
import huolongluo.byw.reform.home.activity.NewsWebviewActivity;
import huolongluo.byw.user.UserInfoManager;
public class BannerAdpter extends RecyclerView.Adapter<BannerAdpter.ViewHolder> {
    private List<BannerBean> dataList = new ArrayList<>();
    private Context context;
    boolean isHide = false;
    private DecimalFormat df = new DecimalFormat("0.0000");

    public void setHide(boolean hide) {
        isHide = hide;
    }

    public boolean hasData() {
        return getItemCount() > 0;
    }

    public BannerAdpter(Context context, List<BannerBean> dataList) {
        this.dataList.addAll(dataList);
        this.context = context;
    }

    public BannerBean getItem(int position) {
        if (dataList.size() > position) {
            return dataList.get(position);
        }
        return null;
    }

    @Override
    public BannerAdpter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_banner_item, parent, false);
        BannerAdpter.ViewHolder viewHole = new BannerAdpter.ViewHolder(view);
        return viewHole;
    }

    public void replaceAll(List<BannerBean> items) {
        dataList.clear();
        dataList.addAll(items);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(BannerAdpter.ViewHolder holder, int position) {
        BannerBean item = dataList.get(position);
        RequestOptions ro = new RequestOptions();
        ro.placeholder(R.drawable.banner_default);
        ro.error(R.drawable.banner_default);
        Glide.with(context).load(item.getImg()).apply(ro).into(holder.imgView);
        holder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, NewsWebviewActivity.class);
                if (dataList.size() > position) {
                    if (dataList.get(position).getWhether() == 1) {
                        intent.putExtra("url", dataList.get(position).getUrl());
                        intent.putExtra("token", UserInfoManager.getToken());
                        intent.putExtra("useH5Title", true);
                        context.startActivity(intent);
                    } else {
                        if (!android.text.TextUtils.equals(dataList.get(position).getImg(), "error")) {
                            Intent intent1 = new Intent(context, NoticeDetailActivity.class);
                            intent1.putExtra("id", Integer.parseInt(dataList.get(position).getNewid()));
                            context.startActivity(intent1);
                        }
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList == null || dataList.isEmpty() ? 0 : dataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private View rootView;
        private ImageView imgView;

        public ViewHolder(View itemView) {
            super(itemView);
            this.rootView = itemView;
            this.imgView = itemView.findViewById(R.id.imgView);
        }
    }
}
