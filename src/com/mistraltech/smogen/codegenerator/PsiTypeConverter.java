package com.mistraltech.smogen.codegenerator;

import com.intellij.psi.PsiArrayType;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiPrimitiveType;
import com.intellij.psi.PsiType;
import com.intellij.psi.PsiTypeVisitor;
import com.intellij.psi.impl.source.PsiClassReferenceType;
import com.mistraltech.smogen.codegenerator.javabuilder.TypeBuilder;
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
}
