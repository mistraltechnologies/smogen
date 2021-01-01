package com.mistraltech.smogen.codegenerator.javabuilder;

import java.util.ArrayList;
import java.util.List;

import static com.mistraltech.smogen.codegenerator.javabuilder.BuilderUtils.buildList;

@SuppressWarnings("unused")
public class MethodBuilder extends MethodSignatureBuilder<MethodBuilder> {
    private final List<StatementBuilder<?>> statements = new ArrayList<>();
    protected String accessModifier;
    private boolean staticFlag;
    private boolean finalFlag;
    private boolean abstractFlag;

    private MethodBuilder() {
    }

    public static MethodBuilder aMethod() {
        return new MethodBuilder();
    }

    public MethodBuilder withStaticFlag(boolean staticFlag) {
        if (finalFlag && staticFlag) {
            throw new IllegalStateException("Method cannot be both final and static");
        }

        if (abstractFlag && staticFlag) {
            throw new IllegalStateException("Method cannot be both abstract and static");
        }

        this.staticFlag = staticFlag;
        return self();
    }

    public MethodBuilder withAbstractFlag(boolean abstractFlag) {
        if (finalFlag && abstractFlag) {
            throw new IllegalStateException("Method cannot be both final and abstract");
        }

        if (staticFlag && abstractFlag) {
            throw new IllegalStateException("Method cannot be both static and abstract");
        }

        this.abstractFlag = abstractFlag;
        return self();
    }

    public MethodBuilder withAccessModifier(String modifier) {
        this.accessModifier = modifier;
        return self();
    }

    public MethodBuilder withFinalFlag(boolean finalFlag) {
        if (abstractFlag && finalFlag) {
            throw new IllegalStateException("Method cannot be both abstract and final");
        }

        if (staticFlag && finalFlag) {
            throw new IllegalStateException("Method cannot be both static and final");
        }

        this.finalFlag = finalFlag;
        return self();
    }

    public MethodBuilder withStatement(StatementBuilder<?> statement) {
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
        return super.build(context) +
                "{" +
                buildList(context, "", statements, "", "") +
                "}\n";
    }
}
