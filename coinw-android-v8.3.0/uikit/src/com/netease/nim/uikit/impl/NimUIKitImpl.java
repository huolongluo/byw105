package com.netease.nim.uikit.impl;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.api.UIKitInitStateListener;
import com.netease.nim.uikit.api.UIKitOptions;
import com.netease.nim.uikit.api.model.chatroom.ChatRoomMemberChangedObservable;
import com.netease.nim.uikit.api.model.chatroom.ChatRoomProvider;
import com.netease.nim.uikit.api.model.chatroom.ChatRoomSessionCustomization;
import com.netease.nim.uikit.api.model.contact.ContactChangedObservable;
import com.netease.nim.uikit.api.model.contact.ContactEventListener;
import com.netease.nim.uikit.api.model.contact.ContactProvider;
import com.netease.nim.uikit.api.model.location.LocationProvider;
import com.netease.nim.uikit.api.model.main.CustomPushContentProvider;
import com.netease.nim.uikit.api.model.main.LoginSyncDataStatusObserver;
import com.netease.nim.uikit.api.model.main.OnlineStateChangeObservable;
import com.netease.nim.uikit.api.model.main.OnlineStateContentProvider;
import com.netease.nim.uikit.api.model.recent.RecentCustomization;
import com.netease.nim.uikit.api.model.robot.RobotInfoProvider;
import com.netease.nim.uikit.api.model.session.SessionCustomization;
import com.netease.nim.uikit.api.model.session.SessionEventListener;
import com.netease.nim.uikit.api.model.superteam.SuperTeamChangedObservable;
import com.netease.nim.uikit.api.model.superteam.SuperTeamProvider;
import com.netease.nim.uikit.api.model.team.TeamChangedObservable;
import com.netease.nim.uikit.api.model.team.TeamProvider;
import com.netease.nim.uikit.api.model.user.IUserInfoProvider;
import com.netease.nim.uikit.api.model.user.UserInfoObservable;
import com.netease.nim.uikit.business.chatroom.fragment.ChatRoomMessageFragment;
import com.netease.nim.uikit.business.chatroom.viewholder.ChatRoomMsgViewHolderBase;
import com.netease.nim.uikit.business.chatroom.viewholder.ChatRoomMsgViewHolderFactory;
import com.netease.nim.uikit.business.contact.selector.activity.ContactSelectActivity;
import com.netease.nim.uikit.business.preference.UserPreferences;
import com.netease.nim.uikit.business.session.activity.P2PMessageActivity;
import com.netease.nim.uikit.business.session.activity.TeamMessageActivity;
import com.netease.nim.uikit.business.session.audio.MessageAudioControl;
import com.netease.nim.uikit.business.session.emoji.StickerManager;
import com.netease.nim.uikit.business.session.module.MsgForwardFilter;
import com.netease.nim.uikit.business.session.module.MsgRevokeFilter;
import com.netease.nim.uikit.business.session.viewholder.MsgViewHolderBase;
import com.netease.nim.uikit.business.session.viewholder.MsgViewHolderFactory;
import com.netease.nim.uikit.business.team.activity.AdvancedTeamInfoActivity;
import com.netease.nim.uikit.business.team.activity.NormalTeamInfoActivity;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.common.util.storage.StorageType;
import com.netease.nim.uikit.common.util.storage.StorageUtil;
import com.netease.nim.uikit.common.util.sys.ScreenUtil;
import com.netease.nim.uikit.impl.cache.ChatRoomCacheManager;
import com.netease.nim.uikit.impl.cache.DataCacheManager;
import com.netease.nim.uikit.impl.customization.DefaultContactEventListener;
import com.netease.nim.uikit.impl.customization.DefaultP2PSessionCustomization;
import com.netease.nim.uikit.impl.customization.DefaultRecentCustomization;
import com.netease.nim.uikit.impl.customization.DefaultTeamSessionCustomization;
import com.netease.nim.uikit.impl.provider.DefaultChatRoomProvider;
import com.netease.nim.uikit.impl.provider.DefaultContactProvider;
import com.netease.nim.uikit.impl.provider.DefaultRobotProvider;
import com.netease.nim.uikit.impl.provider.DefaultSuperTeamProvider;
import com.netease.nim.uikit.impl.provider.DefaultTeamProvider;
import com.netease.nim.uikit.impl.provider.DefaultUserInfoProvider;
import com.netease.nim.uikit.support.glide.ImageLoaderKit;
import com.netease.nimlib.sdk.AbortableFuture;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomInfo;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMember;
import com.netease.nimlib.sdk.chatroom.model.EnterChatRoomResultData;
import com.netease.nimlib.sdk.msg.attachment.MsgAttachment;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.team.constant.TeamTypeEnum;
import com.netease.nimlib.sdk.team.model.Team;

