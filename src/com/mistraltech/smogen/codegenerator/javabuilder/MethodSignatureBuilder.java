package com.mistraltech.smogen.codegenerator.javabuilder;

import java.util.ArrayList;
import java.util.List;

import static com.mistraltech.smogen.codegenerator.javabuilder.BuilderUtils.buildList;
import static com.mistraltech.smogen.codegenerator.javabuilder.BuilderUtils.buildMandatoryList;

public abstract class MethodSignatureBuilder<T extends MethodSignatureBuilder<T>> extends AbstractBuilder<T> {
    private String methodName;
    private TypeBuilder returnType;
    private List<ParameterBuilder> parameters = new ArrayList<>();
    private List<AnnotationBuilder> annotations = new ArrayList<>();
    private List<TypeParameterDeclBuilder> typeParameters = new ArrayList<>();

    public T withName(String methodName) {
        this.methodName = methodName;
        return self();
    }

    public T withReturnType(TypeBuilder type) {
        this.returnType = type;
        return self();
    }

    public T withParameter(ParameterBuilder parameter) {
        parameters.add(parameter);
        return self();
    }

    public T withTypeParameters(List<TypeParameterDeclBuilder> typeParameters) {
        typeParameters.forEach(this::withTypeParameter);
        return self();
    }

    private T withTypeParameter(TypeParameterDeclBuilder typeParameter) {
        this.typeParameters.add(typeParameter);
        return self();
    }

    public T withAnnotation(AnnotationBuilder annotation) {
        annotations.add(annotation);
        return self();
    }

    protected boolean isStatic() {
        return false;
    }

    protected boolean isFinal() {
        return false;
    }

    protected boolean isAbstract() {
        return false;
    }

    protected String getAccessModifier() {
        return null;
    }

    protected String getMethodName() {
        return methodName;
    }

    protected TypeBuilder getReturnType() {
        return returnType;
    }

    protected List<ParameterBuilder> getParameters() {
        return parameters;
    }

    protected List<AnnotationBuilder> getAnnotations() {
        return annotations;
    }

    protected List<TypeParameterDeclBuilder> getTypeParameters() {
        return typeParameters;
    }

    @Override
    public String build(JavaBuilderContext context) {
        StringBuilder sb = new StringBuilder();

        sb.append(buildList(context, "", annotations, "\n", ""));

        if (getAccessModifier() != null) {
            sb.append(getAccessModifier()).append(" ");
        }

        if (isStatic()) {
            sb.append("static ");
        }

        if (isFinal()) {
            sb.append("final ");
        }

        if (isAbstract()) {
            sb.append("abstract ");
        }

        sb.append(buildList(context, "<", typeParameters, ">", ", "));

        if (returnType != null) {
            sb.append(returnType.build(context)).append(" ");
        }

        sb.append(methodName);

        sb.append(buildMandatoryList(context, "(", parameters, ")", ", "));

        return sb.toString();
    }
}
