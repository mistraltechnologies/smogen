package com.mistraltech.smogen.codegenerator.javabuilder;

public class TypeParameterBuilder extends AbstractBuilder<TypeParameterBuilder> {
    private String name;
    private TypeBuilder type;

    private TypeParameterBuilder() {
    }

    public static TypeParameterBuilder aTypeParameter() {
        return new TypeParameterBuilder();
    }

    public TypeParameterBuilder withName(String name) {
        assert type == null;
        this.name = name;
        return this;
    }

    public TypeParameterBuilder withType(TypeBuilder type) {
        assert this.type == null;
        this.type = type;
        return this;
    }


    @Override
    public String build(JavaBuilderContext context) {
        if (name != null) {
            return name;
        } else {
            return type.build(context);
        }
    }
}
