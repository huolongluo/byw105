package huolongluo.byw.view;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import huolongluo.byw.R;

/**
 * Created by Administrator on 2018/10/8 0008.
 */

public class CustomLoadingDialog {
    public static Dialog createLoadingDialog(Context context) {


     //   LayoutInflater inflater = LayoutInflater.from(context);
      //  View v = inflater.inflate(R.layout.loading_views, null);
       // TextView textView=v.findViewById(R.id.textView);

//        Dialog loadingDialog = new Dialog(context, R.style.loading_dialog);
//       // loadingDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
//        loadingDialog.setCanceledOnTouchOutside(false);
//        loadingDialog.setContentView(R.layout.loading_views);
        CommLoadingDialog loadingDialog = new CommLoadingDialog(context,R.style.loading_dialog);
      //  TextView textView=  loadingDialog.findViewById(R.id.textView);
       // textView.setText(msg);
        return  loadingDialog;

    }


}
