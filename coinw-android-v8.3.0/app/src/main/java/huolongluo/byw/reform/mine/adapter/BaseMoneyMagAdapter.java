package huolongluo.byw.reform.mine.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import huolongluo.byw.R;
import huolongluo.byw.reform.base.ByperpayBean;
import huolongluo.byw.reform.mine.click.CheckHppClick;

/**
 * Created by Administrator on 2019/1/6 0006.
 */

public class BaseMoneyMagAdapter extends RecyclerView.Adapter<BaseMoneyMagAdapter.ViewHole> {

    CheckHppClick checkHppClick;
    public void updata(List<ByperpayBean.LicaiBean> list){

    }

    @Override
    public ViewHole onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(ViewHole holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }


    public static class ViewHole extends  RecyclerView.ViewHolder{

        public ImageView logo_iv1;
        public TextView coin_name_tv;
        public TextView baifenbi_tv;
        public TextView miaoshu_tv;
        public TextView info_tv;
        public TextView huodong_tv;
        public LinearLayout main_view;


        public ViewHole(View itemView) {
            super(itemView);
            this.logo_iv1 = itemView.findViewById(R.id.logo_iv1);
            this.coin_name_tv = itemView.findViewById(R.id.coin_name_tv);
            this.baifenbi_tv = itemView.findViewById(R.id.baifenbi_tv);
            this.miaoshu_tv = itemView.findViewById(R.id.miaoshu_tv);
            this.info_tv = itemView.findViewById(R.id.info_tv);
            this.huodong_tv = itemView.findViewById(R.id.huodong_tv);
            this.main_view = itemView.findViewById(R.id.main_view);

        }
    }

}
