package com.mistraltech.smogen.codegenerator.javabuilder;

import java.util.ArrayList;
import java.util.List;

import static com.mistraltech.smogen.codegenerator.javabuilder.BuilderUtils.buildList;

public abstract class AbstractClassBuilder<T extends AbstractClassBuilder<T>> extends AbstractTypeBuilder<T> {
    private boolean abstractFlag;
    private boolean finalFlag;
    private TypeBuilder superclass;
    private List<VariableBuilder> variables = new ArrayList<>();
    private List<MethodBuilder> methods = new ArrayList<>();
    private List<NestedClassBuilder> nestedClasses = new ArrayList<>();

    AbstractClassBuilder() {
    }

    public T withAbstractFlag(boolean abstractFlag) {
        if (finalFlag && abstractFlag) {
            throw new IllegalArgumentException("Class cannot be both abstract and final");
        }

        this.abstractFlag = abstractFlag;
        return self();
    }

    public T withFinalFlag(boolean finalFlag) {
        if (finalFlag && abstractFlag) {
            throw new IllegalArgumentException("Class cannot be both abstract and final");
        }

        this.finalFlag = finalFlag;
        return self();
    }

    public T withSuperclass(TypeBuilder type) {
        this.superclass = type;
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

    boolean isAbstract() {
        return abstractFlag;
    }

    boolean isFinal() {
        return finalFlag;
    }

    @Override
    public String build(JavaBuilderContext context) {
        StringBuilder sb = new StringBuilder();

        sb.append(buildList(context, "", getAnnotations(), "\n", ""));

        writeModifiers(sb);

        sb.append("class ")
                .append(getClassName())
                .append(buildList(context, "<", getTypeParameters(), ">", " ,"));

        if (superclass != null) {
            sb.append(" extends ").append(superclass.build(context));
        }

        sb.append(buildList(context, " implements ", getInterfaces(), "", " ,"))
                .append(" {\n")
                .append(buildList(context, "", variables, "", ""))
                .append(buildList(context, "", methods, "", ""))
                .append(buildList(context, "", nestedClasses, "", ""))
                .append("}\n");

        return sb.toString();
    }

    protected abstract void writeModifiers(StringBuilder sb);
}
