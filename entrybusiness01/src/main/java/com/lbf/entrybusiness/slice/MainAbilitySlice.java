package com.lbf.entrybusiness.slice;

import com.lbf.annotations.router.HRouter;
import com.lbf.entrybusiness.ResourceTable;
import com.lbf.entrycommon.Constant;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;

@HRouter(Constant.RouterName.EntryBusiness01MainAbilitySlice)
public class MainAbilitySlice extends AbilitySlice {

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        setUIContent(ResourceTable.Layout_ability_main3);
        findComponentById(ResourceTable.Id_text_helloworld3).setClickedListener(component -> {
            com.lbf.lib.router.HRouter.NewInstance().abilityNavigation(getAbility(), Constant.RouterName.EntryBusiness02MainAbility, new Intent());
        });
    }

}
