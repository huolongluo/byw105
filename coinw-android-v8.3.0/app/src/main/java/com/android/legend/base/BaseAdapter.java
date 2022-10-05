package com.android.legend.base;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import org.jetbrains.annotations.Nullable;

import java.util.List;
public abstract class BaseAdapter<T> extends BaseQuickAdapter<T, BaseViewHolder> implements LoadMoreModule {
    public BaseAdapter(int layoutResId, @Nullable List<T> data) {
        super(layoutResId, data);
    }

    public BaseAdapter(int layoutResId) {
        super(layoutResId);
    }
}
