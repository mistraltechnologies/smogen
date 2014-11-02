package com.mistraltech.smogen.codegenerator.javabuilder;

import java.util.ArrayList;
import java.util.List;

import static com.mistraltech.smogen.codegenerator.javabuilder.TypeParameterBuilder.aTypeParameter;

public class TypeBuilder extends AbstractBuilder<TypeBuilder> {
    private List<TypeParameterBuilder> typeBindings = new ArrayList<TypeParameterBuilder>();
    private String typeFQN;

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
        return withTypeBinding(aTypeParameter().withName(classFQN));
    }

    public TypeBuilder withTypeBinding(TypeBuilder type) {
        return withTypeBinding(aTypeParameter().withType(type));
    }

    public TypeBuilder withTypeBinding(TypeParameterBuilder typeParameter) {
        this.typeBindings.add(typeParameter);
        return this;
    }

    public String getTypeFQN() {
        return typeFQN;
    }

    @Override
    public String build(JavaBuilderContext context) {
        StringBuilder sb = new StringBuilder();

        sb.append(context.normaliseClassReference(typeFQN))
                .append(BuilderUtils.buildList(context, "<", typeBindings, ">", ", "));

        return sb.toString();
    }
}
