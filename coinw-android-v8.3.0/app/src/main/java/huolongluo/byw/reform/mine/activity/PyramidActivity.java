package huolongluo.byw.reform.mine.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import huolongluo.byw.R;
import huolongluo.byw.byw.net.UrlConstants;
import huolongluo.byw.reform.base.BaseActivity;
import huolongluo.byw.reform.mine.bean.BounsBean;
import huolongluo.byw.reform.mine.fragment.PyramidFriendsFragment;
import huolongluo.byw.reform.mine.fragment.PyramidMoneyFragment;
import huolongluo.byw.user.UserInfoManager;
import huolongluo.byw.util.CreateQRImage;
import huolongluo.byw.util.OkhttpManager;
import huolongluo.byw.util.noru.NorUtils;
import huolongluo.byw.util.tip.MToast;
import huolongluo.byw.view.NoScrollViewPager;
import okhttp3.Request;

/**
 * Created by Administrator on 2019/1/22 0022.
 */

public class PyramidActivity extends BaseActivity implements View.OnClickListener {


    private Unbinder unbinder;


    @BindView(R.id.viewPager)
    NoScrollViewPager viewPager;
    @BindView(R.id.cope_tv)
    TextView cope_tv;


    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.qr_code_iv)
    ImageView qr_code_iv;
    private CreateQRImage mCreateQRImage;
    TextView uid_tv;

    @BindView(R.id.friend_rl)
    RelativeLayout friend_rl;
    @BindView(R.id.money_rl)
    RelativeLayout money_rl;

    @BindView(R.id.text1_tv)
    TextView text1_tv;
    @BindView(R.id.text2_tv)
    TextView text2_tv;
    @BindView(R.id.text3_tv)
    TextView text3_tv;
    @BindView(R.id.text4_tv)
    TextView text4_tv;
    @BindView(R.id.imageview1)
    ImageView imageview1;
    @BindView(R.id.imageview2)
    ImageView imageview2;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_pyramid);
        unbinder = ButterKnife.bind(this);

        friend_rl.setOnClickListener(this);
        money_rl.setOnClickListener(this);
        cope_tv.setOnClickListener(this);


        findViewById(R.id.back_iv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        title_tv.setText(getResources().getString(R.string.b1));

        uid_tv = findViewById(R.id.uid_tv);


        uid_tv.setText(UserInfoManager.getUserInfo().getFid() + "");
        mCreateQRImage = new CreateQRImage();

        viewPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {


            Fragment[] fragments = new Fragment[]{new PyramidFriendsFragment(), new PyramidMoneyFragment()};


            @Override
            public Fragment getItem(int position) {

                if (position == 0) {
                    return fragments[0];
                } else {
                    return fragments[1];
                }


            }

            @Override
            public int getCount() {
                return 2;
            }
        });
        viewPager.setScroll(false);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                changeTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mydivide();
    }

    @Override
    public void onClick(View v) {


        switch (v.getId()) {

            case R.id.friend_rl:
                changeTab(0);
                break;
            case R.id.money_rl:
                changeTab(1);
                break;

            case R.id.cope_tv:

                if (!TextUtils.isEmpty(uid_tv.getText().toString())) {
                    NorUtils.copeText(this, uid_tv.getText().toString());
                    MToast.showButton(this, getString(R.string.b2), 1);


                }

                break;
        }


    }


    void changeTab(int position) {

        if (position == 0) {
            imageview1.setImageResource(R.drawable.rebate_bg3);
            imageview2.setImageResource(R.drawable.rebate_bg);
            text1_tv.setTextColor(getResources().getColor(R.color.white));
            text2_tv.setTextColor(getResources().getColor(R.color.white));
            text3_tv.setTextColor(getResources().getColor(R.color.ff8881a6));
            text4_tv.setTextColor(getResources().getColor(R.color.ff222222));
            viewPager.setCurrentItem(0, false);
        } else {
            imageview1.setImageResource(R.drawable.rebate_bg);
            imageview2.setImageResource(R.drawable.rebate_bg3);
            text1_tv.setTextColor(getResources().getColor(R.color.ff8881a6));
            text2_tv.setTextColor(getResources().getColor(R.color.ff222222));
            text3_tv.setTextColor(getResources().getColor(R.color.white));
            text4_tv.setTextColor(getResources().getColor(R.color.white));
            viewPager.setCurrentItem(1, false);
        }

    }

    void mydivide() {

        Map<String, String> params = new HashMap<>();
        params.put("type", "1");
        params = OkhttpManager.encrypt(params);
        params.put("loginToken", UserInfoManager.getToken());
        netTags.add(UrlConstants.mydivide);
        OkhttpManager.postAsync(UrlConstants.mydivide, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
                e.printStackTrace();
            }

            @Override
            public void requestSuccess(String result) {
                try {
                    BounsBean bounsBean = new Gson().fromJson(result, BounsBean.class);

                    if (bounsBean.getCode() == 0) {
                        qr_code_iv.setImageBitmap(mCreateQRImage.createQRImage(bounsBean.getSpreadLink(), qr_code_iv.getWidth(), qr_code_iv.getHeight(), false));

                    } else {

                        MToast.show(PyramidActivity.this, bounsBean.getValue(), 2);
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }


}
