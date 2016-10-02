package com.mistraltech.smogen.codegenerator.matchergenerator;

import com.intellij.psi.PsiArrayType;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiPrimitiveType;
import com.intellij.psi.PsiType;
import com.intellij.psi.PsiTypeVisitor;
import com.intellij.psi.PsiWildcardType;
import com.intellij.psi.impl.source.PsiClassReferenceType;
import com.mistraltech.smogen.codegenerator.javabuilder.TypeBuilder;
import com.mistraltech.smogen.codegenerator.javabuilder.TypeParameterBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class PsiTypeConverter extends PsiTypeVisitor<TypeBuilder> {
    private final Map<String, String> typeParameterMap;
    private final TypeBuilder typeBuilder = TypeBuilder.aType();
    private boolean boxed;

    public PsiTypeConverter(@NotNull Map<String, String> typeParameterMap) {
        this(true, typeParameterMap);
    }

    public PsiTypeConverter(boolean boxed, @NotNull Map<String, String> typeParameterMap) {
        this.boxed = boxed;
        this.typeParameterMap = typeParameterMap;
    }

    @Nullable
    @Override
    public TypeBuilder visitPrimitiveType(PsiPrimitiveType primitiveType) {
        typeBuilder.withName(boxed ? primitiveType.getBoxedTypeName() : primitiveType.getCanonicalText());
        return super.visitPrimitiveType(primitiveType);
    }

    @Nullable
    @Override
    public TypeBuilder visitArrayType(PsiArrayType arrayType) {
        boxed = false; // don't need to (or want to) box array types
        arrayType.getComponentType().accept(this);
        typeBuilder.withArrayDimensions(arrayType.getArrayDimensions());
        return super.visitArrayType(arrayType);
    }

    @Nullable
    @Override
    public TypeBuilder visitClassType(PsiClassType classType) {
        String name = (classType instanceof PsiClassReferenceType) ?
                ((PsiClassReferenceType) classType).getReference().getQualifiedName() :
                classType.getClassName();

        String mappedName = typeParameterMap.containsKey(name) ? typeParameterMap.get(name) : name;

        typeBuilder.withName(mappedName);

        for (PsiType param : classType.getParameters()) {
            PsiTypeParameterConverter converter = new PsiTypeParameterConverter(typeParameterMap);
            param.accept(converter);
            typeBuilder.withTypeBinding(converter.getTypeParameterBuilder());
        }

        return super.visitClassType(classType);
    }

    public TypeBuilder visitType(PsiType type) {
        return typeBuilder;
    }

    private static class PsiTypeParameterConverter extends PsiTypeVisitor<Object> {
        private final Map<String, String> typeParameterMap;
        private TypeParameterBuilder typeParameterBuilder = TypeParameterBuilder.aTypeParameter();

        PsiTypeParameterConverter(Map<String, String> typeParameterMap) {
            this.typeParameterMap = typeParameterMap;
        }

        @Nullable
        @Override
        public Object visitClassType(PsiClassType classType) {
            PsiTypeConverter typeConverter = new PsiTypeConverter(typeParameterMap);
            TypeBuilder typeBuilder = classType.accept(typeConverter);
            typeParameterBuilder.withType(typeBuilder);
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

        TypeParameterBuilder getTypeParameterBuilder() {
            return typeParameterBuilder;
        }
    }
}
