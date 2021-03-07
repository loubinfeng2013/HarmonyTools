package com.lbf.lib.inject;

import com.lbf.lib.inject.annotations.BindView;
import com.lbf.lib.inject.annotations.OnClick;
import com.lbf.lib.inject.annotations.UiContent;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.ability.AbilitySlice;
import ohos.agp.components.Component;
import ohos.app.AbilityContext;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 注入工具类
 */
public class InjectHelper {

    private static final Map<AbilityContext, List<Field>> fieldCache = new HashMap<>();

    private enum AbilityContextType {
        Ability,
        AbilitySlice
    }

    /**
     * 注入
     */
    public static void Inject(AbilityContext abilityContext) {
        if (abilityContext == null)
            return;
        AbilityContextType type = null;
        if (abilityContext instanceof AbilitySlice)
            type = AbilityContextType.AbilitySlice;
        else if (abilityContext instanceof Ability)
            type = AbilityContextType.Ability;
        else
            return;
        processUiContent(abilityContext, type);
        processBindView(abilityContext, type);
        processOnClick(abilityContext, type);
    }

    /**
     * 反注入
     */
    public static void UnInject(AbilityContext abilityContext) {
        if (abilityContext == null)
            return;
        List<Field> cache = fieldCache.remove(abilityContext);
        for (Field field : cache) {
            try {
                field.setAccessible(true);
                field.set(abilityContext, null);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 处理组件绑定
     */
    private static void processBindView(AbilityContext abilityContext, AbilityContextType type) {
        Class<? extends AbilityContext> clazz = abilityContext.getClass();
        Field[] fields = clazz.getDeclaredFields();
        List<Field> cache = new ArrayList<>();
        for (Field field : fields) {
            BindView annotation = field.getAnnotation(BindView.class);
            if (annotation != null) {
                int resId = annotation.value();
                Component component = null;
                if (type == AbilityContextType.Ability)
                    component = ((Ability) abilityContext).findComponentById(resId);
                else
                    component = ((AbilitySlice) abilityContext).findComponentById(resId);
                try {
                    field.setAccessible(true);
                    field.set(abilityContext, component);
                    cache.add(field);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        if (cache.size() > 0)
            fieldCache.put(abilityContext, cache);
    }

    /**
     * 处理点击事件
     */
    private static void processOnClick(AbilityContext abilityContext, AbilityContextType type) {
        Class<? extends AbilityContext> clazz = abilityContext.getClass();
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            OnClick annotation = method.getAnnotation(OnClick.class);
            if (annotation != null) {
                int[] resIds = annotation.value();
                for (int id : resIds) {
                    Component component = null;
                    if (type == AbilityContextType.Ability)
                        component = ((Ability) abilityContext).findComponentById(id);
                    else
                        component = ((AbilitySlice) abilityContext).findComponentById(id);
                    component.setClickedListener(cpt -> {
                        try {
                            method.setAccessible(true);
                            method.invoke(abilityContext, cpt);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    });
                }
            }
        }
    }

    /**
     * 处理布局绑定
     */
    private static void processUiContent(AbilityContext abilityContext, AbilityContextType type) {
        Class<? extends AbilityContext> clazz = abilityContext.getClass();
        UiContent annotation = clazz.getAnnotation(UiContent.class);
        if (annotation != null) {
            int layoutId = annotation.value();
            if (type == AbilityContextType.Ability)
                ((Ability) abilityContext).setUIContent(layoutId);
            else
                ((AbilitySlice) abilityContext).setUIContent(layoutId);

        }
    }
}
