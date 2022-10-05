package huolongluo.byw.byw.ui.fragment.maintab01.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

import huolongluo.byw.R;
import huolongluo.byw.byw.inform.activity.NoticeDetailActivity;
import huolongluo.byw.byw.ui.fragment.maintab01.bean.BannerBean;
import huolongluo.byw.reform.home.activity.NewsWebviewActivity;
import huolongluo.byw.user.UserInfoManager;

//import huolongluo.byw.byw.ui.utils.DisplayImageUtils;

/**
 * Created by Administrator on 2017/12/13.
 */

public class ViewPagerAdpter extends PagerAdapter {
    List<BannerBean> imgList;
    Context mContent;
    private String mUmengEventPre;//umeng自定义事件事件id前缀

    public ViewPagerAdpter(List<BannerBean> imgList, Context content) {
        this.imgList = imgList;
        mContent = content;
    }

    @Override
    public int getCount() {
        return imgList.size() == 0 ? 0 : Integer.MAX_VALUE;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public float getPageWidth(int position) {
        return 1f;
    }

    public void setUmengEventIdPre(String eventPre) {
        mUmengEventPre = eventPre;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int realPosition) {


        int position = realPosition % imgList.size();
        View view = View.inflate(mContent, R.layout.home_banner_item, null);

        ImageView view1 = view.findViewById(R.id.imgView);
        RequestOptions ro = new RequestOptions();
        ro.placeholder(R.drawable.banner_default);
        ro.error(R.drawable.banner_default);
        Glide.with(mContent).load(imgList.get(position).getImg()).apply(ro).into(view1);

        container.addView(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContent, NewsWebviewActivity.class);
                if (imgList.size() > position) {

                    if (!TextUtils.isEmpty(mUmengEventPre)) {
                        MobclickAgent.onEvent(mContent, mUmengEventPre + (position + 1), imgList.get(position).getId() + "");
                    }
                    if (imgList.get(position).getWhether() == 1) {
                        intent.putExtra("url", imgList.get(position).getUrl());
                        intent.putExtra("token", UserInfoManager.getToken());
                        intent.putExtra("useH5Title", true);
                        mContent.startActivity(intent);
                    } else {

                        if (!android.text.TextUtils.equals(imgList.get(position).getImg(), "error")) {
                            Intent intent1 = new Intent(mContent, NoticeDetailActivity.class);

                            intent1.putExtra("id", Integer.parseInt(imgList.get(position).getNewid()));
                            mContent.startActivity(intent1);
                        }

                    }


                }
            }
        });
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View view = (View) object;
        container.removeView(view);
    }
}
