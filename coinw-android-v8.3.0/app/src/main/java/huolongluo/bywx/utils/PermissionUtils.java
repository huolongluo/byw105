package huolongluo.bywx.utils;

import android.Manifest;

public class PermissionUtils {

    public static final String[] ALL_NEED_PERMISSIONS = {
            Manifest.permission.CALL_PHONE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_NUMBERS,
            Manifest.permission.READ_SMS,
            Manifest.permission.RECORD_AUDIO
    };
    public static final String[] CAMERA_NEED_PERMISSIONS = {
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
            , Manifest.permission.RECORD_AUDIO

    };
}