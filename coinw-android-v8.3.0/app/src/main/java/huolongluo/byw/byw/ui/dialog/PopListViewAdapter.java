package huolongluo.byw.byw.ui.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import huolongluo.byw.R;

/**
 * Created by LS on 2018/7/10.
 */

public class PopListViewAdapter extends BaseAdapter{
    private Context context;
    private ArrayList<String> mData;

    public PopListViewAdapter(Context context, ArrayList<String> mData) {
        this.context = context;
        this.mData = mData;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = LayoutInflater.from(context).inflate(R.layout.popupwindow_item,null);
        TextView textView = view.findViewById(R.id.tv_name);
        textView.setText(mData.get(i));
        return view;
    }
}
