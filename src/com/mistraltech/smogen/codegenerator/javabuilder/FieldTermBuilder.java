package com.mistraltech.smogen.codegenerator.javabuilder;

public class FieldTermBuilder extends ExpressionTermBuilder<FieldTermBuilder> {
    private TypeBuilder type;
    private String fieldName;

    private FieldTermBuilder() {
    }

    public static FieldTermBuilder aField() {
        return new FieldTermBuilder();
    }

    public FieldTermBuilder withType(String typeFQN) {
        return withType(TypeBuilder.aType().withName(typeFQN));
    }

    public FieldTermBuilder withType(TypeBuilder type) {
        this.type = type;
        return this;
    }

    public FieldTermBuilder withField(String fieldName) {
        this.fieldName = fieldName;
        return this;
    }

    @Override
    public String build(JavaBuilderContext context) {
        return type.build(context) + "." + fieldName;
    }
}
