package com.netease.nim.uikit.business.session.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.netease.nim.uikit.R;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.api.model.contact.ContactChangedObserver;
import com.netease.nim.uikit.api.model.main.OnlineStateChangeObserver;
import com.netease.nim.uikit.api.model.session.SessionCustomization;
import com.netease.nim.uikit.api.model.user.UserInfoObserver;
import com.netease.nim.uikit.api.wrapper.NimToolBarOptions;
import com.netease.nim.uikit.business.session.constant.Extras;
import com.netease.nim.uikit.business.session.fragment.MessageFragment;
import com.netease.nim.uikit.business.uinfo.UserInfoHelper;
import com.netease.nim.uikit.common.CommonUtil;
import com.netease.nim.uikit.common.ToastHelper;
import com.netease.nim.uikit.common.activity.ToolBarOptions;
import com.netease.nim.uikit.impl.NimUIKitImpl;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.CustomNotification;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * 点对点聊天界面
 * <p/>
 * Created by huangjun on 2015/2/1.
 */
public class P2PMessageActivity extends BaseMessageActivity {

    private boolean isResume = false;
    private Map dataMap = new HashMap();
    private TextView head_img;
    private TextView name;
    private TextView pay_state;
    private TextView time;
    private TextView total_money;
    private TextView totalTxt;
    private CountDownTimer timer;
    public static String orderNo;
    private View diamond;
    public static String adUserName;
    private String userName;
    private String currentUserName;

