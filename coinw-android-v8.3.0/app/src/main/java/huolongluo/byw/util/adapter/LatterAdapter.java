package huolongluo.byw.util.adapter;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import huolongluo.byw.R;
/**
 * 共用的abc排序的 adapter
 */
public class LatterAdapter extends BaseAdapter {

    int position;
    List<String> lettterList = new ArrayList<>();

    @Override
    public int getCount() {
        return lettterList.size();
    }

    public void setLettterList(List<String> lettterList) {
        this.lettterList.clear();
        this.lettterList.addAll(lettterList);
        notifyDataSetChanged();
    }

    @Override
    public String getItem(int position) {
        if (position < 0 || lettterList == null || lettterList.isEmpty()) {
            return "";
        }
        return lettterList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(parent.getContext(), R.layout.tixian_list_item1, null);
        }
        TextView textView = convertView.findViewById(R.id.textView);
        textView.setText(lettterList.get(position));
        if (this.position == position) {
            textView.setBackgroundResource(R.drawable.tixian_dot);
            textView.setTextColor(Color.WHITE);
        } else {
            textView.setBackgroundColor(parent.getContext().getResources().getColor(R.color.transparent));
            textView.setTextColor(parent.getContext().getResources().getColor(R.color.ff262046));
        }
        return convertView;
    }

    public void setPosition(int posotion) {
        position = posotion;
    }
}