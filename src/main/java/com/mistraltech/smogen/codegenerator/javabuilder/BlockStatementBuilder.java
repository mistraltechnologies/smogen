package com.mistraltech.smogen.codegenerator.javabuilder;

import java.util.ArrayList;
import java.util.List;

import static com.mistraltech.smogen.codegenerator.javabuilder.BuilderUtils.buildList;

@SuppressWarnings("UnusedReturnValue")
public class BlockStatementBuilder extends StatementBuilder<BlockStatementBuilder> {

    private final List<StatementBuilder<?>> statements = new ArrayList<>();
    private String headerText;

    private BlockStatementBuilder() {
    }

    public static BlockStatementBuilder aBlockStatement() {
        return new BlockStatementBuilder();
    }

    public BlockStatementBuilder withHeader(String text) {
        this.headerText = text;
        return this;
    }

    public BlockStatementBuilder withStatement(StatementBuilder<?> statement) {
        statements.add(statement);
        return this;
    }

    public BlockStatementBuilder withStatement(ExpressionTermBuilder<?> expression) {
        return withStatement(ExpressionStatementBuilder.anExpressionStatement().withExpression(expression));
    }

    @Override
    public String build(JavaBuilderContext context) {
        return headerText + "{\n" + buildList(context, "", statements, "", "") + "}\n";
    }
}
