package com.mistraltech.smogen.codegenerator.matchergenerator

import com.mistraltech.smogen.codegenerator.javabuilder.AnnotationBuilder
import com.mistraltech.smogen.codegenerator.javabuilder.BlockStatementBuilder
import com.mistraltech.smogen.codegenerator.javabuilder.CastBuilder
import com.mistraltech.smogen.codegenerator.javabuilder.ClassBuilder
import com.mistraltech.smogen.codegenerator.javabuilder.ExpressionBuilder
import com.mistraltech.smogen.codegenerator.javabuilder.ExpressionStatementBuilder
import com.mistraltech.smogen.codegenerator.javabuilder.ExpressionTextBuilder
import com.mistraltech.smogen.codegenerator.javabuilder.FieldTermBuilder
import com.mistraltech.smogen.codegenerator.javabuilder.JavaDocumentBuilder
import com.mistraltech.smogen.codegenerator.javabuilder.MethodBuilder
import com.mistraltech.smogen.codegenerator.javabuilder.MethodCallBuilder
import com.mistraltech.smogen.codegenerator.javabuilder.NestedClassBuilder
import com.mistraltech.smogen.codegenerator.javabuilder.NewInstanceBuilder
import com.mistraltech.smogen.codegenerator.javabuilder.ParameterBuilder
import com.mistraltech.smogen.codegenerator.javabuilder.ReturnStatementBuilder
import com.mistraltech.smogen.codegenerator.javabuilder.StaticMethodCallBuilder
import com.mistraltech.smogen.codegenerator.javabuilder.TypeBuilder
import com.mistraltech.smogen.codegenerator.javabuilder.TypeParameterBuilder
import com.mistraltech.smogen.codegenerator.javabuilder.TypeParameterDeclBuilder
import com.mistraltech.smogen.codegenerator.javabuilder.VariableBuilder
import com.mistraltech.smogen.property.Property
import com.mistraltech.smogen.property.PropertyLocator.locatePropertiesFromGetters
import com.mistraltech.smogen.utils.NameUtils.createFQN

