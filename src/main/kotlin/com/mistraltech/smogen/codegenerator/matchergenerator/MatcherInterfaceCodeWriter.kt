package com.mistraltech.smogen.codegenerator.matchergenerator

import com.mistraltech.smogen.codegenerator.javabuilder.AnnotationBuilder
import com.mistraltech.smogen.codegenerator.javabuilder.ExpressionBuilder
import com.mistraltech.smogen.codegenerator.javabuilder.ExpressionTextBuilder
import com.mistraltech.smogen.codegenerator.javabuilder.InterfaceBuilder
import com.mistraltech.smogen.codegenerator.javabuilder.InterfaceMethodBuilder
import com.mistraltech.smogen.codegenerator.javabuilder.JavaDocumentBuilder
import com.mistraltech.smogen.codegenerator.javabuilder.MethodBuilder
import com.mistraltech.smogen.codegenerator.javabuilder.MethodCallBuilder
import com.mistraltech.smogen.codegenerator.javabuilder.MethodSignatureBuilder
import com.mistraltech.smogen.codegenerator.javabuilder.NewInstanceBuilder
import com.mistraltech.smogen.codegenerator.javabuilder.ParameterBuilder
import com.mistraltech.smogen.codegenerator.javabuilder.ReturnStatementBuilder
import com.mistraltech.smogen.codegenerator.javabuilder.StaticMethodCallBuilder
import com.mistraltech.smogen.codegenerator.javabuilder.StaticVariableReaderBuilder
import com.mistraltech.smogen.codegenerator.javabuilder.TypeBuilder
import com.mistraltech.smogen.codegenerator.javabuilder.TypeParameterBuilder
import com.mistraltech.smogen.codegenerator.javabuilder.TypeParameterDeclBuilder
import com.mistraltech.smogen.property.Property
import com.mistraltech.smogen.property.PropertyLocator.locatePropertiesFromGetters
import com.mistraltech.smogen.utils.NameUtils.createFQN

