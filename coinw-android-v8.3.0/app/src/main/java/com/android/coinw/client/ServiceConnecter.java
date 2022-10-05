package com.android.coinw.client;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.text.TextUtils;

import com.android.coinw.ICoreService;
import com.android.coinw.ServiceConstants;
import com.android.coinw.model.Message;
import com.android.coinw.model.Request;
import com.android.coinw.service.CoreService;
import com.android.coinw.utils.Utilities;

import java.util.ArrayList;
import java.util.List;

import huolongluo.byw.byw.base.BaseApp;
import huolongluo.byw.log.Logger;
import huolongluo.byw.util.Base64;
import huolongluo.byw.util.Constant;
import huolongluo.byw.util.SPUtils;
import huolongluo.byw.util.config.ConfigurationUtils;
/**
 * 核心服务连接器
 */
public class ServiceConnecter implements ServiceConnection, IBinder.DeathRecipient {
    private static volatile ICoreService service;
    private ServiceCallback callback = new ServiceCallback();
    //是否正在尝试连接
    private static volatile boolean isAttemptConnecting;
    private long connectTime = System.currentTimeMillis();
    List<Message> message = new ArrayList<>();

    public ServiceConnecter() {
    }

    public void sendMessage(Message message) {
        Logger.getInstance().debug("ServiceConnecter", "msg: " + message.ciphertext);
        if (service == null) {
            this.message.add(message);
            //TODO 是否需要重连？因为已经加入重连策略。
            bindService(BaseApp.getSelf());
            return;
        }
        //向本地核心服务发送消息
        Utilities.clientQueue.postRunnable(new Runnable() {
            @Override
            public void run() {
                try {
                    if (service == null) {
                        //TODO 是否需要重连？因为已经加入重连策略。
                        //在子线程中，需要大量测试，当前位置是否适合
                        return;
                    }
                    service.sendMessage(message);
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        });
    }

    public void sendRequest(Request request) {
        if (service == null) {
            //TODO 是否需要重连？因为已经加入重连策略。
            bindService(BaseApp.getSelf());
            return;
        }
        //向本地核心服务发送消息
        Utilities.clientQueue.postRunnable(new Runnable() {
            @Override
            public void run() {
                try {
                    if (service == null) {
                        //TODO 是否需要重连？因为已经加入重连策略。
                        //在子线程中，需要大量测试，当前位置是否适合
                        return;
                    }
                    service.sendRequest(request);
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        });
    }

    public void removeRequest(String url) {
        if (service == null) {
            //TODO 是否需要重连？因为已经加入重连策略。
            bindService(BaseApp.getSelf());
            return;
        }
        //向本地核心服务发送消息
        Utilities.clientQueue.postRunnable(new Runnable() {
            @Override
            public void run() {
                try {
                    if (service == null) {
                        //TODO 是否需要重连？因为已经加入重连策略。
                        //在子线程中，需要大量测试，当前位置是否适合
                        return;
                    }
                    service.removeRequest(url);
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        });
    }

    public void updateConfig(String json) {
        if (TextUtils.isEmpty(json)) {
            return;
        }
        if (service == null) {
            //TODO 是否需要重连？因为已经加入重连策略。
            bindService(BaseApp.getSelf());
            return;
        }
        //向本地核心服务发送消息
        Utilities.clientQueue.postRunnable(new Runnable() {
            @Override
            public void run() {
                try {
                    if (service == null) {
                        //TODO 是否需要重连？因为已经加入重连策略。
                        //在子线程中，需要大量测试，当前位置是否适合
                        return;
                    }
                    String data = Base64.encode(json.getBytes());
                    service.updateConfig(data);
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        });
    }

    @Override
    public void binderDied() {
        try {
            Logger.getInstance().debug(ServiceConstants.TAG_CLIENT, "deathRecipient-binderDied");
            if (service == null) {
                bindService(BaseApp.getSelf());
                return;
            }
            service.asBinder().unlinkToDeath(this, 0);
            service = null;
            // TODO:重新绑定远程服务
            Logger.getInstance().debug(ServiceConstants.TAG_CLIENT, "deathRecipient-binderDied-bindService!");
            bindService(BaseApp.getSelf());
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public boolean bindService(Context context) {
        if (service == null) {
            if (context == null) {
                Logger.getInstance().error(ServiceConstants.TAG_CLIENT, "context is null!");
                return false;
            }
            /**********************此处可能存在系统版本兼容性问题**********************/
//            //控制5s内只能通过一次深度绑定的操作
//            long currTime = System.currentTimeMillis();
//            if (currTime - connectTime < 5000L) {
//                return false;
//            }
//            //检测核心服务是否正常运行
//            if (!AppUtils.isServiceExisted(context, "com.android.coinw.service.CoreService")) {
//                Logger.getInstance().debug(ServiceConstants.TAG_CLIENT, "核心服务已经中止，重新启动核心服务!");
////                ServiceUtils.stopService(context);
//                ServiceUtils.startService(context);
//                return false;
//            }
//            if (isAttemptConnecting && currTime - connectTime < 5000L) {
//                Logger.getInstance().debug(ServiceConstants.TAG_CLIENT, "正在尝试连接核心服务,取消重复绑定!");
//                return false;
//            }
//            connectTime = currTime;
//            isAttemptConnecting = true;
//            try {
//                Logger.getInstance().debug(ServiceConstants.TAG_CLIENT, "尝试连接核心服务!");
//                Intent intent = new Intent(context, CoreService.class);
//                return context.bindService(intent, this, Context.BIND_AUTO_CREATE);
//            } catch (Exception e) {
//                e.printStackTrace();
//                return false;
//            }
            /********************************************/
            if (isAttemptConnecting) {
                Logger.getInstance().debug(ServiceConstants.TAG_CLIENT, "正在尝试连接核心服务,取消重复绑定!");
                return false;
            }
            if (context == null) {
                Logger.getInstance().error(ServiceConstants.TAG_CLIENT, "context is null!");
                return false;
            }
            isAttemptConnecting = true;
            try {
                Logger.getInstance().debug(ServiceConstants.TAG_CLIENT, "尝试连接核心服务!");
                Intent intent = new Intent();
                //TODO 测试专用
                intent.setClass(context, CoreService.class);
                return context.bindService(intent, this, Context.BIND_AUTO_CREATE);
                //return false;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        Logger.getInstance().debug(ServiceConstants.TAG_CLIENT, "service is not null!");
        return true;
    }

    private void initConfig() {
        //语言配置
        try {
            String language = SPUtils.getString(BaseApp.getSelf(), Constant.KEY_LANG, null);
            if (!TextUtils.isEmpty(language)) {
                ConfigurationUtils.updateActivity(BaseApp.getSelf(), language);
//                AppClient.getInstance().updateLanguage(language);
            } else {
                SPUtils.saveString(BaseApp.getSelf(), Constant.KEY_LANG, ConfigurationUtils.getCurrentLang(BaseApp.getSelf()));
            }
        } catch (Throwable t) {
            Logger.getInstance().error(t);
        }
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder binder) {
        try {
            service = ICoreService.Stub.asInterface(binder);
            if (service != null) {
                for (Message message : this.message) {
                    sendMessage(message);
                }
                this.message.clear();
                Logger.getInstance().debug(ServiceConstants.TAG_CLIENT, "连接核心服务成功!");
                service.registerListener(callback);
                //初化配置数据
                initConfig();
            } else {
                Logger.getInstance().debug(ServiceConstants.TAG_CLIENT, "连接核心服务失败!");
            }
            //已经完成连接，重置连接状态
            isAttemptConnecting = false;
            try {
                binder.linkToDeath(this, 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Logger.getInstance().debug(ServiceConstants.TAG_CLIENT, "onServiceConnected!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        try {
            service = null;
            isAttemptConnecting = false;
            Logger.getInstance().debug(ServiceConstants.TAG_CLIENT, "onServiceDisconnected!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBindingDied(ComponentName name) {
        try {
            service = null;
            isAttemptConnecting = false;
            Logger.getInstance().debug(ServiceConstants.TAG_CLIENT, "onBindingDied!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
