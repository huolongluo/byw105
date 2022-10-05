package huolongluo.byw.reform.base;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
/**
 * Created by hy on 2018/10/31 0031.
 */
public abstract class BaseViewFragment extends BaseFragment {
    @Override
    protected int getRootViewResId() {
        return View.NO_ID;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = getRootView();
        onCreatedView(rootView);
        return rootView;
    }

    @NonNull
    protected abstract View getRootView();
}
