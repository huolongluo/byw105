package com.legend.modular_contract_sdk.widget;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.lxj.xpopup.R;
import com.lxj.xpopup.core.AttachPopupView;
import com.lxj.xpopup.interfaces.OnSelectListener;
import com.lxj.xpopup.widget.VerticalRecyclerView;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

/**
 * Description: Attach类型的列表弹窗(自定义，带选中效果)
 * Create by dance, at 2018/12/12
 */
public class CustomListPopupView extends AttachPopupView {
    RecyclerView recyclerView;
    protected int bindLayoutId;
    protected int bindItemLayoutId;
    protected int contentGravity = Gravity.CENTER;
    private int mCheckPosition;

    /**
     *
     * @param context
     * @param bindLayoutId layoutId 要求layoutId中必须有一个id为recyclerView的RecyclerView
     * @param bindItemLayoutId itemLayoutId 条目的布局id，要求布局中必须有id为iv_image的ImageView，和id为tv_text的TextView
     */
    public CustomListPopupView(@NonNull Context context, int bindLayoutId, int bindItemLayoutId, int checkPosition) {
        super(context);
        this.bindLayoutId = bindLayoutId;
        this.bindItemLayoutId = bindItemLayoutId;
        this.mCheckPosition = checkPosition;
        addInnerContent();
    }

    @Override
    protected int getImplLayoutId() {
        return bindLayoutId == 0 ? R.layout._xpopup_attach_impl_list : bindLayoutId;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        recyclerView = findViewById(R.id.recyclerView);
        if(bindLayoutId!=0){
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        }

        QuickAdapter adapter = new QuickAdapter(bindItemLayoutId == 0 ? R.layout._xpopup_adapter_text : bindItemLayoutId, Arrays.asList(data), mCheckPosition);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull @NotNull BaseQuickAdapter<?, ?> adapter, @NonNull @NotNull View view, int position) {
                if (selectListener != null) {
                    selectListener.onSelect(position, (String) adapter.getData().get(position));
                }
                if (popupInfo.autoDismiss) dismiss();
            }
        });
        recyclerView.setAdapter(adapter);
        applyTheme();
    }

    protected void applyTheme(){
        if(bindLayoutId==0) {
            if(popupInfo.isDarkTheme){
                applyDarkTheme();
            }else {
                applyLightTheme();
            }
        }
    }

    @Override
    protected void applyDarkTheme() {
        super.applyDarkTheme();
        ((VerticalRecyclerView)recyclerView).setupDivider(true);
    }

    @Override
    protected void applyLightTheme() {
        super.applyLightTheme();
        ((VerticalRecyclerView)recyclerView).setupDivider(false);
    }

    String[] data;
//    int[] iconIds;

//    public CustomListPopupView setStringData(String[] data, int[] iconIds) {
//        this.data = data;
//        this.iconIds = iconIds;
//        return this;
//    }

    public CustomListPopupView setStringData(String[] data) {
        this.data = data;
        return this;
    }

    public CustomListPopupView setContentGravity(int gravity) {
        this.contentGravity = gravity;
        return this;
    }

    private OnSelectListener selectListener;

    public CustomListPopupView setOnSelectListener(OnSelectListener selectListener) {
        this.selectListener = selectListener;
        return this;
    }

    public class QuickAdapter extends BaseQuickAdapter<String, BaseViewHolder> {
        private int mCheckPosition;

        public QuickAdapter(int layoutResId, @Nullable List<String> data, int checkPosition) {
            super(layoutResId, data);
            mCheckPosition = checkPosition;
        }

        @Override
        protected void convert(@NotNull BaseViewHolder holder, String s) {
            holder.setText(R.id.tv_text, s);
//            if (iconIds != null && iconIds.length > position) {
//                holder.getView(R.id.iv_image).setVisibility(VISIBLE);
//                holder.getView(R.id.iv_image).setBackgroundResource(iconIds[position]);
//            } else {
//                holder.getView(R.id.iv_image).setVisibility(GONE);
//            }
            holder.getView(R.id.iv_image).setVisibility(GONE);

            if(bindItemLayoutId==0 ){
                if(popupInfo.isDarkTheme){
                    holder.<TextView>getView(R.id.tv_text).setTextColor(getResources().getColor(R.color._xpopup_white_color));
                }else {
                    holder.<TextView>getView(R.id.tv_text).setTextColor(getResources().getColor(R.color._xpopup_dark_color));
                }
                if (holder.getAdapterPosition() == mCheckPosition) {
                    holder.<TextView>getView(R.id.tv_text).setTextColor(Color.parseColor("#0084FF"));
                }
//                LinearLayout linearLayout = holder.getView(R.id._ll_temp);
//                linearLayout.setGravity(contentGravity);
            }
        }
    }
}
