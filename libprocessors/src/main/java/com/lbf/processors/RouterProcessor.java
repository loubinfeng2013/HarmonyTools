package com.lbf.processors;

import com.google.auto.service.AutoService;
import com.lbf.annotations.router.HRouter;
import com.squareup.javapoet.*;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@AutoService(Processor.class)
public class RouterProcessor extends AbstractProcessor {

    private Map<String, JavaFileDetail> javaFileDetailMap = new HashMap<>();

    private Elements mElementUtils;
    private Filer mFiler;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mElementUtils = processingEnv.getElementUtils();
        mFiler = processingEnv.getFiler();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(HRouter.class);
        for (Element element : elements) {
            if (element.getKind() == ElementKind.CLASS) {
                TypeElement typeElement = (TypeElement) element;
                String routerName = typeElement.getAnnotation(HRouter.class).value();
                String className = routerName.split("/")[1];
                JavaFileDetail javaFileDetail = javaFileDetailMap.get(className);
                if (javaFileDetail == null) {
                    javaFileDetail = new JavaFileDetail(className);
                    javaFileDetailMap.put(className, javaFileDetail);
                }
                javaFileDetail.addRouterName(routerName, typeElement);
            }
        }

        //生成文件
        for (Map.Entry<String, JavaFileDetail> entry : javaFileDetailMap.entrySet()) {
            JavaFile javaFile = JavaFile.builder(entry.getValue().getPackageName(), entry.getValue().generateFile()).build();
            try {
                javaFile.writeTo(mFiler);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //清除，否则会有无谓的报错
        javaFileDetailMap.clear();
        return true;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotations = new HashSet<>();
        annotations.add(HRouter.class.getCanonicalName());
        return annotations;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    /**
     * 自动生成的java文件描述
     */
    private class JavaFileDetail {
        private String mPackageName;//生成的包名
        private String mClassName;//生成的类名
        private Map<String, TypeElement> routerNameMap = new HashMap<>();

        public JavaFileDetail(String className) {
            this.mPackageName = "com.lbf.hrouter";
            this.mClassName = className;
        }

        public void addRouterName(String name, TypeElement element) {
            routerNameMap.put(name, element);
        }

        /**
         * 获取包名
         *
         * @return
         */
        public String getPackageName() {
            return this.mPackageName;
        }

        /**
         * 获取待生成的文件信息
         *
         * @return
         */
        public TypeSpec generateFile() {
            MethodSpec.Builder addRouterBuilder = MethodSpec.methodBuilder("addRouter")
                    .addModifiers(Modifier.PUBLIC)
                    .returns(void.class)
                    .addParameter(Map.class, "routerMap");

            for (Map.Entry<String, TypeElement> entry : routerNameMap.entrySet()) {
                addRouterBuilder.addStatement("routerMap.put($S," + entry.getValue().asType() + ".class)", entry.getKey());
            }

            TypeSpec typeSpec = TypeSpec.classBuilder(mClassName)
                    .addModifiers(Modifier.PUBLIC)
                    .addSuperinterface(ClassName.get("com.lbf.lib.router", "IRouter"))
                    .addMethod(addRouterBuilder.build())
                    .build();

            return typeSpec;
        }
    }
}
