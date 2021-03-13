package com.lbf.lib.router;

import com.lbf.utils.ClassUtils;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.app.AbilityContext;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HRouter {

    private Map<String, Class> routerMap = new HashMap<>();

    private HRouter() {
    }

    private static class Holder {
        private static HRouter Instance = new HRouter();
    }

    public static HRouter NewInstance() {
        return Holder.Instance;
    }

    /**
     * 需要在主工程的MyApplication中调用
     *
     * @param abilityContext
     * @return
     */
    public boolean init(AbilityContext abilityContext) {
        try {
            List<Class<?>> classList = ClassUtils.ScanClassInfoWithPackageName("com.lbf.hrouter", abilityContext);
            for (Class clz : classList) {
                Constructor constructor = clz.getConstructor();
                IRouter router = (IRouter) constructor.newInstance();
                router.addRouter(routerMap);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Ability装载AbilitySlice
     *
     * @param routerName
     * @return
     */
    public Class getClassByRouterName(String routerName) {
        return routerMap.get(routerName);
    }

    /**
     * abilitySlice启动abilitySlice
     *
     * @param abilitySlice
     * @param routerName
     * @param intent
     * @return
     */
    public boolean abilitySliceNavigation(AbilitySlice abilitySlice, String routerName, Intent intent) {
        try {
            abilitySlice.present((AbilitySlice) routerMap.get(routerName).getConstructor().newInstance(), intent);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 在Ability启动Ability
     *
     * @param ability
     * @param routerName
     * @param intent
     * @return
     */
    public boolean abilityNavigation(Ability ability, String routerName, Intent intent) {
        try {
            ability.startAbility(intent.setElementName(ability.getBundleName(), routerMap.get(routerName)));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
