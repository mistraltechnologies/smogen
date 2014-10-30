package com.mistraltech.smogen.codegenerator.javabuilder;

import java.util.ArrayList;

import static com.mistraltech.smogen.codegenerator.javabuilder.BuilderUtils.buildList;
import static com.mistraltech.smogen.codegenerator.javabuilder.BuilderUtils.buildMandatoryList;

public class MethodBuilder extends AbstractBuilder<MethodBuilder> {

    private String accessModifier;
    private boolean staticFlag;
    private boolean finalFlag;
    private boolean abstractFlag;
    private String methodName;
    private TypeBuilder returnType;
    private ArrayList<ParameterBuilder> parameters = new ArrayList<ParameterBuilder>();
    private ArrayList<StatementBuilder> statements = new ArrayList<StatementBuilder>();
    private ArrayList<AnnotationBuilder> annotations = new ArrayList<AnnotationBuilder>();

    private MethodBuilder() {
    }

    public static MethodBuilder aMethod() {
        return new MethodBuilder();
    }

    public MethodBuilder withAccessModifier(String modifier) {
        this.accessModifier = modifier;
        return this;
    }

    public MethodBuilder withStaticFlag(boolean staticFlag) {
        assert !finalFlag && !abstractFlag;

        this.staticFlag = staticFlag;
        return this;
    }

    public MethodBuilder withAbstractFlag(boolean abstractFlag) {
        assert !finalFlag && !staticFlag;

        this.abstractFlag = abstractFlag;
        return this;
    }

    public MethodBuilder withFinalFlag(boolean finalFlag) {
        assert !staticFlag && !abstractFlag;

        this.finalFlag = finalFlag;
        return this;
    }

    public MethodBuilder withName(String methodName) {
        this.methodName = methodName;
        return this;
    }

    public MethodBuilder withReturnType(TypeBuilder type) {
        this.returnType = type;
        return this;
    }

    public MethodBuilder withParameter(ParameterBuilder parameter) {
        parameters.add(parameter);
        return this;
    }

    public MethodBuilder withStatement(StatementBuilder statement) {
        statements.add(statement);
        return this;
    }

    public MethodBuilder withAnnotation(AnnotationBuilder annotation) {
        annotations.add(annotation);
        return this;
    }

    @Override
    public String build(JavaBuilderContext context) {
        StringBuilder sb = new StringBuilder();

        sb.append(buildList(context, "", annotations, "\n", "\n"));

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
