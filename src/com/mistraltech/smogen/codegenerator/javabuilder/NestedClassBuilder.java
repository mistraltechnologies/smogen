package com.mistraltech.smogen.codegenerator.javabuilder;

public class NestedClassBuilder extends AbstractClassBuilder<NestedClassBuilder> {
    private boolean staticFlag;

    private NestedClassBuilder() {
    }

    public static NestedClassBuilder aNestedClass() {
        return new NestedClassBuilder();
    }

    public NestedClassBuilder withStaticFlag(boolean staticFlag) {
        this.staticFlag = staticFlag;
        return this;
    }

    @Override
    protected void writeModifiers(StringBuilder sb) {
        if (getAccessModifier() != null) {
            sb.append(getAccessModifier()).append(" ");
        }

        if (staticFlag) {
            sb.append("static ");
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
