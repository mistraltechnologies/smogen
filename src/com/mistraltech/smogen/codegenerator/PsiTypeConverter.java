package com.mistraltech.smogen.codegenerator;

import com.intellij.psi.PsiArrayType;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiPrimitiveType;
import com.intellij.psi.PsiType;
import com.intellij.psi.PsiTypeVisitor;
import com.intellij.psi.impl.source.PsiClassReferenceType;
import com.mistraltech.smogen.codegenerator.javabuilder.TypeBuilder;
import org.jetbrains.annotations.Nullable;

public class PsiTypeConverter extends PsiTypeVisitor<Object> {
    private boolean boxed;
    private TypeBuilder typeBuilder = TypeBuilder.aType();

    public PsiTypeConverter() {
        this(true);
    }

    public PsiTypeConverter(boolean boxed) {
        this.boxed = boxed;
    }

    @Nullable
    @Override
    public Object visitPrimitiveType(PsiPrimitiveType primitiveType) {
        typeBuilder.withName(boxed ? primitiveType.getBoxedTypeName() : primitiveType.getCanonicalText());
        return super.visitPrimitiveType(primitiveType);
    }

    @Nullable
    @Override
    public Object visitArrayType(PsiArrayType arrayType) {
        boxed = false; // don't need to (or want to) box array types
        arrayType.getComponentType().accept(this);
        typeBuilder.withArrayDimensions(arrayType.getArrayDimensions());
        return super.visitArrayType(arrayType);
    }

    @Nullable
    @Override
    public Object visitClassType(PsiClassType classType) {
        String name = (classType instanceof PsiClassReferenceType) ?
                ((PsiClassReferenceType) classType).getReference().getQualifiedName() :
                classType.getClassName();
        typeBuilder.withName(name);

        for (PsiType param : classType.getParameters()) {
            PsiTypeParameterConverter converter = new PsiTypeParameterConverter();
            param.accept(converter);
            typeBuilder.withTypeBinding(converter.getTypeParameterBuilder());
        }

        return super.visitClassType(classType);
    }

    public TypeBuilder getTypeBuilder() {
        return typeBuilder;
    }
}
