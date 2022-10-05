package huolongluo.byw.byw.ui.fragment.maintab01.home;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.legend.ui.earn.EarnActivity;
import com.android.legend.ui.home.newCoin.NewCoinsActivity;
import com.android.legend.ui.taskcenter.TaskCenterActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.reflect.TypeToken;
import com.umeng.analytics.MobclickAgent;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import huolongluo.byw.R;
import huolongluo.byw.byw.base.BaseApp;
import huolongluo.byw.byw.net.UrlConstants;
import com.android.legend.ui.login.LoginActivity;
import huolongluo.byw.byw.ui.activity.main.MainActivity;
import huolongluo.byw.byw.ui.fragment.maintab04.MineFragment;
import huolongluo.byw.byw.ui.fragment.maintab04.WebViewActivity;
import huolongluo.byw.byw.ui.fragment.maintab05.AllFinanceFragment;
import huolongluo.byw.helper.INetCallback;
import huolongluo.byw.helper.OKHttpHelper;
import huolongluo.byw.heyue.ui.TransActionHomeFragment;
import huolongluo.byw.io.AppConstants;
import huolongluo.byw.log.Logger;
import huolongluo.byw.model.HomeDynamicResult;
import huolongluo.byw.model.result.SingleResult;
import huolongluo.byw.reform.home.activity.NewsWebviewActivity;
import huolongluo.byw.reform.home.activity.WelfareWebviewActivity;
import huolongluo.byw.reform.market.activity.ChongzhiListActivity;
import huolongluo.byw.reform.mine.activity.MoneyManagerActivity;
import huolongluo.byw.reform.mine.activity.PyramidSaleWebViewActivity;
import huolongluo.byw.user.UserInfoManager;
import huolongluo.byw.util.Constant;
import huolongluo.byw.util.GsonUtil;
import huolongluo.byw.util.SPUtils;
import huolongluo.bywx.utils.AppUtils;
import huolongluo.bywx.utils.EncryptUtils;
/**
 * 首页点击跳转
 */
public class HomeDynamicHandler implements IDynamicTarget, View.OnClickListener {
    private final String TAG = "HomeDynamicHandler";
    private Context context;
    //dynamic-banner
    private RecyclerView entranceRV;
    private LinearLayout bannerLayout;
    private ImageView bannerIV;
    private HomeDynamicAdapter adapter;

    private RelativeLayout rltLeft;
    private TextView tvLeft1,tvLeft2;
    private ImageView ivLeft1;
    private RelativeLayout rltRight;
    private TextView tvRight1,tvRight2;
    private ImageView ivRight1;

    public void init(Context context, View rootView) {
        if (rootView == null) {
            return;
        }
        this.context = context;

        entranceRV = rootView.findViewById(R.id.rv_dynamic_entrance);
        bannerLayout = rootView.findViewById(R.id.ll_dynamic_banner);
        bannerIV = rootView.findViewById(R.id.iv_dynamic_banner_img);

        rltLeft = rootView.findViewById(R.id.rltLeft);
        tvLeft1 = rootView.findViewById(R.id.tvLeft1);
        tvLeft2 = rootView.findViewById(R.id.tvLeft2);
        ivLeft1 = rootView.findViewById(R.id.ivLeft1);
        rltRight = rootView.findViewById(R.id.rltRight);
        tvRight1 = rootView.findViewById(R.id.tvRight1);
        tvRight2 = rootView.findViewById(R.id.tvRight2);
        ivRight1 = rootView.findViewById(R.id.ivRight1);
        //获得动态区默认数据
        List<HomeDynamicResult.HomeDynamic> dataList = new LinkedList<HomeDynamicResult.HomeDynamic>();
//        getDefaultData();
        adapter = new HomeDynamicAdapter(context, dataList, this);
        entranceRV.setLayoutManager(new GridLayoutManager(context,4));
        entranceRV.setAdapter(adapter);
        //
        bannerIV.setOnClickListener(this);
        rltLeft.setOnClickListener(this);
        rltRight.setOnClickListener(this);
        //
        loadCacheData();
        //初始化数据
        refresh();
    }

