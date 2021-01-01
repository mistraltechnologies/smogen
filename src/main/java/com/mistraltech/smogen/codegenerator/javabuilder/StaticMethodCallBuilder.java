package com.mistraltech.smogen.codegenerator.javabuilder;

import java.util.ArrayList;
import java.util.List;

import static com.mistraltech.smogen.codegenerator.javabuilder.BuilderUtils.buildList;
import static com.mistraltech.smogen.codegenerator.javabuilder.BuilderUtils.buildMandatoryList;
import static com.mistraltech.smogen.codegenerator.javabuilder.ExpressionBuilder.anExpression;

@SuppressWarnings("UnusedReturnValue")
public class StaticMethodCallBuilder extends ExpressionTermBuilder<StaticMethodCallBuilder> {
    private final List<ExpressionTermBuilder<?>> parameters = new ArrayList<>();
    private final List<TypeBuilder> typeBindings = new ArrayList<>();
    private String name;
    private TypeBuilder type;

    private StaticMethodCallBuilder() {
    }

    public static StaticMethodCallBuilder aStaticMethodCall() {
        return new StaticMethodCallBuilder();
    }

    public StaticMethodCallBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public StaticMethodCallBuilder withParameter(String parameter) {
        return withParameter(anExpression().withText(parameter));
    }

    public StaticMethodCallBuilder withParameter(ExpressionTermBuilder<?> expression) {
        parameters.add(expression);
        return this;
    }

    public StaticMethodCallBuilder withType(TypeBuilder type) {
        this.type = type;
        return this;
    }

    public StaticMethodCallBuilder withTypeBinding(TypeBuilder type) {
        typeBindings.add(type);
        return this;
    }

    @Override
    public String build(JavaBuilderContext context) {
        StringBuilder sb = new StringBuilder();

        String typeBindingsStr = buildList(context, "", typeBindings, "", ", ");
        String methodName = context.normaliseClassMemberReference(type.getTypeFQN() + "." + name, typeBindingsStr);

        sb.append(methodName)
                .append(buildMandatoryList(context, "(", parameters, ")", ", "));

        return sb.toString();
    }
}
