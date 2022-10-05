package huolongluo.byw.byw.inform.activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import huolongluo.byw.R;
import huolongluo.byw.byw.base.BaseActivity;
import huolongluo.byw.byw.net.UrlConstants;
import huolongluo.byw.helper.INetCallback;
import huolongluo.byw.helper.OKHttpHelper;
import huolongluo.byw.io.AppConstants;
import huolongluo.byw.model.ScrollNoticeBean;
import huolongluo.byw.model.ScrollNoticeItemBean;
import huolongluo.byw.reform.home.activity.NewsWebviewActivity;
import huolongluo.byw.user.UserInfoManager;
import huolongluo.byw.util.GsonUtil;
import huolongluo.byw.util.SPUtils;
import huolongluo.byw.util.tip.MToast;
import huolongluo.byw.util.tip.SnackBarUtils;
import huolongluo.byw.widget.pulltorefresh.PullToRefreshListView;
/**
 * 滚动公告
 */
public class ScrollNoticeActivity extends BaseActivity{
    @BindView(R.id.net_error_view)
    FrameLayout net_error_view;
    @BindView(R.id.listView)
    PullToRefreshListView listView;
    private List<ScrollNoticeItemBean> farticleBeanList = new ArrayList<>();
    private NoticeAdapter adapter;
    private int currentPage = 1;
    @Override
    protected int getContentViewId() {
        return R.layout.activity_scroll_notifice;
    }

    @Override
    protected void injectDagger() {
    }

    @Override
    protected String initTitle() {
        return getString(R.string.notice);
    }

    @Override
    protected void initViewsAndEvents() {
        initView();
        initData();
    }

