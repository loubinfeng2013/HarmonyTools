package com.lbf.harmonytools;

import com.lbf.utils.ClassUtils;
import ohos.aafwk.ability.AbilityPackage;

import java.util.List;

public class MyApplication extends AbilityPackage {
    @Override
    public void onInitialize() {
        super.onInitialize();

        try {
            List<Class<?>> classList = ClassUtils.ScanClassInfoWithPackageName("com.lbf.harmonytools.slice", this);
            for (Class clz : classList){
                System.out.println("clz="+clz.getCanonicalName());
            }
        } catch (Exception e) {
            System.out.println("error");
            e.printStackTrace();
        }
    }
}
