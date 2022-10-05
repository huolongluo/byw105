package huolongluo.byw.byw.ui.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.mob.tools.utils.ResHelper;

import cn.baymax.android.keyboard.Utils;
import huolongluo.byw.R;
import huolongluo.byw.byw.bean.UserInfoBean;
import huolongluo.byw.util.Util;
import huolongluo.bywx.helper.AppHelper;

/**
 * Created by LS on 2018/8/6.
 */

public class VersionDialog {

    public AlertDialog dialog;
    Context context;
    DialogPositiveListener positiveListener;
    DialogNegativeListener negativeListener;
    DialogClickListener clickListener;
    private UserInfoBean userInfo;

    public VersionDialog(Context context) {
        super();
        this.context = context;
        this.userInfo = userInfo;
    }

   static VersionDialog instance;


    public static VersionDialog  getDefault(Context context){

        if(instance==null){

            synchronized (VersionDialog.class){
                if(instance==null){
                    instance=new VersionDialog(context);
                }
            }
        }
        return instance;
    }


    public void setPositiveListener(DialogPositiveListener positiveListener) {
        this.positiveListener = positiveListener;
    }

    public void setNegativeListener(DialogNegativeListener negativeListener) {
        this.negativeListener = negativeListener;
    }

    public void setClickListener(DialogClickListener clickListener){
        this.clickListener = clickListener;
    }
    /**
     * 该升级对话框部分逻辑是在checkVersionLib的UIActivity内，本来应该全部放在UIActivity内，但方便查询和阅读将布局和部分逻辑放于
     * 该处
     * */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public AlertDialog initDialog(String virsonName, String instructions) {

        AppHelper.dismissDialog(dialog);
        dialog = new AlertDialog.Builder(context).create();
        dialog.show();
        View view = LayoutInflater.from(context).inflate(
                R.layout.layout_version, null);

        TextView tvVersionName=view.findViewById(R.id.tv_version_name);
        TextView tvContent=view.findViewById(R.id.tv_msg);
        tvVersionName.setText("V"+virsonName);
        tvContent.setText(instructions);

        dialog.setCancelable(false);
        Window windows = dialog.getWindow();
        if (windows != null) {
            windows.setBackgroundDrawableResource(android.R.color.transparent);
        }
        dialog.setView(view);
        dialog.setContentView(view);
        return dialog;
    }

    /**
     * 升级中的进度对话框
     * */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public AlertDialog initDialogUpgrading() {

        AppHelper.dismissDialog(dialog);
        dialog = new AlertDialog.Builder(context).create();

        View view = LayoutInflater.from(context).inflate(
                R.layout.layout_version_progress, null);
        view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        dialog.setCancelable(false);
        dialog.setView(view);
        dialog.setContentView(view);
        return dialog;
    }

    public interface DialogPositiveListener {
        void onClick();
    }

    public interface DialogNegativeListener {
        void onClick();
    }
    public interface DialogClickListener {
        void onClick();
    }
}
