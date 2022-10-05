package huolongluo.byw.base;

import android.app.Application;
import android.content.Context;

import java.lang.reflect.Method;


/**
 * 需要在在application的onCreate中初始化: s.Ext.init(this);
 */
public final class s
{

    private s()
    {
    }

    public static boolean isDebug()
    {
        return Ext.debug;
    }


    public static class Ext
    {
        private static boolean debug;
        private static Application app;

        private Ext()
        {
        }


        public static void init(Application app)
        {
            if (Ext.app == null)
            {
                Ext.app = app;
            }
        }

        public static void setDebug(boolean debug)
        {
            Ext.debug = debug;
        }

    }

    private static class MockApplication extends Application
    {
        public MockApplication(Context baseContext)
        {
            this.attachBaseContext(baseContext);
        }
    }
}
