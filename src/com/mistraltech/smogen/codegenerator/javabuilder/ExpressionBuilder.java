package com.mistraltech.smogen.codegenerator.javabuilder;

import java.util.ArrayList;
import java.util.List;

import static com.mistraltech.smogen.codegenerator.javabuilder.BuilderUtils.buildList;
import static com.mistraltech.smogen.codegenerator.javabuilder.ExpressionTextBuilder.expressionText;

public class ExpressionBuilder extends ExpressionTermBuilder<ExpressionBuilder> {
    private List<ExpressionTermBuilder> terms = new ArrayList<>();

    private ExpressionBuilder() {
    }

    public static ExpressionBuilder anExpression() {
        return new ExpressionBuilder();
    }

    public ExpressionBuilder withText(String text) {
        withText(expressionText(text));
        return this;
    }

    public ExpressionBuilder withText(ExpressionTextBuilder text) {
        terms.add(text);
        return this;
    }

    public ExpressionBuilder withTerm(ExpressionTermBuilder term) {
        terms.add(term);
        return this;
    }


    @Override
    public String build(JavaBuilderContext context) {
        return buildList(context, "", terms, "", "");
    }
}
