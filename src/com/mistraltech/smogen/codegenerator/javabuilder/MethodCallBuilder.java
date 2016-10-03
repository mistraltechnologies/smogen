package com.mistraltech.smogen.codegenerator.javabuilder;

import java.util.ArrayList;

import static com.mistraltech.smogen.codegenerator.javabuilder.ExpressionBuilder.anExpression;

public class MethodCallBuilder extends ExpressionTermBuilder<MethodCallBuilder> {
    private String name;
    private ArrayList<ExpressionTermBuilder> parameters = new ArrayList<>();
    private ExpressionTermBuilder targetObject;

    private MethodCallBuilder() {
    }

    public static MethodCallBuilder aMethodCall() {
        return new MethodCallBuilder();
    }

    public MethodCallBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public MethodCallBuilder withParameter(String parameter) {
        return withParameter(anExpression().withText(parameter));
    }

    public MethodCallBuilder withParameter(ExpressionTermBuilder expression) {
        parameters.add(expression);
        return this;
    }

    public MethodCallBuilder withObject(String target) {
        return withObject(anExpression().withText(target));
    }

    public MethodCallBuilder withObject(ExpressionTermBuilder expression) {
        this.targetObject = expression;
        return this;
    }

    @Override
    public String build(JavaBuilderContext context) {
        StringBuilder sb = new StringBuilder();

        if (targetObject != null) {
            sb.append(targetObject.build(context)).append(".");
        }

        sb.append(name).append(BuilderUtils.buildMandatoryList(context, "(", parameters, ")", ", "));

        return sb.toString();
    }
}
