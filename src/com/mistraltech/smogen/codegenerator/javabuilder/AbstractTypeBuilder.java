package com.mistraltech.smogen.codegenerator.javabuilder;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractTypeBuilder<T extends AbstractTypeBuilder<T>> extends AbstractBuilder<T> {
    protected String className;
    protected List<TypeParameterDeclBuilder> typeParameters = new ArrayList<TypeParameterDeclBuilder>();
    protected ArrayList<TypeBuilder> interfaces = new ArrayList<TypeBuilder>();
    protected List<AnnotationBuilder> annotations = new ArrayList<AnnotationBuilder>();
    private String accessModifier;

    public T withAccessModifier(String modifier) {
        this.accessModifier = modifier;
        return self();
    }

    public T withName(String className) {
        this.className = className;
        return self();
    }

    public T withTypeParameters(List<TypeParameterDeclBuilder> params) {
        this.typeParameters.addAll(params);
        return self();
    }

    public T withTypeParameter(TypeParameterDeclBuilder param) {
        this.typeParameters.add(param);
        return self();
    }

    public T withImplementedInterface(TypeBuilder type) {
        this.interfaces.add(type);
        return self();
    }

    public T withAnnotation(AnnotationBuilder annotation) {
        annotations.add(annotation);
        return self();
    }

    protected String getAccessModifier() {
        return accessModifier;
    }

    protected abstract void writeModifiers(StringBuilder sb);
}
