package com.legend.common.base;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public abstract class BaseBottomSheetDialogFragment extends BottomSheetDialogFragment {
    public String TAG = this.getClass().getSimpleName();

    protected Context mContext;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getDialog() != null) {
            getDialog().setCancelable(false);
            getDialog().setCanceledOnTouchOutside(false);
        }
    }

    @Override
    public void show(@NonNull FragmentManager manager, @Nullable String tag) {
        try {
            if (!isAdded()) {//add后不需要再调用show,否则连续调用show会崩溃
                super.show(manager, tag);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void dismiss() {
        try {
            if (isAdded()) {//没有add不能调用dismiss,否则会崩溃
                super.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
