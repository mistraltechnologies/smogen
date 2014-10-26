package com.mistraltech.smogen.codegenerator.javabuilder;

import java.util.ArrayList;
import java.util.List;

public class TypeBuilder extends AbstractBuilder<TypeBuilder> {

    private String name;
    private List<String> typeBindings = new ArrayList<String>();

    private TypeBuilder()
    {
    }

    public static TypeBuilder aType()
    {
        return new TypeBuilder();
    }

    public TypeBuilder withName(String typeFQN)
    {
        this.name = getClassReference(typeFQN);
        return this;
    }

    public TypeBuilder withTypeBinding(String classFQN) {
        this.typeBindings.add(getClassReference(classFQN));
        return this;
    }

    public TypeBuilder withTypeBinding(TypeParameter typeParameter) {
        this.typeBindings.add(typeParameter.getName());
        return this;
    }

    @Override
    public String build() {
        StringBuilder sb = new StringBuilder();

        sb.append(name)
                .append(BuilderUtils.buildList("<", typeBindings, ">", ", "));

        return sb.toString();
    }
}
