package com.mistraltech.smogen.codegenerator;

import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiTypeVisitor;
import com.intellij.psi.PsiWildcardType;
import com.mistraltech.smogen.codegenerator.javabuilder.TypeParameterBuilder;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class PsiTypeParameterConverter extends PsiTypeVisitor<Object> {
    private final Map<String, String> typeParameterMap;
    private TypeParameterBuilder typeParameterBuilder = TypeParameterBuilder.aTypeParameter();

    public PsiTypeParameterConverter(Map<String, String> typeParameterMap) {
        this.typeParameterMap = typeParameterMap;
    }

    @Nullable
    @Override
    public Object visitClassType(PsiClassType classType) {
        PsiTypeConverter typeConverter = new PsiTypeConverter(typeParameterMap);
        classType.accept(typeConverter);
        typeParameterBuilder.withType(typeConverter.getTypeBuilder());
        return super.visitClassType(classType);
    }

    @Nullable
    @Override
    public Object visitWildcardType(PsiWildcardType wildcardType) {
        if (wildcardType.getBound() != null) {
            wildcardType.getBound().accept(this);
        }

        typeParameterBuilder.withSubTypes(wildcardType.isExtends());
        typeParameterBuilder.withSuperTypes(wildcardType.isSuper());

        return super.visitWildcardType(wildcardType);
    }

    public TypeParameterBuilder getTypeParameterBuilder() {
        return typeParameterBuilder;
    }
}