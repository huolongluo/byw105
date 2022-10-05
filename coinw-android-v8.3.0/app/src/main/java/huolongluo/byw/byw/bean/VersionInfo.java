package huolongluo.byw.byw.bean;


import java.io.Serializable;

/**
 * Created by 火龙裸 on 2018/1/8.
 */
public class VersionInfo implements Serializable
{

    /**
     * result : true
     * ios_version : 2.5
     * android_version : 2.7
     * android_downurl : https://www.coinw.com/mobile/download.html
     * ios_downurl : https://www.coinw.com/mobile/download.html
     */

    private boolean result;
    private String ios_version;
    private String android_version;
    private String android_version_code;
    private String add_update_instructions;
    private String android_downurl;
    private String ios_downurl;
    private String android_apk_download_url;
   private int force;

    public String getAndroid_apk_download_url() {
        return android_apk_download_url;
    }

    public void setAndroid_apk_download_url(String android_apk_download_url) {
        this.android_apk_download_url = android_apk_download_url;
    }

    public boolean isResult()
    {
        return result;
    }

    public void setResult(boolean result)
    {
        this.result = result;
    }

    public String getIos_version()
    {
        return ios_version;
    }

    public void setIos_version(String ios_version)
    {
        this.ios_version = ios_version;
    }

    public String getAndroid_version()
    {
        return android_version;
    }

    public void setAndroid_version(String android_version)
    {
        this.android_version = android_version;
    }

    public String getAndroid_downurl()
    {
        return android_downurl;
    }

    public void setAndroid_downurl(String android_downurl)
    {
        this.android_downurl = android_downurl;
    }


    public String getIos_downurl()
    {
        return ios_downurl;
    }

    public void setIos_downurl(String ios_downurl)
    {
        this.ios_downurl = ios_downurl;
    }

    public int getForce() {
        return force;
    }

    public void setForce(int force) {
        this.force = force;
    }

    public String getAndroid_version_code() {
        return android_version_code;
    }

    public void setAndroid_version_code(String android_version_code) {
        this.android_version_code = android_version_code;
    }

    public String getAdd_update_instructions() {
        return add_update_instructions;
    }

    public void setAdd_update_instructions(String add_update_instructions) {
        this.add_update_instructions = add_update_instructions;
    }

    @Override
    public String toString() {
        return "VersionInfo{" +
                "result=" + result +
                ", ios_version='" + ios_version + '\'' +
                ", android_version='" + android_version + '\'' +
                ", android_version_code='" + android_version_code + '\'' +
                ", add_update_instructions='" + add_update_instructions + '\'' +
                ", android_downurl='" + android_downurl + '\'' +
                ", ios_downurl='" + ios_downurl + '\'' +
                ", force=" + force +
                '}';
    }
}
