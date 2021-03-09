package com.lbf.lib.inject;

import ohos.app.AbilityContext;

import java.lang.reflect.Constructor;

public class InjectHelperV2 {

    public static void Inject(AbilityContext abilityContext) {
        try {
            Class clazz = Class.forName(abilityContext.getClass().getCanonicalName() + "$$ViewBinder");
            Constructor constructor = clazz.getConstructor();
            IViewBinder instance = (IViewBinder) constructor.newInstance();
            instance.bind(abilityContext);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void UnInject(AbilityContext abilityContext) {
        try {
            Class clazz = Class.forName(abilityContext.getClass().getCanonicalName() + "$$ViewBinder");
            Constructor constructor = clazz.getConstructor();
            IViewBinder instance = (IViewBinder) constructor.newInstance();
            instance.unBind(abilityContext);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
