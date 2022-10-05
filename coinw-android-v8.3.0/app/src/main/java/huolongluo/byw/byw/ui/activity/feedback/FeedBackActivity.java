package huolongluo.byw.byw.ui.activity.feedback;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.legend.common.util.StatusBarUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import huolongluo.byw.BuildConfig;
import huolongluo.byw.R;
import huolongluo.byw.byw.net.UrlConstants;
import huolongluo.byw.user.UserInfoManager;
import huolongluo.byw.util.GsonUtil;
import huolongluo.byw.util.OkhttpManager;
import huolongluo.byw.util.SPUtils;
import huolongluo.byw.util.tip.MToast;
import huolongluo.byw.util.tip.SnackBarUtils;
import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;
import okhttp3.Request;

/**
 * Created by LS on 2018/7/15.
 */
public class FeedBackActivity extends SwipeBackActivity {

    @BindView(R.id.back_iv)
    ImageButton iv_left;
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.layoutSelectType)
    LinearLayout layoutSelectType;
    @BindView(R.id.textSuggestType)
    TextView textSuggestType;
    @BindView(R.id.editContent)
    EditText editContent;
    @BindView(R.id.tv_submit)
    TextView tvSubmit;
    @BindView(R.id.gridview)
    GridView gridview;
    private List<FeedList> mData = new ArrayList<>();
    private Adapter adapter;
    Unbinder unbinder;
    private SwipeBackLayout mSwipeBackLayout;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置状态栏背景及字体颜色
        StatusBarUtils.setStatusBar(this);
        setContentView(R.layout.activity_feedback);
        unbinder = ButterKnife.bind(this);
        mSwipeBackLayout = getSwipeBackLayout();
        mSwipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);
        mSwipeBackLayout.setEdgeSize(200);//
        initToolBar();
        getType();
        iv_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //eventClick(tv_right).subscribe(o ->{
        //     startActivity(FeedHistoryActivity.class);
        // });
        layoutSelectType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FeedDialog dialog = new FeedDialog(FeedBackActivity.this, mData);
                dialog.setItemClickListener(new FeedDialog.OnItemClickListener() {
                    @Override
                    public void onItem(int i) {
                        textSuggestType.setText(mData.get(i).getName());
                        ////  type = mData.get(i).getType();
                    }
                });
                dialog.initDialog().show();
            }
        });
        tvSubmit.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                save();
            }
        });
        adapter = new Adapter(mData, FeedBackActivity.this);
        gridview.setAdapter(adapter);
    }

    private void initToolBar() {
        //全屏。
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //  ImmersedStatusbarUtils.initAfterSetContentView(this, lin1); // 沉浸式
//        my_toolbar.setTitle("");
        title_tv.setText(getString(R.string.idea));
        iv_left.setVisibility(View.VISIBLE);
        iv_left.setImageResource(R.mipmap.back);
    }

    //获取问题类型
    private void getType() {
        HashMap<String, String> params = new HashMap<>();
        String loginToken = SPUtils.getLoginToken();
        if (!UserInfoManager.isLogin()) {
            return;
        }
        params.put("loginToken", UserInfoManager.getToken());
        OkhttpManager.postAsync(UrlConstants.DOMAIN + UrlConstants.GET_FEED_TYPE, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
                SnackBarUtils.ShowRed(FeedBackActivity.this, errorMsg);
                e.printStackTrace();
            }

            @Override
            public void requestSuccess(String result) {
                Feed feed = null;
                try {
                    feed = GsonUtil.json2Obj(result, Feed.class);
                    if (feed.isResult()) {
                        if (mData == null) {
                            mData = new ArrayList<>();
                        }
                        mData.clear();
                        mData = feed.getList();
                        //  textSuggestType.setText(mData.get(0).getName());
                        //  adapter = new Adapter(mData,FeedBackActivity.this);
                        //gridview.setAdapter(adapter);
                        adapter.setData(mData);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    SnackBarUtils.ShowRed(FeedBackActivity.this, getString(R.string.ser_exp));
                }
            }
        });
    }

    //新增问题反馈
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void save() {
        if (editContent.getText().toString().isEmpty()) {
            MToast.showButton(FeedBackActivity.this, getString(R.string.tjnrbu), 2);
            return;
        }
        if (editContent.getText().toString().length() < 10) {
            MToast.showButton(FeedBackActivity.this, getString(R.string.nrbsy), 2);
            return;
        }
        if (adapter.chechPosition == -1) {
            MToast.showButton(FeedBackActivity.this, getString(R.string.select_bq), 2);
            return;
        }
     /*
        HashMap<String,String> params = new HashMap<>();
      RSACipher rsaCipher = new RSACipher();
       // String body = "content="+ URLEncoder.encode(editContent.getText().toString())+"&type="+URLEncoder.encode(String.valueOf(type));
        String body = "type="+URLEncoder.encode(String.valueOf(adapter.chech));
        try {
            String body1 = rsaCipher.encrypt(body,UrlConstants.publicKeys);
            params.put("body", body1);




            params.put("loginToken",loginToken);
            params.put("content",editContent.getText().toString());
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
        }
*/
        Map<String, String> params = new HashMap<>();
        params.put("type", adapter.chech + "");
        params.put("appVersion", BuildConfig.VERSION_CODE+"");
        params.put("operationType", android.os.Build.MODEL);
        params = OkhttpManager.encrypt(params);
        params.put("loginToken", UserInfoManager.getToken());
        params.put("content", editContent.getText().toString());
        OkhttpManager.postAsync(UrlConstants.DOMAIN + UrlConstants.SAVE_FEED, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
                //  MToast.show(FeedBackActivity.this,errorMsg,1);
                SnackBarUtils.ShowRed(FeedBackActivity.this, errorMsg);
                e.printStackTrace();
            }

            @Override
            public void requestSuccess(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    boolean result1 = jsonObject.getBoolean("result");
                    int code = jsonObject.getInt("code");
                    if (code == 0) {
                        MToast.showButton(FeedBackActivity.this, getString(R.string.submit_suc), 2);
                        SnackBarUtils.ShowBlue(FeedBackActivity.this, getString(R.string.submit_suc));
                        finish();
                    } else {
                        MToast.showButton(FeedBackActivity.this, getString(R.string.submit_fail), 2);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    MToast.showButton(FeedBackActivity.this, getString(R.string.ser_exp), 2);
                }
            }
        });
    }

    static class Adapter extends BaseAdapter {

        private List<FeedList> mData = new ArrayList<>();
        Context context;
        public int chechPosition = -1;
        public int chech = -1;

        public Adapter(List<FeedList> mData) {
            this.mData = mData;
        }

        public Adapter(List<FeedList> mData, Context context) {
            this.mData.clear();
            this.mData.addAll(mData);
            this.context = context;
        }

        public void setData(List<FeedList> mData) {
            this.mData.clear();
            this.mData.addAll(mData);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public FeedList getItem(int position) {
            if (position < 0 || mData == null || mData.isEmpty()) {
                return null;
            }
            return mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.feedback_item, null);
            }
            TextView text_tv = convertView.findViewById(R.id.text_tv);
            text_tv.setText(mData.get(position).getName());
            RelativeLayout rn_bn = convertView.findViewById(R.id.rn_bn);
            if (chechPosition == position) {
                rn_bn.setBackgroundResource(R.drawable.feedback_bg1);
                text_tv.setTextColor(context.getResources().getColor(R.color.white));
            } else {
                rn_bn.setBackgroundResource(R.drawable.feedback_bg);
                text_tv.setTextColor(context.getResources().getColor(R.color.ff222222));
            }
            rn_bn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    chechPosition = position;
                    chech = mData.get(position).getType();
                    notifyDataSetChanged();
                }
            });
            return convertView;
        }
    }
}
