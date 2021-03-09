1目的
实现鸿蒙版本的ButterKnife

2实现方式
本质上是注入，可以选择运行期注入，或者编译期注入。运行期注入基于的技术主要是Runtime-Annotation和反射，编译期注入基于Class-Annotation
和APT。APT是属于Java的技术，并不是只有在Android才能用，理论上在鸿蒙上也是行的通的。

3运行期注入

绑定控件的注解：
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface BindView {
    int value();
}

点击控件的注解：
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface OnClick {
    int[] value();
}

绑定界面布局的注解：
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface UiContent {
    int value();
}

上面三种的注解都是RetentionPolicy.RUNTIME，表示会在程序运行期生效。

注入代码：
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

反注入代码：
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

举例处理绑定控件注释：
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

代码就是扫描目标实例中所有属性，将被@BindView修饰的属性找出来处理，一样是通过findComponentById找个控件实例，再用反射技术把控件实例设置回去。
至此，注入工作就完成了。

使用代码：
@UiContent(ResourceTable.Layout_ability_main)
public class MainAbilitySlice extends AbilitySlice {

    @BindView(ResourceTable.Id_text_helloworld)
    private Text text;

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        InjectHelper.Inject(this);
        text.setText("hm2021");
    }

    @Override
    protected void onStop() {
        super.onStop();
        InjectHelper.UnInject(this);
    }

    @OnClick(ResourceTable.Id_text_helloworld)
    private void clickText(Component component) {
        present(new AptAbilitySlice(), new Intent());
    }
}

代码运行正常，V1版本完成。Android早期也会不少开源的注入框架是基于这个技术实现的，还是比较简单的。

4编译期注入

APT技术介绍
APT全称Annotation Processing Tool，即编辑器注解处理技术。APT技术会在代码编译的时候处理注解，自动生成一些通用代码，这样就可以
大大减轻开发中重复性的工作，即提高的工作效率，也让代码看上去比较简洁。在Android领域，APT的运行非常广泛，比如ButterKnife，ARouter
等开源框架都是用到了这项技术。

APT开发步骤
1声明编译期注解，还是用绑定控件举例
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.FIELD)
public @interface BindViewV2 {
    int value();
}

2创建一个注解处理的模块，模块配置如下
//用来注册自定义注解处理器
implementation "com.google.auto.service:auto-service:1.0-rc4"
annotationProcessor "com.google.auto.service:auto-service:1.0-rc4"
//辅助生成java文件的帮助类
implementation 'com.squareup:javapoet:1.11.1'
//存放注解的模块
implementation project(':annotations')

3创建自定义注解处理器
@AutoService(Processor.class)
public class InjectProcessor extends AbstractProcessor {

    private Map<String, JavaFileDetail> javaFileMap = new HashMap<>();

    private Elements mElementUtils;//用来处理程序元素的工具类
    private Filer mFilerUtils;//用来生成java文件的工具类

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mElementUtils = processingEnv.getElementUtils();
        mFilerUtils = processingEnv.getFiler();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        processUiContentV2(annotations, roundEnv);
        processBindViewV2(annotations, roundEnv);
        processOnClickV2(annotations, roundEnv);

        //生成文件
        for (Map.Entry<String, JavaFileDetail> entry : javaFileMap.entrySet()) {
            JavaFile javaFile = JavaFile.builder(entry.getValue().getPackageName(), entry.getValue().generateFile()).build();
            try {
                javaFile.writeTo(mFilerUtils);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //清除，否则会有无谓的报错
        javaFileMap.clear();
        return true;
    }
    ......
}

自定义的注解处理类，需要继承AbstractProcessor，主要重写4个方法：
//初始化函数
public synchronized void init(ProcessingEnvironment processingEnv)
//处理注解的函数
public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv)
//设置支持的注解类型
public Set<String> getSupportedAnnotationTypes()
//设置代码支持的JDK版本
public SourceVersion getSupportedSourceVersion()

4创建注入Api

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

5使用

@UiContentV2(ResourceTable.Layout_ability_apt)
public class AptAbilitySlice extends AbilitySlice {

    @BindViewV2(ResourceTable.Id_text_apt)
    Text aptText;

    @Override
    protected void onStart(Intent intent) {
        super.onStart(intent);
        InjectHelperV2.Inject(this);
        aptText.setText("apt222");
    }

    @Override
    protected void onStop() {
        super.onStop();
        InjectHelperV2.UnInject(this);
    }

    @OnClickV2(ResourceTable.Id_text_apt)
    void clickText(Component component) {
        ((Text) component).setText("apt33333");
    }
}

工作执行流程

1当我们执行build编译的时候，会先在entry/build/generated/sources/...生成一个java文件，如下：
public class AptAbilitySlice$$ViewBinder implements IViewBinder<AptAbilitySlice> {
  public void bind(AptAbilitySlice target) {
    target.setUIContent(16777221);
    target.aptText= (ohos.agp.components.Text)target.findComponentById(16777223);
    target.findComponentById(16777223).setClickedListener(cpt -> {target.clickText(cpt);});
  }

  public void unBind(AptAbilitySlice target) {
    target.aptText= null;
  }
}
2然后执行注入的时候，会根据命名规则得到自动生成java类的类名，然后反射获取实例：
Class clazz = Class.forName(abilityContext.getClass().getCanonicalName() + "$$ViewBinder");
3执行bind方法完成注入：
IViewBinder instance = (IViewBinder) constructor.newInstance();
instance.bind(abilityContext);

检验成果，代码运行在P40上一切正常，至此注入V2版本大功告成。

Github地址：https://github.com/loubinfeng2013/HarmonyTools


