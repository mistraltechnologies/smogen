package com.mistraltech.smogen.codegenerator.javabuilder;

import java.util.ArrayList;

import static com.mistraltech.smogen.codegenerator.javabuilder.BuilderUtils.buildList;

public class MethodBuilder extends AbstractBuilder<MethodBuilder> {

    private String accessModifier;
    private boolean staticFlag;
    private boolean finalFlag;
    private boolean abstractFlag;
    private String methodName;
    private TypeBuilder returnType;
    private ArrayList<ParameterBuilder> parameters = new ArrayList<ParameterBuilder>();
    private ArrayList<StatementBuilder> statements = new ArrayList<StatementBuilder>();

    private MethodBuilder() {
    }

    public static MethodBuilder aMethod() {
        return new MethodBuilder();
    }

    public MethodBuilder withAccessModifier(String modifier) {
        this.accessModifier = modifier;
        return this;
    }

    public MethodBuilder withStaticFlag(boolean staticFlag) {
        assert !finalFlag && !abstractFlag;

        this.staticFlag = staticFlag;
        return this;
    }

    public MethodBuilder withAbstractFlag(boolean abstractFlag) {
        assert !finalFlag && !staticFlag;

        this.abstractFlag = abstractFlag;
        return this;
    }

    public MethodBuilder withFinalFlag(boolean finalFlag) {
        assert !staticFlag && !abstractFlag;

        this.finalFlag = finalFlag;
        return this;
    }

    public MethodBuilder withName(String methodName) {
        this.methodName = methodName;
        return this;
    }

    public MethodBuilder withReturnType(TypeBuilder type) {
        addNestedBuilder(type);
        this.returnType = type;
        return this;
    }

    public MethodBuilder withParameter(ParameterBuilder parameter) {
        addNestedBuilder(parameter);
        parameters.add(parameter);
        return this;
    }

    public MethodBuilder withStatement(StatementBuilder statement) {
        addNestedBuilder(statement);
        statements.add(statement);
        return this;
    }

    @Override
    public String build() {
        StringBuilder sb = new StringBuilder();

        if (accessModifier != null) {
            sb.append(accessModifier).append(" ");
        }

        if (staticFlag) {
            sb.append("static ");
        } else if (finalFlag) {
            sb.append("final ");
        } else if (abstractFlag) {
            sb.append("abstract ");
        }

        if (returnType != null) {
            sb.append(returnType.build()).append(" ");
        }

        sb.append(methodName);

        sb.append(buildList("(", parameters, ")", ", ")).append(" {\n");

        sb.append(buildList("", statements, "", ""));

        sb.append("}\n");

        return sb.toString();
    }
}
