package com.android.legend.common;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
//使用该livedata，第一次订阅有值不会执行，适用于一些公共的数据
public class BusMutableLiveData<T> extends MutableLiveData<T> {
    @Override
    public void observe(@NonNull LifecycleOwner owner, @NonNull Observer<? super T> observer) {
        super.observe(owner, observer);
        hook(observer);
    }

    private void hook(Observer observer) {
        try {
            //1.得到mLastVersion
            Class liveDataClass = LiveData.class;
            Field mObserversField = liveDataClass.getDeclaredField("mObservers");
            mObserversField.setAccessible(true);
            //获取到这个成员变量对应的对象
            Object mObserversObject = mObserversField.get(this);
            //得到map
            Class mObserversObjectClass = mObserversObject.getClass();
            //获取到mObservers对象的get方法
            Method get = mObserversObjectClass.getDeclaredMethod("get", Object.class);
            get.setAccessible(true);
            //执行get方法
            Object invokeEntry = get.invoke(mObserversObject, observer);
            //取到map中的value
            Object observerWraper = null;
            if (invokeEntry != null && invokeEntry instanceof Map.Entry){
                observerWraper = ((Map.Entry) invokeEntry).getValue();
            }
            if (observerWraper == null) {
                throw new NullPointerException("observerWraper is null");
            }
            //得到ObserverWrapper的类对象
            Class superclass = observerWraper.getClass().getSuperclass();
            Field mLastVersion = superclass.getDeclaredField("mLastVersion");
            mLastVersion.setAccessible(true);
            //2.得到mVersion
            Field mVersion = liveDataClass.getDeclaredField("mVersion");
            mVersion.setAccessible(true);
            //3.把mVersion的值填入到mLastVersion中
            Object mVersionValue = mVersion.get(this);
            mLastVersion.set(observerWraper, mVersionValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
