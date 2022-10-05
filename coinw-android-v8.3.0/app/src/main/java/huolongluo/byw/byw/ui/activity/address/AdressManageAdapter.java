package huolongluo.byw.byw.ui.activity.address;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.legend.common.view.textview.DinproMediumTextView;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import huolongluo.byw.R;
import huolongluo.byw.byw.net.UrlConstants;
import huolongluo.byw.byw.share.Share;
import huolongluo.byw.util.OkhttpManager;
import okhttp3.Request;

/**
 * Created by LS on 2018/7/11.
 */
public class AdressManageAdapter extends BaseAdapter {

    private Context context;
    private List<AddressListBean> mData = new ArrayList<>();
    private OperationClickListener clickListener = null;
    private OperationClickListener1 clickListener1 = null;
    private OperationClickListener2 clickListener2 = null;

    public AdressManageAdapter(Context context, List<AddressListBean> list, OperationClickListener listener, OperationClickListener1 listener1, OperationClickListener2 listener2) {
        this.context = context;
        mData.clear();
        this.mData.addAll(list);
        this.clickListener = listener;
        this.clickListener1 = listener1;
        this.clickListener2 = listener2;
    }

    public void FreshData(List<AddressListBean> list) {
        mData.clear();
        mData.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public AddressListBean getItem(int i) {
        if (i < 0 || mData == null || mData.isEmpty()) {
            return null;
        }
        return mData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if (view == null) {
            // view = LayoutInflater.from(context).inflate(R.layout.address_manage_item,null);
            view = LayoutInflater.from(context).inflate(R.layout.address_item, null);
            holder = new ViewHolder();
            holder.tv_name = view.findViewById(R.id.tv_name);
            holder.num_tv = view.findViewById(R.id.num_tv);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        AddressListBean listBean = mData.get(i);
        if (listBean != null) {
            holder.tv_name.setText(listBean.getShortName());
            if (listBean.getCoinAddress().size() == 0) {
                holder.num_tv.setVisibility(View.INVISIBLE);
            } else {
                holder.num_tv.setVisibility(View.VISIBLE);
                holder.num_tv.setText(listBean.getCoinAddress().size() + "");
            }
            // List<AddressList1Bean> list1Beans = listBean.getAddress();
            // holder.llMain.removeAllViews();
           /* for (int j = 0;j<list1Beans.size();j++){
                View view1 = LayoutInflater.from(context).inflate(R.layout.activity_address_manage,null);
                TextView tv_title = view1.findViewById(R.id.tv_title);
                TextView tv_add = view1.findViewById(R.id.tv_add);
                LinearLayout ll_edit = view1.findViewById(R.id.ll_edit);
                LinearLayout ll_edit1 = view1.findViewById(R.id.ll_edit1);
                LinearLayout delete_right= view1.findViewById(R.id.delete_right);

                //修改地址备注
                ll_edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (clickListener1 != null){
                            clickListener1.edit((int) view.getTag(),i);
                        }
                    }
                });
                ll_edit1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (clickListener2 != null){
                            clickListener2.edit1((int) view.getTag(),i);
                        }
                    }
                });

                //删除提币地址
                int finalJ = j;
                delete_right.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (clickListener != null){
                            clickListener.delete((int) view.getTag(),i);
                        }
                    }
                });

                tv_title.setText(list1Beans.get(j).getRemark());
                tv_add.setText(list1Beans.get(j).getAddress());

                delete_right.setTag(j);
                ll_edit.setTag(j);
                ll_edit1.setTag(j);

                holder.llMain.addView(view1);
            }*/
        }
        return view;
    }

    static class ViewHolder {

        private TextView num_tv;
        private DinproMediumTextView tv_name;
    }

    public interface OperationClickListener {

        void delete(int position, int i);
    }

    public interface OperationClickListener1 {

        void edit(int position, int i);
    }

    public interface OperationClickListener2 {

        void edit1(int position, int i);
    }
}
