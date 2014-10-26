package com.mistraltech.smogen.codegenerator.javabuilder;

public class ExpressionTextBuilder extends ExpressionTermBuilder<ExpressionTextBuilder> {
    private final String text;

    private ExpressionTextBuilder(String text) {
        this.text = text;
    }

    public static ExpressionTextBuilder expressionText(String text) {
        return new ExpressionTextBuilder(text);
    }

    @Override
    public String build() {
        return text;
    }
}
