package com.mistraltech.smogen.codegenerator.javabuilder;

import java.util.ArrayList;
import java.util.List;

import static com.mistraltech.smogen.codegenerator.javabuilder.BuilderUtils.buildList;

@SuppressWarnings("UnusedReturnValue")
public class InterfaceBuilder extends AbstractTypeBuilder<InterfaceBuilder> {
    private final List<MethodSignatureBuilder<?>> methods = new ArrayList<>();

    public static InterfaceBuilder aJavaInterface() {
        return new InterfaceBuilder();
    }

    public InterfaceBuilder withMethods(List<? extends MethodSignatureBuilder<?>> methods) {
        this.methods.addAll(methods);
        return self();
    }

    public InterfaceBuilder withMethod(MethodSignatureBuilder<?> method) {
        this.methods.add(method);
        return self();
    }

    protected void writeModifiers(StringBuilder sb) {
        if (getAccessModifier() != null) {
            sb.append(getAccessModifier()).append(" ");
        }
    }

    @Override
    public String build(JavaBuilderContext context) {
        StringBuilder sb = new StringBuilder();

        sb.append(buildList(context, "", getAnnotations(), "\n", ""));

        writeModifiers(sb);

        sb.append("interface ")
                .append(getClassName())
                .append(buildList(context, "<", getTypeParameters(), ">", " ,"));

        sb.append(buildList(context, " extends ", getInterfaces(), "", " ,"))
                .append(" {\n")
                .append(buildList(context, "", methods, "", ""))
                .append("}\n");

        return sb.toString();
    }

}
