package huolongluo.byw.byw.net;

import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import huolongluo.byw.BuildConfig;
import huolongluo.byw.byw.base.BaseApp;
import huolongluo.byw.io.AppConstants;
import huolongluo.byw.util.SPUtils;
import huolongluo.byw.util.domain.DomainUtil;
public class DomainHelper {
    private static Domain domain;

    public static final List<Domain> getDomainList() {
        List<Domain> domainList = new ArrayList<Domain>();
        domainList.add(getHostDomain());
        if (BuildConfig.DEBUG || BuildConfig.ENV_DEV) {
            domainList.addAll(getAllTestList());
        }
        return domainList;
    }
    public static final List<String> getDomainContractList() {
        List<String> domainContractList = new ArrayList<String>();
        domainContractList.add(DomainUtil.INSTANCE.getHostWithSlashSuffix(BuildConfig.HOST_SWAP));
        if (BuildConfig.DEBUG || BuildConfig.ENV_DEV) {
            for (String host : BuildConfig.DEV_CONTRACT_HOSTS) {
                if (TextUtils.isEmpty(host)) {
                    continue;
                }
                domainContractList.add(DomainUtil.INSTANCE.getHostWithSlashSuffix(host));
            }
        }
        return domainContractList;
    }

    private static List<Domain> getAllTestList() {
        List<Domain> domainList = new ArrayList<Domain>();
        if (BuildConfig.DEV_HOSTS == null && BuildConfig.DEV_HOSTS.length == 0) {
            return domainList;
        }
        for (String host : BuildConfig.DEV_HOSTS) {
            if (TextUtils.isEmpty(host)) {
                continue;
            }
            Domain domain = new Domain(host);
            domainList.add(domain);
        }
        return domainList;
    }

    /**
     * 线上主机域名
     *
     * @return
     */
    public static Domain getHostDomain() {
        Domain domain = new Domain(BuildConfig.HOST);
        return domain;
    }

    /**
     * 当前选择的主机域名
     *
     * @return
     */
    public static Domain getDomain() {
        if (DomainHelper.domain != null) {
            return DomainHelper.domain;
        }
        String host = SPUtils.getString(BaseApp.getSelf(), AppConstants.LOCAL.KEY_LOCAL_HOST, "");
        //如果为空，默认选择线上库
        if (TextUtils.isEmpty(host)) {
            host = BuildConfig.HOST;
        }
        DomainHelper.domain = new Domain(host);
        return DomainHelper.domain;
    }

    public static String getWebUrl() {
        String host = SPUtils.getString(BaseApp.getSelf(), AppConstants.LOCAL.KEY_LOCAL_HOST_WEB, BuildConfig.HOST_WEB);
        return host;
    }

    public static String getSwapUrl() {
        String host = SPUtils.getString(BaseApp.getSelf(), AppConstants.LOCAL.KEY_LOCAL_HOST_SWAP, BuildConfig.HOST_SWAP);
        return DomainUtil.INSTANCE.getHostWithSlashSuffix(host);
    }

    public static void setDomain(Domain domain) {
        DomainHelper.domain = null;
    }
}
