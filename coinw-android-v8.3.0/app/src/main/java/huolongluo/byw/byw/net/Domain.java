package huolongluo.byw.byw.net;

import android.text.TextUtils;

public class Domain {
    public String host = "";//主机名称
    public DomainParam domainParam;

    public String getHost() {
        if (domainParam != null) {
            return domainParam.http;
        }
        return "";
    }

    public String getWS() {
        if (domainParam != null) {
            return domainParam.ws;
        }
        return "";
    }

    public String getNewWS() {
        if (domainParam != null) {
            return domainParam.newws;
        }
        return "";
    }

    public Domain(String host) {
        this.host = host;
        this.domainParam = new DomainParam(host);
    }

    public static final class DomainParam {
        public String http = "";
        public String ws = "";
        public String newws = "";//新的ws服务地址

        public DomainParam(String host) {
            if (TextUtils.isEmpty(host)) {
                return;
            }
            host = host.toLowerCase();
            boolean ssl = host.startsWith("https");
            if (ssl || host.startsWith("http")) {
                if (host.endsWith("/")) {
                    this.http = host;
                } else {
                    this.http = host + "/";
                }
                String w = host.replace("https://", "").replace("http://", "");
                if (ssl) {
                    this.ws = "wss://" + w;
                    this.newws = "wss://" + w;
                } else {
                    this.ws = "ws://" + w;
                    this.newws = "ws://" + w;
                }
            } else {
                this.http = "http://" + host + "/";
                this.ws = "ws://" + host;
                this.newws = "ws://" + host;
            }
        }
    }
}
