package com.mistraltech.smogen.codegenerator.javabuilder;

import java.util.ArrayList;
import java.util.List;

import static com.mistraltech.smogen.codegenerator.javabuilder.BuilderUtils.buildList;

public class InterfaceBuilder extends AbstractTypeBuilder<InterfaceBuilder> {
    private List<InterfaceMethodBuilder> methods = new ArrayList<InterfaceMethodBuilder>();

    public static InterfaceBuilder aJavaInterface() {
        return new InterfaceBuilder();
    }

    public InterfaceBuilder withMethods(List<InterfaceMethodBuilder> methods) {
        this.methods.addAll(methods);
        return self();
    }

    public InterfaceBuilder withMethod(InterfaceMethodBuilder method) {
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

        sb.append(buildList(context, "", annotations, "\n", ""));

        writeModifiers(sb);

        sb.append("interface ")
                .append(className)
                .append(buildList(context, "<", typeParameters, ">", " ,"));

        sb.append(buildList(context, " extends ", interfaces, "", " ,"))
                .append(" {\n")
                .append(buildList(context, "", methods, "", ""))
                .append("}\n");

        return sb.toString();
    }

}
