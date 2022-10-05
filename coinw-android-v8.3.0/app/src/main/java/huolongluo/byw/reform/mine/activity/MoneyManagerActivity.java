package huolongluo.byw.reform.mine.activity;

import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.baymax.android.keyboard.Utils;
import huolongluo.byw.R;
import huolongluo.byw.byw.base.BaseApp;
import huolongluo.byw.byw.net.UrlConstants;
import huolongluo.byw.byw.ui.activity.UpVersionActivity;
import huolongluo.byw.log.Logger;
import huolongluo.byw.reform.base.BaseActivity;
import huolongluo.byw.reform.base.hyperpayBannerBean;
import huolongluo.byw.reform.mine.adapter.ViewPagerAdpter;
import huolongluo.byw.reform.mine.fragment.MoneyManagerFragment;
import huolongluo.byw.util.DeviceUtils;
import huolongluo.byw.util.OkhttpManager;
import huolongluo.byw.util.Util;
import huolongluo.byw.util.cache.CacheManager;
import huolongluo.byw.util.tip.DialogUtils;
import huolongluo.byw.util.tip.MToast;
import huolongluo.bywx.utils.AppUtils;
import okhttp3.Request;

/**
 * Created by Administrator on 2019/1/6 0006.
 */
public class MoneyManagerActivity extends BaseActivity implements View.OnClickListener {
    Unbinder unbinder;
    @BindView(R.id.main_viewPager)
    ViewPager main_viewPager;
    @BindView(R.id.banner_viewpager)
    ViewPager banner_viewpager;
    @BindView(R.id.titlebg_iv)
    ImageView titlebg_iv;
    @BindView(R.id.tab1_tv)
    TextView tab1_tv;
    @BindView(R.id.tab2_tv)
    TextView tab2_tv;
    @BindView(R.id.tab3_tv)
    TextView tab3_tv;
    @BindView(R.id.tab4_tv)
    TextView tab4_tv;
    @BindView(R.id.dot_parent)
    LinearLayout dot_parent;
    @BindView(R.id.confirm_bn)
    Button confirm_bn;
    @BindView(R.id.back_iv)
    ImageButton back_iv;
    @BindView(R.id.download_ll)
    LinearLayout download_ll;
    private int lift;
    private int titleWidth;
    int realPosition = 0;
    boolean tanPressed = false;
    String url = "https://www.hyperpay.tech/?con=api&ctl=coinw_banner";
    List<hyperpayBannerBean.BannerBean> list = new ArrayList<>();
    private ViewPagerAdpter bannerAdapter;
    public static String hyperpay_download_rul;

