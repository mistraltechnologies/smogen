package com.mistraltech.smogen.codegenerator.javabuilder;

public class TypeParameterBuilder extends AbstractBuilder<TypeParameterBuilder> {
    private String name;
    private TypeBuilder type;
    private boolean subTypes;
    private boolean superTypes;

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

    public TypeParameterBuilder withSubTypes(boolean subTypes) {
        assert !(superTypes && subTypes);
        this.subTypes = subTypes;
        return this;
    }

    public TypeParameterBuilder withSuperTypes(boolean superTypes) {
        assert !(superTypes && subTypes);
        this.superTypes = superTypes;
        return this;
    }

    @Override
    public String build(JavaBuilderContext context) {
        StringBuilder sb = new StringBuilder();

        if (subTypes) {
            sb.append("? extends ");
        } else if (superTypes) {
            sb.append("? super ");
        }

        if (name != null) {
            sb.append(name);
        } else {
            sb.append(type.build(context));
        }

        return sb.toString();
    }
}
