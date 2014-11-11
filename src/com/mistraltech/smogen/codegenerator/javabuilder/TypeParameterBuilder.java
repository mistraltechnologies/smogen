package com.mistraltech.smogen.codegenerator.javabuilder;

import java.util.ArrayList;
import java.util.List;

import static com.mistraltech.smogen.codegenerator.javabuilder.TypeBuilder.aType;

public class TypeParameterBuilder extends AbstractBuilder<TypeParameterBuilder> {
    private List<TypeBuilder> types = new ArrayList<TypeBuilder>();
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
        assert !(superTypes && subTypes);
        this.subTypes = subTypes;
        return this;
    }

    public TypeParameterBuilder withSuperTypes(boolean superTypes) {
        assert !(superTypes && subTypes);
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        if (subTypes) {
            sb.append("? extends ");
        } else if (superTypes) {
            sb.append("? super ");
        } else if (types.isEmpty()) {
            sb.append("?");
        }

        sb.append(BuilderUtils.buildList("", types, "", " & "));

        return sb.toString();
    }
}
