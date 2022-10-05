package huolongluo.byw.byw.inform.activity;

import android.annotation.TargetApi;
import android.app.AlertDialog;
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
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.android.legend.ui.home.notice.MailDetailsActivity;

import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import huolongluo.byw.R;
import huolongluo.byw.byw.base.BaseActivity;
import huolongluo.byw.byw.inform.bean.MailBean;
import huolongluo.byw.byw.net.UrlConstants;
import huolongluo.byw.io.AppConstants;
import huolongluo.byw.reform.home.activity.NewsWebviewActivity;
import huolongluo.byw.user.UserInfoManager;
import huolongluo.byw.util.OkhttpManager;
import huolongluo.byw.util.RSACipher;
import huolongluo.byw.util.tip.DialogUtils;
import huolongluo.byw.util.tip.MToast;
import huolongluo.byw.util.tip.SnackBarUtils;
import huolongluo.byw.widget.pulltorefresh.PullToRefreshListView;
import huolongluo.bywx.helper.AppHelper;
import okhttp3.Request;
/**
 * 站内信
 */
public class MailActivity extends BaseActivity {
    private PullToRefreshListView listView;
    private List<MailBean> mailBeanList = new ArrayList<>();
    private NoticeAdapter adapter;
    private int currentPage = 1;
    private FrameLayout net_error_view;
    private boolean isreFreshing;
    @Override
    protected int getContentViewId() {
        return R.layout.activity_mail;
    }

    @Override
    protected void injectDagger() {
    }

    @Override
    protected String initTitle() {
        return getString(R.string.mail);
    }

    @Override
    protected String initRightText() {
        return getString(R.string.already_read);
    }

    @Override
    protected int initRightLeftDrawable() {
        return R.mipmap.ic_read;
    }

