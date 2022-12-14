package huolongluo.byw.superAdapter.list;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import com.jakewharton.rxbinding.view.RxView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import huolongluo.byw.log.Logger;
import huolongluo.byw.superAdapter.BaseFilter;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Wrapper of BaseAdapter.
 */
public abstract class BaseSuperAdapter<T, H extends BaseViewHolder> extends BaseAdapter implements Filterable {

    private BaseFilter<T> filter;
    protected Context mContext;
    protected int mLayoutResId;
    protected List<T> mList;
    protected IMultiItemViewType<T> mMultiItemViewType;

    public BaseSuperAdapter(Context context, List<T> data, int layoutResId) {
        this.mContext = context;
        this.mList = data == null ? new ArrayList<T>() : new ArrayList<T>(data);
        this.mLayoutResId = layoutResId;
    }

    public BaseSuperAdapter(Context context, List<T> data, IMultiItemViewType<T> multiItemViewType) {
        this.mContext = context;
        this.mList = data == null ? new ArrayList<T>() : new ArrayList<T>(data);
        this.mMultiItemViewType = multiItemViewType;
    }

    @Override
    public int getCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public T getItem(int position) {
        if (position < 0 || mList == null || mList.isEmpty()) {
            return null;
        }
        if (position >= mList.size()) {
            return null;
        }
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        if (mMultiItemViewType != null) {
            return mMultiItemViewType.getViewTypeCount();
        }
        return 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (mMultiItemViewType != null) {
            return mMultiItemViewType.getItemViewType(position, mList.get(position));
        }
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final H viewHolder = onCreate(getItemViewType(position), convertView, parent);
        T item = getItem(position);
        onBind(getItemViewType(position), viewHolder, position, item);
        return viewHolder.getItemView();
    }

    protected abstract H onCreate(int viewType, View convertView, ViewGroup parent);

    /**
     * Abstract method for binding view and data.
     *
     * @param viewType {@link #getItemViewType}
     * @param holder ViewHolder
     * @param position position
     * @param item data
     */
    protected abstract void onBind(int viewType, H holder, int position, T item);

    public void add(T item) {
        mList.add(item);
        notifyDataSetChanged();
    }

    public void add(T item, boolean isChanged) {
        mList.add(item);
        if (isChanged) {
            notifyDataSetChanged();
        }
    }

    public void add(int index, T item) {
        mList.add(index, item);
        notifyDataSetChanged();
    }

    public void addAll(List<T> items) {
        mList.addAll(items);
        notifyDataSetChanged();
    }

    public void remove(T item) {
        mList.remove(item);
        notifyDataSetChanged();
    }

    public void remove(int index) {
        mList.remove(index);
        notifyDataSetChanged();
    }

    public void remove(int index, boolean isChange) {
        mList.remove(index);
        if (isChange) {
            notifyDataSetChanged();
        }
    }

    public void set(T oldItem, T newItem) {
        set(mList.indexOf(oldItem), newItem);
    }

    public void set(int index, T item) {
        mList.set(index, item);
        notifyDataSetChanged();
    }

    public void replaceAll(List<T> items) {
        mList.clear();
        addAll(items);
    }

    public boolean contains(T item) {
        return mList.contains(item);
    }

    public void clear() {
        mList.clear();
        notifyDataSetChanged();
    }

    public List<T> getAllData() {
        return mList;
    }

    @Override
    public BaseFilter<T> getFilter() {
        if (filter == null) {
            filter = getCustomFilter();
        }
        return filter;
    }

    /**
     * ???????????????????????????????????????????????????????????????Filter??????????????????????????????????????????????????????
     */
    public void onRefreshFilterData() {
        getFilter().setUnfilteredData(mList);
    }

    /**
     * ??????Filter?????????
     */
    private BaseFilter<T> getCustomFilter() {
        BaseFilter<T> customFilter = new BaseFilter<T>(mList) {
            @Override
            public List<T> onFilterData(String prefixString, List<T> unfilteredValues) {
                //????????????????????????????????????????????????
                return onFilterRule(prefixString, unfilteredValues);
            }

            @Override
            protected void publishResults(CharSequence constraint, Filter.FilterResults results) {
                mList = (List<T>) results.values;
                if (results.count > 0) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
        return customFilter;
    }

    /**
     * ???????????????????????????,????????????????????????
     */
    public List<T> onFilterRule(String prefixString, List<T> unfilteredValues) {
        return null;
    }

    public Observable<Void> eventClick(View view) {
        return eventClick(view, 1000);
    }

    public Observable<Void> eventClick(View view, int milliseconds) {
        return RxView.clicks(view).throttleFirst(milliseconds, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread()).doOnError(throwable -> Logger.getInstance().error(throwable));
    }
}
