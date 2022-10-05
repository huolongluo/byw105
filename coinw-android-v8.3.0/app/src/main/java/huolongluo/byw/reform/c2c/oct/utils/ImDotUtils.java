package huolongluo.byw.reform.c2c.oct.utils;

import android.view.View;
import android.widget.TextView;

import com.netease.nim.uikit.business.session.fragment.IMMessageEntity;
import com.netease.nimlib.sdk.msg.model.IMMessage;

public class ImDotUtils {
    public static void showDot(TextView dot, String orderNo1) {
        int count = 0;
        for (IMMessage imMessage : IMMessageEntity.getMessages()) {
            if (imMessage == null || imMessage.getRemoteExtension() == null) {
                continue;
            }
            if (null != imMessage.getRemoteExtension().get("orderNo")) {
                String orderNo = imMessage.getRemoteExtension().get("orderNo").toString();
                if (orderNo.equals(orderNo1)) {
                    count = ++count;
                }
            }
        }
        if (count == 0) {
            dot.setVisibility(View.GONE);
        } else {
            dot.setVisibility(View.VISIBLE);
            dot.setText(String.valueOf(count));
        }
    }

}
