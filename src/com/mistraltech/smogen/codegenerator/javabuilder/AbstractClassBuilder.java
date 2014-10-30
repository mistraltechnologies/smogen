package com.mistraltech.smogen.codegenerator.javabuilder;

import java.util.ArrayList;
import java.util.List;

import static com.mistraltech.smogen.codegenerator.javabuilder.BuilderUtils.buildList;

public abstract class AbstractClassBuilder<T extends AbstractClassBuilder<T>> extends AbstractBuilder<T> {
    private String accessModifier;
    private boolean abstractFlag;
    private boolean finalFlag;
    private String className;
    private TypeBuilder superclass;
    private List<TypeParameterBuilder> typeParameters = new ArrayList<TypeParameterBuilder>();
    private ArrayList<TypeBuilder> interfaces = new ArrayList<TypeBuilder>();
    private ArrayList<VariableBuilder> variables = new ArrayList<VariableBuilder>();
    private ArrayList<MethodBuilder> methods = new ArrayList<MethodBuilder>();
    private ArrayList<NestedClassBuilder> nestedClasses = new ArrayList<NestedClassBuilder>();

    protected AbstractClassBuilder() {
    }

    public T withAccessModifier(String modifier) {
        this.accessModifier = modifier;
        return self();
    }

    public T withAbstractFlag(boolean abstractFlag) {
        assert !finalFlag;

        this.abstractFlag = abstractFlag;
        return self();
    }

    public T withFinalFlag(boolean finalFlag) {
        assert !abstractFlag;

        this.finalFlag = finalFlag;
        return self();
    }

    public T withName(String className) {
        this.className = className;
        return self();
    }

    public T withTypeParameter(TypeParameterBuilder param) {
        this.typeParameters.add(param);
        return self();
    }

    public T withSuperclass(TypeBuilder typeBuilder) {
        this.superclass = typeBuilder;
        return self();
    }

    public T withImplementedInterface(TypeBuilder typeBuilder) {
        this.interfaces.add(typeBuilder);
        return self();
    }

    public T withVariables(List<VariableBuilder> variableBuilders) {
        this.variables.addAll(variableBuilders);
        return self();
    }

    public T withMethods(List<MethodBuilder> methodBuilders) {
        this.methods.addAll(methodBuilders);
        return self();
    }

    public T withMethod(MethodBuilder method) {
        this.methods.add(method);
        return self();
    }

    public T withNestedClass(NestedClassBuilder nestedClass) {
        nestedClasses.add(nestedClass);
        return self();
    }

    protected String getAccessModifier() {
        return accessModifier;
    }

    protected boolean isAbstract() {
        return abstractFlag;
    }

    protected boolean isFinal() {
        return finalFlag;
    }

    @Override
    public String build(JavaBuilderContext context) {
        StringBuilder sb = new StringBuilder();

        writeModifiers(sb);

        sb.append("class ")
                .append(className)
                .append(buildList(context, "<", typeParameters, ">", " ,"));

        if (superclass != null) {
            sb.append(" extends ").append(superclass.build(context));
        }

        sb.append(buildList(context, " implements ", interfaces, "", " ,"))
                .append(" {\n")
                .append(buildList(context, "", variables, "", ""))
                .append(buildList(context, "", methods, "", ""))
                .append(buildList(context, "", nestedClasses, "", ""))
                .append("}\n");

        return sb.toString();
    }

    protected abstract void writeModifiers(StringBuilder sb);
}
