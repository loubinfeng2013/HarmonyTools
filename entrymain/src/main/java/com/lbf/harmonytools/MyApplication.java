package com.lbf.harmonytools;

import com.lbf.lib.imageloader.RequestManager;
import com.lbf.lib.router.HRouter;
import ohos.aafwk.ability.AbilityPackage;

public class MyApplication extends AbilityPackage {
    @Override
    public void onInitialize() {
        super.onInitialize();
        HRouter.NewInstance().init(this);
        RequestManager.getInstance().init(this);
    }
}