    @Override
    protected View.OnClickListener initRightTextClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtils.getInstance().showTwoButtonDialog(MailActivity.this, getString(R.string.sure_mark_all_read), getString(R.string.cz42), getString(R.string.cz43), new DialogUtils.onBnClickListener() {
                    @Override
                    public void onLiftClick(AlertDialog dialog, View view) {
                        AppHelper.dismissDialog(dialog);
                    }

                    @Override
                    public void onRightClick(AlertDialog dialog, View view) {
                        AppHelper.dismissDialog(dialog);
                        setAllReady();
                    }
                });
            }
        };
    }

    @Override
    protected void initViewsAndEvents() {
        initView();
        initData();
    }
    private void initView() {
        net_error_view =findViewById(R.id.net_error_view);
        net_error_view.setVisibility(View.GONE);
        listView = findViewById(R.id.listView);
        adapter = new NoticeAdapter(this, mailBeanList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    TextView notice_title_tv = view.findViewById(R.id.notice_title_tv);
                    if (notice_title_tv != null) {
                        notice_title_tv.setTextColor(ContextCompat.getColor(MailActivity.this,R.color.text_main2));
                    }
                    if (position <= 0) {
                        return;
                    }
                    MailBean mail = mailBeanList.get((int) id);
                    if (mail == null) {
                        return;
                    }
                    mail.setfStatus(2);
                    adapter.notifyDataSetChanged();
                    Intent intent=new Intent(MailActivity.this, MailDetailsActivity.class);
                    intent.putExtra("fId",mail.getfId());
                    startActivity(intent);
//
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        listView.setPullLoadEnable(true);
        listView.setPullToListViewListener(new PullToRefreshListView.PullToListViewListener() {
            @Override
            public void onRefresh() {
                isreFreshing = true;
                currentPage = 1;
                farticleList(currentPage);
            }

            @Override
            public void onLoadMore() {
                farticleList(currentPage);
            }
        });
    }
    private void initData() {
        currentPage = 1;
        net_error_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentPage = 1;
                farticleList(currentPage);
            }
        });
        farticleList(currentPage);
    }

    private void farticleList(int page) {
        OkhttpManager.get(UrlConstants.DOMAIN + UrlConstants.MAIL_LIST+"?pageNum=" + page+"&pageSize="+AppConstants.UI.DEFAULT_PAGE_SIZE, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
                isreFreshing = false;
                SnackBarUtils.ShowRed(MailActivity.this, errorMsg);
                if (adapter.getCount() > 0) {
                    net_error_view.setVisibility(View.GONE);
                } else {
                    net_error_view.setVisibility(View.VISIBLE);
                }
                e.printStackTrace();
            }

            @Override
            public void requestSuccess(String result) {
                if (net_error_view.getVisibility() != View.GONE) {
                    net_error_view.setVisibility(View.GONE);
                }
                if (isreFreshing) {
                    MToast.show(MailActivity.this, getString(R.string.refresh_suc), 1);
                    isreFreshing = false;
                }
                if (currentPage == 1) {
                    mailBeanList.clear();
                    listView.stopRefresh();
                } else {
                    listView.stopLoadMore();
                }
                try {
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    int code = jsonObject.getIntValue("code");
                    String value = jsonObject.getString("message");
                    if (code == 200) {
                        JSONObject jsonData=jsonObject.getJSONObject("data");
                        JSONObject jsonUserLetter=jsonData.getJSONObject("userLetter");

                        int totalPage = jsonUserLetter.getIntValue("pages");
                        if (totalPage == 1) {
                            listView.setPullLoadEnable(false);
                        } else if (currentPage >= totalPage) {
                            if(currentPage>1){
                                MToast.show(MailActivity.this, getString(R.string.adla), 2);
                            }
                            listView.setPullLoadEnable(false);
                        } else {
                            listView.setPullLoadEnable(true);
                        }
                        JSONArray jsonArray = jsonUserLetter.getJSONArray("list");
                        List<MailBean> list = JSONObject.parseArray(jsonArray.toString(), MailBean.class);
                        if (list != null && list.size() > 0) {
                            mailBeanList.addAll(list);
                            if (mailBeanList != null) {
                                currentPage++;
                                adapter.notifyDataSetChanged();
                            }
                        }
                    } else if(!TextUtils.isEmpty(value)){
                        MToast.show(MailActivity.this, value + "", 2);
                    }
                } catch (Exception e) {
                    SnackBarUtils.ShowRed(MailActivity.this, getString(R.string.service_expec));
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

    public void setAllReady() {
        OkhttpManager.get(UrlConstants.DOMAIN + UrlConstants.MAIL_SET_ALL_READ + "?loginToken=" + UserInfoManager.getToken()
                , new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
            }

            @Override
            public void requestSuccess(String result) {
                try {
                    com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(result);
                    if(jsonObject.getIntValue("code")==200){
                        currentPage=1;
                        farticleList(currentPage);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    private static class NoticeAdapter extends BaseAdapter {
        private Context mContext;
        private List<MailBean> farticleBeanList = new ArrayList<>();

        public NoticeAdapter(Context mContext, List<MailBean> list) {
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
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            NoticeAdapter.ViewHolder holder;
            if (convertView == null) {
                convertView = View.inflate(mContext, R.layout.notice_list_item, null);
                holder = new NoticeAdapter.ViewHolder();
                holder.notice_title_tv = convertView.findViewById(R.id.notice_title_tv);
                holder.notice_time_tv = convertView.findViewById(R.id.notice_time_tv);
                convertView.setTag(holder);
            } else {
                holder = (NoticeAdapter.ViewHolder) convertView.getTag();
            }
            if (farticleBeanList.get(position).getfStatus()==2) {
                holder.notice_title_tv.setTextColor(ContextCompat.getColor(mContext,R.color.text_main2));
            } else {
                holder.notice_title_tv.setTextColor(ContextCompat.getColor(mContext,R.color.text_main));
            }
            holder.notice_title_tv.setText(farticleBeanList.get(position).getfTitle());
            holder.notice_time_tv.setText(farticleBeanList.get(position).getfCreateDate());
            return convertView;
        }

        private class ViewHolder {
            private TextView notice_title_tv;
            private TextView notice_time_tv;
        }
    }
}
