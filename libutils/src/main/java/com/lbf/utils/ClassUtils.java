package com.lbf.utils;

import ohos.app.AbilityContext;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.*;

/**
 * 对象工具类
 */
public class ClassUtils {

    /**
     * 扫描目标包下面的类
     *
     * @param packageName
     * @param abilityContext
     * @return
     */
    public static List<Class<?>> ScanClassInfoWithPackageName(String packageName, AbilityContext abilityContext) throws Exception {
        //dalvik.system.DexFile无法直接引用，但是已经被加载到了内存中，所以用采用反射
        Class<?> dexFileClass = Class.forName("dalvik.system.DexFile");
        //获得dalvik.system.DexFile的构造方法
        Constructor dexFileConstructor = dexFileClass.getConstructor(String.class);
        //获得dalvik.system.DexFile的entries方法
        Method entriesMethod = dexFileClass.getMethod("entries");
        //获取hap文件的物理路径
        String bundleCodePath = abilityContext.getBundleCodePath();
        System.out.println("bundleCodePath=" + bundleCodePath);
        //准备存放dexFile的集合，考虑到可能会有多个hap文件
        Set dexFiles = new HashSet();
        File dir = new File(bundleCodePath).getParentFile();
        System.out.println("dir=" + dir.getAbsolutePath());
        File[] files = dir.listFiles();
        for (File file : files) {
            String absolutePath = file.getAbsolutePath();
            System.out.println(absolutePath);
            if (!absolutePath.contains(".")) continue;
            String suffix = absolutePath.substring(absolutePath.lastIndexOf("."));
            if (!suffix.endsWith(".hap")) continue;
            //过滤完成，和Android类似，一个dexFile对应一个hap（apk）
            Object dexFileObj = dexFileConstructor.newInstance(absolutePath);
            dexFiles.add(dexFileObj);
        }
        System.out.println("dexFiles.size()=" + dexFiles.size());
        //用来存放扫描到的class对象集合
        List<Class<?>> classList = new ArrayList<>();
        //获取类加载器（本质上还是PathClassLoader）
        ClassLoader classLoader = abilityContext.getClassloader();
        for (Object dexFile : dexFiles) {
            if (dexFile == null) continue;
            //获取当前dexFile下面所有的类信息
            Enumeration<String> entries = (Enumeration<String>) entriesMethod.invoke(dexFile);
            //遍历过滤目标包名下的class
            while (entries.hasMoreElements()) {
                String currentClassPath = entries.nextElement();
                System.out.println(currentClassPath);
                if (currentClassPath == null || currentClassPath.isEmpty() || currentClassPath.indexOf(packageName) != 0)
                    continue;
                Class<?> entryClass = Class.forName(currentClassPath, true, classLoader);
                if (entryClass != null) classList.add(entryClass);
            }
        }
        return classList;
    }
}
