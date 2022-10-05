package huolongluo.byw.byw.injection.component;

import android.app.Application;
import android.content.Context;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Component;
import huolongluo.byw.base.ApplicationContext;
import huolongluo.byw.byw.base.BaseApp;
import huolongluo.byw.byw.injection.model.ApiModule;
import huolongluo.byw.byw.injection.model.ApplicationModule;
import huolongluo.byw.byw.net.okhttp.OkHttpHelper;
import okhttp3.OkHttpClient;

/**
 * <p>
 * Created by 火龙裸 on 2017/8/21.
 */

@Singleton
@Component(modules = {ApplicationModule.class, ApiModule.class})
public interface ApplicationComponent
{
    void inject(BaseApp application);

    @ApplicationContext
    Context context();

    Application application();

    @Named("api")
    OkHttpClient getOkHttpClient();

    @Named("apiCache")
    OkHttpClient getOkHttpClientCache();

    OkHttpHelper getOkHttpHelper();
}
