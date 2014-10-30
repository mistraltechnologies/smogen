package com.mistraltech.smogen.codegenerator.javabuilder;

import static com.mistraltech.smogen.codegenerator.javabuilder.TypeBuilder.aType;

public class TypeParameterBuilder extends AbstractBuilder<TypeParameterBuilder> implements TypeParameter {
    private String name;
    private String extendsClass;

    private TypeParameterBuilder() {
    }

    public static TypeParameterBuilder aTypeParameter() {
        return new TypeParameterBuilder();
    }

    public TypeParameterBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public TypeParameterBuilder withExtends(String classFQN) {
        this.extendsClass = classFQN;
        return this;
    }

    public String getName() {
        return name;
    }

    public String build(JavaBuilderContext context) {
        StringBuilder sb = new StringBuilder();

        if (extendsClass != null) {
            sb.append(name)
                    .append(" extends ")
                    .append(context.normaliseClassReference(extendsClass));
        } else {
            sb.append(context.normaliseClassReference(name));
        }

        return sb.toString();
    }

    public TypeBuilder getType() {
        return aType().withName(getName());
    }
}

