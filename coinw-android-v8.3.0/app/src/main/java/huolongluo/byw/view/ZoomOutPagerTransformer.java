package huolongluo.byw.view;

import android.view.View;

import androidx.viewpager.widget.ViewPager;

/**
 * 实现ViewPager左右滑动放大缩小的效果
 * Created by xmuSistone on 2016/9/18.
 */
public class ZoomOutPagerTransformer implements ViewPager.PageTransformer {
    private float MIN_SCALE = 0.70f;
    private static final float MAX_SCALE = 1f;
    //    private float MIN_ALPHA = 0.5f;
    public ZoomOutPagerTransformer() {

    }

    public ZoomOutPagerTransformer(float MIN_SCALE) {
        this.MIN_SCALE = MIN_SCALE;
    }


    @Override
    public void transformPage(View view, float position) {
        if (position < -1){
            view.setScaleX(MIN_SCALE);
            view.setScaleY(MIN_SCALE);
        } else if (position <= 1) //a页滑动至b页 ； a页从 0.0 -1 ；b页从1 ~ 0.0
        { // [-1,1]
//              Log.e("TAG", view + " , " + position + "");
            float scaleFactor =  MIN_SCALE+(1-Math.abs(position))*(MAX_SCALE-MIN_SCALE);
            view.setScaleX(scaleFactor);
            //每次滑动后进行微小的移动目的是为了防止在三星的某些手机上出现两边的页面为显示的情况
            if(position>0){
                view.setTranslationX(-scaleFactor*2);
            }else if(position<0){
                view.setTranslationX(scaleFactor*2);
            }
            view.setScaleY(scaleFactor);

        } else
        { // (1,+Infinity]

            view.setScaleX(MIN_SCALE);
            view.setScaleY(MIN_SCALE);

        }
    }




}
