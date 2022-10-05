package huolongluo.byw.byw.injection.model;

import android.app.Activity;
import android.content.Context;
import dagger.Module;
import dagger.Provides;
import huolongluo.byw.base.ActivityContext;

/**
 * <p>
 * Created by 火龙裸 on 2017/8/21.
 */

@Module
public class ActivityModule
{
    private Activity mActivity;
    
    public ActivityModule(Activity mActivity)
    {
        this.mActivity = mActivity;
    }

    @Provides
    Activity provideActivity()
    {
        return mActivity;
    }

    @Provides
    @ActivityContext
    Context provideContext()
    {
        return mActivity;
    }

//    @Provides
//    @PerActivity
//    public Api provideApi(@Named("api") OkHttpClient okHttpClient, @ActivityContext Context mContext)
//    {
//        return new Api(okHttpClient, mContext);
//    }
//
//    @Provides
//    @PerActivity
//    public ApiCache provideApiCache(@Named("apiCache") OkHttpClient okHttpClient, @ActivityContext Context mContext)
//    {
//        return new ApiCache(okHttpClient, mContext);
//    }
}
