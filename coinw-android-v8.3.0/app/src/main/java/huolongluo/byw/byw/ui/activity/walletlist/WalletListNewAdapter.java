package huolongluo.byw.byw.ui.activity.walletlist;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.legend.common.view.textview.DinproMediumTextView;

import java.util.ArrayList;
import java.util.List;

import huolongluo.byw.R;
import huolongluo.byw.byw.bean.WithdrawChainBean;

/**
 * Created by LS on 2018/7/12.
 */
public class WalletListNewAdapter extends BaseAdapter {

    private Context context;
    private List<WithdrawChainBean> mData = new ArrayList<>();
    private OperationClickListener clickListener = null;
    private OperationClickListener1 clickListener1 = null;
    private OperationClickListener2 clickListener2 = null;

    public WalletListNewAdapter(Context context, List<WithdrawChainBean> mData, OperationClickListener clickListener,
                                OperationClickListener1 clickListener1, OperationClickListener2 clickListener2) {
        this.context = context;
        if (mData != null) {
            this.mData = mData;
        }
        //this.mData = mData;
        this.clickListener = clickListener;
        this.clickListener1 = clickListener1;
        this.clickListener2 = clickListener2;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int i) {
        if (i < 0 || mData == null || mData.isEmpty()) {
            return null;
        }
        return mData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if (view == null) {
            //  view = LayoutInflater.from(context).inflate(R.layout.activity_address_manage,null);
            view = LayoutInflater.from(context).inflate(R.layout.activity_newwallet_list_item, null);
            holder = new ViewHolder();
            holder.tv_title = view.findViewById(R.id.tv_title);
            holder.tv_add = view.findViewById(R.id.tv_add);
            holder.ll_edit = view.findViewById(R.id.ll_edit);
            holder.delete_right = view.findViewById(R.id.delete_right);
            holder.swipe_menu_layout = view.findViewById(R.id.swipe_menu_layout);
            holder.chain_name = view.findViewById(R.id.chain_name);
            holder.chain_name_ll = view.findViewById(R.id.chain_name_ll);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        WithdrawChainBean listBean = mData.get(i);
        if (listBean != null) {
            if (TextUtils.isEmpty(listBean.getChainName())) {
                holder.chain_name_ll.setVisibility(View.GONE);
            } else {
                holder.chain_name_ll.setVisibility(View.VISIBLE);
            }
            holder.chain_name.setText(listBean.getChainName());
            holder.tv_title.setText(listBean.getRemark());
            holder.tv_add.setText(listBean.getAddress());
            holder.swipe_menu_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (clickListener2 != null) {
                        clickListener2.edit1((int) view.getTag());
                    }
                }
            });
        }
        //修改地址备注
        holder.ll_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickListener1 != null) {
                    clickListener1.edit((int) view.getTag());
                }
            }
        });
        //删除提币地址
        int finalJ = i;
        holder.delete_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickListener != null) {
                    clickListener.delete((int) view.getTag());
                }
            }
        });
        holder.delete_right.setTag(i);
        holder.ll_edit.setTag(i);
        holder.swipe_menu_layout.setTag(i);
        return view;
    }

    static class ViewHolder {
        LinearLayout chain_name_ll;
        DinproMediumTextView chain_name;
        DinproMediumTextView tv_title;
        DinproMediumTextView tv_add;
        ImageView ll_edit;
        ImageView delete_right;
        LinearLayout swipe_menu_layout;
    }

    public interface OperationClickListener {

        void delete(int position);
    }

    public interface OperationClickListener1 {

        void edit(int position);
    }

    public interface OperationClickListener2 {

        void edit1(int position);
    }
}
