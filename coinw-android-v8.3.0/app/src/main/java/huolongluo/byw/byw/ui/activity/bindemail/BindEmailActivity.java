package huolongluo.byw.byw.ui.activity.bindemail;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.ViewModelProvider;

import com.android.legend.ui.viewmodel.BasicViewModel;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import huolongluo.byw.R;
import huolongluo.byw.byw.base.BaseActivity;
import huolongluo.byw.byw.bean.CommonBean;
import huolongluo.byw.log.Logger;

/**
 * Created by 火龙裸 on 2018/2/1.
 */
public class BindEmailActivity extends BaseActivity {
    @BindView(R.id.back_iv)
    ImageView back_iv;
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.et_email)
    EditText et_email;
    @BindView(R.id.et_email_show)
    TextView et_email_show;
    @BindView(R.id.btn_to_sure)
    TextView btn_to_sure;
    private Handler handler = new Handler();
    private BasicViewModel viewModel;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_bind_email;
    }

    @Override
    protected void injectDagger() {
        activityComponent().inject(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void initViewsAndEvents() {
        if (!getIntent().getBooleanExtra("isBindEmail", false)) {
            et_email_show.setVisibility(View.GONE);
        } else {
            et_email_show.setText(getIntent().getStringExtra("email"));
            et_email.setVisibility(View.GONE);
            btn_to_sure.setVisibility(View.GONE);
        }
        //由于服务器接口废弃，因此同步改动
        viewModel = new ViewModelProvider(this).get(BasicViewModel.class);
        viewModel.getBindEmail().observe(this, result -> bindEmailSucce(result.getData()));
        title_tv.setText(getString(R.string.bind_email));
        eventClick(back_iv).subscribe(o -> close(), throwable -> {
            Logger.getInstance().error(throwable);
        });
        eventClick(btn_to_sure).subscribe(o -> {
            String email = et_email.getText().toString().trim();
            if (isEmail(email)) {
                viewModel.bindEmail(email);
            } else {
                showMessage(getString(R.string.email_add_error), 1);
            }
        }, throwable -> {
            Logger.getInstance().error(throwable);
        });
    }

    public void bindEmailSucce(CommonBean response) {
        if (response.getCode() == 0) {
            showMessage(getString(R.string.bind_email_code), 1);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    close();
                }
            }, 1500L);
        } else {
            showMessage(response.getValue(), 1);
        }
    }

    public static boolean isEmail(String email) {
        if (null == email || "".equals(email)) {
            return false;
        }
        // Pattern p = Pattern.compile("\\w+@(\\w+.)+[a-z]{2,3}"); //简单匹配
        Pattern p = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
        Matcher m = p.matcher(email);
        return m.matches();
    }

    @Override
    public void finish() {
        if (handler != null) {
            try {
                handler.removeCallbacksAndMessages(null);
            } catch (Throwable t) {
                Logger.getInstance().error(t);
            }
        }
        super.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}
