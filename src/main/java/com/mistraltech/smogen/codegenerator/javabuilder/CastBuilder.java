package com.mistraltech.smogen.codegenerator.javabuilder;

public class CastBuilder extends ExpressionTermBuilder<CastBuilder> {
    private TypeBuilder type;

    private CastBuilder() {
    }

    public static CastBuilder aCast() {
        return new CastBuilder();
    }

    public CastBuilder withType(TypeBuilder type) {
        this.type = type;
        return this;
    }

    @Override
    public String build(JavaBuilderContext context) {
        return "(" + type.build(context) + ")";
    }
}