    void hyperpay() {
//        zh-CN 中文
//        en 英文
//        korea 繁体
//        korea_real 韩文
        String reqUrl = url + "&lang=" + AppUtils.getLanguageTag();
        OkhttpManager.get(reqUrl, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
                Log.e("requestSuccess= ", "result=  ");
            }

            @Override
            public void requestSuccess(String result) {
                Log.e("requestSuccess= ", "result=  " + result);
                if (dot_parent == null) {
                    return;
                }
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    int code = jsonObject.getInt("code");
                    if (code == 200) {
                        JSONObject object = jsonObject.getJSONObject("data");
                        hyperpayBannerBean byperpayBean = new Gson().fromJson(object.toString(), hyperpayBannerBean.class);
                        CacheManager.getDefault(BaseApp.getSelf().getApplicationContext()).getAcache().put("coinw_banner", object.toString());
                        list.clear();
                        list.addAll(byperpayBean.getBanners());
                        hyperpay_download_rul = byperpayBean.getDown_url();
                        int leng = list.size();
                        dot_parent.removeAllViews();
                        if (leng != 0) {
                            for (int i = 0; i < leng; i++) {
                                ImageView dotView = new ImageView(MoneyManagerActivity.this);
                                if (i == 0) {
                                    dotView.setImageResource(R.drawable.dot_white_sel_);
                                } else {
                                    dotView.setImageResource(R.drawable.dot_white_);
                                }
                                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                layoutParams.setMargins(Util.dp2px(MoneyManagerActivity.this, 5), 0, 0, 0);
                                dot_parent.addView(dotView, layoutParams);
                            }
                        }
                        bannerAdapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        int currtentPos = main_viewPager.getCurrentItem();
        switch (v.getId()) {
            case R.id.tab1_tv:
                realPosition = 0;
                tanPressed = true;
                ValueAnimator animator = ValueAnimator.ofFloat(0, 100);
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        Float value = (Float) animation.getAnimatedValue();
                        Log.e("getAnimatedValue", "=  " + value);
                        if (realPosition < currtentPos) {
                            animatiion(realPosition, (1 - (value / 100)), Math.abs(realPosition - currtentPos));
                        }
                    }
                });
                animator.setDuration(500).start();
                     /*  ObjectAnimator translationX = new ObjectAnimator().ofFloat(titlebg_iv,"translationX",0f);
                       translationX.setDuration(500l);
                      translationX.start();*/
                main_viewPager.setCurrentItem(0, false);
                break;
            case R.id.tab2_tv:
                realPosition = 1;
                tanPressed = true;
                ValueAnimator animator1 = ValueAnimator.ofFloat(0, 100);
                animator1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        Float value = (Float) animation.getAnimatedValue();
                        Log.e("getAnimatedValue", "=  " + value);
                        if (realPosition > currtentPos) {
                            animatiion(realPosition - 1, value / 100, Math.abs(realPosition - currtentPos));
                        } else if (realPosition < currtentPos) {
                            animatiion(realPosition, (1 - value / 100), Math.abs(realPosition - currtentPos));
                        }
                    }
                });
                animator1.setDuration(500).start();
                main_viewPager.setCurrentItem(1, false);
                break;
            case R.id.tab3_tv:
                realPosition = 2;
                tanPressed = true;
                ValueAnimator animator2 = ValueAnimator.ofFloat(0, 100);
                animator2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        Float value = (Float) animation.getAnimatedValue();
                        Log.e("getAnimatedValue", "=  " + value);
                        if (realPosition > currtentPos) {
                            animatiion(currtentPos, value / 100, Math.abs(realPosition - currtentPos));
                        } else if (realPosition < currtentPos) {
                            animatiion(realPosition, (1 - (value / 100)), Math.abs(realPosition - currtentPos));
                        }
                    }
                });
                animator2.setDuration(500).start();
                main_viewPager.setCurrentItem(2, false);
                break;
            case R.id.tab4_tv:
                realPosition = 3;
                tanPressed = true;
                ValueAnimator animator3 = ValueAnimator.ofFloat(0, 100);
                animator3.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        Float value = (Float) animation.getAnimatedValue();
                        Log.e("getAnimatedValue", "=  " + value);
                        if (realPosition > currtentPos) {
                            animatiion(currtentPos, value / 100, Math.abs(realPosition - currtentPos));
                        }
                    }
                });
                animator3.setDuration(500).start();
                main_viewPager.setCurrentItem(3, false);
                break;
            case R.id.confirm_bn:
                checkApp();
                break;
            case R.id.back_iv:
                finish();
                break;
            case R.id.download_ll:
                checkApp();
                break;
        }
    }

    public void checkApp() {
        PackageManager packageManager = getPackageManager();
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
        String packageName = null;
        for (PackageInfo infos : packageInfos) {
            Log.i("infos", "=" + infos.packageName);
            if (TextUtils.equals(infos.packageName, UrlConstants.hppPackapeNameo)) {
                packageName = infos.packageName;
                if (infos.versionCode < UrlConstants.hppVerCode) {
                    showInstallDoalog(getString(R.string.e5), getString(R.string.e6));
                    return;
                }
            } else if (TextUtils.equals(infos.packageName, UrlConstants.hppPackapeNamet)) {
                //   startActivity(new Intent(BindHPayActivity.this,BindHPaySuccessActivity.class));
                packageName = infos.packageName;
                if (infos.versionCode < UrlConstants.hppVerCode) {
                    showInstallDoalog(getString(R.string.e3), getString(R.string.e4));
                    return;
                }
            }
            if (!TextUtils.isEmpty(packageName)) {
                //TODO 由于Hyperpay开发团队改动启动入口，因此需要同步修改
//                if (startActivity(infos.packageName, ".main.MainActivity")) {
//                    return;
//                } else if (startActivity(infos.packageName, ".main.home2.MainActivity")) {
//                    return;
//                } else {
//                    Intent intent = getPackageManager().getLaunchIntentForPackage(UrlConstants.hppPackapeNameo);
//                    if (intent != null) {
//                        startLocalActivity(intent);
//                    }
//                }
                if (TextUtils.equals(packageName, UrlConstants.hppPackapeNamet)) {
                    Intent intent = getPackageManager().getLaunchIntentForPackage(UrlConstants.hppPackapeNamet);
                    if (intent != null) {
                        startLocalActivity(intent);
                    }
                } else if (TextUtils.equals(packageName, UrlConstants.hppPackapeNameo)) {
                    Intent intent = getPackageManager().getLaunchIntentForPackage(UrlConstants.hppPackapeNameo);
                    if (intent != null) {
                        startLocalActivity(intent);
                    }
                }
                return;
            }
        }
        showInstallDoalog();
    }

    private boolean startActivity(String packageName, String actionName) {
        try {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            //前提：知道要跳转应用的包名、类名
            // ComponentName componentName = new ComponentName("com.legendwd.hcash", "com.legendwd.hcash.main.login.LoginActivity");
            ComponentName componentName = new ComponentName(packageName, packageName + ".main.MainActivity");
            intent.setComponent(componentName);
            return startLocalActivity(intent);
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return false;
    }

    private boolean startLocalActivity(Intent intent) {
        try {
//            Intent intent = new Intent(Intent.ACTION_MAIN);
            //前提：知道要跳转应用的包名、类名
//            ComponentName componentName = new ComponentName(infos.packageName, infos.packageName + ".main.MainActivity");
//            intent.setComponent(componentName);
            // String uniqueId=null;
            // String appId=null;
            //intent.putExtra("uniqueId", uniqueId);
            //  intent.putExtra("appId", appId);
            intent.putExtra("isCoinw", true);
            startActivity(intent);
            return true;
        } catch (Throwable t) {
            Logger.getInstance().error(t);
        }
        return false;
    }

    void showInstallDoalog(String text, String rightText) {
        DialogUtils.getInstance().showTwoButtonDialog(this, text, getString(R.string.j2), rightText);
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
                Intent intent = new Intent();
                intent.setData(Uri.parse(UrlConstants.getDownloadHpp(MoneyManagerActivity.this)));//Url 就是你要打开的网址
                intent.setAction(Intent.ACTION_VIEW);
                List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, 0);
                Log.i("版本升级", "ResolveInfo= size:" + list.size());
                for (ResolveInfo info : list) {
                    Log.i("版本升级", "ResolveInfo= :" + info.loadLabel(getPackageManager()).toString());
                }
                if (list.size() > 0) {
                    startActivity(intent); //启动浏览器
                    ///浏览器存在
                }
            }
        });
    }

    //显示下载提示框
    void showInstallDoalog() {
        DialogUtils.getInstance().showDownloadHPDialog(this, new DialogUtils.onBnClickListener() {
            @Override
            public void onLiftClick(AlertDialog dialog, View view) {
            }

            @Override
            public void onRightClick(AlertDialog dialog, View view) {
                dialog.dismiss();
                if (TextUtils.isEmpty(hyperpay_download_rul)) {
                    MToast.show(MoneyManagerActivity.this, getString(R.string.m4), 1);
                    hyperpay();
                    return;
                }
                Intent intent = new Intent();
                intent.setData(Uri.parse(hyperpay_download_rul));//Url 就是你要打开的网址
                intent.setAction(Intent.ACTION_VIEW);
                List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, 0);
                if (list.size() > 0) {
                    startActivity(intent); //启动浏览器
                    ///浏览器存在
                } else {
                    ///浏览器不存在
                    Bundle bundle = new Bundle();
                    bundle.putString("downUrl", hyperpay_download_rul);
                    Intent intent1 = new Intent(MoneyManagerActivity.this, UpVersionActivity.class);
                    intent1.putExtra("bundle", bundle);
                    startActivity(intent1);
                }
            }
        });
    }

    private Fragment[] fragments = new Fragment[4];
    int lastPage = 0;
    private Timer timer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__moneymanager);
        unbinder = ButterKnife.bind(this);
        if (mSwipeBackLayout != null) {
            // mSwipeBackLayout.setEnableGesture(false);
        }
        for (int i = 0; i < 4; i++) {
            Bundle bundle = new Bundle();
            bundle.putInt("type", i + 1);
            MoneyManagerFragment fragment = new MoneyManagerFragment();
            fragment.setArguments(bundle);
            fragments[i] = fragment;
        }
        download_ll.setOnClickListener(this);
        back_iv.setOnClickListener(this);
        confirm_bn.setOnClickListener(this);
        tab1_tv.setOnClickListener(this);
        tab2_tv.setOnClickListener(this);
        tab3_tv.setOnClickListener(this);
        tab4_tv.setOnClickListener(this);
        String result = CacheManager.getDefault(BaseApp.getSelf().getApplicationContext()).getAcache().getAsString("coinw_banner");
        if (!TextUtils.isEmpty(result)) {
            hyperpayBannerBean byperpayBean = new Gson().fromJson(result, hyperpayBannerBean.class);
            hyperpay_download_rul = byperpayBean.getDown_url();
            if (byperpayBean.getCode() == 200) {
                list.clear();
                list.addAll(byperpayBean.getBanners());
                int leng = list.size();
                dot_parent.removeAllViews();
                if (leng != 0) {
                    for (int i = 0; i < leng; i++) {
                        ImageView dotView = new ImageView(MoneyManagerActivity.this);
                        if (i == 0) {
                            dotView.setImageResource(R.drawable.dot_white_sel_);
                        } else {
                            dotView.setImageResource(R.drawable.dot_white_);
                        }
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        layoutParams.setMargins(Util.dp2px(MoneyManagerActivity.this, 5), 0, 0, 0);
                        dot_parent.addView(dotView, layoutParams);
                    }
                }
            }
        }
        banner_viewpager.setPageMargin(DeviceUtils.dip2px(this, 14));//设置viewpage两个页面间距
        banner_viewpager.setOffscreenPageLimit(3);//提前预加载3个,数量最好大于3个
        bannerAdapter = new ViewPagerAdpter(list, this);
        banner_viewpager.setAdapter(bannerAdapter);
        banner_viewpager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int reaposition) {
                int position = reaposition % list.size();
                ImageView lasview = (ImageView) dot_parent.getChildAt(lastPage);
                if (lasview != null) {
                    lasview.setImageResource(R.drawable.dot_white_);
                }
                lastPage = position;
                ImageView view = (ImageView) dot_parent.getChildAt(position);
                view.setImageResource(R.drawable.dot_white_sel_);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        hyperpay();
        main_viewPager.setOffscreenPageLimit(4);
        main_viewPager.setAdapter(pagerAdapter);
        int phoneWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
        lift = Utils.dipToPx(this, 10);
        titleWidth = (phoneWidth - lift - lift) / 4;
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) titlebg_iv.getLayoutParams();
        ///  params.setMargins(po,0,0,0);
        params.width = titleWidth;
        params.height = Utils.dipToPx(this, 47);
        titlebg_iv.setLayoutParams(params);
        main_viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override//位置，百分比，像素值
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (tanPressed) {
                    return;
                }
                animatiion(position, positionOffset, 1);
            }

            @Override
            public void onPageSelected(int position) {
                tanPressed = false;
                if (position == 0) {
                    if (mSwipeBackLayout != null) {
                        mSwipeBackLayout.setEnableGesture(true);
                    }
                } else {
                    if (mSwipeBackLayout != null) {
                        mSwipeBackLayout.setEnableGesture(false);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                Log.e("onPageScrollded", "state=  " + state);
                if (state == 0) {
                    tanPressed = false;
                }
            }
        });
        if (timer == null) {
            timer = new Timer();
            timer.schedule(timeTask, 8000, 3000);
        }
    }

    void animatiion(int position, float positionOffset, int a) {
        if (titlebg_iv == null) {
            return;
        }
        int po;
        // if(x>=0){
        po = (int) (titleWidth * position + (titleWidth * positionOffset * a));
        // }else {
        //    po= (int) (titleWidth *position-(titleWidth *positionOffset));
        // }
        //   Log.e("onPageScrolled","position ="+ position+"; positionOffset= "+positionOffset+" ; positionOffsetPixels "+positionOffsetPixels);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) titlebg_iv.getLayoutParams();
        params.setMargins(po, 0, 0, 0);
        //  params.width=titleWidth;
        // params.height=higth;
        titlebg_iv.setLayoutParams(params);
    }

    FragmentPagerAdapter pagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
        @Override
        public Fragment getItem(int position) {
            if (position < 0 || fragments == null || fragments.length <= 0) {
                return null;
            }
            if (position < fragments.length) {
                return fragments[position];
            }
            return null;
        }

        @Override
        public int getCount() {
            return fragments.length;
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
        if (timeTask != null) {
            try {
                timeTask.cancel();
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
        if (timer != null) {
            try {
                timer.cancel();
            } catch (Throwable t) {
                t.printStackTrace();
            }
            timer = null;
        }
    }

    private TimerTask timeTask = new TimerTask() {
        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (bannerAdapter == null || banner_viewpager == null) {
                        return;
                    }
                    if (bannerAdapter.getCount() > 1) {
                        banner_viewpager.setCurrentItem((banner_viewpager.getCurrentItem() + 1) % bannerAdapter.getCount());
                    }
                }
            });
        }
    };
}
