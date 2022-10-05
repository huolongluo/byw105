package huolongluo.byw.byw.inform.activity;

import android.app.AlertDialog;
import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import butterknife.BindView;
import huolongluo.byw.R;
import huolongluo.byw.byw.base.BaseActivity;
import huolongluo.byw.byw.inform.fragment.NewsFragment;
import huolongluo.byw.byw.inform.fragment.NoticeFragment;
import huolongluo.byw.util.tip.DialogUtils;
import huolongluo.bywx.helper.AppHelper;
/**
 * Created by Administrator on 2018/9/17 0017.
 */
public class NoticeActivity extends BaseActivity implements View.OnClickListener {
    private ViewPager viewpager;
    @BindView(R.id.gongao_tv)
    TextView gongao_tv;
    @BindView(R.id.news1_tv)
    TextView news1_tv;
    private NoticeFragment noticeFragment;
    private NewsFragment newsFragment;
    private Fragment[] fragments;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_notifice;
    }

    @Override
    protected void injectDagger() {
    }

    @Override
    protected String initTitle() {
        return getString(R.string.notification);
    }

    @Override
    protected String initRightText() {
        return getString(R.string.already_read);
    }

    @Override
    protected int initRightLeftDrawable() {
        return R.mipmap.ic_read;
    }

    @Override
    protected View.OnClickListener initRightTextClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtils.getInstance().showTwoButtonDialog(NoticeActivity.this, getString(R.string.sure_mark_all_read), getString(R.string.cz42), getString(R.string.cz43), new DialogUtils.onBnClickListener() {
                    @Override
                    public void onLiftClick(AlertDialog dialog, View view) {
                        AppHelper.dismissDialog(dialog);
                    }

                    @Override
                    public void onRightClick(AlertDialog dialog, View view) {
                        AppHelper.dismissDialog(dialog);
                        read();
                    }
                });
            }
        };
    }

    @Override
    protected void initViewsAndEvents() {
        noticeFragment = new NoticeFragment();
        newsFragment = new NewsFragment();
        fragments = new Fragment[]{noticeFragment, newsFragment};
        viewpager = findViewById(R.id.viewpager);
        viewpager.setOffscreenPageLimit(2);
        Adapter adapter = new Adapter(getSupportFragmentManager(), fragments);
        viewpager.setAdapter(adapter);
        viewpager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                setSelect(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        viewpager.setCurrentItem(0, false);
        gongao_tv.setOnClickListener(this);
        news1_tv.setOnClickListener(this);
        setSelect(0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.news1_tv:
                setSelect(1);
                viewpager.setCurrentItem(1, false);
                break;
            case R.id.gongao_tv:
                setSelect(0);
                viewpager.setCurrentItem(0, false);
                break;
        }
    }
    private void read(){
        if (newsFragment != null) {
            newsFragment.setAllReady();
        }
        if (noticeFragment != null) {
            noticeFragment.setAllReady();
        }
    }
    void setSelect(int position) {
        if (position == 0) {
            news1_tv.setSelected(false);
            gongao_tv.setSelected(true);
        } else if (position == 1) {
            news1_tv.setSelected(true);
            gongao_tv.setSelected(false);
        }
    }

    private static class Adapter extends FragmentPagerAdapter {
        private Fragment[] fragments;

        public Adapter(FragmentManager fm, Fragment[] fragments) {
            super(fm);
            this.fragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment=null;
            if (position == 0) {
                if (fragments[0] != null) {
                    fragment = fragments[0];
                } else {
                    fragment = new NoticeFragment();
                    fragments[0] = fragment;
                }
            } else if (position == 1) {
                if (fragments[1] != null) {
                    fragment = fragments[1];
                } else {
                    fragment = new NewsFragment();
                    fragments[1] = fragment;
                }
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return fragments.length;
        }
    }
}
