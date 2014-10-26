package com.mistraltech.smogen.codegenerator.javabuilder;

public class VariableBuilder extends AbstractBuilder<VariableBuilder> {

    private String accessModifier;
    private boolean staticFlag;
    private boolean finalFlag;
    private String variableName;
    private TypeBuilder type;
    private ExpressionTermBuilder initialiser;

    private VariableBuilder() {
    }

    public static VariableBuilder aVariable() {
        return new VariableBuilder();
    }

    public VariableBuilder withAccessModifier(String modifier) {
        this.accessModifier = modifier;
        return this;
    }

    public VariableBuilder withStaticFlag(boolean staticFlag) {
        this.staticFlag = staticFlag;
        return this;
    }

    public VariableBuilder withFinalFlag(boolean finalFlag) {
        this.finalFlag = finalFlag;
        return this;
    }

    public VariableBuilder withName(String variableName) {
        this.variableName = variableName;
        return this;
    }

    public VariableBuilder withType(TypeBuilder type)
    {
        addNestedBuilder(type);
        this.type = type;
        return this;
    }

    public VariableBuilder withInitialiser(ExpressionTermBuilder expression) {
        addNestedBuilder(expression);
        this.initialiser = expression;
        return this;
    }

    @Override
    public String build() {
        StringBuilder sb = new StringBuilder();

        if (accessModifier != null)
        {
            sb.append(accessModifier).append(" ");
        }

        if (staticFlag)
        {
            sb.append("static ");
        }

        if (finalFlag)
        {
            sb.append("final ");
        }

        sb.append(type.build());

        sb.append(variableName);

        if (initialiser != null)
        {
            sb.append(" = ").append(initialiser.build());
        }

        sb.append(";\n");

        return sb.toString();
    }
}
