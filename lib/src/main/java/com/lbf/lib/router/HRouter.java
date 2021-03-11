package com.lbf.lib.router;

import com.lbf.utils.ClassUtils;
import ohos.app.AbilityContext;

import java.util.List;

public class HRouter {

    private HRouter() {
    }

    private static class Holder {
        private static HRouter Instance = new HRouter();
    }

    public static HRouter NewInstance() {
        return Holder.Instance;
    }

    public void init(AbilityContext abilityContext) {
        try {
            List<Class<?>> classList = ClassUtils.ScanClassInfoWithPackageName("com.lbf.hrouter", abilityContext);
            for (Class clz : classList) {
                System.err.println(clz.getCanonicalName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
