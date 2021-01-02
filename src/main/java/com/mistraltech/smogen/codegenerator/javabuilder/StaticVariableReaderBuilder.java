package com.mistraltech.smogen.codegenerator.javabuilder;

public class StaticVariableReaderBuilder extends ExpressionTermBuilder<StaticVariableReaderBuilder> {
    private String name;
    private TypeBuilder type;

    private StaticVariableReaderBuilder() {
    }

    public static StaticVariableReaderBuilder aStaticVariable() {
        return new StaticVariableReaderBuilder();
    }

    public StaticVariableReaderBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public StaticVariableReaderBuilder withType(TypeBuilder type) {
        this.type = type;
        return this;
    }

    @Override
    public String build(JavaBuilderContext context) {
        return context.normaliseClassMemberReference(type.getTypeFQN() + "." + name, null);
    }
}
