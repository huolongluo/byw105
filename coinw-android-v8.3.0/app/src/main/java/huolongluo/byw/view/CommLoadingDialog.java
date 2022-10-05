package huolongluo.byw.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import huolongluo.byw.R;

public class CommLoadingDialog extends Dialog {
    private Animation anim1, anim2;

    public CommLoadingDialog(Context context, int themeResId) {
        super(context, themeResId);
        anim1 = AnimationUtils.loadAnimation(context, R.anim.comm_loading);
        anim2 = AnimationUtils.loadAnimation(context, R.anim.comm_loading2);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        setContentView(R.layout.loading_views);
        ImageView iv_loading = findViewById(R.id.iv_loading);
        ImageView iv_loading2 = findViewById(R.id.iv_loading2);
        iv_loading.startAnimation(anim1);
        iv_loading2.startAnimation(anim2);
    }

    @Override
    public void dismiss() {
        cancelAnimation(anim1);
        cancelAnimation(anim2);
        super.dismiss();
    }

    private void cancelAnimation(Animation anim) {
        if (anim == null) {
            return;
        }
        try {
            anim.cancel();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}