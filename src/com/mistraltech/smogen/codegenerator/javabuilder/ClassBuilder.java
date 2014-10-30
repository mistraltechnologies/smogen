package com.mistraltech.smogen.codegenerator.javabuilder;

public class ClassBuilder extends AbstractClassBuilder<ClassBuilder> {

    private ClassBuilder() {
    }

    public static ClassBuilder aJavaClass() {
        return new ClassBuilder();
    }

    protected void writeModifiers(StringBuilder sb) {
        if (getAccessModifier() != null) {
            sb.append(getAccessModifier()).append(" ");
        }

        if (isAbstract()) {
            sb.append("abstract ");
        } else if (isFinal()) {
            sb.append("final ");
        }
    }

    @Override
    public String build(JavaBuilderContext context) {
        StringBuilder sb = new StringBuilder();

        sb.append(super.build(context));

        return sb.toString();
    }
}
