package com.legend.modular_contract_sdk.common.event;

import com.legend.modular_contract_sdk.repository.model.ExperienceGold;

public class SelectExperienceGoldEvent {
    private ExperienceGold mExperienceGold;

    public SelectExperienceGoldEvent(ExperienceGold experienceGold) {
        mExperienceGold = experienceGold;
    }

    public ExperienceGold getExperienceGold() {
        return mExperienceGold;
    }

    public void setExperienceGold(ExperienceGold experienceGold) {
        mExperienceGold = experienceGold;
    }
}