/**
 * UIKit??????????????????
 */
public final class NimUIKitImpl {

    // context
    private static Context context;

    // ?????????????????????
    private static String account;

    private static UIKitOptions options;

    // ?????????????????????
    private static IUserInfoProvider userInfoProvider;

    // ????????????????????????
    private static ContactProvider contactProvider;

    // ???????????????????????????
    private static LocationProvider locationProvider;

    // ????????????????????????????????????
    private static ImageLoaderKit imageLoaderKit;

    // ???????????????????????????????????????????????????????????????
    private static SessionEventListener sessionListener;

    // ??????????????????????????????????????????????????????
    private static ContactEventListener contactEventListener;

    // ?????????????????????
    private static MsgForwardFilter msgForwardFilter;

    // ?????????????????????
    private static MsgRevokeFilter msgRevokeFilter;

    // ?????????????????????
    private static CustomPushContentProvider customPushContentProvider;

    // ??????????????????
    private static SessionCustomization commonP2PSessionCustomization;

    // ??????????????????
    private static SessionCustomization commonTeamSessionCustomization;

    // ???????????????????????????
    private static RecentCustomization recentCustomization;

    // ????????????????????????
    private static OnlineStateContentProvider onlineStateContentProvider;

    // ????????????????????????
    private static OnlineStateChangeObservable onlineStateChangeObservable;

    // userInfo ????????????
    private static UserInfoObservable userInfoObservable;

    // contact ????????????
    private static ContactChangedObservable contactChangedObservable;

    //??????????????????????????????
    private static TeamProvider teamProvider;

    /**
     * ????????????????????????????????????
     */
    private static SuperTeamProvider superTeamProvider;

    //???????????????????????????
    private static TeamChangedObservable teamChangedObservable;

    //???????????????????????????
    private static SuperTeamChangedObservable superTeamChangedObservable;

    //????????????????????????
    private static RobotInfoProvider robotInfoProvider;

    // ??????????????????
    private static ChatRoomProvider chatRoomProvider;

    // ???????????????????????????
    private static ChatRoomMemberChangedObservable chatRoomMemberChangedObservable;

    // ??????????????????
    private static boolean buildCacheComplete = false;

    //?????????????????????
    private static UIKitInitStateListener initStateListener;

    /*
     * ****************************** ????????? ******************************
     */
    public static void init(Context context) {
        init(context, new UIKitOptions(), null, null);
    }

    public static void init(Context context, UIKitOptions options) {
        init(context, options, null, null);
    }

    public static void init(Context context, IUserInfoProvider userInfoProvider, ContactProvider contactProvider) {
        init(context, new UIKitOptions(), userInfoProvider, contactProvider);
    }

    public static void init(Context context, UIKitOptions options, IUserInfoProvider userInfoProvider, ContactProvider contactProvider) {
        NimUIKitImpl.context = context.getApplicationContext();
        NimUIKitImpl.options = options;
        // init tools
        StorageUtil.init(context, options.appCacheDir);
        ScreenUtil.init(context);

        if (options.loadSticker) {
            StickerManager.getInstance().init();
        }

        // init log
        String path = StorageUtil.getDirectoryByDirType(StorageType.TYPE_LOG);
        LogUtil.init(path, Log.DEBUG);

        NimUIKitImpl.imageLoaderKit = new ImageLoaderKit(context);

        if (!options.independentChatRoom) {
            initUserInfoProvider(userInfoProvider);
            initContactProvider(contactProvider);
            initDefaultSessionCustomization();
            initDefaultContactEventListener();
            // init data cache
            LoginSyncDataStatusObserver.getInstance().registerLoginSyncDataStatus(true);  // ????????????????????????????????????
            DataCacheManager.observeSDKDataChanged(true);
        }

        ChatRoomCacheManager.initCache();
        if (!TextUtils.isEmpty(getAccount())) {
            if (options.initAsync) {
                DataCacheManager.buildDataCacheAsync(); // build data cache on auto login
            } else {
                DataCacheManager.buildDataCache(); // build data cache on auto login
                buildCacheComplete = true;
            }
            getImageLoaderKit().buildImageCache(); // build image cache on auto login
        }
    }

