package com.mistraltech.smogen.codegenerator.javabuilder;

public class TypeParameterBuilder extends AbstractBuilder<TypeParameterBuilder> implements TypeParameter {
    private String name;
    private String extendsClass;

    private TypeParameterBuilder() {
    }

    public static TypeParameterBuilder aTypeParameter()
    {
        return new TypeParameterBuilder();
    }

    public TypeParameterBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public TypeParameterBuilder withExtends(String classFQN) {
        this.extendsClass = getClassReference(classFQN);
        return this;
    }

    public String getName() {
        return name;
    }

    public String build()
    {
        StringBuilder sb = new StringBuilder();

        sb.append(name);
        if (extendsClass != null)
        {
            sb.append(" extends ").append(extendsClass);
        }

        return sb.toString();
    }
}

