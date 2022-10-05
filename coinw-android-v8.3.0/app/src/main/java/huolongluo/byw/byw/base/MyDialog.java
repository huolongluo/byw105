package huolongluo.byw.byw.base;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.appcompat.app.AlertDialog;

/**
 * Created by LS on 2018/7/21.
 */

public class MyDialog {
    private AlertDialog dialog;
    private Context context;
    private static huolongluo.byw.byw.base.MyDialog MyDialog = null;
    private View rootView;

    public static huolongluo.byw.byw.base.MyDialog getInstance() {
        if (MyDialog == null) {
            synchronized (huolongluo.byw.byw.base.MyDialog.class) {
                if (MyDialog == null) {
                    MyDialog = new MyDialog();
                }
            }
        }

        return MyDialog;
    }

    private MyDialog() {
    }


    public huolongluo.byw.byw.base.MyDialog init(Context context, @LayoutRes int resId) {
        this.context = context;
        rootView = LayoutInflater.from(context).inflate(resId, null);
        dialog = new AlertDialog.Builder(context).create();
        Window windows = dialog.getWindow();
        if (windows != null) {
            windows.setBackgroundDrawableResource(android.R.color.transparent);
        }
//        windows.setContentView(rootView);
        dialog.setView(rootView);
        dialog.show();
        // dialog.setContentView(rootView);
        return this;
    }


    public huolongluo.byw.byw.base.MyDialog setPositiveButton(String str, final OnClickListener listener) {
//        Button button = (Button) rootView.findViewById(R.id.btn1);
//        button.setText(str);
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (listener == null) {
//                    dialog.dismiss();
//                } else {
//                    listener.OnClick(v);
//                    dialog.dismiss();
//                }
//            }
//        });
        return this;
    }

    public huolongluo.byw.byw.base.MyDialog setCancelButton(String str, final OnClickListener listener) {
//        Button button = (Button) rootView.findViewById(R.id.btn2);
//        button.setText(str);
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (listener == null) {
//                    dialog.dismiss();
//                } else {
//                    listener.OnClick(v);
//                    dialog.dismiss();
//                }
//            }
//        });
        return this;
    }


    public View getView(@IdRes int resId) {
        return rootView.findViewById(resId);
    }

    public interface OnClickListener {
        void OnClick(View view);
    }
}
