package com.mistraltech.smogen.codegenerator.javabuilder;

import java.util.ArrayList;
import java.util.List;

import static com.mistraltech.smogen.codegenerator.javabuilder.BuilderUtils.buildList;
import static com.mistraltech.smogen.codegenerator.javabuilder.BuilderUtils.buildMandatoryList;

public class MethodBuilder extends MethodSignatureBuilder<MethodBuilder> {
    private boolean staticFlag;
    private boolean finalFlag;
    private boolean abstractFlag;
    private List<StatementBuilder> statements = new ArrayList<StatementBuilder>();

    private MethodBuilder() {
    }

    public static MethodBuilder aMethod() {
        return new MethodBuilder();
    }

    public MethodBuilder withStaticFlag(boolean staticFlag) {
        assert !finalFlag && !abstractFlag;

        this.staticFlag = staticFlag;
        return self();
    }

    public MethodBuilder withAbstractFlag(boolean abstractFlag) {
        assert !finalFlag && !staticFlag;

        this.abstractFlag = abstractFlag;
        return self();
    }

    public MethodBuilder withFinalFlag(boolean finalFlag) {
        assert !staticFlag && !abstractFlag;

        this.finalFlag = finalFlag;
        return self();
    }

    public MethodBuilder withStatement(StatementBuilder statement) {
        statements.add(statement);
        return self();
    }

    @Override
    public String build(JavaBuilderContext context) {
        StringBuilder sb = new StringBuilder();

        sb.append(buildList(context, "", annotations, "\n", ""));

        if (accessModifier != null) {
            sb.append(accessModifier).append(" ");
        }

        if (staticFlag) {
            sb.append("static ");
        } else if (finalFlag) {
            sb.append("final ");
        } else if (abstractFlag) {
            sb.append("abstract ");
        }

        sb.append(buildList(context, "<", typeParameters, ">", ", "));

        if (returnType != null) {
            sb.append(returnType.build(context)).append(" ");
        }

        sb.append(methodName);

        sb.append(buildMandatoryList(context, "(", parameters, ")", ", ")).append(" {\n");

        sb.append(buildList(context, "", statements, "", ""));

        sb.append("}\n");

        return sb.toString();
    }
}
