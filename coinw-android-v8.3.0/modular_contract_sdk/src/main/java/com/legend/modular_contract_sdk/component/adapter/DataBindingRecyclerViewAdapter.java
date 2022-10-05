package com.legend.modular_contract_sdk.component.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Android on 2016/6/3.
 */
public class DataBindingRecyclerViewAdapter<T>
        extends RecyclerView.Adapter<DataBindingRecyclerViewAdapter.DataBindingViewHolder> {

    private Context mContext;
    private int mLayoutId;
    private int mVarId;
    //    private ObservableList<T> mData;
    private List<T> mData;
    //ViewType 1 ~ 100为header 自定义ViewType请大于100
    public static final int TYPE_HEADER = 1, TYPE_FOOTER = -1, TYPE_EMPTY = -2, TYPE_NORMAL = 0;

    private List<View> headerView;
    private View emptyView;
    private View footerView;
    private int footerViewIndex, headerViewIndex;
    private OnItemClickListener itemClickListener;
    private OnBindingViewHolderListener onBindingViewHolderListener;
    private OnViewCreateListener onViewCreateListener;
    private ViewTypeProvider viewTypeProvider;
    private Map<Integer, Integer> viewTypeAndLayoutResId;

    public DataBindingRecyclerViewAdapter() {

    }

    public DataBindingRecyclerViewAdapter(Context context, int layoutId, int varId, List<T> data) {
        mContext = context;
        mLayoutId = layoutId;
        mVarId = varId;
        headerView = new LinkedList<>();
        //        mData = getObservableList(data);
        mData = data;
    }

    public ObservableList<T> getObservableList(List<T> data) {
        return new ObservableList<>(data);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }

    public void setOnBindingViewHolderListener(OnBindingViewHolderListener listener) {
        onBindingViewHolderListener = listener;
    }

    public void setViewTypeProvider(Map<Integer, Integer> map, ViewTypeProvider provider) {
        viewTypeProvider = provider;
        viewTypeAndLayoutResId = map;
    }

    public void setOnViewCreateListener(OnViewCreateListener onViewCreateListener) {
        this.onViewCreateListener = onViewCreateListener;
    }

    @Override
    public DataBindingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType >= 1 && viewType <= 100) {
            View tempHearer = null;
            tempHearer = headerView.get(viewType - 1);

            if (onViewCreateListener != null) {
                onViewCreateListener.onViewCreate(tempHearer, viewType);
            }
            return new DataBindingViewHolder(tempHearer, viewType);
        }

        switch (viewType) {

            case TYPE_EMPTY:
                if (onViewCreateListener != null) {
                    onViewCreateListener.onViewCreate(emptyView, viewType);
                }
                DataBindingViewHolder emptyViewHolder = new DataBindingViewHolder(emptyView, viewType);
                emptyViewHolder.setIsRecyclable(false);
                return emptyViewHolder;
            case TYPE_FOOTER:
                View tempFooter = footerView;//暂时只能添加一个footer
                if (onViewCreateListener != null) {
                    onViewCreateListener.onViewCreate(tempFooter, viewType);
                }
                return new DataBindingViewHolder(tempFooter, viewType);
            default:

                View defaultView = null;
                if (viewTypeProvider != null && viewTypeAndLayoutResId != null &&
                        viewTypeAndLayoutResId.get(viewType) != null) {

                    defaultView = LayoutInflater.from(mContext)
                            .inflate(viewTypeAndLayoutResId.get(viewType),
                                    parent, false);
                    if (onViewCreateListener != null) {
                        onViewCreateListener.onViewCreate(defaultView, viewType);
                    }
                    return new DataBindingViewHolder(defaultView, viewType);
                }

                defaultView = LayoutInflater.from(mContext)
                        .inflate(mLayoutId, parent, false);
                if (onViewCreateListener != null) {
                    onViewCreateListener.onViewCreate(defaultView, viewType);
                }
                return new DataBindingViewHolder(defaultView, viewType);
        }
    }

    @Override
    public void onBindViewHolder(final DataBindingViewHolder holder, final int position) {

        if (holder.viewType > 100 || holder.viewType == 0) {
            ViewDataBinding binding = DataBindingUtil.bind(holder.itemView);
            Object data = mData.get(position - headerView.size());

            if (itemClickListener != null) {
                holder.itemView.setOnClickListener(view -> {
                    itemClickListener.itemClick(holder.itemView, position - headerView.size());
                });
            }
            binding.setVariable(mVarId, data);
            if (onBindingViewHolderListener != null) {
                try {
                    onBindingViewHolderListener.onHolderBinding(holder,
                            position - headerView.size());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            binding.executePendingBindings();
        }
    }

    @Override
    public int getItemCount() {
        int extraCount = 0;
        extraCount += headerView.size();
        if (footerView != null) {
            extraCount++;
        }

        if (emptyView != null && (mData == null || mData.size() == 0)) {
            extraCount++;
        }

        if (mData == null) {
            return extraCount;
        } else {
            return mData.size() + extraCount;
        }
    }
    // header1 -> 0
    // header2 -> 1
    // header3 -> 2
    // empty -> 3
    // footer -> 4
    @Override
    public int getItemViewType(int position) {

        int emptyCount = mData.size() > 0 ? 0 : 1;

        if (headerView.size() > position) {
            return position + 1;
        }else if (mData.size() <= 0) {
            return TYPE_EMPTY;
        } else if (position >= mData.size() + headerView.size() + emptyCount) {
            return TYPE_FOOTER;
        } else {
            if (viewTypeProvider != null) {
                int type = viewTypeProvider.getViewType(position - headerView.size());
                if (type >= 0) {
                    return type;
                }
            }
            return TYPE_NORMAL;
        }
    }

    public void setFooterView(View view) {
        footerView = view;
        notifyItemInserted(mData.size() + headerView.size());
    }

    public void addHeaderView(int index, View view) {
        headerView.add(index, view);
        notifyItemInserted(index);
    }

    public void addHeaderView(View view) {
        headerView.add(view);
        notifyItemInserted(headerView.size() - 1);
    }

    public void setEmptyView(View view){
        emptyView = view;
        notifyDataSetChanged();
    }

    public void removeHeaderView(RecyclerView.LayoutManager manager, View view) {
        int index = headerView.indexOf(view);
        headerView.remove(index);
        manager.removeView(view);
        notifyItemRemoved(index);
    }

    public View removeHeaderView(RecyclerView.LayoutManager manager, int index) {
        View view = headerView.remove(index);
        manager.removeView(view);
        notifyItemRemoved(index);
        return view;
    }

    public void removeHeaderView() {
        int headerSize = headerView.size();
        headerView.clear();
        notifyItemRangeRemoved(1, headerSize);
    }

    public void removeFooterView() {
        if (footerView != null) {
            footerView = null;
            notifyItemRemoved(getItemCount());
        }
    }

    public View getFooterView() {
        return footerView;
    }

    public View getHeaderView(int index) {
        if (headerView.size() > 0) {
            return headerView.get(index);
        }
        return null;
    }

    public int getHeaderViewCount() {
        return headerView.size();
    }

    public List<T> getAllData() {
        return mData;
    }

    public void addData(List<T> data) {
        if (data == null || data.size() == 0) {
            return;
        }
        mData.addAll(data);
        notifyDataSetChanged();
    }

    public void refreshData(List<T> data) {
        //        mData.clear();
        //        mData.addAll(data);
        if (data == null) mData = new ArrayList<>();
        else mData = data;
        notifyDataSetChanged();
    }

    @Override
    public void onViewRecycled(DataBindingViewHolder holder) {
        super.onViewRecycled(holder);
    }

    public void cleanData() {
        mData.clear();
        notifyDataSetChanged();
    }

    public static class DataBindingViewHolder extends RecyclerView.ViewHolder {

        int viewType;

        public DataBindingViewHolder(View itemView, int viewType) {
            super(itemView);
            if (viewType >= 1 && viewType <= 100) {
                setIsRecyclable(false);
            }
            this.viewType = viewType;
        }

        public <T extends ViewDataBinding> T getBinding() {
            return (T) DataBindingUtil.getBinding(itemView);
        }

        @Override
        public String toString() {
            return super.toString() + " viewType = " + viewType;
        }
    }

    public interface OnItemClickListener {
        void itemClick(View view, int position);
    }

    public interface OnBindingViewHolderListener {
        void onHolderBinding(DataBindingViewHolder holder, int position)
                throws InterruptedException;
    }

    public interface ViewTypeProvider {
        int getViewType(int position);
    }

    public interface OnViewCreateListener {
        void onViewCreate(View view, int viewType);
    }

    public class ObservableList<T> extends LinkedList<T> {

        public ObservableList() {
            super();
        }

        public ObservableList(@NonNull Collection<? extends T> c) {
            super(c);
        }

        @Override
        public boolean remove(Object o) {
            int size = size();
            boolean b = super.remove(o);
            //            notifyItemInserted(headerView.size()+size-1);
            notifyDataSetChanged();
            return b;
        }

        @Override
        public T set(int index, T element) {
            T t = super.set(index, element);
            //            notifyItemChanged(index);
            notifyDataSetChanged();
            return t;
        }

        @Override
        public T remove(int index) {
            T t = super.remove(index);
            //            notifyItemRemoved(index);
            notifyDataSetChanged();
            return t;
        }

        @Override
        public void clear() {
            int size = size();
            super.clear();
            //            notifyItemRangeRemoved(headerView.size(), size);
            notifyDataSetChanged();
        }

        @Override
        public boolean addAll(int index, Collection<? extends T> c) {
            boolean b = super.addAll(index, c);
            //            notifyItemRangeInserted(headerView.size()+index, c.size());
            notifyDataSetChanged();
            return b;
        }

        @Override
        public void add(int index, T element) {
            super.add(index, element);
            //            notifyItemInserted(index);
            notifyDataSetChanged();
        }

        @Override
        public boolean add(T t) {
            boolean b = super.add(t);
            //            notifyItemInserted(headerView.size()+size()-1);
            notifyDataSetChanged();
            return b;
        }
    }
}
