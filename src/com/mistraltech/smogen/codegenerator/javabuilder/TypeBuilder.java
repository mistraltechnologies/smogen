package com.mistraltech.smogen.codegenerator.javabuilder;

import java.util.ArrayList;
import java.util.List;

import static com.mistraltech.smogen.codegenerator.javabuilder.TypeParameterBuilder.aTypeParameter;

public class TypeBuilder extends AbstractBuilder<TypeBuilder> {
    private List<TypeParameterBuilder> typeBindings = new ArrayList<TypeParameterBuilder>();
    private String typeFQN;
    private int arrayDimensions;

    private TypeBuilder() {
    }

    public static TypeBuilder aType() {
        return new TypeBuilder();
    }

    public TypeBuilder withName(String typeFQN) {
        this.typeFQN = typeFQN;
        return this;
    }

    public TypeBuilder withTypeBinding(String classFQN) {
        return withTypeBinding(aType().withName(classFQN));
    }

    public TypeBuilder withTypeBinding(TypeBuilder type) {
        return withTypeBinding(aTypeParameter().withType(type));
    }

    public TypeBuilder withTypeBinding(TypeParameterBuilder typeParameter) {
        this.typeBindings.add(typeParameter);
        return this;
    }

    public TypeBuilder withArrayDimensions(int arrayDimensions) {
        this.arrayDimensions = arrayDimensions;
        return this;
    }

    public String getTypeFQN() {
        return typeFQN;
    }

    public boolean containsWildcard() {
        for (TypeParameterBuilder typeBinding : typeBindings) {
            if (typeBinding.containsWildcard()) {
                return true;
            }
        }

        return false;
    }

    @Override
    public String build(JavaBuilderContext context) {
        StringBuilder sb = new StringBuilder();

        sb.append(context.normaliseClassReference(typeFQN))
                .append(BuilderUtils.buildList(context, "<", typeBindings, ">", ", "));

        for (int i = 0; i < arrayDimensions; i++) {
            sb.append("[]");
        }

        return sb.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(typeFQN)
                .append(BuilderUtils.buildList("<", typeBindings, ">", ", "));

        for (int i = 0; i < arrayDimensions; i++) {
            sb.append("[]");
        }

        return sb.toString();
    }
}
