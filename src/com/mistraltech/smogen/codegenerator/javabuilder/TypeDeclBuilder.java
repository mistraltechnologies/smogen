package com.mistraltech.smogen.codegenerator.javabuilder;

import java.util.ArrayList;
import java.util.List;

import static com.mistraltech.smogen.codegenerator.javabuilder.TypeParameterDeclBuilder.aTypeParameterDecl;

public class TypeDeclBuilder extends AbstractBuilder<TypeDeclBuilder> {
    private List<TypeParameterDeclBuilder> typeParams = new ArrayList<>();
    private String typeFQN;

    private TypeDeclBuilder() {
    }

    public static TypeDeclBuilder aType() {
        return new TypeDeclBuilder();
    }

    public TypeDeclBuilder withName(String typeFQN) {
        this.typeFQN = typeFQN;
        return this;
    }

    public TypeDeclBuilder withTypeParam(String classFQN) {
        return withTypeParam(aTypeParameterDecl().withName(classFQN));
    }

    public TypeDeclBuilder withTypeParam(TypeParameterDeclBuilder typeParameter) {
        this.typeParams.add(typeParameter);
        return this;
    }

    @Override
    public String build(JavaBuilderContext context) {
        String sb = context.normaliseClassReference(typeFQN) +
                BuilderUtils.buildList(context, "<", typeParams, ">", ", ");

        return sb;
    }
}
