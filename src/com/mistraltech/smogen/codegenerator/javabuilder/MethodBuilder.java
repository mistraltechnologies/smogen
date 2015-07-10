package com.mistraltech.smogen.codegenerator.javabuilder;

import java.util.ArrayList;
import java.util.List;

import static com.mistraltech.smogen.codegenerator.javabuilder.BuilderUtils.buildList;

public class MethodBuilder extends MethodSignatureBuilder<MethodBuilder> {
    protected String accessModifier;
    private boolean staticFlag;
    private boolean finalFlag;
    private boolean abstractFlag;
    private List<StatementBuilder> statements = new ArrayList<StatementBuilder>();

    private MethodBuilder() {
    }

    public static MethodBuilder aMethod() {
        return new MethodBuilder();
    }

    public MethodBuilder withStaticFlag(boolean staticFlag) {
        assert !finalFlag && !abstractFlag;

        this.staticFlag = staticFlag;
        return self();
    }

    public MethodBuilder withAbstractFlag(boolean abstractFlag) {
        assert !finalFlag && !staticFlag;

        this.abstractFlag = abstractFlag;
        return self();
    }

    public MethodBuilder withAccessModifier(String modifier) {
        this.accessModifier = modifier;
        return self();
    }

    public MethodBuilder withFinalFlag(boolean finalFlag) {
        assert !staticFlag && !abstractFlag;

        this.finalFlag = finalFlag;
        return self();
    }

    public MethodBuilder withStatement(StatementBuilder statement) {
        statements.add(statement);
        return self();
    }

    @Override
    protected boolean isStatic() {
        return staticFlag;
    }

    @Override
    protected boolean isFinal() {
        return finalFlag;
    }

    @Override
    protected boolean isAbstract() {
        return abstractFlag;
    }

    @Override
    protected String getAccessModifier() {
        return accessModifier;
    }

    @Override
    public String build(JavaBuilderContext context) {
        StringBuilder sb = new StringBuilder();

        sb.append(super.build(context));

        sb.append("{");

        sb.append(buildList(context, "", statements, "", ""));

        sb.append("}\n");

        return sb.toString();
    }
}
