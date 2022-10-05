package huolongluo.byw.byw.bean;


import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * Created by 火龙裸 on 2017/12/30 0030.
 */
//@JsonIgnoreProperties({"handler","kkkkk"})
public class UserAssetsBean implements Serializable
{

   /*{
    "code": 0,
    "result": true,
    "userInfo": {
        "fid": 67351,
        "hasC2Validate": true,
        "idDeductible": false,
        "identityNo": "341282199103017610",
        "identityType": 0,
         "isBindEmail": false,
        "isBindMobil": true,
        "isGoogleBind": false,
        "isHasTradePWD": 1,
        "isSecondValidate": false,
        "loginName": "18655056179",
        "nickName": "18655056179",
        "postC2Validate": true,
        "realName": "王海洋",
        "tel": "18655056179",
        "vip": 1
    }
}*/






           private  int code;

    private boolean result;
    private UserInfoBean userInfo;
    private PersonalAssetBean asset;
    private List<WithdrawInfoBean> withdrawInfos;
    private List<CoinAddressBean> coinAddress;

    public UserAssetsBean(){

    }

    public boolean isResult()
    {
        return result;
    }

    public void setResult(boolean result)
    {
        this.result = result;
    }

    public UserInfoBean getUserInfo()
    {
        return userInfo;
    }

    public void setUserInfo(UserInfoBean userInfo)
    {
        this.userInfo = userInfo;
    }

    public PersonalAssetBean getAsset()
    {
        return asset;
    }

    public void setAsset(PersonalAssetBean asset)
    {
        this.asset = asset;
    }

    public List<WithdrawInfoBean> getWithdrawInfos()
    {
        return withdrawInfos;
    }

    public void setWithdrawInfos(List<WithdrawInfoBean> withdrawInfos)
    {
        this.withdrawInfos = withdrawInfos;
    }

    public List<CoinAddressBean> getCoinAddress()
    {
        return coinAddress;
    }

    public void setCoinAddress(List<CoinAddressBean> coinAddress)
    {
        this.coinAddress = coinAddress;
    }

