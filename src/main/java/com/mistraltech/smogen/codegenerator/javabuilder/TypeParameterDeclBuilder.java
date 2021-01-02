package com.mistraltech.smogen.codegenerator.javabuilder;

import static com.mistraltech.smogen.codegenerator.javabuilder.TypeBuilder.aType;

public class TypeParameterDeclBuilder extends AbstractBuilder<TypeParameterDeclBuilder> {
    private String name;
    private TypeBuilder extendsType;

    private TypeParameterDeclBuilder() {
    }

    public static TypeParameterDeclBuilder aTypeParameterDecl() {
        return new TypeParameterDeclBuilder();
    }

    public TypeParameterDeclBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public TypeParameterDeclBuilder withExtends(TypeBuilder type) {
        this.extendsType = type;
        return this;
    }

    public String getName() {
        return name;
    }

    public TypeBuilder getType() {
        return aType().withName(getName());
    }

    public String build(JavaBuilderContext context) {
        StringBuilder sb = new StringBuilder();

        if (extendsType != null) {
            sb.append(name)
                    .append(" extends ")
                    .append(extendsType.build(context));
        } else {
            sb.append(context.normaliseClassReference(name));
        }

        return sb.toString();
    }
}

