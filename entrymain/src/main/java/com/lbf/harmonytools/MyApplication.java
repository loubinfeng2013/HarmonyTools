package com.lbf.harmonytools;

import com.lbf.lib.imageloader.RequestManager;
import com.lbf.lib.router.HRouter;
import ohos.aafwk.ability.AbilityPackage;

public class MyApplication extends AbilityPackage {
    @Override
    public void onInitialize() {
        super.onInitialize();
        //初始化HRouter
        HRouter.NewInstance().init(this);
        //初始化ImageLoader
        RequestManager.getInstance().init(this);
    }
}
