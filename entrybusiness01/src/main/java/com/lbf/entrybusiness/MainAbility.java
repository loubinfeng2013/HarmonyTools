package com.lbf.entrybusiness;

import com.lbf.annotations.router.HRouter;
import com.lbf.entrybusiness.slice.MainAbilitySlice;
import com.lbf.entrycommon.Constant;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;

@HRouter(Constant.RouterName.EntryBusiness01MainAbility)
public class MainAbility extends Ability {
    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setMainRoute(MainAbilitySlice.class.getName());
    }
}
