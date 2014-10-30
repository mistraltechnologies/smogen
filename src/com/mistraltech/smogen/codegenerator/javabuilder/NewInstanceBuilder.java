package com.mistraltech.smogen.codegenerator.javabuilder;

import java.util.ArrayList;

import static com.mistraltech.smogen.codegenerator.javabuilder.BuilderUtils.buildMandatoryList;
import static com.mistraltech.smogen.codegenerator.javabuilder.ExpressionBuilder.anExpression;

public class NewInstanceBuilder extends ExpressionTermBuilder<NewInstanceBuilder> {
    private TypeBuilder type;
    private ArrayList<ExpressionBuilder> parameters = new ArrayList<ExpressionBuilder>();

    private NewInstanceBuilder() {
    }

    public static NewInstanceBuilder aNewInstance() {
        return new NewInstanceBuilder();
    }

    public NewInstanceBuilder withType(TypeBuilder type) {
        this.type = type;
        return this;
    }

    public NewInstanceBuilder withParameter(String parameter) {
        return withParameter(anExpression().withText(parameter));
    }

    public NewInstanceBuilder withParameter(ExpressionBuilder expression) {
        parameters.add(expression);
        return this;
    }

    @Override
    public String build(JavaBuilderContext context) {
        StringBuilder sb = new StringBuilder();

        sb.append("new ").append(type.build(context)).append(buildMandatoryList(context, "(", parameters, ")", ", "));

        return sb.toString();
    }
}
