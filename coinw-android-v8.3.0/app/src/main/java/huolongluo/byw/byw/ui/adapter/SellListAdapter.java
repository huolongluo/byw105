package huolongluo.byw.byw.ui.adapter;

import android.content.Context;
import android.widget.ProgressBar;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import huolongluo.byw.R;
import huolongluo.byw.byw.bean.SellDepthBean;
import huolongluo.byw.superAdapter.recycler.BaseViewHolder;
import huolongluo.byw.superAdapter.recycler.SuperAdapter;

/**
 * <p>
 * Created by 火龙裸 on 2017/12/24 0024.
 */

public class SellListAdapter extends SuperAdapter<SellDepthBean>
{

    StringBuffer sfcount1; // 单价
    StringBuffer sfcount2; // 数量

    DecimalFormat fnumcount1;
    DecimalFormat fnumcount2;
    protected double mMax = 0d;
    ProgressBar progressBar;
    private int Fcount1;
    private int Fcount2;
    private List<SellDepthBean> sellDepthBeen = new ArrayList<>();

    public SellListAdapter(Context context, List<SellDepthBean> list, int layoutResId,int fcount1,int fcount2,boolean isDefault){
        super(context, list, layoutResId);

        this.isDefault=isDefault;
    }

    public SellListAdapter(Context context, List<SellDepthBean> list, int layoutResId,int fcount1,int fcount2)
    {
        super(context, list, layoutResId);

        this.Fcount1 = fcount1;
        this.Fcount2 = fcount2;
        try
        {
            sfcount1 = new StringBuffer();
            sfcount2 = new StringBuffer();
            sfcount1.append("0");

            if(Fcount1>0){
                sfcount1.append(".");
            }
            for (int i = 0; i < Fcount1; i++)
            {
                sfcount1.append("0");
            }

            sfcount2.append("0");
            if(Fcount2>0){
                sfcount2.append(".");
            }
            for (int i = 0; i < Fcount2; i++)
            {
                sfcount2.append("0");
            }
        }
        catch (Exception e)
        {

        }

        fnumcount1 = new DecimalFormat(sfcount1.toString());
        fnumcount2 = new DecimalFormat(sfcount2.toString());

    }

    @Override
    public int getItemCount()
    {
        if(isDefault){
            return 10;
        }

        return mList == null ? 0 : mList.size();
    }

    public boolean isDefault=false;

    public void resertView(int fcount1, int fcount2){
        this.Fcount1 = fcount1;
        this.Fcount2 = fcount2;
        try
        {
            sfcount1 = new StringBuffer();
            sfcount2 = new StringBuffer();
            sfcount1.append("0");

            if(Fcount1>0){
                sfcount1.append(".");
            }
            for (int i = 0; i < Fcount1; i++)
            {
                sfcount1.append("0");
            }

            sfcount2.append("0");
            if(Fcount2>0){
                sfcount2.append(".");
            }
            for (int i = 0; i < Fcount2; i++)
            {
                sfcount2.append("0");
            }
        }
        catch (Exception e)
        {

        }
        fnumcount1 = new DecimalFormat(sfcount1.toString());
        fnumcount2 = new DecimalFormat(sfcount2.toString());
      //  fnumcount1.setRoundingMode(RoundingMode.DOWN);
       // fnumcount2.setRoundingMode(RoundingMode.DOWN);

    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        if(isDefault){
            onBind(getItemViewType(position), holder, position, null);
        }else {
            onBind(getItemViewType(position), holder, position, mList.get(position));
        }

    }
    @Override
    public int getItemViewType(int position)
    {
        if (mMultiItemViewType != null&&mList.size()>position)
        {
            return mMultiItemViewType.getItemViewType(position, mList.get(position));
        }
        return 0;
    }
    @Override
    public void onBind(int viewType, BaseViewHolder holder, int position, SellDepthBean item)
    {

        holder.setText(R.id.btb_c_user, mContext.getString(R.string.i7) + (position + 1));
              if(item!=null){


        if(Fcount2!=0){
            holder.setText(R.id.btb_number, fnumcount2.format(Float.valueOf(item.getAmount())));
        }else {
            holder.setText(R.id.btb_number, item.getAmount());
        }
        holder.setText(R.id.btb_price, fnumcount1.format(Float.valueOf(item.getPrice())));



        progressBar = holder.getView(R.id.progressbar);
//        for (int i = 0;i<5;i++){
//            total = total+ Float.valueOf(mData.get(i).getAmount());
//            Log.d("交易深度",mData.size()+"-->"+i+"-->"+total+"-->" +mData.get(i).getAmount());
        progressBar.setProgress(calculateProgress(item));
              }else {
                  holder.setText(R.id.btb_number, "——");
                  holder.setText(R.id.btb_price, "——");
              }
    }
    private int calculateProgress(SellDepthBean data) {
        if (mMax <= 0) {
            return 3;
        }

        double progress = Double.valueOf(data.getAmount()) * 100 / mMax;
        if (progress <= 3) {
            return 3;
        }

        return (int) progress;
    }

    public void setMax(double max) {
        this.mMax = max;
    }
}