    public void refresh() {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("type", 1);
            params = EncryptUtils.encrypt(params);
            //请求地区数据
            Type type = new TypeToken<SingleResult<HomeDynamicResult>>() {
            }.getType();
            INetCallback<SingleResult<HomeDynamicResult>> callback = new INetCallback<SingleResult<HomeDynamicResult>>() {
                @Override
                public void onSuccess(SingleResult<HomeDynamicResult> result) throws Throwable {
                    Logger.getInstance().debug(TAG, "result!", new Exception());
                    if (result == null || result.data == null) {
                        //TODO 处理异常情况
                        return;
                    }
                    //序列化到本地
                    String json = GsonUtil.obj2Json(result.data, HomeDynamicResult.class);
                    SPUtils.saveString(BaseApp.getSelf(), AppConstants.COMMON.KEY_HOME_DYNAMIC, json);
                    Logger.getInstance().debug(TAG, "json: " + json);
                    update(result.data);
                }

                @Override
                public void onFailure(Exception e) throws Throwable {
                    Logger.getInstance().debug(TAG, "error", e);
                }
            };
            OKHttpHelper.getInstance().postForStringResult(UrlConstants.ACTION_ENTRANCE, params, callback, type);
        } catch (Throwable t) {
            Logger.getInstance().error(t);
        }
    }

    private void update(HomeDynamicResult result) {
        if (result == null) {
            //TODO 处理异常
            return;
        }
        //
        if (result.level0 != null && !result.level0.isEmpty()) {
            if (result.level0.size() >= 1) {
                HomeDynamicResult.HomeDynamic hd0 = result.level0.get(0);
                updateLeft(hd0);
            }
            if (result.level0.size() >= 2) {
                HomeDynamicResult.HomeDynamic hd0 = result.level0.get(1);
                updateRight(hd0);
            }
        }
        if (result.level1 != null && !result.level1.isEmpty()) {//服务器有数据
            if (adapter == null) {
                adapter = new HomeDynamicAdapter(context, result.level1, this);
                entranceRV.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            } else {
                List list=new LinkedList();
                list.addAll(result.level1);
                adapter.update(list);
            }
        } else {//服务器未返回数据
            //采用默认数据
            List<HomeDynamicResult.HomeDynamic> list = getDefaultLineData();
            if (adapter == null) {
                adapter = new HomeDynamicAdapter(context, list, this);
                entranceRV.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            } else {
                //TODO 处理异常情况
                adapter.update(list);
            }
        }
        if (result.level2 != null && !result.level2.isEmpty()) {
            try {
                HomeDynamicResult.HomeDynamic hd = result.level2.get(0);
                bannerIV.setTag(hd);
                Glide.with(context).load(hd.imgUrl).into(bannerIV);
                bannerIV.post(new Runnable() {
                    @Override
                    public void run() {
                        int width=bannerIV.getWidth();
                        if(width==0) return;
                        int height= (int) (width*0.145);
                        LinearLayout.LayoutParams params= new LinearLayout.LayoutParams(width,height);
                        bannerIV.setLayoutParams(params);
                    }
                });

            } catch (Throwable t) {
                Logger.getInstance().error(t);
            }
        }
    }

    private void updateLeft(HomeDynamicResult.HomeDynamic hd) {
        if (!TextUtils.isEmpty(hd.imgUrl)) {
            Glide.with(context).load(hd.imgUrl).into(ivLeft1);
        }

        tvLeft1.setText(TextUtils.isEmpty(hd.title) ? "" : hd.title);
        tvLeft2.setText(TextUtils.isEmpty(hd.secondTitle) ? "" : hd.secondTitle);
        rltLeft.setTag(hd);
    }
    private void updateRight(HomeDynamicResult.HomeDynamic hd) {
        if (!TextUtils.isEmpty(hd.imgUrl)) {
            Glide.with(context).load(hd.imgUrl).into(ivRight1);
        }

        tvRight1.setText(TextUtils.isEmpty(hd.title) ? "" : hd.title);
        tvRight2.setText(TextUtils.isEmpty(hd.secondTitle) ? "" : hd.secondTitle);
        rltRight.setTag(hd);
    }

    private void loadCacheData() {
        //加载序列化缓存数据
        String json = SPUtils.getString(BaseApp.getSelf(), AppConstants.COMMON.KEY_HOME_DYNAMIC, "");
        if (!TextUtils.isEmpty(json)) {
            try {
                HomeDynamicResult hdr = GsonUtil.json2Obj(json, HomeDynamicResult.class);
                if (hdr != null) {
                    update(hdr);
                    return;
                }
            } catch (Throwable t) {
                Logger.getInstance().error(t);
            }
        }
        //
        //第一次或者没有缓存数据时--根据产品需求内容的要求，暂时采用硬编码（接口数据暂未返回正确数据）
        HomeDynamicResult hdr = new HomeDynamicResult();
        //第一行
        List<HomeDynamicResult.HomeDynamic> dataList1 = new LinkedList<>();
        //买币交易
        HomeDynamicResult.HomeDynamic hdc1 = new HomeDynamicResult.HomeDynamic();
        hdc1.isLocal = true;
        hdc1.portalType = 1;
        hdc1.sort = 1;
        hdc1.title = AppUtils.getString(R.string.fbjy);
        hdc1.portalUrl = "app/fabi";
        hdc1.secondTitle = AppUtils.getString(R.string.str_digital_currencies);
        //合约
        HomeDynamicResult.HomeDynamic hdc2 = new HomeDynamicResult.HomeDynamic();
        hdc2.isLocal = true;
        hdc2.portalType = 1;
        hdc2.sort = 2;
        hdc2.title = AppUtils.getString(R.string.swap);
        hdc2.portalUrl = "app/heyu";
        hdc2.imgResId = R.drawable.ic_home_swap;
        hdc2.secondTitle = AppUtils.getString(R.string.str_max_leverage_100mx);
        dataList1.add(hdc1);
        dataList1.add(hdc2);
        //
        //第二行
        List<HomeDynamicResult.HomeDynamic> dataList2 = getDefaultLineData();
        //第三行
        List<HomeDynamicResult.HomeDynamic> dataList3 = new LinkedList<HomeDynamicResult.HomeDynamic>();
        //广告位--默认为（邀请返佣）
        HomeDynamicResult.HomeDynamic hd5 = new HomeDynamicResult.HomeDynamic();
        hd5.isLocal = true;
        hd5.portalType = 1;
        hd5.title = "";
        hd5.portalUrl = "app/yqfy";//
        hd5.imgResId = R.drawable.ic_home_invite;
        dataList3.add(hd5);
        hdr.level0 = dataList1;
        hdr.level1 = dataList2;
        hdr.level2 = dataList3;
        update(hdr);
    }

    private List<HomeDynamicResult.HomeDynamic> getDefaultLineData() {
        //第二行
        List<HomeDynamicResult.HomeDynamic> dataList2 = new LinkedList<HomeDynamicResult.HomeDynamic>();
        //新手营
        HomeDynamicResult.HomeDynamic hd1 = new HomeDynamicResult.HomeDynamic();
        hd1.isLocal = true;
        hd1.portalType = 0;
        hd1.sort = 1;
        hd1.title = AppUtils.getString(R.string.home_novice);
        hd1.portalUrl = UrlConstants.WEB_DOMAIN+"buy_Bitcoin_guide";
        hd1.imgResId = R.drawable.ic_novice_camp;
        //FansUp
        HomeDynamicResult.HomeDynamic hd2 = new HomeDynamicResult.HomeDynamic();
        hd2.isLocal = true;
        hd2.portalType = 0;
        hd2.sort = 2;
        hd2.title = AppUtils.getString(R.string.fansup);
        hd2.portalUrl = UrlConstants.WEB_DOMAIN+"h5/fansup";//
        hd2.imgResId = R.drawable.ic_funs_up;
        //一键充值
        HomeDynamicResult.HomeDynamic hd3 = new HomeDynamicResult.HomeDynamic();
        hd3.isLocal = true;
        hd3.portalType = 1;
        hd3.sort = 3;
        hd3.title = AppUtils.getString(R.string.one_charge);
        hd3.portalUrl = "app/yjcz";//
        hd3.imgResId = R.drawable.ic_home_recharge;
        //在线客服
        HomeDynamicResult.HomeDynamic hd4 = new HomeDynamicResult.HomeDynamic();
        hd4.isLocal = true;
        hd4.portalType = 1;
        hd4.sort = 4;
        hd4.title = AppUtils.getString(R.string.online_service);
        hd4.portalUrl = "app/kf";//
        hd4.imgResId = R.mipmap.ic_customer_sercvice;
        dataList2.add(hd1);
        dataList2.add(hd2);
        dataList2.add(hd3);
        dataList2.add(hd4);
        return dataList2;
    }

    @Override
    public void gotoTarget(HomeDynamicResult.HomeDynamic hd) {
        if (hd == null || TextUtils.isEmpty(hd.portalUrl)) {
            return;
        }
        try {
            //URL
            if (hd.portalType == 0) {
                if (hd.portalUrl.indexOf("app/byb") != -1) {//币赢宝
                    gotoWebView(hd);
                } else if (hd.portalUrl.indexOf("app/yqfy") != -1) {//邀请返佣
                    gotoYqfy(hd);
                } else if (hd.portalUrl.indexOf("app/viewWelfare") != -1) {//福利中心--殊的WebView
                    gotoWelfare(hd);
                } else if (hd.portalUrl.indexOf("app/viewWelfare") != -1) {//福利中心--殊的WebView
                    gotoWelfare(hd);
                }else if (hd.portalUrl.indexOf("app/viewHbt") != -1) {//HBT特殊处理
                    gotoWebView(hd, true);
                    //TODO 因合代码后发版内容变更，拆代码的风险很高，故采用加开关来处理
                    //TODO 币贷宝待修改
                } else if (hd.portalUrl.indexOf("app/viewCoinLoan") != -1) {//币贷宝
                    gotoWebView(hd, false, true);
                } else {
                    gotoWebView(hd);
                }
            } else if (hd.portalType == 1) {//本地
                gotoNativeTarget(hd);
            }
        } catch (Throwable t) {
            Logger.getInstance().error(t);
        }
    }

    private void gotoWebView(HomeDynamicResult.HomeDynamic homeDynamic) {
        this.gotoWebView(homeDynamic, false, false);
    }

    private void gotoWebView(HomeDynamicResult.HomeDynamic homeDynamic, boolean hideTitle) {
        this.gotoWebView(homeDynamic, hideTitle, false);
    }

    private void gotoWebView(HomeDynamicResult.HomeDynamic homeDynamic, boolean hideTitle, boolean isBdb) {
        Logger.getInstance().debug(TAG,"gotoWebView url:"+homeDynamic.portalUrl+" token:"+UserInfoManager.getToken());
        try {
            Intent intent = new Intent(context, NewsWebviewActivity.class);
            intent.putExtra("url", homeDynamic.portalUrl);
            intent.putExtra("token", UserInfoManager.getToken());
            intent.putExtra("title", homeDynamic.title);
            intent.putExtra("hideTitle", hideTitle);
            intent.putExtra("isBdb", isBdb);
            context.startActivity(intent);
        } catch (Throwable t) {
            Logger.getInstance().error(t);
        }
    }

    private void gotoNativeTarget(HomeDynamicResult.HomeDynamic hd) {
        if (TextUtils.equals("app/gxlc", hd.portalUrl)) {//高息理财
            gotoGxlc();
        } else if (hd.portalUrl.indexOf("app/xinbi") != -1) {//新币
            NewCoinsActivity.Companion.launch(context);
        } else if (hd.portalUrl.indexOf("app/task") != -1) {//任务中心
            TaskCenterActivity.Companion.launch(context);
        } else if (TextUtils.equals("app/kf", hd.portalUrl)) {//在线客服
            gotoKf();
        } else if (TextUtils.equals("app/fabi", hd.portalUrl)) {//交易-买币
            try {
                MainActivity.self.gotoOtc();
            } catch (Throwable t) {
                Logger.getInstance().error(t);
            }
        } else if (TextUtils.equals("app/heyu", hd.portalUrl)||TextUtils.equals("app/heyue", hd.portalUrl)) {//交易-合约
            try {
                MainActivity.self.gotoSwap("");
            } catch (Throwable t) {
                Logger.getInstance().error(t);
            }
        } else if (TextUtils.equals("app/yjcz", hd.portalUrl)) {//一键充值
            gotoYjcz();
        } else if (hd.portalUrl.indexOf("app/yqfy") != -1) {//邀请返佣
            gotoYqfy(hd);
        } else if (TextUtils.equals("app/etf", hd.portalUrl)) {//ETF
            try {
                MainActivity.self.gotoTrade(TransActionHomeFragment.TYPE_ETF);
            } catch (Throwable t) {
                Logger.getInstance().error(t);
            }
        } else if (TextUtils.equals("app/myasset/exchange", hd.portalUrl)) {//币币资产
            MainActivity.self.gotoFinance( AllFinanceFragment.TYPE_BB);
        } else if (TextUtils.equals("app/myasset/fiat", hd.portalUrl)) {//买币资产
            MainActivity.self.gotoFinance( AllFinanceFragment.TYPE_FB);
        } else if (TextUtils.equals("app/myasset/swap", hd.portalUrl)) {//合约资产
            MainActivity.self.gotoFinance( AllFinanceFragment.TYPE_HY);
        } else if (hd.portalUrl.indexOf("app/viewWelfare") != -1) {//福利中心
            gotoWelfare(hd);
        } else if (hd.portalUrl.indexOf("app/earn") != -1){
            EarnActivity.Companion.launch(context);
        }
    }

    private void gotoGxlc() {
        Intent intent = new Intent(context, MoneyManagerActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

    private void gotoKf() {
        startActivity(WebViewActivity.class);
    }

    private void gotoYjcz() {
        if (UserInfoManager.isLogin()) {
            startActivity(new Intent(context, ChongzhiListActivity.class));
        } else {
            Bundle bundle = new Bundle();
            bundle.putString("fromClass", MineFragment.class.toString());
            startActivity(LoginActivity.class, bundle);
        }
    }
    private void gotoYqfy(HomeDynamicResult.HomeDynamic homeDynamic){
        Intent intent=new Intent(context, PyramidSaleWebViewActivity.class);
        intent.putExtra("url",UrlConstants.INVITE);
        intent.putExtra("title",homeDynamic.title);
        intent.putExtra("token",UserInfoManager.getToken());
        startActivity(intent);
    }

    private void gotoWelfare(HomeDynamicResult.HomeDynamic homeDynamic) {//福利中心--特殊的WebView
        try {
            Intent intent = new Intent(context, WelfareWebviewActivity.class);
            intent.putExtra("url", homeDynamic.portalUrl);
            intent.putExtra("token", UserInfoManager.getToken());
            intent.putExtra("title", context.getString(R.string.str_welfare_center));
            context.startActivity(intent);
        } catch (Throwable t) {
            Logger.getInstance().error(t);
        }
    }

    public void startActivity(Class<?> clazz, Bundle bundle) {
        try {
            Intent intent = new Intent(context, clazz);
            if (bundle != null) {
                intent.putExtra("bundle", bundle);
            }
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        } catch (Throwable t) {
            Logger.getInstance().error(t);
        }
    }

    public void startActivity(Class<?> clazz) {
        if (clazz == null) {
            return;
        }
        Intent intent = new Intent(context, clazz);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

    private void startActivity(Intent intent) {
        try {
            context.startActivity(intent);
        } catch (Throwable t) {
            Logger.getInstance().error(t);
        }
    }

    @Override
    public void onClick(View v) {
        int vId = v.getId();
        if (vId == R.id.rltLeft) {
            MobclickAgent.onEvent(context, Constant.UMENG_EVENT_PRE_BG_1 + "1", "买币交易");
            gotoTarget(rltLeft.getTag());
        }else if (vId == R.id.rltRight) {
            gotoTarget(rltRight.getTag());
        }
        else if (vId == R.id.iv_dynamic_banner_img) {
            if (bannerIV.getTag() instanceof HomeDynamicResult.HomeDynamic) {
                MobclickAgent.onEvent(context, Constant.UMENG_EVENT_PRE_BANNER2 + "1", ((HomeDynamicResult.HomeDynamic) bannerIV.getTag()).title);
            }
            gotoTarget(bannerIV.getTag());
        }
    }

    private void gotoTarget(Object obj) {
        if (obj == null) {
            return;
        }
        if (obj instanceof HomeDynamicResult.HomeDynamic) {
            HomeDynamicResult.HomeDynamic hd = (HomeDynamicResult.HomeDynamic) obj;
            gotoTarget(hd);
        }
    }

    public void toPage(String notification) {//点击通知做跳转
        if (notification.equals(AppConstants.NOTIFICATION.APP_GXLC)) {//跳转19 高息理财
            gotoGxlc();
        } else if (notification.equals(AppConstants.NOTIFICATION.APP_KF)) {//跳转21 在线客服
            gotoKf();
        } else if (notification.equals(AppConstants.NOTIFICATION.APP_YJCZ)) {//跳转23 一键充值
            gotoYjcz();
        }
    }
}

