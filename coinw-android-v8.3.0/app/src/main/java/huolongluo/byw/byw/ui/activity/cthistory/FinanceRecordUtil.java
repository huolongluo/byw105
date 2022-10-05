package huolongluo.byw.byw.ui.activity.cthistory;
import android.content.Context;
import android.content.Intent;

import huolongluo.byw.R;
import huolongluo.byw.model.FinanceRecordBean;
import huolongluo.byw.reform.mine.activity.FinanceRecordActivity;
import huolongluo.byw.util.Constant;
//财务记录工具
public class FinanceRecordUtil {

    public static void gotoDetail(Context context, FinanceRecordBean bean){
        Intent intent = new Intent(context, CHistoryDetailActivity.class);
        switch (bean.getQueryType()){
            case Constant.TYPE_FINANCE_RECORD_AIR:
                intent.putExtra("airdrop", 1);
                intent.putExtra("famount", bean.getNumber());
                intent.putExtra("logo", R.mipmap.kongtou + "");
                intent.putExtra("fshortName", bean.getCoinName());
                intent.putExtra("fstatus", bean.getStatus());
                intent.putExtra("fcreateTime", bean.getCreateTime());
                intent.putExtra("blockUrl", bean.getFremark());
                break;
            case Constant.TYPE_FINANCE_RECORD_FEE:
                intent.putExtra("airdrop", 2);
                intent.putExtra("famount", bean.getStatus());
                intent.putExtra("logo", R.mipmap.kongtou + "");
                intent.putExtra("fshortName", bean.getCoinName());
                intent.putExtra("fstatus", bean.getNumber());
                intent.putExtra("fcreateTime", bean.getCreateTime());
                intent.putExtra("blockUrl", context.getString(R.string.suc));
                break;
            case Constant.TYPE_FINANCE_RECORD_RECHARGE:
                intent.putExtra("sour", bean.getFtype());
                intent.putExtra("famount", bean.getNumber());
                intent.putExtra("logo", bean.getLogo());
                intent.putExtra("fshortName", bean.getCoinName());
                intent.putExtra("fstatus", bean.getStatus());
                intent.putExtra("recharge_virtual_address", bean.getRechargeAddress());//充值地址
                intent.putExtra("withdraw_virtual_address", bean.getWithdrawAddress());//提现地址
                intent.putExtra("txid", bean.getTxid());
                intent.putExtra("fcreateTime", bean.getCreateTime());
                intent.putExtra("blockUrl", bean.getBlockUrl());
                break;
            case Constant.TYPE_FINANCE_RECORD_MANAGE_MONEY:
                intent.putExtra("airdrop", 3);
                intent.putExtra("famount", bean.getStatus());
                intent.putExtra("logo", R.mipmap.ic_manage_money + "");
                intent.putExtra("fshortName", bean.getCoinName());
                intent.putExtra("fstatus", bean.getNumber());
                intent.putExtra("fcreateTime", bean.getCreateTime());
                intent.putExtra("blockUrl", "");
                break;
        }
        context.startActivity(intent);
    }
}