    private void initData() {
        currentPage = 1;
        net_error_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentPage = 1;
                getScrollNotice(currentPage);
            }
        });
        getScrollNotice(currentPage);
    }

    private boolean isreFreshing;

    private void initView() {
        net_error_view.setVisibility(View.GONE);
        adapter = new NoticeAdapter(this, farticleBeanList);
        listView.setAdapter(adapter);
        listView.setPullLoadEnable(false);
        listView.setPullRefreshEnable(false);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    TextView notice_title_tv = view.findViewById(R.id.notice_title_tv);
                    if (notice_title_tv != null) {
                        notice_title_tv.setTextColor(ContextCompat.getColor(ScrollNoticeActivity.this,R.color.text_main2));
                    }
                    if (position <= 0) {
                        return;
                    }
                    ScrollNoticeItemBean farticle = farticleBeanList.get((int) id);
                    if (farticle == null) {
                        return;
                    }
                    //由历史原因，暂时这样处理
                    //更新状态
                    updateRead(farticle.getId());
                    if (farticle.getGoOut() && !TextUtils.isEmpty(farticle.getOutUrl())) {
                        Intent intent = new Intent(ScrollNoticeActivity.this, NewsWebviewActivity.class);
                        intent.putExtra("url", farticle.getOutUrl());
                        intent.putExtra("token", UserInfoManager.getToken());
                        intent.putExtra("title", farticle.getTitle());
                        ScrollNoticeActivity.this.startActivity(intent);
                    } else {
                        Intent intent = new Intent(ScrollNoticeActivity.this, NoticeDetailActivity.class);
                        intent.putExtra("id", farticle.getId());
                        startActivity(intent);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        listView.setPullToListViewListener(new PullToRefreshListView.PullToListViewListener() {
            @Override
            public void onRefresh() {
                isreFreshing = true;
                currentPage = 1;
                getScrollNotice(currentPage);
            }

            @Override
            public void onLoadMore() {
                getScrollNotice(currentPage);
            }
        });
    }
    public void updateRead(int fid) {
        String s = SPUtils.getString(this,"scrollNotice","");
        StringBuilder builder = new StringBuilder();
        if (s != null && s.length() > 0) {
            builder.append(s);
        }
        if (!builder.toString().contains("*" + fid + "*")) {
            builder.append("*" + fid + "*");
        }
        SPUtils.saveString(this, "scrollNotice", builder.toString());
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void  getScrollNotice(int page) {
        Map<String, Object> params = new HashMap<>();
        params.put("size", AppConstants.UI.DEFAULT_PAGE_SIZE);
        String url = UrlConstants.GET_SCROLL_NOTICE;
        netTags.add(url);
        OKHttpHelper.getInstance().get(url, params, getScrollNoticeCallback, new TypeToken<ScrollNoticeBean>() {
        }.getType());
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }
    private INetCallback<ScrollNoticeBean> getScrollNoticeCallback = new INetCallback<ScrollNoticeBean>() {
        @Override
        public void onSuccess(ScrollNoticeBean bean)  {
            if (net_error_view.getVisibility() != View.GONE) {
                net_error_view.setVisibility(View.GONE);
            }
            if (isreFreshing) {
                MToast.show(ScrollNoticeActivity.this, getString(R.string.refresh_suc), 1);
                isreFreshing = false;
            }
            if (currentPage == 1) {
                farticleBeanList.clear();
                listView.stopRefresh();
            } else {
                listView.stopLoadMore();
            }
            try {
                if(bean==null) return;
                if(bean.getCode()==0){
                    if (bean.getData() != null && bean.getData().size() > 0) {
                        farticleBeanList.addAll(bean.getData());
                        if (farticleBeanList != null) {
                            currentPage++;
                            adapter.notifyDataSetChanged();
                        }
                    }
                }else{
                    if(!TextUtils.isEmpty(bean.getMessage())){
                        MToast.show(ScrollNoticeActivity.this,bean.getMessage());
                    }
                }
            } catch (Exception e) {
                // MToast.show(getActivity(), "服务器异常，请稍后再试", 1);
                SnackBarUtils.ShowRed(ScrollNoticeActivity.this, getString(R.string.service_expec));
                e.printStackTrace();
            }
            if (adapter.getCount() > 0) {
                net_error_view.setVisibility(View.INVISIBLE);
            } else {
                net_error_view.setVisibility(View.GONE);
            }
        }

        @Override
        public void onFailure(Exception e) {
            isreFreshing = false;
            if (adapter.getCount() > 0) {
                net_error_view.setVisibility(View.GONE);
            } else {
                net_error_view.setVisibility(View.VISIBLE);
            }
        }
    };

    private static class NoticeAdapter extends BaseAdapter {
        private Context mContext;
        private List<ScrollNoticeItemBean> farticleBeanList = new ArrayList<>();
        private String isRedayList;

        public NoticeAdapter(Context mContext, List<ScrollNoticeItemBean> list) {
            isRedayList = SPUtils.getString(mContext, "scrollNotice", null);
            this.mContext = mContext;
            this.farticleBeanList = list;
        }

        @Override
        public int getCount() {
            return farticleBeanList == null ? 0 : farticleBeanList.size();
        }

        @Override
        public Object getItem(int position) {
            if (position < getCount() && position > -1) {
                return farticleBeanList == null || farticleBeanList.isEmpty() ? null : farticleBeanList.get(position);
            }
            return null;
        }

        @Override
        public void notifyDataSetChanged() {
            isRedayList = SPUtils.getString(mContext, "scrollNotice", null);
            super.notifyDataSetChanged();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = View.inflate(mContext, R.layout.notice_list_item, null);
                holder = new ViewHolder();
                holder.notice_title_tv = convertView.findViewById(R.id.notice_title_tv);
                holder.notice_time_tv = convertView.findViewById(R.id.notice_time_tv);
                holder.tvContent = convertView.findViewById(R.id.tvContent);
                holder.tvContent.setVisibility(View.VISIBLE);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            if (isRedayList != null && isRedayList.contains("*" + farticleBeanList.get(position).getId() + "*")) {
                holder.notice_title_tv.setTextColor(ContextCompat.getColor(mContext,R.color.text_main2));
            } else {
                holder.notice_title_tv.setTextColor(ContextCompat.getColor(mContext,R.color.text_main));
            }
            holder.notice_title_tv.setText(farticleBeanList.get(position).getTitle());
            holder.tvContent.setText(farticleBeanList.get(position).getContent());
            holder.notice_time_tv.setText(farticleBeanList.get(position).getDatetime());
            return convertView;
        }

        private class ViewHolder {
            private TextView notice_title_tv;
            private TextView notice_time_tv;
            private TextView tvContent;
        }
    }
}
