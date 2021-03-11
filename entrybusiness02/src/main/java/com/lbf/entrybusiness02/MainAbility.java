package com.lbf.entrybusiness02;

import com.lbf.annotations.router.HRouter;
import com.lbf.entrycommon.Constant;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;

@HRouter(Constant.RouterName.EntryBusiness02MainAbility)
public class MainAbility extends Ability {
    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setMainRoute(com.lbf.lib.router.HRouter.NewInstance().getClassByRouterName(Constant.RouterName.EntryMainAptAbilitySlice).getName());
    }
}
