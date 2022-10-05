package huolongluo.byw.byw.ui.fragment.maintab01.home;
import android.app.Activity;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.gson.Gson;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import huolongluo.byw.R;
import huolongluo.byw.byw.bean.UserInfoBean;
import huolongluo.byw.byw.net.UrlConstants;
import huolongluo.byw.byw.ui.fragment.maintab01.adapter.GalleryTransformer;
import huolongluo.byw.byw.ui.fragment.maintab01.adapter.ViewPagerAdpter;
import huolongluo.byw.byw.ui.fragment.maintab01.bean.BannerBean;
import huolongluo.byw.log.Logger;
import huolongluo.byw.util.Constant;
import huolongluo.byw.util.OkhttpManager;
import huolongluo.byw.util.Util;
import huolongluo.bywx.utils.AppUtils;
import okhttp3.Request;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
public class HomeBannerHandler {
    private Activity atv;
    private ViewPager viewPager;
    private ViewPagerAdpter viewPagerAdpter;
    private List<BannerBean> bannerList = new ArrayList<>();
    private LinearLayout dotLayout;
    private int lastPage = 0;
    private boolean viewPagerState = false;
    private Subscription subscribe;

    public void init(Activity atv, ViewPager viewPager, LinearLayout dotLayout) {
        if (atv == null || atv.isFinishing() || atv.isDestroyed() || viewPager == null || dotLayout == null) {
            //TODO 处理异常
            return;
        }
        this.atv = atv;
        this.viewPager = viewPager;
        this.dotLayout = dotLayout;
        viewPagerAdpter = new ViewPagerAdpter(bannerList, atv);
        viewPagerAdpter.setUmengEventIdPre(Constant.UMENG_EVENT_PRE_BANNER1);
        viewPager.setAdapter(viewPagerAdpter);
//        viewPager.setPageTransformer(true, new GalleryTransformer());
//        viewPager.setPageTransformer(true, new GalleryTransformer());
//        viewPager.setAdapter(viewPagerAdpter);
        //
        addListener();
        //获得数据
        getData();
        //切换动作
        autoChange();
    }

    private void addListener() {
        viewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_MOVE:
                        viewPagerState = true;
                        break;
                    case MotionEvent.ACTION_UP:
                        viewPagerState = false;
                        break;
                }
                return false;
            }
        });
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (dotLayout == null) {
                    return;
                }
                if (bannerList == null || bannerList.isEmpty()) {
                    return;
                }
                int realPos = position % bannerList.size();
                ImageView lasview = (ImageView) dotLayout.getChildAt(lastPage);
                if (lasview != null) {
                    lasview.setImageDrawable(ContextCompat.getDrawable(atv,R.drawable.bg_cor2_background_tab_w15_h2));
                }
                lastPage = realPos;
                ImageView view = (ImageView) dotLayout.getChildAt(realPos);
                if (view != null) {
                    view.setImageDrawable(ContextCompat.getDrawable(atv,R.drawable.bg_cor2_accent_w15_h2));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    private void autoChange() {
        subscribe = Observable.interval(3, 8, TimeUnit.SECONDS).limit(6000).map(aLong -> 6000 - aLong).observeOn(AndroidSchedulers.mainThread()).doOnNext(aLong -> {
            if (atv == null || viewPager == null) {
                return;
            }
            if (viewPagerAdpter == null) {
                return;
            }
            if (viewPagerAdpter.getCount() > 1 && !viewPagerState) {
                viewPager.setCurrentItem((viewPager.getCurrentItem() + 1) % viewPagerAdpter.getCount());
            }
        }).onErrorReturn(throwable -> {
            Logger.getInstance().errorLog(throwable);
            return 0L;
        }).doOnError(throwable -> {
            Logger.getInstance().error(throwable);
        }).subscribe();
    }

    public void refresh() {
        //TODO 重新刷新Banner
    }

    public void release() {
        //TODO 释放资源
        if (subscribe != null && !subscribe.isUnsubscribed()) {
            subscribe.unsubscribe();
        }
    }

    /**
     *
     */
    private void getData() {
        //TODO 待优化
        if (!AppUtils.isNetworkConnected()) {
            return;
        }
        Map<String, String> params = new HashMap<>();
        params.put("type", "9");
        params = OkhttpManager.encrypt(params);
        OkhttpManager.postAsync(UrlConstants.DOMAIN + UrlConstants.bannerList, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
                e.printStackTrace();
                bannerList.add(new BannerBean("error"));
                viewPagerAdpter.notifyDataSetChanged();
                for (int i = 0; i < 1; i++) {
                    ImageView dotView = new ImageView(atv);
                    if (i == 0) {
                        dotView.setImageResource(R.drawable.bg_cor2_accent_w15_h2);
                    } else {
                        dotView.setImageResource(R.drawable.bg_cor2_background_tab_w15_h2);
                    }
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    layoutParams.setMargins(Util.dp2px(atv, 0), 0, 0, 0);
                    dotLayout.addView(dotView, layoutParams);
                }
            }

            @Override
            public void requestSuccess(String result) {
                try {
                    Gson gson = new Gson();
                    BannerBean bannerBean = gson.fromJson(result, BannerBean.class);
                    if (bannerBean != null && bannerBean.getCode() == 0) {
                        bannerList.addAll(bannerBean.getBannerList());
                        viewPager.setVisibility(View.GONE);
//                        bannerAdpter.replaceAll(beanList);
                        viewPagerAdpter.notifyDataSetChanged();
                        viewPager.setCurrentItem(bannerList.size());
                        new Handler().postDelayed(() -> viewPager.setVisibility(View.VISIBLE), 300);   //5秒
                        try {
                            Class c = Class.forName("androidx.viewpager.widget.ViewPager");
                            Field field = c.getDeclaredField("mCurItem");
                            field.setAccessible(true);
                            field.setInt(viewPager, bannerList.size());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        int leng = bannerBean.getBannerList().size();
                        if (leng != 0) {
                            for (int i = 0; i < leng; i++) {
                                ImageView dotView = new ImageView(atv);
                                if (i == 0) {
                                    dotView.setImageDrawable(ContextCompat.getDrawable(atv,R.drawable.bg_cor2_accent_w15_h2));
                                } else {
                                    dotView.setImageDrawable(ContextCompat.getDrawable(atv,R.drawable.bg_cor2_background_tab_w15_h2));
                                }
                                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                layoutParams.setMargins(Util.dp2px(atv, 0), 0, 0, 0);
                                dotLayout.addView(dotView, layoutParams);
                            }
                        } else {
                            bannerList.add(new BannerBean("error"));
                            viewPagerAdpter.notifyDataSetChanged();
                            for (int i = 0; i < 1; i++) {
                                ImageView dotView = new ImageView(atv);
                                if (i == 0) {
                                    dotView.setImageDrawable(ContextCompat.getDrawable(atv,R.drawable.bg_cor2_accent_w15_h2));
                                } else {
                                    dotView.setImageDrawable(ContextCompat.getDrawable(atv,R.drawable.bg_cor2_background_tab_w15_h2));
                                }
                                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                layoutParams.setMargins(Util.dp2px(atv, 5), 0, 0, 0);
                                dotLayout.addView(dotView, layoutParams);
                            }
                        }
                    } else {
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
