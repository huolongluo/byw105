package huolongluo.byw.byw.bean;

import com.legend.modular_contract_sdk.repository.model.ExperienceGold;
import com.legend.modular_contract_sdk.repository.model.SpotExperienceGold;

public class HomePopup {
    private ActivityAD activityAd;
    private int goldAmount;// 用于判断是否开通合约， 等于0或者为空表示开通，否则未开通
    private SpotExperienceGold goldRecordUser;// 新获得的体验金

    public ActivityAD getActivityAd() {
        return activityAd;
    }

    public void setActivityAd(ActivityAD activityAd) {
        this.activityAd = activityAd;
    }

    public int getGoldAmount() {
        return goldAmount;
    }

    public void setGoldAmount(int goldAmount) {
        this.goldAmount = goldAmount;
    }

    public ExperienceGold getGoldRecordUser() {
        if (goldRecordUser == null) {
            return null;
        }
        return goldRecordUser.toExperienceGold();
    }

}
