package com.lbf.harmonytools;

import com.lbf.lib.router.HRouter;
import ohos.aafwk.ability.AbilityPackage;

public class MyApplication extends AbilityPackage {
    @Override
    public void onInitialize() {
        super.onInitialize();
        HRouter.NewInstance().init(this);
    }
}
