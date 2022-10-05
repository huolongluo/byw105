package huolongluo.byw.helper;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import huolongluo.byw.R;
import huolongluo.byw.byw.ui.activity.renzheng.RenZhengBeforeActivity;
import huolongluo.byw.user.UserInfoManager;
import huolongluo.byw.util.tip.DialogUtils;
public class IdentityVerifyHelper {
    private static IdentityVerifyHelper instance;

    public static IdentityVerifyHelper getInstance() {
        if (instance == null) {
            instance=new IdentityVerifyHelper();
        }
        return instance;
    }

    /**
     * 统一处理otc交易判断身份认证状态的逻辑
     * @param context
     * @return
     */
    public boolean isOtcIdentityOk(Context context){
        if(UserInfoManager.getUserInfo() != null && !UserInfoManager.getUserInfo().isHasC2Validate()){
            if(UserInfoManager.getUserInfo().isPostC2Validate()){
                DialogUtils.getInstance().showDialog(context, context.getString(R.string.kyc_other_info_review),
                        context.getString(R.string.b38), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                });
                return false;
            }
            DialogUtils.getInstance().showTwoButtonDialog(context, context.getString(R.string.as13),
                    context.getString(R.string.as2), context.getString(R.string.qq28));
            DialogUtils.getInstance().setOnclickListener(new DialogUtils.onBnClickListener() {
                @Override
                public void onLiftClick(AlertDialog dialog, View view) {
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                }

                @Override
                public void onRightClick(AlertDialog dialog, View view) {
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    Intent intent = new Intent(context, RenZhengBeforeActivity.class);
                    context.startActivity(intent);
                }
            });
            return false;
        }
        return true;
    }
}
