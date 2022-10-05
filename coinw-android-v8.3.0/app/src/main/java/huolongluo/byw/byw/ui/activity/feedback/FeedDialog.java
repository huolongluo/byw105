package huolongluo.byw.byw.ui.activity.feedback;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

import huolongluo.byw.R;
import huolongluo.byw.byw.ui.dialog.ResultDialog;
import huolongluo.bywx.helper.AppHelper;

/**
 * Created by LS on 2018/7/15.
 */
public class FeedDialog {

    public Dialog dialog;
    Context context;
    DialogPositiveListener positiveListener;
    DialogNegativeListener negativeListener;
    private List<FeedList> mData;
    public OnItemClickListener itemClickListener;

    public FeedDialog(Context context, List<FeedList> lists) {
        super();
        this.context = context;
        this.mData = lists;
    }

    public void setItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void setPositiveListener(DialogPositiveListener positiveListener) {
        this.positiveListener = positiveListener;
    }

    public void setNegativeListener(DialogNegativeListener negativeListener) {
        this.negativeListener = negativeListener;
    }

    public Dialog initDialog() {
        View view = LayoutInflater.from(context).inflate(
                R.layout.layout_feed, null);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        dialog = ResultDialog.creatAlertDialog(context, view);
        final ListView listView = view.findViewById(R.id.lv_content);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                itemClickListener.onItem(i);
                AppHelper.dismissDialog(dialog);
            }
        });
        FeedAdapter mAdapter = new FeedAdapter(context, mData);
        listView.setAdapter(mAdapter);
        View vMain = view.findViewById(R.id.v_main);
        vMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppHelper.dismissDialog(dialog);
            }
        });
        return dialog;
    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int tag = (int) v.getTag();
            if (positiveListener != null) {
                positiveListener.onClick(tag);
            }
            AppHelper.dismissDialog(dialog);
        }
    };

    public interface DialogPositiveListener {

        void onClick(int tag);
    }

    public interface DialogNegativeListener {

        void onClick(int tag);
    }

    public interface DialogClickListener {

        void onClick();
    }

    public interface OnItemClickListener {

        void onItem(int i);
    }
}
