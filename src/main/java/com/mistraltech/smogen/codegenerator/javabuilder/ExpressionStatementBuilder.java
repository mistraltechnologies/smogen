package com.mistraltech.smogen.codegenerator.javabuilder;

public class ExpressionStatementBuilder extends StatementBuilder<ExpressionStatementBuilder> {

    private ExpressionTermBuilder<?> expression;

    public static ExpressionStatementBuilder anExpressionStatement() {
        return new ExpressionStatementBuilder();
    }

    public ExpressionStatementBuilder withExpression(ExpressionTermBuilder<?> expression) {
        this.expression = expression;
        return self();
    }

    @Override
    public String build(JavaBuilderContext context) {
        StringBuilder sb = new StringBuilder();

        if (expression != null) {
            sb.append(expression.build(context))
                    .append(";\n");
        }


        return sb.toString();
    }
}
