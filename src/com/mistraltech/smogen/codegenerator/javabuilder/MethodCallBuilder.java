package com.mistraltech.smogen.codegenerator.javabuilder;

import java.util.ArrayList;

import static com.mistraltech.smogen.codegenerator.javabuilder.ExpressionBuilder.anExpression;

public class MethodCallBuilder extends ExpressionTermBuilder<MethodCallBuilder> {
    private String name;
    private ArrayList<ExpressionTermBuilder> parameters = new ArrayList<ExpressionTermBuilder>();
    private String targetObject;

    private MethodCallBuilder() {
    }

    public static MethodCallBuilder aMethodCall()
    {
        return new MethodCallBuilder();
    }

    public MethodCallBuilder withName(String name)
    {
        this.name = name;
        return this;
    }

    public MethodCallBuilder withParameter(String parameter)
    {
        return withParameter(anExpression().withText(parameter));
    }

    public MethodCallBuilder withParameter(ExpressionTermBuilder expression)
    {
        addNestedBuilder(expression);
        parameters.add(expression);
        return this;
    }

    public MethodCallBuilder withObject(String target) {
        this.targetObject = target;
        return this;
    }

    @Override
    public String build() {
        StringBuilder sb = new StringBuilder();

        if (targetObject != null)
        {
            sb.append(targetObject).append(".");
        }

        sb.append(name).append(BuilderUtils.buildMandatoryList("(", parameters, ")", ", "));

        return sb.toString();
    }
}
