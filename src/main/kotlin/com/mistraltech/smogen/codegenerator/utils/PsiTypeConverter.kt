package com.mistraltech.smogen.codegenerator.utils

import com.intellij.psi.PsiArrayType
import com.intellij.psi.PsiClassType
import com.intellij.psi.PsiPrimitiveType
import com.intellij.psi.PsiType
import com.intellij.psi.PsiTypeVisitor
import com.intellij.psi.PsiWildcardType
import com.intellij.psi.impl.source.PsiClassReferenceType
import com.mistraltech.smogen.codegenerator.javabuilder.TypeBuilder
import com.mistraltech.smogen.codegenerator.javabuilder.TypeParameterBuilder

class PsiTypeConverter(private var boxed: Boolean, private val typeParameterMap: Map<String, String>) :
    PsiTypeVisitor<TypeBuilder>() {

    private val typeBuilder = TypeBuilder.aType()

    constructor(typeParameterMap: Map<String, String>) : this(true, typeParameterMap)

    override fun visitPrimitiveType(primitiveType: PsiPrimitiveType): TypeBuilder {
        val typeName = if (boxed) primitiveType.boxedTypeName else primitiveType.canonicalText
        typeBuilder.withName(typeName)

        return super.visitPrimitiveType(primitiveType)
    }

    override fun visitArrayType(arrayType: PsiArrayType): TypeBuilder {
        boxed = false // don't need to (or want to) box array types

        arrayType.componentType.accept(this)
        typeBuilder.withArrayDimensions(arrayType.arrayDimensions)

        return super.visitArrayType(arrayType)
    }

    override fun visitClassType(classType: PsiClassType): TypeBuilder {
        val name = if (classType is PsiClassReferenceType) classType.reference.qualifiedName else classType.className
        val mappedName = typeParameterMap.getOrDefault(name, name)
        typeBuilder.withName(mappedName)

        classType.parameters.forEach {
            val converter = PsiTypeParameterConverter(typeParameterMap)
            it.accept(converter)
            typeBuilder.withTypeBinding(converter.typeParameterBuilder)
        }

        return super.visitClassType(classType)
    }

    override fun visitType(type: PsiType): TypeBuilder {
        return typeBuilder
    }

    private class PsiTypeParameterConverter(private val typeParameterMap: Map<String, String>) :
        PsiTypeVisitor<Any?>() {

        val typeParameterBuilder: TypeParameterBuilder = TypeParameterBuilder.aTypeParameter()

        override fun visitClassType(classType: PsiClassType): Any? {
            val typeConverter = PsiTypeConverter(typeParameterMap)
            val typeBuilder = classType.accept(typeConverter)
            typeParameterBuilder.withType(typeBuilder)

            return super.visitClassType(classType)
        }

        override fun visitWildcardType(wildcardType: PsiWildcardType): Any? {
            wildcardType.bound?.accept(this)

            typeParameterBuilder.withSubTypes(wildcardType.isExtends)
            typeParameterBuilder.withSuperTypes(wildcardType.isSuper)

            return super.visitWildcardType(wildcardType)
        }
    }
}
