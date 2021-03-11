package com.lbf.entrybusiness02.slice;

import com.lbf.annotations.router.HRouter;
import com.lbf.entrybusiness02.ResourceTable;
import com.lbf.entrycommon.Constant;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;

@HRouter(Constant.RouterName.EntryBusiness02MainAbilitySlice)
public class MainAbilitySlice extends AbilitySlice {

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        setUIContent(ResourceTable.Layout_ability_main2);
    }

}