    @Override
    public String toString() {
        return "UserAssetsBean{" +
                "result=" + result +
                ", userInfo=" + userInfo +
                ", asset=" + asset +
                ", withdrawInfos=" + withdrawInfos +
                ", coinAddress=" + coinAddress +
                '}';
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}



/*
{"result":true,"userInfo":{"nickName":"18656050137","isGoogleBind":true,"isSecondValidate":false,"fid":67347,"postC2Validate":true,"hasC2Validate":true,"realName":"李双","isBindMobil":true,"tel":"18656050137","isHasTradePWD":1},"asset":{"totalAsset":16284893.71,"tradeAccount":{"coins":[{"id":0,"cnName":"人民币","shortName":"CNY","total":"159.0354","frozen":"650.0000","zhehe":"809.0353","isCoin":false,"logo":"https://btc018.oss-cn-shenzhen.aliyuncs.com/static/front/images/cnyt.png"},{"id":5,"logo":"https://btc018.oss-cn-shenzhen.aliyuncs.com/201709010458040_frL1C.png","ftrademappingId":4,"cnName":"byc","shortName":"byc","total":"0.0000","frozen":"0.0000","borrow":0,"zhehe":"0.0000","curPrice":"25.0000","isCoin":true,"isWithDraw":true,"isRecharge":false,"iseos":false,"withdrawDigit":2},{"id":6,"logo":"https://www.coinw.me/upload/images/201807251949023_GGLxr.png","ftrademappingId":5,"cnName":"HC","shortName":"HC","total":"99889.9980","frozen":"111.0000","borrow":0,"zhehe":"8484084.6703","curPrice":"84.8400","isCoin":true,"isWithDraw":true,"isRecharge":true,"iseos":true,"withdrawDigit":4},{"id":7,"logo":"https://btc018.oss-cn-shenzhen.aliyuncs.com/201709010327006_kgFDf.png","ftrademappingId":6,"cnName":"STX","shortName":"STX","total":"0.0000","frozen":"0.0000","borrow":0,"zhehe":"0.0000","curPrice":"3.9000","isCoin":true,"isWithDraw":true,"isRecharge":false,"iseos":false,"withdrawDigit":4},{"id":8,"logo":"https://btc018.oss-cn-shenzhen.aliyuncs.com/201709010327031_xRQgY.png","ftrademappingId":null,"cnName":"CDT","shortName":"CDT","total":"0.0000","frozen":"0.0000","borrow":0,"zhehe":"0.0000","curPrice":"0.0000","isCoin":true,"isWithDraw":true,"isRecharge":false,"iseos":false,"withdrawDigit":4},{"id":16,"logo":"https://btc018.oss-cn-shenzhen.aliyuncs.com/201711020114014_nIiOH.png","ftrademappingId":null,"cnName":"ETH","shortName":"ETH","total":"100210.3243","frozen":"6.0000","borrow":0,"zhehe":"0.0000","curPrice":"0.0000","isCoin":true,"isWithDraw":true,"isRecharge":true,"iseos":false,"withdrawDigit":0},{"id":26,"logo":"https://btc018.oss-cn-shenzhen.aliyuncs.com/favicon.ico","ftrademappingId":null,"cnName":"coins1","shortName":"coins1","total":"0.0000","frozen":"0.0000","borrow":0,"zhehe":"0.0000","curPrice":"0.0000","isCoin":true,"isWithDraw":true,"isRecharge":false,"iseos":false,"withdrawDigit":4},{"id":28,"logo":"https://btc018.oss-cn-shenzhen.aliyuncs.com/favicon.ico","ftrademappingId":null,"cnName":"EOS","shortName":"EOS","total":"99889.0000","frozen":"101.0000","borrow":0,"zhehe":"0.0000","curPrice":"0.0000","isCoin":true,"isWithDraw":true,"isRecharge":true,"iseos":false,"withdrawDigit":4},{"id":29,"logo":"https://btc018.oss-cn-shenzhen.aliyuncs.com/favicon.ico","ftrademappingId":19,"cnName":"USDT","shortName":"USDT","total":"0.0000","frozen":"0.0000","borrow":0,"zhehe":"0.0000","curPrice":"8.0000","isCoin":true,"isWithDraw":false,"isRecharge":false,"iseos":false,"withdrawDigit":4},{"id":35,"logo":"https://www.coinw.me/upload/images/201807081718000_WMSkD.png","ftrademappingId":17,"cnName":"coins","shortName":"coins","total":"100000.0000","frozen":"0.0000","borrow":0,"zhehe":"7800000.0000","curPrice":"78.0000","isCoin":true,"isWithDraw":true,"isRecharge":false,"iseos":false,"withdrawDigit":4}],"totalAsset":16284893.71,"netAsset":16284893.71},"marginAccount":{"coins":[{"id":0,"total":0,"frozen":0,"borrow":0},{"id":5,"total":0,"frozen":0,"borrow":0},{"id":6,"total":0,"frozen":0,"borrow":0},{"id":7,"total":0,"frozen":0,"borrow":0},{"id":8,"total":0,"frozen":0,"borrow":0},{"id":16,"total":0,"frozen":0,"borrow":0},{"id":26,"total":0,"frozen":0,"borrow":0},{"id":28,"total":0,"frozen":0,"borrow":0},{"id":29,"total":0,"frozen":0,"borrow":0},{"id":35,"total":0,"frozen":0,"borrow":0}],"totalAsset":0}},"withdrawInfos":[{"id":22558,"bankType":6,"address":"贵州省毕节市纳雍县","bankNumber":"邮政储蓄银行 尾号4664"}],"coinAddress":[{"coinId":6,"coinName":"HC","coinShortName":"HC","address":"coinw1bbd96ddda7948"},{"coinId":16,"coinName":"ETH","coinShortName":"ETH","address":null},{"coinId":28,"coinName":"EOS","coinShortName":"EOS","address":"cda60d6fd85ae214e3c"}]}*/
