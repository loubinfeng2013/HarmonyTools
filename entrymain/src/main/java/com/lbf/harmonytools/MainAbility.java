package com.lbf.harmonytools;

import com.lbf.annotations.router.HRouter;
import com.lbf.entrycommon.Constant;
import com.lbf.harmonytools.slice.MainAbilitySlice;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;

@HRouter(Constant.RouterName.EntryMainMainAbility)
public class MainAbility extends Ability {
    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setMainRoute(MainAbilitySlice.class.getName());
    }
}
