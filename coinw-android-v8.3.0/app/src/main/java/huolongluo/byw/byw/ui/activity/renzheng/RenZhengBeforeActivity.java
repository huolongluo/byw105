package huolongluo.byw.byw.ui.activity.renzheng;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.legend.common.util.StatusBarUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import huolongluo.byw.R;
import huolongluo.byw.log.Logger;
import huolongluo.byw.reform.safecenter.RenzhengActivity;
import huolongluo.byw.util.FastClickUtils;
import huolongluo.bywx.utils.PermissionUtils;
import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

/**
 * Created by Administrator on 2019/1/3 0003.
 * 选择国内还是其他地区的身份认证页面
 */

public class RenZhengBeforeActivity extends SwipeBackActivity {

    Unbinder unbinder;


    @BindView(R.id.iv_1)
    ImageView iv_1;
    @BindView(R.id.rl_1)
    RelativeLayout rl_1;

    @BindView(R.id.iv_2)
    ImageView iv_2;
    @BindView(R.id.rl_2)
    RelativeLayout rl_2;

    @BindView(R.id.next_bn)
    Button next_bn;

    @BindView(R.id.back_iv)
    ImageButton back_iv;

    @BindView(R.id.title_tv)
    TextView title_tv;
    // 请求状态码
    private static int REQUEST_PERMISSION_CODE = 1;

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(unbinder!=null){
            unbinder.unbind();
        }
    }
    private SwipeBackLayout mSwipeBackLayout;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置状态栏背景及字体颜色
        StatusBarUtils.setStatusBar(this);
        setContentView(R.layout.activity_renzheng_before);

        unbinder = ButterKnife.bind(this);


        mSwipeBackLayout = getSwipeBackLayout();

        mSwipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);
        mSwipeBackLayout.setEdgeSize(200);//
        back_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        title_tv.setText(getString(R.string.identity1));
        next_bn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                if (FastClickUtils.isFastClick(1000)) {
                    return;
                }

                if(iv_1.getVisibility()==View.GONE){

                    Intent intent=new Intent(RenZhengBeforeActivity.this, RenzhengActivity.class);
                    intent.putExtra("cardId","unknow");

                    startActivity(intent);
                }else {
                    startActivity(new Intent(RenZhengBeforeActivity.this,RenZhengGetTokenActivity .class));

                }

            }
        });
        rl_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rl_1.setBackgroundResource(R.drawable.renzheng_bg1);
                rl_2.setBackgroundResource(R.drawable.renzheng_bg2);
                iv_1.setVisibility(View.VISIBLE);
                iv_2.setVisibility(View.GONE);
            }
        });
        rl_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rl_2.setBackgroundResource(R.drawable.renzheng_bg1);
                rl_1.setBackgroundResource(R.drawable.renzheng_bg2);
                iv_2.setVisibility(View.VISIBLE);
                iv_1.setVisibility(View.GONE);
            }
        });
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, PermissionUtils.CAMERA_NEED_PERMISSIONS, REQUEST_PERMISSION_CODE);
    }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE) {
            for (int i = 0; i < permissions.length; i++) {
                Logger.getInstance().debug("RZB", "申请的权限为：" + permissions[i] + ",申请结果：" + grantResults[i]);
            }
        }
    }
}