@Suppress("DuplicatedCode")
class MatcherInterfaceCodeWriter(matcherGeneratorProperties: MatcherGeneratorProperties) :
    AbstractMatcherCodeWriter(matcherGeneratorProperties) {

    override fun generateDocumentContent(document: JavaDocumentBuilder) {
        document.addInterface(generateMatcherInterface())
    }

    private fun generateMatcherInterface(): InterfaceBuilder {
        val generatedClassFQN = createFQN(targetPackage.qualifiedName, generatorProperties.className!!)

        val matcherType: TypeBuilder

        val clazz = InterfaceBuilder.aJavaInterface()
            .withAccessModifier("public")
            .withName(generatorProperties.className)
            .withTypeParameters(typeParameterDecls())
            .withAnnotation(
                AnnotationBuilder.anAnnotation()
                    .withType(
                        TypeBuilder.aType()
                            .withName("com.mistraltech.smog.core.annotation.Matches")
                    )
                    .withParameter(ExpressionBuilder.anExpression().withText("value=$sourceClassFQName.class"))
                    .withParameter(
                        ExpressionBuilder.anExpression().withText("description=\"$matchedObjectDescription\"")
                    )
            )

        val matchedType = TypeBuilder.aType()
            .withName(sourceClassFQName)
            .withTypeBindings(typeParameters())

        val returnType: TypeBuilder
        val matchedTypeParam: TypeBuilder

        if (generatorProperties.isExtensible) {
            val returnTypeDecl = TypeParameterDeclBuilder.aTypeParameterDecl()
                .withName("R")
                .withExtends(
                    TypeBuilder.aType()
                        .withName(generatedClassFQN)
                        .withTypeBindings(typeParameters())
                        .withTypeBinding(
                            TypeParameterBuilder.aTypeParameter()
                                .withName("R")
                        )
                        .withTypeBinding(
                            TypeParameterBuilder.aTypeParameter()
                                .withName("T")
                        )
                )

            returnType = returnTypeDecl.type

            val matchedTypeDecl = TypeParameterDeclBuilder.aTypeParameterDecl().withName("T")
                .withExtends(matchedType)

            matchedTypeParam = matchedTypeDecl.type

            matcherType = TypeBuilder.aType().withName(generatorProperties.className)
                .withTypeBinding(TypeParameterBuilder.aTypeParameter())
                .withTypeBinding(matchedType)

            clazz.withTypeParameter(returnTypeDecl)
                .withTypeParameter(matchedTypeDecl)
        } else {
            returnType = TypeBuilder.aType()
                .withName(generatedClassFQN)
                .withTypeBindings(typeParameters())
            matchedTypeParam = matchedType
            matcherType = returnType
        }

        applySuperInterface(clazz, returnType, matchedTypeParam)

        applyInterfaceBody(clazz, returnType, matchedType, matcherType)

        return clazz
    }

    private fun applySuperInterface(clazz: InterfaceBuilder, returnType: TypeBuilder, matchedTypeParam: TypeBuilder) {
        val superType: TypeBuilder = if (generatorProperties.matcherSuperClassName != null) {
            TypeBuilder.aType()
                .withName(generatorProperties.matcherSuperClassName)
                .withTypeBindings(sourceSuperClassParameterBuilders)
                .withTypeBinding(returnType)
                .withTypeBinding(matchedTypeParam)
        } else {
            TypeBuilder.aType()
                .withName("org.hamcrest.Matcher")
                .withTypeBinding(matchedTypeParam)
        }

        clazz.withImplementedInterface(superType)
    }

    private fun applyInterfaceBody(
        clazz: InterfaceBuilder,
        returnType: TypeBuilder,
        matchedType: TypeBuilder,
        matcherType: TypeBuilder
    ) {
        val includeSuperClassProperties = generatorProperties.matcherSuperClassName == null

        val sourceClassProperties = locatePropertiesFromGetters(sourceClass!!, includeSuperClassProperties)

        clazz.withMethod(generateStaticFactoryMethod(matcherType))

        if (generatorProperties.isGenerateTemplateFactoryMethod) {
            clazz.withMethod(generateLikeStaticFactoryMethod(matcherType, matchedType))
            clazz.withMethod(generateLikeMethod(returnType, matchedType))
        }

        sourceClassProperties.forEach { property ->
            clazz.withMethods(generateMatcherSetters(property, returnType))
        }
    }

    private fun generateStaticFactoryMethod(matcherType: TypeBuilder): MethodBuilder {
        val newInstance = NewInstanceBuilder.aNewInstance()
            .withType(matcherType)
            .withParameter("MATCHED_OBJECT_DESCRIPTION")

        if (generatorProperties.isGenerateTemplateFactoryMethod) {
            newInstance.withParameter("null")
        }

        val method = MethodBuilder.aMethod()
            .withStaticFlag(true)
            .withReturnType(matcherType)
            .withTypeParameters(typeParameterDecls())
            .withName(
                generatorProperties.factoryMethodPrefix + sourceClassName +
                    generatorProperties.factoryMethodSuffix
            )
            .withStatement(
                ReturnStatementBuilder.aReturnStatement()
                    .withExpression(
                        StaticMethodCallBuilder.aStaticMethodCall()
                            .withType(
                                TypeBuilder.aType()
                                    .withName("com.mistraltech.smog.proxy.javassist.JavassistMatcherGenerator")
                            )
                            .withName("matcherOf")
                            .withParameter(
                                StaticVariableReaderBuilder.aStaticVariable()
                                    .withType(matcherType)
                                    .withName("class")
                            )
                    )
            )

        if (generatorProperties.isExtensible) {
            method.withAnnotation(
                AnnotationBuilder.anAnnotation()
                    .withType(
                        TypeBuilder.aType()
                            .withName("java.lang.SuppressWarnings")
                    )
                    .withParameter(ExpressionTextBuilder.expressionText("\"unchecked\""))
            )
        }

        return method
    }

    private fun generateLikeStaticFactoryMethod(
        matcherType: TypeBuilder,
        matchedType: TypeBuilder
    ): MethodSignatureBuilder<*> {
        val method = MethodBuilder.aMethod()
            .withStaticFlag(true)
            .withReturnType(matcherType)
            .withTypeParameters(typeParameterDecls())
            .withName(
                generatorProperties.factoryMethodPrefix +
                    sourceClassName + generatorProperties.templateFactoryMethodSuffix
            )
            .withParameter(
                ParameterBuilder.aParameter()
                    .withFinalFlag(generatorProperties.isMakeMethodParametersFinal)
                    .withType(matchedType)
                    .withName("template")
            )
            .withStatement(
                ReturnStatementBuilder.aReturnStatement()
                    .withExpression(
                        MethodCallBuilder.aMethodCall()
                            .withObject(
                                StaticMethodCallBuilder.aStaticMethodCall()
                                    .withType(
                                        TypeBuilder.aType()
                                            .withName("com.mistraltech.smog.proxy.javassist.JavassistMatcherGenerator")
                                    )
                                    .withName("matcherOf")
                                    .withParameter(
                                        StaticVariableReaderBuilder.aStaticVariable()
                                            .withType(matcherType)
                                            .withName("class")
                                    )
                            )
                            .withName("like")
                            .withParameter("template")
                    )
            )

        if (generatorProperties.isExtensible) {
            method.withAnnotation(
                AnnotationBuilder.anAnnotation()
                    .withType(
                        TypeBuilder.aType()
                            .withName("java.lang.SuppressWarnings")
                    )
                    .withParameter(ExpressionTextBuilder.expressionText("\"unchecked\""))
            )
        }

        return method
    }

    private fun generateLikeMethod(returnType: TypeBuilder, matchedType: TypeBuilder): InterfaceMethodBuilder {
        return InterfaceMethodBuilder.anInterfaceMethod()
            .withReturnType(returnType)
            .withName("like")
            .withParameter(
                ParameterBuilder.aParameter()
                    .withType(matchedType)
                    .withName("template")
            )
    }

    private fun generateMatcherSetters(property: Property, returnType: TypeBuilder): List<InterfaceMethodBuilder> {
        val boxedPropertyType = getPropertyTypeBuilder(property, true)

        val valueSetter = InterfaceMethodBuilder.anInterfaceMethod()
            .withReturnType(returnType)
            .withName(setterMethodName(property))
            .withParameter(
                ParameterBuilder.aParameter()
                    .withType(getPropertyTypeBuilder(property, false))
                    .withName(property.fieldName)
            )

        val matcherSetter = InterfaceMethodBuilder.anInterfaceMethod()
            .withReturnType(returnType)
            .withName(setterMethodName(property))
            .withParameter(
                ParameterBuilder.aParameter()
                    .withType(
                        TypeBuilder.aType()
                            .withName("org.hamcrest.Matcher")
                            .withTypeBinding(
                                TypeParameterBuilder.aTypeParameter()
                                    .withType(boxedPropertyType)
                                    .withSuperTypes(true)
                            )
                    )
                    .withName(matcherAttributeName(property))
            )

        if (isCustomSetterName) {
            val matchesPropertyAnnotationType = TypeBuilder.aType()
                .withName(MATCHES_PROPERTY_ANNOTATION_CLASS_NAME)

            val matchesPropertyAnnotation = AnnotationBuilder.anAnnotation()
                .withType(matchesPropertyAnnotationType)
                .withParameter(
                    ExpressionBuilder.anExpression()
                        .withText("\"" + property.fieldName + "\"")
                )

            valueSetter.withAnnotation(matchesPropertyAnnotation)

            matcherSetter.withAnnotation(matchesPropertyAnnotation)
        }

        return listOf(valueSetter, matcherSetter)
    }

    private val isCustomSetterName: Boolean
        get() = generatorProperties.setterPrefix != DEFAULT_SETTER_METHOD_PREFIX ||
            generatorProperties.setterSuffix != DEFAULT_SETTER_METHOD_SUFFIX
}
