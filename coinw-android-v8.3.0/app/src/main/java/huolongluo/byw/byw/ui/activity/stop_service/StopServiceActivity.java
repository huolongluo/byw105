package huolongluo.byw.byw.ui.activity.stop_service;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.blankj.utilcodes.utils.ToastUtils;
import com.bumptech.glide.Glide;
import com.legend.modular_contract_sdk.coinw.CoinwHyUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import huolongluo.byw.R;
import huolongluo.byw.byw.bean.ServiceStopBean;
import huolongluo.byw.byw.ui.activity.main.MainActivity;
import huolongluo.byw.log.Logger;
import huolongluo.byw.util.ClipboardUtils;

public class StopServiceActivity extends AppCompatActivity {
    private static final String TAG = "StopServiceActivity";
    public static boolean isStopServiceActivityOn=false;

    @BindView(R.id.time)
    TextView time;
    @BindView(R.id.tv_hy_des)
    TextView tvHyDes;
    @BindView(R.id.tv_hy_go_trade)
    TextView tvHyGoTrade;
    @BindView(R.id.wx_des)
    TextView wxDes;
    @BindView(R.id.tv_wx_copy)
    TextView tvWxCopy;
    private ServiceStopBean.DataBean data;
    @BindView(R.id.code_img)
    ImageView code_img;
    @BindView(R.id.des)
    TextView des;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.tv_weChat)
    TextView tv_weChat;
    private CountDownTimer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.getInstance().debug(TAG,"onCreate");
        setContentView(R.layout.activity_stop_service);
        CoinwHyUtils.isServiceStop = true;
        isStopServiceActivityOn=true;
        ButterKnife.bind(this);
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Logger.getInstance().debug(TAG,"onResume");
    }

    private void initData() {
        Intent intent = getIntent();
        if (null != intent && null != intent.getSerializableExtra("data")) {
            data = (ServiceStopBean.DataBean) intent.getSerializableExtra("data");
            if(data==null){
                return;
            }
            countDown(data.getCountDown());
            try {
                Glide.with(this).load(data.getCodeUrl()).into(code_img);
                des.setText(data.getContent());
                title.setText(data.getTitle());
                tv_weChat.setText(data.getWechatNum());
                tvHyDes.setText(data.getOuterContent());
            }catch (Exception e){
                e.printStackTrace();
            }

        }
        if(MainActivity.self!=null){
            MainActivity.self.gotoSwap("");
        }
//        new Timer().schedule(new TimerTask() {
//            @Override
//            public void run() {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//
//                    }
//                });
//            }
//        },1000);
    }

    /**
     * 倒计时显示
     */
    private void countDown(long times) {

        timer = new CountDownTimer(times * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                time.setText(getDate(millisUntilFinished));
            }

            @Override
            public void onFinish() {

            }
        }.start();


    }

    @Override
    public void onBackPressed() {
        Logger.getInstance().debug(TAG,"onBackPressed");
    }

    @Override
    protected void onPause() {
        super.onPause();
        isStopServiceActivityOn=false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Logger.getInstance().debug(TAG,"onDestroy");
        if (null != time) {
            timer.cancel();
            timer=null;
        }
    }

    public static String getDate(long date) {
        date = date / 1000;
        long h = date / 3600;
        long m = (date % 3600) / 60;
        long s = (date % 3600) % 60;
        return (h < 10 ? "0" + h : h) + ":" + (m < 10 ? "0" + m : m) + ":" + (s < 10 ? "0" + s : s);
    }

    @OnClick({R.id.tv_hy_go_trade, R.id.tv_wx_copy})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_hy_go_trade:
                Intent intent=new Intent(StopServiceActivity.this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_wx_copy:
                ClipboardUtils.copyText(StopServiceActivity.this,tv_weChat.getText().toString());
                tvWxCopy.setText(getResources().getString(R.string.stop_copied));
                ToastUtils.showShortToast(R.string.stop_copied);
                break;
        }
    }
}
