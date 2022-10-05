package huolongluo.byw.view;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import huolongluo.byw.R;

public class LoadingDialog {
//    Dialog mLoadingDialog;

    public static Dialog createLoadingDialog(Context context) {
//        // 获取视图
//        View view = LayoutInflater.from(context).inflate(R.layout.loading_views, null);
//        // 获取整个布局
//        RelativeLayout layout = view.findViewById(R.id.dialog_view);
//        ImageView gifView = view.findViewById(R.id.giv_loading);
//        Glide.with(context).load(R.drawable.loading_gif).asGif().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(gifView);
//        Dialog mLoadingDialog = new Dialog(context, R.style.loading_dialog);
//        // 设置返回键无效
//        mLoadingDialog.setCancelable(false);
//        mLoadingDialog.setContentView(layout, new RelativeLayout.LayoutParams(
//                RelativeLayout.LayoutParams.MATCH_PARENT,
//                RelativeLayout.LayoutParams.MATCH_PARENT));


        Dialog loadingDialog = new Dialog(context, R.style.loading_dialog);
        // loadingDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        loadingDialog.setCanceledOnTouchOutside(false);
        loadingDialog.setContentView(R.layout.loading_views);
        // 获取整个布局
//        RelativeLayout layout = loadingDialog.findViewById(R.id.dialog_view);
//        ImageView gifView = loadingDialog.findViewById(R.id.giv_loading);
//        Glide.with(context).load(R.drawable.loading_gif).asGif().placeholder(R.drawable.loading_gif3).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(gifView);

        return loadingDialog;
    }
//    public void show() {
//        mLoadingDialog.show();
//    }
//
//    public void close() {
//        if (mLoadingDialog != null) {
//            mLoadingDialog.dismiss();
//            mLoadingDialog = null;
//        }
//    }
}
