package com.mistraltech.smogen.codegenerator.javabuilder;

import java.util.ArrayList;
import java.util.List;

import static com.mistraltech.smogen.codegenerator.javabuilder.TypeBuilder.aType;

@SuppressWarnings("UnusedReturnValue")
public class TypeParameterBuilder extends AbstractBuilder<TypeParameterBuilder> {
    private final List<TypeBuilder> types = new ArrayList<>();
    private boolean subTypes;
    private boolean superTypes;

    private TypeParameterBuilder() {
    }

    public static TypeParameterBuilder aTypeParameter() {
        return new TypeParameterBuilder();
    }

    public TypeParameterBuilder withName(String name) {
        return withType(aType().withName(name));
    }

    public TypeParameterBuilder withType(TypeBuilder type) {
        types.add(type);
        return this;
    }

    public TypeParameterBuilder withSubTypes(boolean subTypes) {
        if (superTypes && subTypes) {
            throw new IllegalArgumentException("Cannot specify subtype and supertype bounds");
        }

        this.subTypes = subTypes;
        return this;
    }

    public TypeParameterBuilder withSuperTypes(boolean superTypes) {
        if (superTypes && subTypes) {
            throw new IllegalArgumentException("Cannot specify subtype and supertype bounds");
        }

        this.superTypes = superTypes;
        return this;
    }

    public boolean containsWildcard() {
        return superTypes || subTypes || types.isEmpty();
    }

    @Override
    public String build(JavaBuilderContext context) {
        StringBuilder sb = new StringBuilder();

        if (subTypes) {
            sb.append("? extends ");
        } else if (superTypes) {
            sb.append("? super ");
        } else if (types.isEmpty()) {
            sb.append("?");
        }

        sb.append(BuilderUtils.buildList(context, "", types, "", " & "));

        return sb.toString();
    }
}
