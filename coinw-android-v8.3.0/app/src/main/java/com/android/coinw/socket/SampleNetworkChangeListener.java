package com.android.coinw.socket;
import com.android.coinw.INetworkChangeListener;
import com.android.coinw.utils.Utilities;

import huolongluo.byw.log.Logger;
public class SampleNetworkChangeListener implements INetworkChangeListener {
    private ConnectionManager connectionManager = new ConnectionManager();

    @Override
    public void onConnected() {
        Logger.getInstance().debugThreadLog("SampleNetworkChangeListener", "onConnected");
        Utilities.stageQueue.postRunnable(new Runnable() {
            @Override
            public void run() {
                connectionManager.connect(false);
            }
        });
    }

    @Override
    public void onDisconnected() {
        Logger.getInstance().debugThreadLog("SampleNetworkChangeListener", "onDisconnected");
        Utilities.stageQueue.postRunnable(new Runnable() {
            @Override
            public void run() {
                connectionManager.disconnect();
            }
        });
    }
}
