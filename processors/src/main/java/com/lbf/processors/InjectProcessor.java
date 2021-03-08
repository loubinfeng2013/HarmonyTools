package com.lbf.processors;

import com.google.auto.service.AutoService;
import com.lbf.annotations.BindViewV2;
import com.lbf.annotations.OnClickV2;
import com.lbf.annotations.UiContentV2;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.util.Elements;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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

    /**
     * 处理OnClickV2注解
     *
     * @param annotations
     * @param roundEnv
     */
    private void processOnClickV2(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(OnClickV2.class);
        for (Element element : elements) {
            if (element.getKind() == ElementKind.METHOD) {
                ExecutableElement executableElement = (ExecutableElement) element;
                int[] resIds = executableElement.getAnnotation(OnClickV2.class).value();
                TypeElement typeElement = (TypeElement) executableElement.getEnclosingElement();
                String fullName = typeElement.getQualifiedName().toString();
                JavaFileDetail javaFileDetail = javaFileMap.get(fullName);
                if (javaFileDetail == null) {
                    javaFileDetail = new JavaFileDetail(mElementUtils, typeElement);
                    javaFileMap.put(fullName, javaFileDetail);
                }
                javaFileDetail.addClickId(resIds, executableElement);
            }
        }
    }

    /**
     * BindViewV2
     *
     * @param annotations
     * @param roundEnv
     */
    private void processBindViewV2(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(BindViewV2.class);
        for (Element element : elements) {
            if (element.getKind() == ElementKind.FIELD) {
                VariableElement variableElement = (VariableElement) element;
                int resId = variableElement.getAnnotation(BindViewV2.class).value();
                TypeElement typeElement = (TypeElement) variableElement.getEnclosingElement();
                String fullName = typeElement.getQualifiedName().toString();
                JavaFileDetail javaFileDetail = javaFileMap.get(fullName);
                if (javaFileDetail == null) {
                    javaFileDetail = new JavaFileDetail(mElementUtils, typeElement);
                    javaFileMap.put(fullName, javaFileDetail);
                }
                javaFileDetail.addViewId(resId, variableElement);
            }
        }
    }

    /**
     * 处理UiContentV2注解
     *
     * @param annotations
     * @param roundEnv
     */
    private void processUiContentV2(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(UiContentV2.class);
        for (Element element : elements) {
            if (element.getKind() == ElementKind.CLASS) {
                TypeElement typeElement = (TypeElement) element;
                int layoutId = typeElement.getAnnotation(UiContentV2.class).value();
                String fullName = typeElement.getQualifiedName().toString();
                JavaFileDetail javaFileDetail = javaFileMap.get(fullName);
                if (javaFileDetail == null) {
                    javaFileDetail = new JavaFileDetail(mElementUtils, typeElement);
                    javaFileMap.put(fullName, javaFileDetail);
                }
                javaFileDetail.setLayoutId(layoutId);
            }
        }
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotations = new HashSet<>();
        annotations.add(BindViewV2.class.getCanonicalName());
        annotations.add(OnClickV2.class.getCanonicalName());
        annotations.add(UiContentV2.class.getCanonicalName());
        return annotations;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    /**
     * 自动生成的java文件描述
     */
    class JavaFileDetail {
        private String mPackageName;//生成的包名
        private String mClassName;//生成的类名
        private TypeElement mTypeElement;//对应处理的类
        private Map<Integer, VariableElement> variableElementMap = new HashMap<>();//需要绑定的控件集合
        private Map<int[], ExecutableElement> executableElementMap = new HashMap<>();//需要绑定点击的控件集合
        private int layoutId;

        public JavaFileDetail(Elements elementUtils, TypeElement typeElement) {
            this.mTypeElement = typeElement;
            this.mPackageName = elementUtils.getPackageOf(typeElement).getQualifiedName().toString();
            this.mClassName = typeElement.getSimpleName() + "$$ViewBinder";
        }

        /**
         * 设置布局
         *
         * @param layoutId
         */
        public void setLayoutId(int layoutId) {
            this.layoutId = layoutId;
        }

        /**
         * 添加控件
         *
         * @param viewId
         * @param element
         */
        public void addViewId(int viewId, VariableElement element) {
            variableElementMap.put(viewId, element);
        }

        /**
         * 添加点击控件
         *
         * @param viewIds
         * @param element
         */
        public void addClickId(int[] viewIds, ExecutableElement element) {
            executableElementMap.put(viewIds, element);
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
            MethodSpec.Builder unBindBuilder = MethodSpec.methodBuilder("unBind")
                    .addModifiers(Modifier.PUBLIC)
                    .returns(void.class)
                    .addParameter(TypeVariableName.get(mTypeElement.getSimpleName().toString()), "target");

            MethodSpec.Builder bindBuilder = MethodSpec.methodBuilder("bind")
                    .addModifiers(Modifier.PUBLIC)
                    .returns(void.class)
                    .addParameter(TypeVariableName.get(mTypeElement.getSimpleName().toString()), "target")
                    .addStatement("target.setUIContent(" + layoutId + ")");

            for (Map.Entry<Integer, VariableElement> entry : variableElementMap.entrySet()) {
                bindBuilder.addStatement("target." + entry.getValue().getSimpleName().toString() +
                        "= (" + entry.getValue().asType().toString() + ")target.findComponentById(" + entry.getKey() + ")");
                unBindBuilder.addStatement("target." + entry.getValue().getSimpleName().toString() +
                        "= null");
            }

            for (Map.Entry<int[], ExecutableElement> entry : executableElementMap.entrySet()) {
                for (int id : entry.getKey()) {
                    bindBuilder.addStatement("target.findComponentById("+id+").setClickedListener(cpt -> {target."+entry.getValue().getSimpleName().toString()+"(cpt);})");
                }
            }

            TypeSpec typeSpec = TypeSpec.classBuilder(mClassName)
                    .addModifiers(Modifier.PUBLIC)
                    .addMethod(bindBuilder.build())
                    .addMethod(unBindBuilder.build())
                    .build();

            return typeSpec;
        }
    }
}
