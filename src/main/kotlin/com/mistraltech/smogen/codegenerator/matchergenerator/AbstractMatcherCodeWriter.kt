package com.mistraltech.smogen.codegenerator.matchergenerator

import com.intellij.psi.JavaDirectoryService
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiPackage
import com.mistraltech.smogen.codegenerator.CodeWriter
import com.mistraltech.smogen.codegenerator.javabuilder.JavaDocumentBuilder
import com.mistraltech.smogen.codegenerator.javabuilder.TypeBuilder
import com.mistraltech.smogen.codegenerator.javabuilder.TypeParameterBuilder
import com.mistraltech.smogen.codegenerator.javabuilder.TypeParameterDeclBuilder
import com.mistraltech.smogen.codegenerator.utils.PsiTypeConverter
import com.mistraltech.smogen.property.Property
import java.util.stream.Collectors
import java.util.stream.IntStream

@Suppress("MemberVisibilityCanBePrivate")
abstract class AbstractMatcherCodeWriter(protected var generatorProperties: MatcherGeneratorProperties) : CodeWriter {

    override fun writeCode(): String {
        val document = JavaDocumentBuilder.aJavaDocument()
        document.setPackageName(targetPackage.qualifiedName)

        generateDocumentContent(document)

        return document.build()
    }

    protected abstract fun generateDocumentContent(document: JavaDocumentBuilder)

    protected fun getTypeParameter(n: Int): String {
        return "P" + (n + 1)
    }

    protected fun typeParameters(): List<TypeParameterBuilder> {
        val noOfTypeParameters = sourceClass!!.typeParameters.size

        return IntStream.range(0, noOfTypeParameters)
            .mapToObj { i: Int -> TypeParameterBuilder.aTypeParameter().withName(getTypeParameter(i)) }
            .collect(Collectors.toList())
    }

    protected fun typeParameterDecls(): List<TypeParameterDeclBuilder> {
        val noOfTypeParameters = sourceClass!!.typeParameters.size

        return IntStream.range(0, noOfTypeParameters)
            .mapToObj { i: Int -> TypeParameterDeclBuilder.aTypeParameterDecl().withName(getTypeParameter(i)) }
            .collect(Collectors.toList())
    }

    protected val targetPackage: PsiPackage
        get() {
            val parentDirectory: PsiDirectory = generatorProperties.parentDirectory!!
            return JavaDirectoryService.getInstance().getPackage(parentDirectory)
                ?: throw IllegalStateException("Can't get package for directory " + parentDirectory.name)
        }

    protected val sourceClass: PsiClass?
        get() = generatorProperties.sourceClass

    protected val sourceClassFQName: String?
        get() = sourceClass!!.qualifiedName

    protected val sourceClassName: String
        get() = sourceClass!!.name!!

    protected val sourceSuperClassParameterBuilders: List<TypeParameterBuilder>
        get() = sourceSuperClassTypeBuilder?.typeBindings ?: emptyList()

    private val sourceSuperClassTypeBuilder: TypeBuilder?
        get() = generatorProperties.sourceSuperClassType?.accept(PsiTypeConverter(true, typeParameterMap()))

    protected fun getPropertyTypeBuilder(property: Property, boxed: Boolean): TypeBuilder {
        return property.accept(PsiTypeConverter(boxed, typeParameterMap()))
    }

    private fun typeParameterMap(): Map<String, String> {
        return sourceClass!!.typeParameters.associateBy({ it.name ?: "" }, { getTypeParameter(it.index) })
    }

    protected fun matcherAttributeName(property: Property): String {
        return property.fieldName + "Matcher"
    }

    protected fun setterMethodName(property: Property): String {
        val propertyName = if (generatorProperties.setterPrefix.isNullOrBlank()) {
            property.name
        } else {
            property.capitalisedName
        }
        return (generatorProperties.setterPrefix ?: "") + propertyName + (generatorProperties.setterSuffix ?: "")
    }

    protected val matchedObjectDescription: String
        get() = if (generatorProperties.factoryMethodPrefix.isNullOrBlank()) {
            sourceClassName
        } else {
            generatorProperties.factoryMethodPrefix + " " + sourceClassName
        }

    companion object {
        const val MATCHES_PROPERTY_ANNOTATION_CLASS_NAME = "com.mistraltech.smog.core.annotation.MatchesProperty"
        const val DEFAULT_SETTER_METHOD_PREFIX = "has"
        const val DEFAULT_SETTER_METHOD_SUFFIX = ""
    }
}