    public static boolean isInitComplete() {
        return !options.initAsync || TextUtils.isEmpty(account) || buildCacheComplete;
    }

    public static void setInitStateListener(UIKitInitStateListener listener) {
        initStateListener = listener;
    }

    public static void notifyCacheBuildComplete() {
        buildCacheComplete = true;
        if (initStateListener != null) {
            initStateListener.onFinish();
        }
    }

    /*
    * ****************************** ???????????? ******************************
    */
    public static AbortableFuture<LoginInfo> login(LoginInfo loginInfo, final RequestCallback<LoginInfo> callback) {
        AbortableFuture<LoginInfo> loginRequest = NIMClient.getService(AuthService.class).login(loginInfo);
        loginRequest.setCallback(new RequestCallback<LoginInfo>() {
            @Override
            public void onSuccess(LoginInfo loginInfo) {
                 if(loginInfo == null){
                    //TODO ???????????????????????????
                    return;
                }
                loginSuccess(loginInfo.getAccount());
                callback.onSuccess(loginInfo);
            }

            @Override
            public void onFailed(int code) {
                callback.onFailed(code);

            }

            @Override
            public void onException(Throwable exception) {
                callback.onException(exception);
            }
        });
        return loginRequest;
    }

    public static void loginSuccess(String account) {
        setAccount(account);
        DataCacheManager.buildDataCache();
        buildCacheComplete = true;
        getImageLoaderKit().buildImageCache();
    }

    public static void logout() {
        DataCacheManager.clearDataCache();
        ChatRoomCacheManager.clearCache();
        getImageLoaderKit().clear();
        LoginSyncDataStatusObserver.getInstance().reset();
    }

    public static void enterChatRoomSuccess(EnterChatRoomResultData data, boolean independent) {
        ChatRoomInfo roomInfo = data.getRoomInfo();

        if (independent) {
            setAccount(data.getAccount());
            DataCacheManager.buildRobotCacheIndependent(roomInfo.getRoomId());
        }

        //?????? member
        ChatRoomMember member = data.getMember();
        member.setRoomId(roomInfo.getRoomId());
        ChatRoomCacheManager.saveMyMember(member);
    }

    public static void exitedChatRoom(String roomId) {
        ChatRoomCacheManager.clearRoomCache(roomId);
    }

    public static UIKitOptions getOptions() {
        return options;
    }

    // ??????????????????????????????
    private static void initUserInfoProvider(IUserInfoProvider userInfoProvider) {

        if (userInfoProvider == null) {
            userInfoProvider = new DefaultUserInfoProvider();
        }

        NimUIKitImpl.userInfoProvider = userInfoProvider;
    }

    // ?????????????????????????????????
    private static void initContactProvider(ContactProvider contactProvider) {
        if (contactProvider == null) {
            contactProvider = new DefaultContactProvider();
        }

        NimUIKitImpl.contactProvider = contactProvider;
    }

    // ????????????????????????P2P???Team???ChatRoom
    private static void initDefaultSessionCustomization() {
        if (commonP2PSessionCustomization == null) {
            commonP2PSessionCustomization = new DefaultP2PSessionCustomization();
        }

        if (commonTeamSessionCustomization == null) {
            commonTeamSessionCustomization = new DefaultTeamSessionCustomization();
        }

        if (recentCustomization == null) {
            recentCustomization = new DefaultRecentCustomization();
        }
    }

    // ??????????????????????????????
    private static void initDefaultContactEventListener() {
        if (contactEventListener == null) {
            contactEventListener = new DefaultContactEventListener();
        }
    }

    public static void startP2PSession(Context context, String account) {
        startP2PSession(context, account, null);
    }

    public static void startP2PSession(Context context, String account, IMMessage anchor) {
        NimUIKitImpl.startChatting(context, account, SessionTypeEnum.P2P, commonP2PSessionCustomization, anchor);
    }

    public static void startTeamSession(Context context, String tid) {
        startTeamSession(context, tid, null);
    }

    public static void startTeamSession(Context context, String tid, IMMessage anchor) {
        NimUIKitImpl.startChatting(context, tid, SessionTypeEnum.Team, commonTeamSessionCustomization, anchor);
    }

