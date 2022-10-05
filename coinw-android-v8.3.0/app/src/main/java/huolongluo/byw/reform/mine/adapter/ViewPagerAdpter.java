package huolongluo.byw.reform.mine.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import huolongluo.byw.R;
import huolongluo.byw.reform.base.hyperpayBannerBean;
//import huolongluo.byw.byw.ui.utils.DisplayImageUtils;

/**
 * Created by Administrator on 2017/12/13.
 */
public class ViewPagerAdpter extends PagerAdapter {
    List<hyperpayBannerBean.BannerBean> imgList;
    Context mContent;
    private String imageUrl = "https://www.hyperpay.tech/data/upload/";

    public ViewPagerAdpter(List<hyperpayBannerBean.BannerBean> imgList, Context content) {
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

    @Override
    public Object instantiateItem(ViewGroup container, int realposition) {
        View view = View.inflate(mContent, R.layout.money_banner_item, null);
        int position = realposition % imgList.size();
        ImageView view1 = view.findViewById(R.id.imgView);
        //view1.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT,RelativeLayout.LayoutParams.FILL_PARENT));
//         Glide.with(mContent).load(imageUrl+imgList.get(position).getImageUrl()).error(R.mipmap.rmblogo).centerCrop().into(view1);
        RequestOptions ro = new RequestOptions();
        ro.placeholder(R.drawable.banner_default);
        ro.error(R.drawable.banner_default);
        ro.centerCrop();
        Glide.with(mContent).load(imageUrl + imgList.get(position).getImageUrl()).apply(ro).into(view1);
//       DisplayImageUtils.displayImage(view1, imageUrl+imgList.get(position).getImageUrl(), mContent, R.drawable.banner_default, R.drawable.banner_default, true, true);
        //  DisplayImageUtils.displayImage(view1, "https://btc018.oss-cn-shenzhen.aliyuncs.com/201811131821029_A0w7K.jpg", mContent, R.drawable.banner_default, R.drawable.banner_default, true, true);
        //  RoundedBitmapDisplayer
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View view = (View) object;
        container.removeView(view);
    }
}