@Suppress("DuplicatedCode")
internal class MatcherClassCodeWriter(matcherGeneratorProperties: MatcherGeneratorProperties) :
    AbstractMatcherCodeWriter(matcherGeneratorProperties) {

    override fun generateDocumentContent(document: JavaDocumentBuilder) {
        document.addClass(generateMatcherClass())
    }

    private fun generateMatcherClass(): ClassBuilder {
        val generatedClassFQN = createFQN(targetPackage.qualifiedName, generatorProperties.className!!)

        val clazz = generateClassOutline()

        val matchedType = TypeBuilder.aType()
            .withName(sourceClassFQName)
            .withTypeBindings(typeParameters())

        val returnType: TypeBuilder
        val matchedTypeParam: TypeBuilder
        val matcherType: TypeBuilder

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

            matcherType = TypeBuilder.aType()
                .withName(nestedClassName())
                .withTypeBindings(typeParameters())

            clazz.withTypeParameter(returnTypeDecl)
                .withTypeParameter(matchedTypeDecl)
        } else {
            clazz.withFinalFlag(true)

            returnType = TypeBuilder.aType()
                .withName(generatedClassFQN)
                .withTypeBindings(typeParameters())

            matchedTypeParam = matchedType
            matcherType = returnType
        }

        applySuperClass(clazz, returnType, matchedTypeParam)

        applyClassBody(clazz, returnType, matchedType, matchedTypeParam, matcherType)

        return clazz
    }

    private fun generateClassOutline() = ClassBuilder.aJavaClass()
        .withAccessModifier("public")
        .withName(generatorProperties.className)
        .withTypeParameters(typeParameterDecls())
        .withAnnotation(
            AnnotationBuilder.anAnnotation()
                .withType(
                    TypeBuilder.aType()
                        .withName("com.mistraltech.smog.core.annotation.Matches")
                )
                .withParameter(
                    FieldTermBuilder.aField()
                        .withType(sourceClassFQName)
                        .withField("class")
                )
        )

    private fun applySuperClass(clazz: ClassBuilder, returnType: TypeBuilder, matchedTypeParam: TypeBuilder) {
        val superType: TypeBuilder = if (generatorProperties.matcherSuperClassName != null) {
            TypeBuilder.aType()
                .withName(generatorProperties.matcherSuperClassName)
                .withTypeBindings(sourceSuperClassParameterBuilders)
                .withTypeBinding(returnType)
                .withTypeBinding(matchedTypeParam)
        } else {
            TypeBuilder.aType()
                .withName(generatorProperties.baseClassName)
                .withTypeBinding(matchedTypeParam)
        }

        clazz.withSuperclass(superType)
    }

    private fun applyClassBody(
        clazz: ClassBuilder,
        returnType: TypeBuilder,
        matchedType: TypeBuilder,
        matchedTypeParam: TypeBuilder,
        matcherType: TypeBuilder
    ) {
        val includeSuperClassProperties = generatorProperties.matcherSuperClassName == null

        val sourceClassProperties = locatePropertiesFromGetters(sourceClass!!, includeSuperClassProperties)

        val objectDescription = if (generatorProperties.factoryMethodPrefix.isNullOrBlank()) {
            sourceClassName
        } else {
            generatorProperties.factoryMethodPrefix + " " + sourceClassName
        }

        clazz.withVariable(
            VariableBuilder.aVariable()
                .withAccessModifier("private")
                .withFinalFlag(true)
                .withStaticFlag(true)
                .withType(TypeBuilder.aType().withName("java.lang.String"))
                .withName("MATCHED_OBJECT_DESCRIPTION")
                .withInitialiser(
                    ExpressionBuilder.anExpression()
                        .withText("\"" + objectDescription + "\"")
                )
        )

        clazz.withVariables(generateMatcherVariables(sourceClassProperties))
            .withMethod(generateConstructor(sourceClassProperties, matchedTypeParam))

        clazz.withMethod(generateStaticFactoryMethod(matcherType))

        if (generatorProperties.isGenerateTemplateFactoryMethod) {
            clazz.withMethod(generateLikeStaticFactoryMethod(matcherType, matchedType))
        }

        if (generatorProperties.isExtensible) {
            clazz.withMethod(generateSelfMethod(returnType))
        }

        sourceClassProperties.forEach { p: Property -> clazz.withMethods(generateMatcherSetters(p, returnType)) }

        if (generatorProperties.isExtensible) {
            clazz.withNestedClass(generateNestedClass(matcherType, matchedType, sourceClassProperties))
        } else {
            clazz.withMethod(generateMatchesSafely(matchedType, sourceClassProperties))
        }
    }

    private fun generateMatchesSafely(matchedType: TypeBuilder, properties: List<Property>): MethodBuilder {
        val matchesSafelyMethod = MethodBuilder.aMethod()
            .withAnnotation(
                AnnotationBuilder.anAnnotation()
                    .withType(
                        TypeBuilder.aType()
                            .withName("java.lang.Override")
                    )
            )
            .withAccessModifier("protected")
            .withReturnType(TypeBuilder.VOID)
            .withName("matchesSafely")
            .withParameter(
                ParameterBuilder.aParameter()
                    .withFinalFlag(generatorProperties.isMakeMethodParametersFinal)
                    .withType(matchedType)
                    .withName("item")
            )
            .withParameter(
                ParameterBuilder.aParameter()
                    .withFinalFlag(generatorProperties.isMakeMethodParametersFinal)
                    .withType(
                        TypeBuilder.aType()
                            .withName("com.mistraltech.smog.core.MatchAccumulator")
                    )
                    .withName("matchAccumulator")
            )
            .withStatement(
                ExpressionStatementBuilder.anExpressionStatement()
                    .withExpression(
                        ExpressionBuilder.anExpression()
                            .withText("super.matchesSafely(item, matchAccumulator)")
                    )
            )

        if (!generatorProperties.isUseReflectingPropertyMatcher) {
            properties.forEach { p: Property ->
                matchesSafelyMethod
                    .withStatement(
                        ExpressionStatementBuilder.anExpressionStatement()
                            .withExpression(
                                MethodCallBuilder.aMethodCall()
                                    .withObject("matchAccumulator")
                                    .withName("matches")
                                    .withParameter(matcherAttributeName(p))
                                    .withParameter(
                                        MethodCallBuilder.aMethodCall()
                                            .withObject("item")
                                            .withName(p.accessorName)
                                    )
                            )
                    )
            }
        }

        return matchesSafelyMethod
    }

    private fun generateMatcherVariables(properties: List<Property>): List<VariableBuilder> {
        return properties.map { generateMatcherVariable(it) }
    }

    private fun generateMatcherVariable(property: Property): VariableBuilder {
        val concretePropertyMatcher = if (generatorProperties.isUseReflectingPropertyMatcher) {
            "com.mistraltech.smog.core.ReflectingPropertyMatcher"
        } else {
            "com.mistraltech.smog.core.PropertyMatcher"
        }

        return VariableBuilder.aVariable()
            .withAccessModifier("private")
            .withFinalFlag(true)
            .withType(
                TypeBuilder.aType()
                    .withName("com.mistraltech.smog.core.PropertyMatcher")
                    .withTypeBinding(getPropertyTypeBuilder(property, true))
            )
            .withName(matcherAttributeName(property))
            .withInitialiser(
                NewInstanceBuilder.aNewInstance()
                    .withType(
                        TypeBuilder.aType()
                            .withName(concretePropertyMatcher)
                            .withTypeBinding(getPropertyTypeBuilder(property, true))
                    )
                    .withParameter("\"" + property.name + "\"")
                    .withParameter("this")
            )
    }

    private fun generateConstructor(sourceClassProperties: List<Property>, matchedType: TypeBuilder): MethodBuilder {
        val superMethodCall = MethodCallBuilder.aMethodCall()
            .withName("super")
            .withParameter("matchedObjectDescription")

        if (generatorProperties.matcherSuperClassName != null && generatorProperties.isGenerateTemplateFactoryMethod) {
            superMethodCall.withParameter("template")
        }

        val constructor = MethodBuilder.aMethod()
            .withAccessModifier(if (generatorProperties.isExtensible) "protected" else "private")
            .withName(generatorProperties.className)
            .withParameter(
                ParameterBuilder.aParameter()
                    .withFinalFlag(generatorProperties.isMakeMethodParametersFinal)
                    .withType(
                        TypeBuilder.aType()
                            .withName("java.lang.String")
                    )
                    .withName("matchedObjectDescription")
            )
            .withStatement(
                ExpressionStatementBuilder.anExpressionStatement()
                    .withExpression(superMethodCall)
            )

        if (generatorProperties.isGenerateTemplateFactoryMethod) {
            constructor.withParameter(
                ParameterBuilder.aParameter()
                    .withFinalFlag(generatorProperties.isMakeMethodParametersFinal)
                    .withType(matchedType)
                    .withName("template")
            )

            if (sourceClassProperties.isNotEmpty()) {
                val setFromTemplate = BlockStatementBuilder.aBlockStatement()
                    .withHeader("if (template != null)")

                sourceClassProperties.forEach { property: Property ->
                    setFromTemplate.withStatement(
                        MethodCallBuilder.aMethodCall()
                            .withName(setterMethodName(property))
                            .withParameter(
                                MethodCallBuilder.aMethodCall()
                                    .withObject("template")
                                    .withName(property.accessorName)
                            )
                    )
                }

                constructor.withStatement(setFromTemplate)
            }
        }

        return constructor
    }

    private fun generateNestedClass(
        matcherType: TypeBuilder,
        matchedType: TypeBuilder,
        properties: List<Property>
    ): NestedClassBuilder {

        val superCall = MethodCallBuilder.aMethodCall()
            .withName("super")
            .withParameter("matchedObjectDescription")

        if (generatorProperties.isGenerateTemplateFactoryMethod) {
            superCall.withParameter("template")
        }

        val constructor = MethodBuilder.aMethod()
            .withAccessModifier("protected")
            .withName(nestedClassName())
            .withParameter(
                ParameterBuilder.aParameter()
                    .withFinalFlag(generatorProperties.isMakeMethodParametersFinal)
                    .withType(
                        TypeBuilder.aType()
                            .withName("java.lang.String")
                    )
                    .withName("matchedObjectDescription")
            )
            .withStatement(ExpressionStatementBuilder.anExpressionStatement().withExpression(superCall))

        if (generatorProperties.isGenerateTemplateFactoryMethod) {
            constructor.withParameter(
                ParameterBuilder.aParameter()
                    .withFinalFlag(generatorProperties.isMakeMethodParametersFinal)
                    .withType(matchedType)
                    .withName("template")
            )
        }

        return NestedClassBuilder.aNestedClass()
            .withAccessModifier("public")
            .withStaticFlag(true)
            .withName(nestedClassName())
            .withTypeParameters(typeParameterDecls())
            .withSuperclass(
                TypeBuilder.aType()
                    .withName(generatorProperties.className)
                    .withTypeBindings(typeParameters())
                    .withTypeBinding(matcherType)
                    .withTypeBinding(matchedType)
            )
            .withMethod(constructor)
            .withMethod(generateMatchesSafely(matchedType, properties))
    }

    private fun generateStaticFactoryMethod(matcherType: TypeBuilder): MethodBuilder {
        val newInstance = NewInstanceBuilder.aNewInstance()
            .withType(matcherType)
            .withParameter("MATCHED_OBJECT_DESCRIPTION")

        if (generatorProperties.isGenerateTemplateFactoryMethod) {
            newInstance.withParameter("null")
        }

        return MethodBuilder.aMethod()
            .withAccessModifier("public")
            .withStaticFlag(true)
            .withReturnType(matcherType)
            .withTypeParameters(typeParameterDecls())
            .withName(
                generatorProperties.factoryMethodPrefix + sourceClassName +
                    generatorProperties.factoryMethodSuffix
            )
            .withStatement(
                ReturnStatementBuilder.aReturnStatement()
                    .withExpression(newInstance)
            )
    }

    private fun generateLikeStaticFactoryMethod(matcherType: TypeBuilder, matchedType: TypeBuilder): MethodBuilder {
        return MethodBuilder.aMethod()
            .withAccessModifier("public")
            .withStaticFlag(true)
            .withReturnType(matcherType)
            .withTypeParameters(typeParameterDecls())
            .withName(
                generatorProperties.factoryMethodPrefix + sourceClassName +
                    generatorProperties.templateFactoryMethodSuffix
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
                        NewInstanceBuilder.aNewInstance()
                            .withType(matcherType)
                            .withParameter("MATCHED_OBJECT_DESCRIPTION")
                            .withParameter("template")
                    )
            )
    }

    private fun generateSelfMethod(typeParameterR: TypeBuilder): MethodBuilder {
        return MethodBuilder.aMethod()
            .withAnnotation(
                AnnotationBuilder.anAnnotation()
                    .withType(
                        TypeBuilder.aType()
                            .withName("java.lang.SuppressWarnings")
                    )
                    .withParameter(ExpressionTextBuilder.expressionText("\"unchecked\""))
            )
            .withAccessModifier("private")
            .withReturnType(typeParameterR)
            .withName("self")
            .withStatement(
                ReturnStatementBuilder.aReturnStatement()
                    .withExpression(
                        ExpressionBuilder.anExpression()
                            .withTerm(CastBuilder.aCast().withType(typeParameterR))
                            .withText("this")
                    )
            )
    }

    private fun generateMatcherSetters(property: Property, returnType: TypeBuilder): List<MethodBuilder> {
        val methods: MutableList<MethodBuilder> = ArrayList()
        val boxedPropertyType = getPropertyTypeBuilder(property, true)

        methods.add(generateSetterTakingValue(returnType, property, boxedPropertyType))
        methods.add(generateSetterTakingMatcher(returnType, property, boxedPropertyType))

        return methods
    }

    private fun generateSetterTakingValue(
        returnType: TypeBuilder,
        property: Property,
        boxedPropertyType: TypeBuilder
    ): MethodBuilder {
        val equalToCall = StaticMethodCallBuilder.aStaticMethodCall()
            .withType(
                TypeBuilder.aType()
                    .withName("org.hamcrest.CoreMatchers")
            )
            .withName("equalTo")
            .withParameter(property.fieldName)

        if (boxedPropertyType.containsWildcard()) {
            equalToCall.withTypeBinding(boxedPropertyType)
        }

        return MethodBuilder.aMethod()
            .withAccessModifier("public")
            .withReturnType(returnType)
            .withName(setterMethodName(property))
            .withParameter(
                ParameterBuilder.aParameter()
                    .withFinalFlag(generatorProperties.isMakeMethodParametersFinal)
                    .withType(getPropertyTypeBuilder(property, false))
                    .withName(property.fieldName)
            )
            .withStatement(
                ReturnStatementBuilder.aReturnStatement()
                    .withExpression(
                        MethodCallBuilder.aMethodCall()
                            .withName(setterMethodName(property))
                            .withParameter(equalToCall)
                    )
            )
    }

    private fun generateSetterTakingMatcher(
        returnType: TypeBuilder,
        property: Property,
        boxedPropertyType: TypeBuilder
    ): MethodBuilder {
        val returnExpression = if (generatorProperties.isExtensible) {
            ExpressionBuilder.anExpression()
                .withTerm(
                    MethodCallBuilder.aMethodCall()
                        .withName("self")
                )
        } else {
            ExpressionBuilder.anExpression()
                .withText("this")
        }

        return MethodBuilder.aMethod()
            .withAccessModifier("public")
            .withReturnType(returnType)
            .withName(setterMethodName(property))
            .withParameter(
                ParameterBuilder.aParameter()
                    .withFinalFlag(generatorProperties.isMakeMethodParametersFinal)
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
            .withStatement(
                ExpressionStatementBuilder.anExpressionStatement()
                    .withExpression(
                        MethodCallBuilder.aMethodCall()
                            .withObject("this." + matcherAttributeName(property))
                            .withName("setMatcher")
                            .withParameter(
                                ExpressionBuilder.anExpression().withText(matcherAttributeName(property))
                            )
                    )
            )
            .withStatement(
                ReturnStatementBuilder.aReturnStatement()
                    .withExpression(returnExpression)
            )
    }

    private fun nestedClassName(): String {
        return generatorProperties.className.toString() + "Type"
    }
}
