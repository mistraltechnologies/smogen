package com.mistraltech.smogen.codegenerator.javabuilder;

import java.util.ArrayList;

import static com.mistraltech.smogen.codegenerator.javabuilder.BuilderUtils.buildList;

public class BlockStatementBuilder extends StatementBuilder<BlockStatementBuilder> {

    private String headerText;
    private ArrayList<StatementBuilder> statements = new ArrayList<StatementBuilder>();

    private BlockStatementBuilder() {
    }

    public static BlockStatementBuilder aBlockStatement() {
        return new BlockStatementBuilder();
    }

    public BlockStatementBuilder withHeader(String text)
    {
        this.headerText = text;
        return this;
    }

    public BlockStatementBuilder withStatement(StatementBuilder statement)
    {
        statements.add(statement);
        addNestedBuilder(statement);
        return this;
    }

    public BlockStatementBuilder withStatement(ExpressionTermBuilder expression)
    {
        return withStatement(ExpressionStatementBuilder.anExpressionStatement().withExpression(expression));
    }

    @Override
    public String build() {
        StringBuilder sb = new StringBuilder();

        sb.append(headerText).append("{\n").append(buildList("", statements, "", "")).append("}\n");

        return sb.toString();
    }
}