    public static void start(Context context, String contactId, SessionCustomization customization, IMMessage anchor) {
        Intent intent = new Intent();
        intent.putExtra(Extras.EXTRA_ACCOUNT, contactId);
        intent.putExtra(Extras.EXTRA_CUSTOMIZATION, customization);
        //add by guocj
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (anchor != null) {
            intent.putExtra(Extras.EXTRA_ANCHOR, anchor);
        }
        intent.setClass(context, P2PMessageActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        context.startActivity(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static void start(Activity atv, String contactId, SessionCustomization customization, Object object) {
        try {
            if (atv == null || atv.isFinishing() || atv.isDestroyed()) {
                return;
            }
            Intent intent = new Intent();
            intent.putExtra(Extras.EXTRA_ACCOUNT, contactId);
            intent.putExtra(Extras.EXTRA_CUSTOMIZATION, customization);
            //add by guocj
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (object instanceof Serializable) {
                try {
                    intent.putExtra("data", (Serializable) object);
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            } else if (object instanceof Parcelable) {
                try {
                    intent.putExtra("data", (Parcelable) object);
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
            intent.setClass(atv, P2PMessageActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

            atv.startActivityForResult(intent, 2);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 单聊特例话数据，包括个人信息，
        requestBuddyInfo();
        displayOnlineState();
        registerObservers(true);
        initView();
        //初始化获得数据
        initData();
    }

    private void initData() {
        Intent intent = getIntent();
        if (intent == null) {
            return;
        }
        Object object = getIntent().getSerializableExtra("data");
        if (object instanceof Map) {
            Map map = (Map) object;
            dataMap.clear();
            dataMap.putAll(map);
        }
        Log.e("P2PMessageActivity", "dataMap: " + dataMap.toString());
        orderNo = TextUtils.isEmpty(dataMap.get("id").toString()) ? "" : dataMap.get("id").toString();
        //
        //由于服务器采用多个参数控制订单状态，故增加本地参数，用于传输入控制业务状态。
        initHeadInfo();
        int adStatus = getMapIntData(dataMap, "adStatus");
        int status = getMapIntData(dataMap, "status");
        Log.e("P2PMessageActivity", "adStatus: " + adStatus + " status: " + status);
        if (adStatus >= status) {
            initStatus(adStatus);
        } else {
            initStatus(status);
        }
    }

    private void initStatus(int status) {
        switch (status) {
            case 0:
                pay_state.setText(R.string.unPay_);
                break;
            case 1:
                pay_state.setText(R.string.paied_);
                time.setVisibility(View.GONE);
                break;
            case 2:
                pay_state.setText(R.string.finish_);
                break;
            case 3:
                pay_state.setText(R.string.canceled_);
                break;
            case 4:
                pay_state.setText(R.string.canceled_);//app端统一
                break;
            case 5://申诉中
                pay_state.setText(R.string.complaint_);
                break;
            case 6://取消申诉中
                pay_state.setText(R.string.cancel_complaint);
                break;
            case 7://申诉完成
                pay_state.setText(R.string.complaint_complet);
                break;
        }
    }

    private int getMapIntData(Map map, String key) {
        if (map == null || map.isEmpty() || TextUtils.isEmpty(key)) {
            return -1;
        }
        Object obj = map.get(key);
        if (obj == null) {
            return -1;
        }
        try {
            return Integer.parseInt(obj.toString());
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return -1;
    }

    private String getMapStringData(Map map, String key) {
        if (map == null || map.isEmpty() || TextUtils.isEmpty(key)) {
            return "";
        }
        Object obj = map.get(key);
        if (obj == null) {
            return "";
        }
        try {
            return String.valueOf(obj.toString());
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return "";
    }

    /**
     * 聊天界面顶部界面
     */
    private void initHeadInfo() {
        adUserName = dataMap.get("adUserName").toString();
        userName = dataMap.get("userName").toString();
        currentUserName = null != dataMap.get("currentUserName") ? dataMap.get("currentUserName").toString() : "";
        //
        String oppositeUserName = getMapStringData(dataMap, "oppositeUserName");
        if (TextUtils.isEmpty(oppositeUserName)) {
            name.setText(currentUserName.contains(adUserName) ? userName : adUserName);
        } else {
            name.setText(oppositeUserName);
        }
        total_money.setText(dataMap.get("totalAmount").toString());
        head_img.setText(currentUserName.contains(adUserName) ? userName.substring(0, 1) : adUserName.substring(0, 1));
        if (null != dataMap.get("otcLevel") && (int) dataMap.get("otcLevel") == 3) {
            diamond.setVisibility(View.VISIBLE);
        } else {
            diamond.setVisibility(View.GONE);
        }

        long payLimit = (long) dataMap.get("countdown");
        if (payLimit < 1) {
            time.setText("--:--");
            time.setVisibility(View.GONE);
        } else {
            timer = new CountDownTimer(payLimit + 1000, 1000) {
                public void onTick(long millisUntilFinished) {
                    time.setText(new SimpleDateFormat("mm:ss", Locale.getDefault()).format(new Date(millisUntilFinished - 1000)));
                    if (time.getText().equals("00:00")) {
                        time.setText("--:--");
                        time.setVisibility(View.GONE);
                        if (pay_state.getText().equals("未付款")) {
                            pay_state.setText("已取消");
                        }
                    }
                }

                public void onFinish() {

                }
            };
            timer.start();
            time.setVisibility(View.VISIBLE);
        }
    }

    public Map getData() {
        return dataMap;
    }

    private void initView() {
        findViewById(R.id.close).setOnClickListener(view -> backToOrder());
        head_img = findViewById(R.id.head_img);
        totalTxt = findView(R.id.tv_total);
        findViewById(R.id.phone).setOnClickListener(view -> call());
        head_img.setOnClickListener(view -> toMerchant());
        name = findViewById(R.id.name);
        pay_state = findViewById(R.id.pay_state);
        time = findViewById(R.id.time);
        total_money = findViewById(R.id.total_money);
        diamond = findViewById(R.id.diamond);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        backToOrder();
    }

    private void backToOrder() {
        Intent intent = new Intent();
        this.setResult(2, intent);
        this.finish();
    }

    private void call() {
        try {
            if (null != dataMap && null != dataMap.get("oppositePhoneNumber")) {
                Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + dataMap.get("oppositePhoneNumber")));//跳转到拨号界面，同时传递电话号码
                startActivity(dialIntent);
            }
        } catch (Throwable t) {
            ToastHelper.showToastLong(P2PMessageActivity.this, "请确认已正确使用移动通话设备!");
        }
    }

    private void toMerchant() {
        if (!TextUtils.isEmpty(currentUserName) && !currentUserName.contains(adUserName)) {
            ComponentName componentName = new ComponentName(getPackageName(), "huolongluo.byw.reform.c2c.oct.activity.SellerInfoActivity");
            Intent intent = new Intent();
            intent.setComponent(componentName);
            intent.putExtra("userId", dataMap.get("adUserId").toString());
            startActivity(intent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        registerObservers(false);
        if (null != timer) {
            timer.cancel();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        isResume = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        isResume = false;
    }

    private void requestBuddyInfo() {
        setTitle(UserInfoHelper.getUserTitleName(sessionId, SessionTypeEnum.P2P));
    }

    private void displayOnlineState() {
        if (!NimUIKitImpl.enableOnlineState()) {
            return;
        }
        String detailContent = NimUIKitImpl.getOnlineStateContentProvider().getDetailDisplay(sessionId);
        setSubTitle(detailContent);
    }

    /**
     * 命令消息接收观察者
     */
    private Observer<CustomNotification> commandObserver = new Observer<CustomNotification>() {
        @Override
        public void onEvent(CustomNotification message) {
            if (!sessionId.equals(message.getSessionId()) || message.getSessionType() != SessionTypeEnum.P2P) {
                return;
            }
            showCommandMessage(message);
        }
    };

    /**
     * 用户信息变更观察者
     */
    private UserInfoObserver userInfoObserver = new UserInfoObserver() {
        @Override
        public void onUserInfoChanged(List<String> accounts) {
            if (!accounts.contains(sessionId)) {
                return;
            }
            requestBuddyInfo();
        }
    };

    /**
     * 好友资料变更（eg:关系）
     */
    private ContactChangedObserver friendDataChangedObserver = new ContactChangedObserver() {
        @Override
        public void onAddedOrUpdatedFriends(List<String> accounts) {
            setTitle(UserInfoHelper.getUserTitleName(sessionId, SessionTypeEnum.P2P));
        }

        @Override
        public void onDeletedFriends(List<String> accounts) {
            setTitle(UserInfoHelper.getUserTitleName(sessionId, SessionTypeEnum.P2P));
        }

        @Override
        public void onAddUserToBlackList(List<String> account) {
            setTitle(UserInfoHelper.getUserTitleName(sessionId, SessionTypeEnum.P2P));
        }

        @Override
        public void onRemoveUserFromBlackList(List<String> account) {
            setTitle(UserInfoHelper.getUserTitleName(sessionId, SessionTypeEnum.P2P));
        }
    };

    /**
     * 好友在线状态观察者
     */
    private OnlineStateChangeObserver onlineStateChangeObserver = new OnlineStateChangeObserver() {
        @Override
        public void onlineStateChange(Set<String> accounts) {
            if (!accounts.contains(sessionId)) {
                return;
            }
            // 按照交互来展示
            displayOnlineState();
        }
    };

    private void registerObservers(boolean register) {
        NIMClient.getService(MsgServiceObserve.class).observeReceiveMessage(incomingMessageObserver, register);
        NIMClient.getService(MsgServiceObserve.class).observeCustomNotification(commandObserver, register);
        NimUIKit.getUserInfoObservable().registerObserver(userInfoObserver, register);
        NimUIKit.getContactChangedObservable().registerObserver(friendDataChangedObserver, register);
        if (NimUIKit.enableOnlineState()) {
            NimUIKit.getOnlineStateChangeObservable().registerOnlineStateChangeListeners(onlineStateChangeObserver, register);
        }
    }

    /**
     * 消息接收观察者
     */
    Observer<List<IMMessage>> incomingMessageObserver = new Observer<List<IMMessage>>() {
        @Override
        public void onEvent(List<IMMessage> messages) {
            onMessageIncoming(messages);
        }
    };

    private void onMessageIncoming(List<IMMessage> messages) {
        if (CommonUtil.isEmpty(messages)) {
            return;
        }
        //TODO 处理订单类信息
        updateView(messages);
    }

    private void updateView(List<IMMessage> messages) {
        for (IMMessage message : messages) {
            Map<String, Object> dataMap = message.getRemoteExtension();
            try {
                Log.e("P2pMessageActivity", dataMap.toString());
                Object obj = dataMap.get("orderStatus");
                if (obj != null) {
                    String orderStatus = String.valueOf(obj);
                    int status = Integer.parseInt(orderStatus);
                    initStatus(status);
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

    protected void showCommandMessage(CustomNotification message) {
        if (!isResume) {
            return;
        }
        String content = message.getContent();
        try {
            JSONObject json = JSON.parseObject(content);
            int id = json.getIntValue("id");
            if (id == 1) {
                // 正在输入
//                ToastHelper.showToastLong(P2PMessageActivity.this, "对方正在输入...");
            } else {
                ToastHelper.showToast(P2PMessageActivity.this, "command: " + content);
            }
        } catch (Exception ignored) {

        }
    }

    @Override
    protected MessageFragment fragment() {
        Bundle arguments = getIntent().getExtras();
        arguments.putSerializable(Extras.EXTRA_TYPE, SessionTypeEnum.P2P);
        MessageFragment fragment = new MessageFragment();
        fragment.setArguments(arguments);
        fragment.setContainerId(R.id.message_fragment_container);
        return fragment;
    }

    @Override
    protected int getContentViewId() {
        return R.layout.nim_message_activity;
    }

    @Override
    protected void initToolBar() {
        ToolBarOptions options = new NimToolBarOptions();
        setToolBar(R.id.toolbar, options);
    }

    @Override
    protected boolean enableSensor() {
        return true;
    }
}
