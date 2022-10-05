package huolongluo.byw.byw.ui.adapter;

import android.content.Context;
import android.view.View;

import java.util.List;

import huolongluo.byw.R;
import huolongluo.byw.byw.bean.WithdrawInfoBean;
import huolongluo.byw.superAdapter.list.BaseViewHolder;
import huolongluo.byw.superAdapter.list.SuperAdapter;

/**
 * Created by 火龙裸 on 2018/1/4.
 */

public class BankCardListAdapter extends SuperAdapter<WithdrawInfoBean>
{

    public  interface BankCardListAdapterListener{
       void onDelete(String id,int position);
    }

    private  BankCardListAdapterListener adapterListener;
    public BankCardListAdapter(Context context, List<WithdrawInfoBean> data, int layoutResId)
    {
        super(context, data, layoutResId);

    }


       public  void setAdapterListener(BankCardListAdapterListener adapterListener){
        this.adapterListener=adapterListener;
       }

    @Override
    protected void onBind(int viewType, BaseViewHolder holder, int position, WithdrawInfoBean item)
    {
        holder.setText(R.id.tv1, item.getBankNumber().trim().split("尾号")[0]);
//        holder.setText(R.id.tv2, "");
        holder.setText(R.id.tv3, "**** **** ****" + item.getBankNumber().trim().split("尾号")[1]);
        holder.setOnClickListener(R.id.delete_iv, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                     if(adapterListener!=null){
                         adapterListener.onDelete(item.getId()+"",position);
                     }
            }
        });

        /*if (item.getBankNumber().trim().split("尾号")[0].indexOf("建设") != -1) {
            holder.getView(R.id.iv).setBackgroundResource(R.mipmap.icon_bank_ccb);
        } else if (item.getBankNumber().trim().split("尾号")[0].indexOf("农业") != -1) {
            holder.getView(R.id.iv).setBackgroundResource(R.mipmap.icon_bank_abc);
        } else if (item.getBankNumber().trim().split("尾号")[0].indexOf("光大") != -1) {
            holder.getView(R.id.iv).setBackgroundResource(R.mipmap.icon_bank_ceb);
        } else if (item.getBankNumber().trim().split("尾号")[0].indexOf("工商") != -1) {
            holder.getView(R.id.iv).setBackgroundResource(R.mipmap.icon_bank_icbc);
        } else if (item.getBankNumber().trim().split("尾号")[0].indexOf("交通") != -1) {
            holder.getView(R.id.iv).setBackgroundResource(R.mipmap.icon_bank_comm);
        } else if (item.getBankNumber().trim().split("尾号")[0].indexOf("招商") != -1) {
            holder.getView(R.id.iv).setBackgroundResource(R.mipmap.icon_bank_cmb);
        } else if (item.getBankNumber().trim().split("尾号")[0].indexOf("邮政") != -1) {
            holder.getView(R.id.iv).setBackgroundResource(R.mipmap.icon_bank_psbc);
        } else if (item.getBankNumber().trim().split("尾号")[0].indexOf("中国银行") != -1) {
            holder.getView(R.id.iv).setBackgroundResource(R.mipmap.icon_bank_boc);
        } else if (item.getBankNumber().trim().split("尾号")[0].indexOf("民生") != -1) {
            holder.getView(R.id.iv).setBackgroundResource(R.mipmap.icon_bank_cmsb);
        } else if (item.getBankNumber().trim().split("尾号")[0].indexOf("兴业") != -1) {
            holder.getView(R.id.iv).setBackgroundResource(R.mipmap.icon_bank_cib);
        } else if (item.getBankNumber().trim().split("尾号")[0].indexOf("浦东") != -1) {
            holder.getView(R.id.iv).setBackgroundResource(R.mipmap.icon_bank_spdb);
        } else if (item.getBankNumber().trim().split("尾号")[0].indexOf("中信") != -1) {
            holder.getView(R.id.iv).setBackgroundResource(R.mipmap.icon_bank_cncb);
        } else if (item.getBankNumber().trim().split("尾号")[0].indexOf("平安") != -1) {
            holder.getView(R.id.iv).setBackgroundResource(R.mipmap.icon_bank_pab);
        } else if (item.getBankNumber().trim().split("尾号")[0].indexOf("华夏") != -1) {
            holder.getView(R.id.iv).setBackgroundResource(R.mipmap.icon_bank_hxb);
        } else if (item.getBankNumber().trim().split("尾号")[0].indexOf("其他银行") != -1) {
            holder.getView(R.id.iv).setBackgroundResource(R.mipmap.icon_bank_defalut);
        } else {
            holder.getView(R.id.iv).setBackgroundResource(R.mipmap.icon_bank_defalut);
        }*/
    }
}
