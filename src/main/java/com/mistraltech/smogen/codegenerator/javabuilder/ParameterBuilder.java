package com.mistraltech.smogen.codegenerator.javabuilder;

public class ParameterBuilder extends AbstractBuilder<ParameterBuilder> {
    private boolean finalFlag;
    private String parameterName;
    private TypeBuilder type;

    private ParameterBuilder() {
    }

    public static ParameterBuilder aParameter() {
        return new ParameterBuilder();
    }

    public ParameterBuilder withFinalFlag(boolean finalFlag) {
        this.finalFlag = finalFlag;
        return this;
    }

    public ParameterBuilder withName(String className) {
        this.parameterName = className;
        return this;
    }

    public ParameterBuilder withType(TypeBuilder type) {
        this.type = type;
        return this;
    }

    @Override
    public String build(JavaBuilderContext context) {
        StringBuilder sb = new StringBuilder();

        if (finalFlag) {
            sb.append("final ");
        }

        sb.append(type.build(context))
                .append(" ")
                .append(parameterName);

        return sb.toString();
    }
}