    public static void startTeamSession(Context context, String tid, SessionCustomization sessionCustomization, IMMessage anchor) {
        NimUIKitImpl.startChatting(context, tid, SessionTypeEnum.Team, sessionCustomization, anchor);
    }

    public static void startChatting(Context context, String id, SessionTypeEnum sessionType, SessionCustomization
            customization, IMMessage anchor) {
        if (sessionType == SessionTypeEnum.P2P) {
            P2PMessageActivity.start(context, id, customization, anchor);
        } else if (sessionType == SessionTypeEnum.Team) {
            TeamMessageActivity.start(context, id, customization, null, anchor);
        }
    }

    public static void startChatting(Context context, String id, SessionTypeEnum sessionType, SessionCustomization customization,
                                     Class<? extends Activity> backToClass, IMMessage anchor) {
        if (sessionType == SessionTypeEnum.Team) {
            TeamMessageActivity.start(context, id, customization, backToClass, anchor);
        }
    }

    public static void startContactSelector(Context context, ContactSelectActivity.Option option, int requestCode) {
        ContactSelectActivity.startActivityForResult(context, option, requestCode);
    }

    public static void startTeamInfo(Context context, String teamId) {
        Team team = NimUIKit.getTeamProvider().getTeamById(teamId);
        if (team == null) {
            return;
        }
        if (team.getType() == TeamTypeEnum.Advanced) {
            AdvancedTeamInfoActivity.start(context, teamId); // ????????????????????????
        } else if (team.getType() == TeamTypeEnum.Normal) {
            NormalTeamInfoActivity.start(context, teamId); // ????????????????????????
        }
    }

    public static IUserInfoProvider getUserInfoProvider() {
        return userInfoProvider;
    }

    public static UserInfoObservable getUserInfoObservable() {
        if (userInfoObservable == null) {
            userInfoObservable = new UserInfoObservable(context);
        }
        return userInfoObservable;
    }

    public static ContactProvider getContactProvider() {
        return contactProvider;
    }


    public static void setTeamProvider(TeamProvider provider) {
        teamProvider = provider;
    }

    public static ContactChangedObservable getContactChangedObservable() {
        if (contactChangedObservable == null) {
            contactChangedObservable = new ContactChangedObservable(context);
        }
        return contactChangedObservable;
    }

    public static TeamProvider getTeamProvider() {
        if (teamProvider == null) {
            teamProvider = new DefaultTeamProvider();
        }
        return teamProvider;
    }

    public static SuperTeamProvider getSuperTeamProvider() {
        if (superTeamProvider == null) {
            superTeamProvider = new DefaultSuperTeamProvider();
        }
        return superTeamProvider;
    }

    public static void setSuperTeamProvider(SuperTeamProvider provider) {
        superTeamProvider = provider;
    }

    public static TeamChangedObservable getTeamChangedObservable() {
        if (teamChangedObservable == null) {
            teamChangedObservable = new TeamChangedObservable(context);
        }
        return teamChangedObservable;
    }

    public static SuperTeamChangedObservable getSuperTeamChangedObservable() {
        if (superTeamChangedObservable == null) {
            superTeamChangedObservable = new SuperTeamChangedObservable(context);
        }
        return superTeamChangedObservable;
    }

    public static void setRobotInfoProvider(RobotInfoProvider provider) {
        robotInfoProvider = provider;
    }

    public static RobotInfoProvider getRobotInfoProvider() {
        if (robotInfoProvider == null) {
            robotInfoProvider = new DefaultRobotProvider();
        }
        return robotInfoProvider;
    }

    public static void setChatRoomProvider(ChatRoomProvider provider) {
        chatRoomProvider = provider;
    }

    public static ChatRoomProvider getChatRoomProvider() {
        if (chatRoomProvider == null) {
            chatRoomProvider = new DefaultChatRoomProvider();
        }
        return chatRoomProvider;
    }

    public static ChatRoomMemberChangedObservable getChatRoomMemberChangedObservable() {
        if (chatRoomMemberChangedObservable == null) {
            chatRoomMemberChangedObservable = new ChatRoomMemberChangedObservable(context);
        }
        return chatRoomMemberChangedObservable;
    }

    public static LocationProvider getLocationProvider() {
        return locationProvider;
    }

