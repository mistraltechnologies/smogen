package com.mistraltech.smogen.codegenerator.javabuilder;

import java.util.ArrayList;
import java.util.List;

public abstract class MethodSignatureBuilder<T extends MethodSignatureBuilder<T>> extends AbstractBuilder<T> {
    protected String accessModifier;
    protected String methodName;
    protected TypeBuilder returnType;
    protected ArrayList<ParameterBuilder> parameters = new ArrayList<ParameterBuilder>();
    protected ArrayList<AnnotationBuilder> annotations = new ArrayList<AnnotationBuilder>();
    protected List<TypeParameterDeclBuilder> typeParameters = new ArrayList<TypeParameterDeclBuilder>();

    public T withAccessModifier(String modifier) {
        this.accessModifier = modifier;
        return self();
    }

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
}
