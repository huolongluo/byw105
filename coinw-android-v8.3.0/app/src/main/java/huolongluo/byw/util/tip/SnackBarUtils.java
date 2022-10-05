package huolongluo.byw.util.tip;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.trycatch.mysnackbar.Prompt;
import com.trycatch.mysnackbar.TSnackbar;

import huolongluo.byw.R;
import huolongluo.byw.log.Logger;
import huolongluo.byw.util.Util;

/**
 * Created by Administrator on 2018/9/25 0025.
 */
public class SnackBarUtils {
    public static void ShowBlue(Activity atv, String text) {
        if (atv == null || TextUtils.isEmpty(text)) {
            return;
        }
        if(!Util.isShowErrorMsg(text)){//停机维护屏蔽某些文案
            return;
        }
        final ViewGroup viewGroup = (ViewGroup) atv.findViewById(android.R.id.content).getRootView();
        TSnackbar snackBar = TSnackbar.make(viewGroup, text, TSnackbar.LENGTH_SHORT, TSnackbar.APPEAR_FROM_TOP_TO_DOWN);
        snackBar.setAction(atv.getString(R.string.cancle), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        }, com.trycatch.mysnackbar.R.drawable.h_line);
        snackBar.setPromptThemBackground(Prompt.SUCCESS);
        snackBar.show();
    }

    public static void ShowRed(Activity atv, String text) {
        Logger.getInstance().debug("BarUtils", "text: " + text, new Exception());
        if (atv == null || TextUtils.isEmpty(text)) {
            return;
        }
        if(!Util.isShowErrorMsg(text)){//停机维护屏蔽某些文案
            return;
        }
        final ViewGroup viewGroup = (ViewGroup) atv.findViewById(android.R.id.content).getRootView();
        TSnackbar snackBar = TSnackbar.make(viewGroup, text, TSnackbar.LENGTH_SHORT, TSnackbar.APPEAR_FROM_TOP_TO_DOWN);
        snackBar.setAction(atv.getString(R.string.cancle), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        }, com.trycatch.mysnackbar.R.drawable.h_line1);
        snackBar.setPromptThemBackground(Prompt.WARNING);

        snackBar.show();
    }
}
