package com.mistraltech.smogen.codegenerator.javabuilder;

import java.util.ArrayList;
import java.util.List;

import static com.mistraltech.smogen.codegenerator.javabuilder.BuilderUtils.buildList;
import static com.mistraltech.smogen.codegenerator.javabuilder.BuilderUtils.buildMandatoryList;

public abstract class MethodSignatureBuilder<T extends MethodSignatureBuilder<T>> extends AbstractBuilder<T> {
    protected String methodName;
    protected TypeBuilder returnType;
    protected ArrayList<ParameterBuilder> parameters = new ArrayList<ParameterBuilder>();
    protected ArrayList<AnnotationBuilder> annotations = new ArrayList<AnnotationBuilder>();
    protected List<TypeParameterDeclBuilder> typeParameters = new ArrayList<TypeParameterDeclBuilder>();

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
        for (TypeParameterDeclBuilder typeParameter : typeParameters) {
            withTypeParameter(typeParameter);
        }
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