    public static ImageLoaderKit getImageLoaderKit() {
        return imageLoaderKit;
    }

    public static void setLocationProvider(LocationProvider locationProvider) {
        NimUIKitImpl.locationProvider = locationProvider;
    }

    public static void setCommonP2PSessionCustomization(SessionCustomization commonP2PSessionCustomization) {
        NimUIKitImpl.commonP2PSessionCustomization = commonP2PSessionCustomization;
    }

    public static void setCommonTeamSessionCustomization(SessionCustomization commonTeamSessionCustomization) {
        NimUIKitImpl.commonTeamSessionCustomization = commonTeamSessionCustomization;
    }

    public static void setRecentCustomization(RecentCustomization recentCustomization) {
        NimUIKitImpl.recentCustomization = recentCustomization;
    }

    public static void setCommonChatRoomSessionCustomization(ChatRoomSessionCustomization commonChatRoomSessionCustomization) {
        ChatRoomMessageFragment.setChatRoomSessionCustomization(commonChatRoomSessionCustomization);
    }

    public static RecentCustomization getRecentCustomization() {
        return recentCustomization;
    }

    public static void registerMsgItemViewHolder(Class<? extends MsgAttachment> attach, Class<? extends MsgViewHolderBase> viewHolder) {
        MsgViewHolderFactory.register(attach, viewHolder);
    }

    public static void registerChatRoomMsgItemViewHolder(Class<? extends MsgAttachment> attach, Class<? extends ChatRoomMsgViewHolderBase> viewHolder) {
        ChatRoomMsgViewHolderFactory.register(attach, viewHolder);
    }

    public static void registerTipMsgViewHolder(Class<? extends MsgViewHolderBase> viewHolder) {
        MsgViewHolderFactory.registerTipMsgViewHolder(viewHolder);
    }

    public static void setAccount(String account) {
        NimUIKitImpl.account = account;
    }

    public static SessionEventListener getSessionListener() {
        return sessionListener;
    }

    public static void setSessionListener(SessionEventListener sessionListener) {
        NimUIKitImpl.sessionListener = sessionListener;
    }

    public static ContactEventListener getContactEventListener() {
        return contactEventListener;
    }


    public static void setContactEventListener(ContactEventListener contactEventListener) {
        NimUIKitImpl.contactEventListener = contactEventListener;
    }

    public static void setMsgForwardFilter(MsgForwardFilter msgForwardFilter) {
        NimUIKitImpl.msgForwardFilter = msgForwardFilter;
    }

    public static MsgForwardFilter getMsgForwardFilter() {
        return msgForwardFilter;
    }

    public static void setMsgRevokeFilter(MsgRevokeFilter msgRevokeFilter) {
        NimUIKitImpl.msgRevokeFilter = msgRevokeFilter;
    }

    public static MsgRevokeFilter getMsgRevokeFilter() {
        return msgRevokeFilter;
    }

    public static CustomPushContentProvider getCustomPushContentProvider() {
        return customPushContentProvider;
    }

    public static void setCustomPushContentProvider(CustomPushContentProvider mixPushCustomConfig) {
        NimUIKitImpl.customPushContentProvider = mixPushCustomConfig;
    }

    /*
    * ****************************** ???????????? ******************************
    */

    public static void setOnlineStateContentProvider(OnlineStateContentProvider onlineStateContentProvider) {
        NimUIKitImpl.onlineStateContentProvider = onlineStateContentProvider;
    }

    public static OnlineStateContentProvider getOnlineStateContentProvider() {
        return onlineStateContentProvider;
    }

    public static OnlineStateChangeObservable getOnlineStateChangeObservable() {
        if (onlineStateChangeObservable == null) {
            onlineStateChangeObservable = new OnlineStateChangeObservable(context);
        }
        return onlineStateChangeObservable;
    }

    public static boolean enableOnlineState() {
        return onlineStateContentProvider != null;
    }


    public static void setEarPhoneModeEnable(boolean enable) {
        MessageAudioControl.getInstance(context).setEarPhoneModeEnable(enable);
        UserPreferences.setEarPhoneModeEnable(enable);
    }

    public static boolean getEarPhoneModeEnable() {
        return UserPreferences.isEarPhoneModeEnable();
    }

    /*
    * ****************************** basic ******************************
    */
    public static Context getContext() {
        return context;
    }

    public static String getAccount() {
        return account;
    }
}
