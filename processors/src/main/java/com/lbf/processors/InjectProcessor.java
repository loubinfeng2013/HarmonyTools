package com.lbf.processors;

import com.google.auto.service.AutoService;
import com.lbf.lib.injectv2.annotations.BindViewV2;
import com.lbf.lib.injectv2.annotations.OnClickV2;
import com.lbf.lib.injectv2.annotations.UiContentV2;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;

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

        //生成文件
        for (Map.Entry<String, JavaFileDetail> entry : javaFileMap.entrySet()) {
            JavaFile javaFile = JavaFile.builder(entry.getValue().getPackageName(), entry.getValue().generateFile()).build();
            try {
                javaFile.writeTo(mFilerUtils);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
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
        private int layoutId;

        public JavaFileDetail(Elements elementUtils, TypeElement typeElement) {
            this.mTypeElement = typeElement;
            this.mPackageName = elementUtils.getPackageOf(typeElement).getQualifiedName().toString();
            this.mClassName = typeElement.getSimpleName() + "$$Binder";
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
            MethodSpec.Builder bindBuilder = MethodSpec.methodBuilder("bind")
                    .addModifiers(Modifier.PUBLIC)
                    .returns(void.class)
                    .addParameter(TypeVariableName.get(mTypeElement.getSimpleName().toString()), "target")
                    .addStatement("target.setUIContent(" + layoutId + ")");

            TypeSpec typeSpec = TypeSpec.classBuilder(mClassName)
                    .addModifiers(Modifier.PUBLIC)
                    .addMethod(bindBuilder.build())
                    .build();

            return typeSpec;
        }
    }
}
