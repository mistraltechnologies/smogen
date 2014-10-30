package com.mistraltech.smogen.codegenerator.javabuilder;

public class ReturnStatementBuilder extends StatementBuilder<ReturnStatementBuilder> {
    private ExpressionTermBuilder<?> expression;

    private ReturnStatementBuilder() {
    }

    public static ReturnStatementBuilder aReturnStatement() {
        return new ReturnStatementBuilder();
    }

    public ReturnStatementBuilder withExpression(ExpressionTermBuilder<?> expression) {
        this.expression = expression;
        return this;
    }

    @Override
    public String build(JavaBuilderContext context) {
        StringBuilder sb = new StringBuilder();

        sb.append("return ").append(expression.build(context)).append(";\n");

        return sb.toString();
    }
}
