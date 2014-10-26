package com.mistraltech.smogen.codegenerator.javabuilder;

import java.util.ArrayList;
import java.util.List;

import static com.mistraltech.smogen.codegenerator.javabuilder.BuilderUtils.buildList;

public class JavaClassBuilder extends AbstractBuilder<JavaClassBuilder> {
    private String accessModifier;
    private boolean abstractFlag;
    private boolean finalFlag;
    private String className;
    private List<TypeParameterBuilder> typeParameters = new ArrayList<TypeParameterBuilder>();
    private ArrayList<TypeBuilder> interfaces = new ArrayList<TypeBuilder>();
    private ArrayList<VariableBuilder> variables = new ArrayList<VariableBuilder>();
    private ArrayList<MethodBuilder> methods = new ArrayList<MethodBuilder>();
    private TypeBuilder superclass;

    private JavaClassBuilder() {
    }

    public static JavaClassBuilder aJavaClass() {
        return new JavaClassBuilder();
    }

    public JavaClassBuilder withAccessModifier(String modifier) {
        this.accessModifier = modifier;
        return this;
    }

    public JavaClassBuilder withAbstractFlag(boolean abstractFlag) {
        assert !finalFlag;

        this.abstractFlag = abstractFlag;
        return this;
    }

    public JavaClassBuilder withFinalFlag(boolean finalFlag) {
        assert !abstractFlag;

        this.finalFlag = finalFlag;
        return this;
    }

    public JavaClassBuilder withName(String className) {
        this.className = className;
        return this;
    }

    public JavaClassBuilder withTypeParameter(TypeParameterBuilder param) {
        this.typeParameters.add(param);
        this.addNestedBuilder(param);
        return this;
    }

    public JavaClassBuilder withSuperclass(TypeBuilder typeBuilder) {
        this.superclass = typeBuilder;
        this.addNestedBuilder(typeBuilder);
        return this;
    }

    public JavaClassBuilder withImplementedInterface(TypeBuilder typeBuilder) {
        this.interfaces.add(typeBuilder);
        this.addNestedBuilder(typeBuilder);
        return this;
    }

    public JavaClassBuilder withVariables(List<VariableBuilder> variableBuilders) {
        this.variables.addAll(variableBuilders);
        this.addNestedBuilders(variableBuilders);
        return this;
    }

    public JavaClassBuilder withMethods(List<MethodBuilder> methodBuilders) {
        this.methods.addAll(methodBuilders);
        this.addNestedBuilders(methodBuilders);
        return this;
    }

    public JavaClassBuilder withMethod(MethodBuilder method) {
        this.methods.add(method);
        this.addNestedBuilder(method);
        return this;
    }

    public String build() {
        StringBuilder sb = new StringBuilder();
        if (accessModifier != null) {
            sb.append(accessModifier).append(" ");
        }

        if (abstractFlag) {
            sb.append("abstract ");
        } else if (finalFlag) {
            sb.append("final ");
        }

        sb.append("class ").append(className);

        sb.append(buildList("<", typeParameters, ">", " ,"));

        if (superclass != null)
        {
            sb.append(" extends ").append(superclass.build());
        }

        sb.append(buildList(" implements ", interfaces, "", " ,"));

        sb.append(" {\n");

        sb.append(buildList("", variables, "", ""));

        sb.append(buildList("", methods, "", ""));

        sb.append("}\n");

        return sb.toString();
    }
}
