package huolongluo.byw.byw.ui.activity.feedback;
import android.os.Build;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import butterknife.BindView;
import huolongluo.byw.R;
import huolongluo.byw.byw.base.BaseActivity;
import huolongluo.byw.byw.net.UrlConstants;
import huolongluo.byw.io.AppConstants;
import huolongluo.byw.log.Logger;
import huolongluo.byw.util.GsonUtil;
import huolongluo.byw.util.ImmersedStatusbarUtils;
import huolongluo.byw.util.OkhttpManager;
import huolongluo.byw.util.RSACipher;
import huolongluo.byw.util.SPUtils;
import okhttp3.Request;
/**
 * Created by LS on 2018/7/15.
 */
public class FeedHistoryActivity extends BaseActivity {
    @BindView(R.id.iv_left)
    ImageView iv_left;
    @BindView(R.id.toolbar_center_title)
    TextView toolbar_center_title;
    @BindView(R.id.my_toolbar)
    Toolbar my_toolbar;
    @BindView(R.id.lin1)
    RelativeLayout lin1;
    @BindView(R.id.lv_content)
    ListView listView;
    private List<FeedListHis> mData;
    private FeedHisAdapter mAdapter;

    /**
     * bind layout resource file
     */
    @Override
    protected int getContentViewId() {
        return R.layout.activity_feedback_his;
    }

    /**
     * Dagger2 use in your application module(not used in 'base' module)
     */
    @Override
    protected void injectDagger() {
    }

    /**
     * init views and events here
     */
    @Override
    protected void initViewsAndEvents() {
        initToolBar();
        getData();
        eventClick(iv_left).subscribe(o -> {
            finish();
        }, throwable -> {
            Logger.getInstance().error(throwable);
        });
    }

    private void initToolBar() {
        lin1.setVisibility(View.VISIBLE);
        //全屏。
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ImmersedStatusbarUtils.initAfterSetContentView(this, lin1); // 沉浸式
//        my_toolbar.setTitle("");
        toolbar_center_title.setText(getString(R.string.submit_his));
        iv_left.setVisibility(View.VISIBLE);
        iv_left.setImageResource(R.mipmap.back);
        setSupportActionBar(my_toolbar);
    }

    //获取反馈历史
    private void getData() {
        HashMap<String, String> params = new HashMap<>();
        String loginToken = SPUtils.getLoginToken();
        if (loginToken.isEmpty()) {
            return;
        }
        params.put("loginToken", loginToken);
        netTags.add(UrlConstants.DOMAIN + UrlConstants.GET_FEED_HIS);
        OkhttpManager.postAsync(UrlConstants.DOMAIN + UrlConstants.GET_FEED_HIS, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
            }

            @Override
            public void requestSuccess(String result) {
                FeedHistory history = null;
                try {
                    history = GsonUtil.json2Obj(result, FeedHistory.class);
                    if (history.isResult()) {
                        if (mData == null) {
                            mData = new ArrayList<>();
                        }
                        mData.clear();
                        mData = history.getList();
                        setAdapter();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setAdapter() {
        mAdapter = new FeedHisAdapter(this, mData, clickListener);
        listView.setAdapter(mAdapter);
    }

    FeedHisAdapter.OperationClickListener clickListener = new FeedHisAdapter.OperationClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void delete(int position) {
            Detele(String.valueOf(mData.get(position).getId()));
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void Detele(String id) {
        HashMap<String, String> params = new HashMap<>();
        String loginToken = SPUtils.getLoginToken();
        if (loginToken.isEmpty()) {
            return;
        }
        RSACipher rsaCipher = new RSACipher();
        String body = "id=" + URLEncoder.encode(id);
        try {
            String body1 = rsaCipher.encrypt(body, AppConstants.KEY.PUBLIC_KEY);
            params.put("body", body1);
            params.put("loginToken", loginToken);
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
//        params.put("id",id);
        netTags.add(UrlConstants.DOMAIN + UrlConstants.DETELE_FEED_HIS);
        OkhttpManager.postAsync(UrlConstants.DOMAIN + UrlConstants.DETELE_FEED_HIS, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
            }

            @Override
            public void requestSuccess(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    boolean result1 = jsonObject.getBoolean("result");
                    int code = jsonObject.getInt("code");
                    String value = jsonObject.getString("value");
                    if (code == 0) {
                        showMessage(value, 2);
                        getData();
                    } else {
                        showMessage(value, 2);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
