package huolongluo.byw.model;
public class Config {
    //{"data":{"appEnableOtc":"0","appEnableEn":"0","appFinancalTimeInterval":2}}
    public int appFinancalTimeInterval;//代表天数
    public String appEnableOtc;//是否开启OTC功能
    public String appEnableEn;//是否开启多语言功能
    public int AppEnableC2C;//APP 是否支持C2C, 0-不支持， 1-支持
}
