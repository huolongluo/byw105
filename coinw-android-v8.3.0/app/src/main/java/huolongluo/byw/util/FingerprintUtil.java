package huolongluo.byw.util;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import androidx.core.app.ActivityCompat;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import huolongluo.byw.log.Logger;
public class FingerprintUtil {
    private static final String TAG = FingerprintUtil.class.getSimpleName();
    private static final String FINGER_PRINT = "fingerprint";

    /**
     * 获取设备指纹
     * 如果从SharedPreferences文件中拿不到，那么重新生成一个，
     * 并保存到SharedPreferences文件中。
     * @param context
     * @return fingerprint 设备指纹
     */
    public static String getFingerprint(Context context) {
        String fingerprint = null;
        fingerprint = readFingerprintFromFile(context);
        if (TextUtils.isEmpty(fingerprint)) {
            fingerprint = createFingerprint(context);
        } else {
            Logger.getInstance().debug(TAG, "从文件中获取设备指纹：" + fingerprint);
        }
        return fingerprint;
    }

    /**
     * 从SharedPreferences 文件获取设备指纹
     * @return fingerprint 设备指纹
     */
    private static String readFingerprintFromFile(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(FINGER_PRINT, null);
    }

    /**
     * 生成一个设备指纹（耗时50毫秒以内）：
     * 1.IMEI + 设备硬件信息（主要）+ ANDROID_ID + WIFI MAC组合成的字符串
     * 2.用MessageDigest将以上字符串处理成32位的16进制字符串
     * @param context
     * @return 设备指纹
     */
    public static String createFingerprint(Context context) {
        long startTime = System.currentTimeMillis();
        String imei = "";
        try {
            // 1.IMEI
            TelephonyManager TelephonyMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
//            return TODO;
            }
            imei = TelephonyMgr.getDeviceId();
        } catch (Throwable t) {
            Logger.getInstance().error(t);
        }
        Logger.getInstance().info(TAG, "imei=" + imei);
        //2.android 设备信息（主要是硬件信息）    
        final String hardwareInfo = Build.ID + Build.DISPLAY + Build.PRODUCT + Build.DEVICE + Build.BOARD /*+ Build.CPU_ABI*/ + Build.MANUFACTURER + Build.BRAND + Build.MODEL + Build.BOOTLOADER + Build.HARDWARE /* + Build.SERIAL */ + Build.TYPE + Build.TAGS + Build.FINGERPRINT + Build.HOST + Build.USER;
        //Build.SERIAL => 需要API 9以上    
        Logger.getInstance().info(TAG, "hardward info=" + hardwareInfo);

        /* 3. Android_id 刷机和恢复出厂会变
         * A 64-bit number (as a hex string) that is randomly
         * generated when the user first sets up the device and should remain
         * constant for the lifetime of the user's device. The value may
         * change if a factory reset is performed on the device.
         */
        final String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        Logger.getInstance().info(TAG, "android_id=" + androidId);
        /**
         * 4. The WLAN MAC Address string（个别手机刚开机完成后会获取不到，舍去）  
         */    
        /*WifiManager wifiMgr = (WifiManager) context  
                .getSystemService(Context.WIFI_SERVICE);  
        final String wifiMAC = wifiMgr.getConnectionInfo().getMacAddress();  
        Logger.getInstance().info(TAG,"wifi Mac="+wifiMAC);*/


        /*
         *  5. get the bluetooth MAC Address
         *  （有部分手机，如三星GT-S5660 2.3.3，当蓝牙关闭时，获取不到蓝牙MAC;
         *   所以为了保证 device id 的不变，舍去）
         */    
        /*BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();  
        String bt_MAC = null;  
        if (bluetoothAdapter == null) {  
            Logger.getInstance().error(TAG, "bluetoothAdapter is null");  
        } else {  
            bt_MAC = bluetoothAdapter.getAddress();  
        }  
        Logger.getInstance().info(TAG,"m_szBTMAC="+bt_MAC);*/
        // Combined Device ID    
        final String deviceId = imei + hardwareInfo + androidId/* + wifiMAC + bt_MAC*/;
        Logger.getInstance().info(TAG, "deviceId=" + deviceId);
        // 创建一个 messageDigest 实例    
        MessageDigest msgDigest = null;
        try {
            msgDigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        //用 MessageDigest 将 deviceId 处理成32位的16进制字符串    
        msgDigest.update(deviceId.getBytes(), 0, deviceId.length());
        // get md5 bytes    
        byte md5ArrayData[] = msgDigest.digest();
        // create a hex string    
        String deviceUniqueId = new String();
        for (int i = 0; i < md5ArrayData.length; i++) {
            int b = (0xFF & md5ArrayData[i]);
            // if it is a single digit, make sure it have 0 in front (proper    
            // padding)    
            if (b <= 0xF) {
                deviceUniqueId += "0";
            }
            // add number to string    
            deviceUniqueId += Integer.toHexString(b);
//          Logger.getInstance().info(TAG,"deviceUniqueId=" + deviceUniqueId);
        } // hex string to uppercase    
        deviceUniqueId = deviceUniqueId.toUpperCase();
        Logger.getInstance().debug(TAG, "生成的设备指纹：" + deviceUniqueId);
        Logger.getInstance().error(TAG, "生成DeviceId 耗时：" + (System.currentTimeMillis() - startTime));
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(FINGER_PRINT, deviceUniqueId).commit();
        return deviceUniqueId;
    }
}