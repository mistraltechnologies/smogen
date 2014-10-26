package com.mistraltech.smogen.codegenerator.javabuilder;

import java.util.ArrayList;

import static com.mistraltech.smogen.codegenerator.javabuilder.BuilderUtils.buildList;
import static com.mistraltech.smogen.codegenerator.javabuilder.ExpressionTextBuilder.expressionText;

public class ExpressionBuilder extends ExpressionTermBuilder<ExpressionBuilder> {
    private ArrayList<ExpressionTermBuilder> terms = new ArrayList<ExpressionTermBuilder>();

    private ExpressionBuilder() {
    }

    public static ExpressionBuilder anExpression()
    {
        return new ExpressionBuilder();
    }

    public ExpressionBuilder withText(String text) {
        withText(expressionText(text));
        return this;
    }

    public ExpressionBuilder withText(ExpressionTextBuilder text) {
        terms.add(text);
        addNestedBuilder(text);
        return this;
    }

//    public ExpressionBuilder withType(TypeBuilder type) {
//        terms.add(type);
//        addNestedBuilder(type);
//        return this;
//    }

    @Override
    public String build() {
        StringBuilder sb = new StringBuilder();

        sb.append(buildList("", terms, "", ""));

        return sb.toString();
    }
}
