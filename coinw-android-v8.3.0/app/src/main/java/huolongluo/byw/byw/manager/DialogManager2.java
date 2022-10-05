package huolongluo.byw.byw.manager;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;

import com.android.tu.loadingdialog.LoadingDailog;

import huolongluo.byw.R;
import huolongluo.byw.view.CustomLoadingDialog;
import huolongluo.bywx.helper.AppHelper;
/**
 * 因为DialogManager使用在某些轮询或socket内导致异常关闭
 * 新增DialogManager2
 */
public enum DialogManager2 {
    INSTANCE;
    //    private SweetAlertDialog mDialog;
    //private LoadingDailog mDialog;
    private Dialog mDialog;
//    public void showWarningDialog(Context context, String title, String content, SweetAlertDialog.OnSweetClickListener listener) {
//        if (context == null) {
//            return;
//        } else if (context instanceof Activity) {
//            Activity atv = (Activity) context;
//            if (atv.isFinishing()) {
//                return;
//            }
//        }
//        releaseDialog(context);
//        LoadingDailog.Builder loadBuilder = new LoadingDailog.Builder(context)
//                .setMessage(context.getString(R.string.loading))
//                .setCancelable(true)
//                .setCancelOutside(true);
//        try {
//            mDialog = loadBuilder.create();
//            mDialog.show();
//        } catch (Throwable t) {
//            t.printStackTrace();
//        }
////        mDialog = new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE).setTitleText(title).setContentText(content).setConfirmText("确定")
////                .setCancelText("取消").setConfirmClickListener(listener);
////        mDialog.show();
//    }

    public void showErrorDialog(Context context, String title, String content) {
//        releaseDialog();
//        mDialog = new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE).setConfirmText("确定").setTitleText(title).setContentText(content)
//                .setConfirmClickListener(listener);
//        mDialog.show();
        if (context == null) {
            return;
        } else if (context instanceof Activity) {
            Activity atv = (Activity) context;
            if (atv.isFinishing()) {
                return;
            }
        }
        releaseDialog(context);
        LoadingDailog.Builder loadBuilder = new LoadingDailog.Builder(context)
                .setMessage(context.getString(R.string.loading))
                .setCancelable(true)
                .setCancelOutside(true);
        try {
            mDialog = loadBuilder.create();
            mDialog.show();
        } catch (Throwable t) {
            t.printStackTrace();
        }
//        Logger.getInstance().debug("DialogManager", "show", new Exception());
    }

    public void showProgressDialog(Context context, String message) {
        if (context == null) {
            return;
        } else if (context instanceof Activity) {
            Activity atv = (Activity) context;
            if (atv.isFinishing()) {
                return;
            }
        }
//        releaseDialog();
//        mDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
//        mDialog.getProgressHelper().setBarColor(context.getResources().getColor(R.color.text_414577));
//        mDialog.setTitleText(message);
//        mDialog.setCancelable(true);
//        mDialog.show();
        releaseDialog(context);
      /*  LoadingDailog.Builder loadBuilder=new LoadingDailog.Builder(context)
                .setMessage(message)
                .setCancelable(true)
                .setCancelOutside(false);
        mDialog =  loadBuilder.create();*/
        try {
            if (mDialog == null) {
                mDialog = CustomLoadingDialog.createLoadingDialog(context);
//                mDialog = LoadingDialog.createLoadingDialog(context);
            }
            mDialog.show();
            try {
//                mDialog.findViewById(R.id.dialog_view).setVisibility(View.VISIBLE);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
//        Logger.getInstance().debug("DialogManager", "show", new Exception());
    }

    //最新加载框
    public void showProgressDialog(Context context) {
        if (context == null) {
            return;
        } else if (context instanceof Activity) {
            Activity atv = (Activity) context;
            if (atv.isFinishing()) {
                return;
            }
        }
//        releaseDialog();
//        mDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
//        mDialog.getProgressHelper().setBarColor(context.getResources().getColor(R.color.text_414577));
//        mDialog.setTitleText(message);
//        mDialog.setCancelable(true);
//        mDialog.show();
        releaseDialog(context);
      /*  LoadingDailog.Builder loadBuilder=new LoadingDailog.Builder(context)
                .setMessage(message)
                .setCancelable(true)
                .setCancelOutside(false);
        mDialog =  loadBuilder.create();*/
        try {
            if (mDialog == null) {
                mDialog = CustomLoadingDialog.createLoadingDialog(context);
//                mDialog = LoadingDialog.createLoadingDialog(context);
            }
            mDialog.show();
        } catch (Throwable t) {
            t.printStackTrace();
        }
//        Logger.getInstance().debug("DialogManager", "show", new Exception());
    }

    public void showProgressDialog(Context context, String message, int progress) {
        if (context == null) {
            return;
        } else if (context instanceof Activity) {
            Activity atv = (Activity) context;
            if (atv.isFinishing()) {
                return;
            }
        }
//        mDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
//        mDialog.getProgressHelper().setBarColor(context.getResources().getColor(R.color.text_414577));
//        mDialog.setTitleText(message);
//        mDialog.setCancelable(true);
//        mDialog.getProgressHelper().setProgress(progress);
//        mDialog.show();
        releaseDialog(context);
       /* LoadingDailog.Builder loadBuilder=new LoadingDailog.Builder(context)
                .setMessage(context.getString(R.string.loading))
                .setCancelable(true)
                .setCancelOutside(true);
        mDialog =  loadBuilder.create();*/
        try {
            if (mDialog == null) {
                mDialog = CustomLoadingDialog.createLoadingDialog(context);
//                mDialog = LoadingDialog.createLoadingDialog(context);
            }
            mDialog.show();
        } catch (Throwable t) {
            t.printStackTrace();
        }
//        Logger.getInstance().debug("DialogManager", "show", new Exception());
    }

    private void releaseDialog(Context context) {
        AppHelper.dismissDialog(mDialog);
        mDialog = null;
    }

    public void dismiss() {
        AppHelper.dismissDialog(mDialog);
        mDialog = null;
//        Logger.getInstance().debug("DialogManager", "dismiss", new Exception());
    }
}
