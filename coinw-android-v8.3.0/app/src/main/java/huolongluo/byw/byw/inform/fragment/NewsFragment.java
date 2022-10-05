package huolongluo.byw.byw.inform.fragment;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.beiing.roundimage.RoundImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import huolongluo.byw.R;
import huolongluo.byw.byw.inform.activity.NoticeDetailActivity;
import huolongluo.byw.byw.inform.bean.FarticleBean;
import huolongluo.byw.byw.net.UrlConstants;
import huolongluo.byw.reform.home.activity.NewsWebviewActivity;
import huolongluo.byw.user.UserInfoManager;
import huolongluo.byw.util.OkhttpManager;
import huolongluo.byw.util.SPUtils;
import huolongluo.byw.util.tip.MToast;
import huolongluo.byw.util.tip.SnackBarUtils;
import huolongluo.byw.widget.pulltorefresh.PullToRefreshListView;
import okhttp3.Request;
/**
 * Created by Administrator on 2018/9/17 0017.
 */
public class NewsFragment extends Fragment {
    private RelativeLayout no_data_tv;
    private View view;
    private PullToRefreshListView listView;
    private List<FarticleBean> farticleBeanList = new ArrayList<>();
    private NewsAdapter adapter;
    private int currentPage = 1;
    private FrameLayout net_error_view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_news, container, false);
        initView();
        initData();
        return view;
    }

    private void initView() {
        no_data_tv = view.findViewById(R.id.no_data_tv);
        net_error_view = view.findViewById(R.id.net_error_view);
        net_error_view.setVisibility(View.GONE);
        listView = view.findViewById(R.id.listView);
    }

    private boolean isreFreshing;

    private void initData() {
        net_error_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentPage = 1;
                farticleList(currentPage);
            }
        });
        adapter = new NewsAdapter(getActivity(), farticleBeanList);
        listView.setAdapter(adapter);
        listView.setPullLoadEnable(true);
        //  listView.setEmptyView(no_data_tv);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    TextView notice_title_tv = view.findViewById(R.id.notice_title_tv);
                    if (notice_title_tv != null) {
                        notice_title_tv.setTextColor(ContextCompat.getColor(requireContext(),R.color.text_main2));
                    }
                    if (position <= 0) {
                        return;
                    }
                    FarticleBean farticle = farticleBeanList.get((int) id);
                    if (farticle == null) {
                        return;
                    }
                    //由历史原因，暂时这样处理
                    //更新状态
                    updateRead(farticle.getFid());
                    if (farticle.goOut && !TextUtils.isEmpty(farticle.outUrl)) {
                        Intent intent = new Intent(getActivity(), NewsWebviewActivity.class);
                        intent.putExtra("url", farticle.getOutUrl());
                        intent.putExtra("token", UserInfoManager.getToken());
                        intent.putExtra("title", farticle.getFtitle());
                        getActivity().startActivity(intent);
                    } else {
                        Intent intent = new Intent(getActivity(), NoticeDetailActivity.class);
                        intent.putExtra("id", farticle.getFid());
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
                listView.setPullLoadEnable(true);
                listView.stopLoadMore();
                currentPage = 1;
                farticleList(currentPage);
            }

            @Override
            public void onLoadMore() {
                listView.stopRefresh();
                farticleList(currentPage);
            }
        });
        farticleList(currentPage);
    }

    public void updateRead(int fid) {
        String s = SPUtils.getDiffString(getActivity());
        StringBuilder builder = new StringBuilder();
        if (s != null && s.length() > 0) {
            builder.append(s);
        }
        if (!builder.toString().contains("*" + fid + "*")) {
            builder.append("*" + fid + "*");
        }
        SPUtils.saveString(getActivity(), "notice", builder.toString());
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    public void setAllReady() {
        String s = SPUtils.getDiffString(getActivity());
        StringBuilder builder = new StringBuilder();
        if (s != null && s.length() > 0) {
            builder.append(s);
        }
        for (FarticleBean bean : farticleBeanList) {
            if (!builder.toString().contains("*" + bean.getFid() + "*")) {
                builder.append("*" + bean.getFid() + "*");
            }
        }
        SPUtils.saveString(getActivity(), "notice", builder.toString());
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void farticleList(int page) {

      /*  String body = null;
        RSACipher rsaCipher = new RSACipher();
        String page1 = URLEncoder.encode(page + "");
        String type = URLEncoder.encode(2 + "");
        body = "type=" + type + "&currentPage=" + page1;

        try {
            body = rsaCipher.encrypt(body, UrlConstants.publicKeys);

            // body=URLEncoder.encode(body);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }*/
        Map<String, String> params = new HashMap<>();
        params.put("type", "2");
        params.put("currentPage", page + "");
        params = OkhttpManager.encrypt(params);
        // params.put("body", body);
        //  DialogManager.INSTANCE.showProgressDialog(getActivity(),"");
        OkhttpManager.postAsync(UrlConstants.DOMAIN + UrlConstants.FAR_TICLE_LIST, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
                //  DialogManager.INSTANCE.dismiss();
                //    MToast.show(getActivity(),"网络请求超时，请稍后重试",1);
                SnackBarUtils.ShowRed(getActivity(), getString(R.string.net_timeout1));
                if (adapter.getCount() > 0) {
                    net_error_view.setVisibility(View.GONE);
                } else {
                    net_error_view.setVisibility(View.VISIBLE);
                }
                e.printStackTrace();
            }

            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void requestSuccess(String result) {
                // DialogManager.INSTANCE.dismiss();
                if (net_error_view.getVisibility() != View.GONE) {
                    net_error_view.setVisibility(View.GONE);
                }
                if (isreFreshing) {
                    MToast.show(getActivity(), getString(R.string.refresh_suc), 1);
                }
                if (currentPage == 1) {
                    farticleBeanList.clear();
                    listView.stopRefresh();
                } else {
                    listView.stopLoadMore();
                }
                try {
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    JSONArray jsonArray = jsonObject.getJSONArray("farticleList");
                    int code = jsonObject.getIntValue("code");
                    String value = jsonObject.getString("value");
                    if (code == 0) {
                        int totalPage = jsonObject.getInteger("totalPage");
                        if (totalPage == 1) {
                            listView.setPullLoadEnable(false);
                        } else if (currentPage >= totalPage) {
                            MToast.show(getActivity(), getString(R.string.adla), 2);
                            listView.setPullLoadEnable(false);
                        } else {
                            listView.setPullLoadEnable(true);
                        }
                        farticleBeanList.addAll(JSONObject.parseArray(jsonArray.toString(), FarticleBean.class));
                        if (farticleBeanList != null) {
                            currentPage++;
                            adapter.notifyDataSetChanged();
                        }
                    } else {
                        MToast.show(getActivity(), value + "", 2);
                    }
                } catch (Exception e) {
                    //MToast.show(getActivity(),"服务器异常，请稍后再试",1);
                    SnackBarUtils.ShowRed(getActivity(), getString(R.string.ser_exp));
                    e.printStackTrace();
                }
                if (adapter.getCount() > 0) {
                    net_error_view.setVisibility(View.INVISIBLE);
                } else {
                    net_error_view.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("NoticeAdapter", "onResume");
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    private static class NewsAdapter extends BaseAdapter {
        private Context mContext;
        private List<FarticleBean> farticleBeanList = new ArrayList<>();
        private String isRedayList;

        public NewsAdapter(Context mContext, List<FarticleBean> list) {
            isRedayList = SPUtils.getString(mContext, "notice", null);
            this.mContext = mContext;
            farticleBeanList = list;
        }

        public List<FarticleBean> getDataList() {
            return farticleBeanList;
        }

        @Override
        public int getCount() {
            return farticleBeanList.size();
        }

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
            isRedayList = SPUtils.getString(mContext, "notice", null);
        }

        @Override
        public Object getItem(int position) {
            //java.lang.ArrayIndexOutOfBoundsException: length=14; index=-1
            if (position < getCount() && position > -1) {
                return farticleBeanList == null || farticleBeanList.isEmpty() ? null : farticleBeanList.get(position);
            }
            return null;
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
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            if (isRedayList != null && isRedayList.contains("*" + farticleBeanList.get(position).getFid() + "*")) {
                holder.notice_title_tv.setTextColor(ContextCompat.getColor(mContext,R.color.text_main2));
            } else {
                holder.notice_title_tv.setTextColor(ContextCompat.getColor(mContext,R.color.text_main));
            }
            holder.notice_title_tv.setText(farticleBeanList.get(position).getFtitle());
            holder.notice_time_tv.setText(farticleBeanList.get(position).getFcreateTime());
            return convertView;
        }

        private class ViewHolder {
            private TextView notice_title_tv;
            private TextView notice_time_tv;
        }
    }
}
