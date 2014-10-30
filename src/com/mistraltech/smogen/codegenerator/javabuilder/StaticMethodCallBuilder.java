package com.mistraltech.smogen.codegenerator.javabuilder;

import java.util.ArrayList;

import static com.mistraltech.smogen.codegenerator.javabuilder.BuilderUtils.buildMandatoryList;
import static com.mistraltech.smogen.codegenerator.javabuilder.ExpressionBuilder.anExpression;

public class StaticMethodCallBuilder extends ExpressionTermBuilder<StaticMethodCallBuilder> {
    private String name;
    private ArrayList<ExpressionTermBuilder> parameters = new ArrayList<ExpressionTermBuilder>();
    private TypeBuilder type;

    private StaticMethodCallBuilder() {
    }

    public static StaticMethodCallBuilder aStaticMethodCall() {
        return new StaticMethodCallBuilder();
    }

    public StaticMethodCallBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public StaticMethodCallBuilder withParameter(String parameter) {
        return withParameter(anExpression().withText(parameter));
    }

    public StaticMethodCallBuilder withParameter(ExpressionTermBuilder expression) {
        parameters.add(expression);
        return this;
    }

    public StaticMethodCallBuilder withType(TypeBuilder type) {
        this.type = type;
        return this;
    }

    @Override
    public String build(JavaBuilderContext context) {
        StringBuilder sb = new StringBuilder();

        String methodName = context.normaliseClassMemberReference(type.getTypeFQN() + "." + name, null);

        sb.append(methodName)
                .append(buildMandatoryList(context, "(", parameters, ")", ", "));

        return sb.toString();
    }
}
