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
    private List<TypeParameterDeclBuilder> typeParameters = new ArrayList<TypeParameterDeclBuilder>();
    private ArrayList<TypeBuilder> interfaces = new ArrayList<TypeBuilder>();
    private ArrayList<VariableBuilder> variables = new ArrayList<VariableBuilder>();
    private ArrayList<MethodBuilder> methods = new ArrayList<MethodBuilder>();
    private ArrayList<NestedClassBuilder> nestedClasses = new ArrayList<NestedClassBuilder>();
    private List<AnnotationBuilder> annotations = new ArrayList<AnnotationBuilder>();

    protected AbstractClassBuilder() {
    }

    public T withAccessModifier(String modifier) {
        this.accessModifier = modifier;
        return self();
    }

    public T withAbstractFlag(boolean abstractFlag) {
        assert !(finalFlag && abstractFlag);

        this.abstractFlag = abstractFlag;
        return self();
    }

    public T withFinalFlag(boolean finalFlag) {
        assert !(finalFlag && abstractFlag);

        this.finalFlag = finalFlag;
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

    public T withSuperclass(TypeBuilder type) {
        this.superclass = type;
        return self();
    }

    public T withImplementedInterface(TypeBuilder type) {
        this.interfaces.add(type);
        return self();
    }

    public T withVariables(List<VariableBuilder> variables) {
        this.variables.addAll(variables);
        return self();
    }

    public T withVariable(VariableBuilder variable) {
        this.variables.add(variable);
        return self();
    }

    public T withMethods(List<MethodBuilder> methods) {
        this.methods.addAll(methods);
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

    public T withAnnotation(AnnotationBuilder annotation) {
        annotations.add(annotation);
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

        sb.append(buildList(context, "", annotations, "\n", ""));

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
