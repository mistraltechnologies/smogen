package com.mistraltech.smogen.codegenerator.javabuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static com.mistraltech.smogen.codegenerator.javabuilder.TypeParameterBuilder.aTypeParameter;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public class TypeBuilder extends AbstractBuilder<TypeBuilder> {
    public static TypeBuilder VOID = new TypeBuilder().withName("void");

    private final List<TypeParameterBuilder> typeBindings = new ArrayList<>();
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

    public TypeBuilder withTypeBindings(List<TypeParameterBuilder> typeParameters) {
        typeParameters.forEach(this::withTypeBinding);
        return this;
    }

    public TypeBuilder withTypeBinding(TypeParameterBuilder typeParameter) {
        typeBindings.add(typeParameter);
        return this;
    }

    public TypeBuilder withArrayDimensions(int arrayDimensions) {
        this.arrayDimensions = arrayDimensions;
        return this;
    }

    public String getTypeFQN() {
        return typeFQN;
    }

    public List<TypeParameterBuilder> getTypeBindings() {
        return typeBindings;
    }

    public boolean containsWildcard() {
        return typeBindings.stream().anyMatch(TypeParameterBuilder::containsWildcard);
    }

    @Override
    public String build(JavaBuilderContext context) {
        StringBuilder sb = new StringBuilder();

        sb.append(context.normaliseClassReference(typeFQN))
                .append(BuilderUtils.buildList(context, "<", typeBindings, ">", ", "));

        IntStream.range(0, arrayDimensions).forEach(i -> sb.append("[]"));

        return sb.toString();
    }
}
