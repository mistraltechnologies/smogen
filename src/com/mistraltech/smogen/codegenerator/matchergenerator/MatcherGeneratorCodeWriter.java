package com.mistraltech.smogen.codegenerator.matchergenerator;

import com.intellij.psi.JavaDirectoryService;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiPackage;
import com.intellij.psi.PsiTypeParameter;
import com.mistraltech.smogen.codegenerator.CodeWriter;
import com.mistraltech.smogen.codegenerator.PsiTypeConverter;
import com.mistraltech.smogen.codegenerator.javabuilder.AbstractClassBuilder;
import com.mistraltech.smogen.codegenerator.javabuilder.BlockStatementBuilder;
import com.mistraltech.smogen.codegenerator.javabuilder.ExpressionBuilder;
import com.mistraltech.smogen.codegenerator.javabuilder.JavaDocumentBuilder;
import com.mistraltech.smogen.codegenerator.javabuilder.MethodBuilder;
import com.mistraltech.smogen.codegenerator.javabuilder.MethodCallBuilder;
import com.mistraltech.smogen.codegenerator.javabuilder.NestedClassBuilder;
import com.mistraltech.smogen.codegenerator.javabuilder.NewInstanceBuilder;
import com.mistraltech.smogen.codegenerator.javabuilder.StaticMethodCallBuilder;
import com.mistraltech.smogen.codegenerator.javabuilder.TypeBuilder;
import com.mistraltech.smogen.codegenerator.javabuilder.TypeParameterBuilder;
import com.mistraltech.smogen.codegenerator.javabuilder.TypeParameterDeclBuilder;
import com.mistraltech.smogen.codegenerator.javabuilder.VariableBuilder;
import com.mistraltech.smogen.property.Property;
import com.mistraltech.smogen.property.PropertyLocator;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.mistraltech.smogen.codegenerator.javabuilder.AnnotationBuilder.anAnnotation;
import static com.mistraltech.smogen.codegenerator.javabuilder.BlockStatementBuilder.aBlockStatement;
import static com.mistraltech.smogen.codegenerator.javabuilder.CastBuilder.aCast;
import static com.mistraltech.smogen.codegenerator.javabuilder.ClassBuilder.aJavaClass;
import static com.mistraltech.smogen.codegenerator.javabuilder.ExpressionBuilder.anExpression;
import static com.mistraltech.smogen.codegenerator.javabuilder.ExpressionStatementBuilder.anExpressionStatement;
import static com.mistraltech.smogen.codegenerator.javabuilder.ExpressionTextBuilder.expressionText;
import static com.mistraltech.smogen.codegenerator.javabuilder.FieldTermBuilder.aField;
import static com.mistraltech.smogen.codegenerator.javabuilder.JavaDocumentBuilder.aJavaDocument;
import static com.mistraltech.smogen.codegenerator.javabuilder.MethodBuilder.aMethod;
import static com.mistraltech.smogen.codegenerator.javabuilder.MethodCallBuilder.aMethodCall;
import static com.mistraltech.smogen.codegenerator.javabuilder.NestedClassBuilder.aNestedClass;
import static com.mistraltech.smogen.codegenerator.javabuilder.NewInstanceBuilder.aNewInstance;
import static com.mistraltech.smogen.codegenerator.javabuilder.ParameterBuilder.aParameter;
import static com.mistraltech.smogen.codegenerator.javabuilder.ReturnStatementBuilder.aReturnStatement;
import static com.mistraltech.smogen.codegenerator.javabuilder.StaticMethodCallBuilder.aStaticMethodCall;
import static com.mistraltech.smogen.codegenerator.javabuilder.TypeBuilder.VOID;
import static com.mistraltech.smogen.codegenerator.javabuilder.TypeBuilder.aType;
import static com.mistraltech.smogen.codegenerator.javabuilder.TypeParameterBuilder.aTypeParameter;
import static com.mistraltech.smogen.codegenerator.javabuilder.TypeParameterDeclBuilder.aTypeParameterDecl;
import static com.mistraltech.smogen.codegenerator.javabuilder.VariableBuilder.aVariable;
import static com.mistraltech.smogen.utils.NameUtils.createFQN;

public class MatcherGeneratorCodeWriter implements CodeWriter {
    private MatcherGeneratorProperties generatorProperties;

    public MatcherGeneratorCodeWriter(MatcherGeneratorProperties matcherGeneratorProperties) {
        this.generatorProperties = matcherGeneratorProperties;
    }

    @Override
    public String writeCode() {
        return generateDocumentContent();
    }

    /*
                    .setGenerateTemplateFactoryMethod(configurationProperties.isGenerateTemplateFactoryMethod())
                .setMakeMethodParametersFinal(configurationProperties.isMakeMethodParametersFinal())
                .setUseReflectingPropertyMatcher(configurationProperties.isUseReflectingPropertyMatcher());
     */

    @NotNull
    private String generateDocumentContent() {
        PsiPackage targetPackage = JavaDirectoryService.getInstance().getPackage(generatorProperties.getParentDirectory());
        assert targetPackage != null;

        JavaDocumentBuilder document = aJavaDocument()
                .setPackageName(targetPackage.getQualifiedName())
                .addClass(generateMatcherClass(targetPackage));

        return document.build();
    }

    private String getTypeParameter(int n) {
        return "P" + (n + 1);
    }

    private List<TypeParameterBuilder> typeParameters() {
        int typeParameterCount = generatorProperties.getSourceClass().getTypeParameters().length;
        List<TypeParameterBuilder> typeParameters = new ArrayList<TypeParameterBuilder>(typeParameterCount);

        for (int i = 0; i < typeParameterCount; i++) {
            typeParameters.add(aTypeParameter()
                    .withName(getTypeParameter(i)));
        }

        return typeParameters;
    }

    private List<TypeParameterDeclBuilder> typeParameterDecls() {
        int typeParameterCount = generatorProperties.getSourceClass().getTypeParameters().length;
        List<TypeParameterDeclBuilder> typeParameters = new ArrayList<TypeParameterDeclBuilder>(typeParameterCount);

        for (int i = 0; i < typeParameterCount; i++) {
            typeParameters.add(aTypeParameterDecl()
                    .withName(getTypeParameter(i)));
        }

        return typeParameters;
    }

    private AbstractClassBuilder generateMatcherClass(PsiPackage targetPackage) {
        final String generatedClassFQN = createFQN(targetPackage.getQualifiedName(), generatorProperties.getClassName());

        AbstractClassBuilder clazz = aJavaClass()
                .withAccessModifier("public")
                .withName(generatorProperties.getClassName())
                .withTypeParameters(typeParameterDecls())
                .withAnnotation(anAnnotation()
                        .withType(aType()
                                .withName("com.mistraltech.smog.core.annotation.Matches"))
                        .withParameter(aField()
                                .withType(getSourceClassFQName())
                                .withField("class")));

        final TypeBuilder matchedType = aType()
                .withName(getSourceClassFQName())
                .withTypeBindings(typeParameters());

        final TypeBuilder returnType;
        final TypeBuilder matchedTypeParam;
        final TypeBuilder matcherType;

        if (generatorProperties.isExtensible()) {
            TypeParameterDeclBuilder returnTypeDecl = aTypeParameterDecl()
                    .withName("R")
                    .withExtends(aType()
                            .withName(generatedClassFQN)
                            .withTypeBindings(typeParameters())
                            .withTypeBinding(aTypeParameter()
                                    .withName("R"))
                            .withTypeBinding(aTypeParameter()
                                    .withName("T")));
            returnType = returnTypeDecl.getType();

            TypeParameterDeclBuilder matchedTypeDecl = aTypeParameterDecl().withName("T")
                    .withExtends(matchedType);

            matchedTypeParam = matchedTypeDecl.getType();

            matcherType = aType().withName(nestedClassName()).withTypeBindings(typeParameters());

            clazz.withTypeParameter(returnTypeDecl)
                    .withTypeParameter(matchedTypeDecl);
        } else {
            clazz.withFinalFlag(true);
            returnType = aType()
                    .withName(generatedClassFQN)
                    .withTypeBindings(typeParameters());
            matchedTypeParam = matchedType;
            matcherType = returnType;
        }

        applySuperClass(clazz, returnType, matchedTypeParam);

        applyClassBody(clazz, returnType, matchedType, matchedTypeParam, matcherType);

        return clazz;
    }

    private void applySuperClass(AbstractClassBuilder clazz, TypeBuilder returnType, TypeBuilder matchedTypeParam) {
        TypeBuilder superType;

        if (generatorProperties.getMatcherSuperClassName() != null) {
            superType = aType()
                    .withName(generatorProperties.getMatcherSuperClassName())
                    .withTypeBindings(getSourceSuperClassParameters())
                    .withTypeBinding(returnType)
                    .withTypeBinding(matchedTypeParam);
        } else {
            superType = aType()
                    .withName(generatorProperties.getBaseClassName())
                    .withTypeBinding(matchedTypeParam);
        }

        clazz.withSuperclass(superType);
    }

    private List<TypeParameterBuilder> getSourceSuperClassParameters() {
        final TypeBuilder sourceSuperClassType = getSourceSuperClassType();

        return sourceSuperClassType != null ?
                sourceSuperClassType.getTypeBindings() :
                Collections.<TypeParameterBuilder>emptyList();
    }

    private TypeBuilder getSourceSuperClassType() {
        PsiTypeConverter typeConverter = new PsiTypeConverter(true, typeParameterMap());

        PsiClassType sourceSuperClassType = generatorProperties.getSourceSuperClassType();

        if (sourceSuperClassType == null) {
            return null;
        }

        sourceSuperClassType.accept(typeConverter);

        return typeConverter.getTypeBuilder();
    }

    private void applyClassBody(AbstractClassBuilder clazz, TypeBuilder returnType, TypeBuilder matchedType,
                                TypeBuilder matchedTypeParam, TypeBuilder matcherType) {
        boolean includeSuperClassProperties = generatorProperties.getMatcherSuperClassName() == null;
        List<Property> sourceClassProperties = PropertyLocator.locateProperties(getSourceClass(), includeSuperClassProperties);

        final String objectDescription = generatorProperties.getFactoryMethodPrefix().isEmpty() ?
                getSourceClassName() :
                generatorProperties.getFactoryMethodPrefix() + " " + getSourceClassName();

        clazz.withVariable(aVariable()
                .withAccessModifier("private")
                .withFinalFlag(true)
                .withStaticFlag(true)
                .withType(aType().withName("java.lang.String"))
                .withName("MATCHED_OBJECT_DESCRIPTION")
                .withInitialiser(anExpression()
                        .withText("\"" + objectDescription + "\"")));

        clazz.withVariables(generateMatcherVariables(sourceClassProperties))
                .withMethod(generateConstructor(sourceClassProperties, matchedTypeParam));

        clazz.withMethod(generateStaticFactoryMethod(matcherType));

        if (generatorProperties.isGenerateTemplateFactoryMethod()) {
            clazz.withMethod(generateLikeStaticFactoryMethod(matcherType, matchedType));
        }

        if (generatorProperties.isExtensible()) {
            clazz.withMethod(generateSelfMethod(returnType));
        }

        for (Property property : sourceClassProperties) {
            clazz.withMethods(generateMatcherSetters(property, returnType));
        }

        if (generatorProperties.isExtensible()) {
            clazz.withNestedClass(generateNestedClass(matcherType, matchedType, sourceClassProperties));
        } else {
            clazz.withMethod(generateMatchesSafely(matchedType, sourceClassProperties));
        }
    }

    private MethodBuilder generateMatchesSafely(TypeBuilder matchedType, List<Property> properties) {
        final MethodBuilder matchesSafelyMethod = aMethod()
                .withAnnotation(anAnnotation()
                        .withType(aType()
                                .withName("java.lang.Override")))
                .withAccessModifier("protected")
                .withReturnType(VOID)
                .withName("matchesSafely")
                .withParameter(aParameter()
                        .withFinalFlag(generatorProperties.isMakeMethodParametersFinal())
                        .withType(matchedType)
                        .withName("item"))
                .withParameter(aParameter()
                        .withFinalFlag(generatorProperties.isMakeMethodParametersFinal())
                        .withType(aType()
                                .withName("com.mistraltech.smog.core.MatchAccumulator"))
                        .withName("matchAccumulator"))
                .withStatement(anExpressionStatement()
                        .withExpression(anExpression()
                                .withText("super.matchesSafely(item, matchAccumulator)")));

        if (!generatorProperties.isUseReflectingPropertyMatcher()) {
            for (Property property : properties) {
                matchesSafelyMethod
                        .withStatement(anExpressionStatement()
                                .withExpression(aMethodCall()
                                        .withObject("matchAccumulator")
                                        .withName("matches")
                                        .withParameter(matcherAttributeName(property))
                                        .withParameter(aMethodCall()
                                                .withObject("item")
                                                .withName(property.getAccessorName()))));
            }
        }

        return matchesSafelyMethod;
    }

    private List<VariableBuilder> generateMatcherVariables(@NotNull List<Property> properties) {
        List<VariableBuilder> variables = new ArrayList<VariableBuilder>();

        for (Property property : properties) {
            variables.add(generateMatcherVariable(property));
        }

        return variables;
    }

    private VariableBuilder generateMatcherVariable(@NotNull Property property) {
        String concretePropertyMatcher = generatorProperties.isUseReflectingPropertyMatcher() ?
                "com.mistraltech.smog.core.ReflectingPropertyMatcher" :
                "com.mistraltech.smog.core.PropertyMatcher";

        return aVariable()
                .withAccessModifier("private")
                .withFinalFlag(true)
                .withType(aType()
                        .withName("com.mistraltech.smog.core.PropertyMatcher")
                        .withTypeBinding(getPropertyType(property, true)))
                .withName(matcherAttributeName(property))
                .withInitialiser(aNewInstance()
                        .withType(aType()
                                .withName(concretePropertyMatcher)
                                .withTypeBinding(getPropertyType(property, true)))
                        .withParameter("\"" + property.getName() + "\"")
                        .withParameter("this"));
    }

    private TypeBuilder getPropertyType(@NotNull Property property, boolean boxed) {
        PsiTypeConverter typeConverter = new PsiTypeConverter(boxed, typeParameterMap());

        property.accept(typeConverter);

        return typeConverter.getTypeBuilder();
    }

    private Map<String, String> typeParameterMap() {
        PsiTypeParameter[] typeParameters = generatorProperties.getSourceClass().getTypeParameters();
        Map<String, String> typeParameterMap = new HashMap<String, String>();
        for (int i = 0; i < typeParameters.length; i++) {
            typeParameterMap.put(typeParameters[i].getName(), getTypeParameter(i));
        }
        return typeParameterMap;
    }

    private MethodBuilder generateConstructor(@NotNull List<Property> sourceClassProperties, TypeBuilder matchedType) {
        MethodCallBuilder superMethodCall = aMethodCall()
                .withName("super")
                .withParameter("matchedObjectDescription");

        if (generatorProperties.getMatcherSuperClassName() != null && generatorProperties.isGenerateTemplateFactoryMethod()) {
            superMethodCall.withParameter("template");
        }

        final MethodBuilder constructor = aMethod()
                .withAccessModifier(generatorProperties.isExtensible() ? "protected" : "private")
                .withName(generatorProperties.getClassName())
                .withParameter(aParameter()
                        .withFinalFlag(generatorProperties.isMakeMethodParametersFinal())
                        .withType(aType()
                                .withName("java.lang.String"))
                        .withName("matchedObjectDescription"))
                .withStatement(anExpressionStatement()
                        .withExpression(superMethodCall));

        if (generatorProperties.isGenerateTemplateFactoryMethod()) {
            constructor.withParameter(aParameter()
                    .withFinalFlag(generatorProperties.isMakeMethodParametersFinal())
                    .withType(matchedType)
                    .withName("template"));

            if (sourceClassProperties.size() > 0) {
                BlockStatementBuilder setFromTemplate = aBlockStatement()
                        .withHeader("if (template != null)");

                for (Property property : sourceClassProperties) {
                    setFromTemplate.withStatement(aMethodCall()
                            .withName(setterMethodName(property))
                            .withParameter(aMethodCall()
                                    .withObject("template")
                                    .withName(property.getAccessorName())));
                }

                constructor.withStatement(setFromTemplate);
            }
        }

        return constructor;
    }

    private NestedClassBuilder generateNestedClass(TypeBuilder matcherType, TypeBuilder matchedType, List<Property> properties) {
        final MethodCallBuilder superCall = aMethodCall()
                .withName("super")
                .withParameter("matchedObjectDescription");

        if (generatorProperties.isGenerateTemplateFactoryMethod()) {
            superCall.withParameter("template");
        }

        final MethodBuilder constructor = aMethod()
                .withAccessModifier("protected")
                .withName(nestedClassName())
                .withParameter(aParameter()
                        .withFinalFlag(generatorProperties.isMakeMethodParametersFinal())
                        .withType(aType()
                                .withName("java.lang.String"))
                        .withName("matchedObjectDescription"))
                .withStatement(anExpressionStatement().withExpression(superCall));

        if (generatorProperties.isGenerateTemplateFactoryMethod()) {
            constructor.withParameter(aParameter()
                    .withFinalFlag(generatorProperties.isMakeMethodParametersFinal())
                    .withType(matchedType)
                    .withName("template"));
        }

        NestedClassBuilder nestedClass = aNestedClass()
                .withAccessModifier("public")
                .withStaticFlag(true)
                .withName(nestedClassName())
                .withTypeParameters(typeParameterDecls())
                .withSuperclass(aType()
                        .withName(generatorProperties.getClassName())
                        .withTypeBindings(typeParameters())
                        .withTypeBinding(matcherType)
                        .withTypeBinding(matchedType))
                .withMethod(constructor)
                .withMethod(generateMatchesSafely(matchedType, properties));

        return nestedClass;
    }

    private MethodBuilder generateStaticFactoryMethod(TypeBuilder matcherType) {
        final NewInstanceBuilder newInstance = aNewInstance()
                .withType(matcherType)
                .withParameter("MATCHED_OBJECT_DESCRIPTION");

        if (generatorProperties.isGenerateTemplateFactoryMethod()) {
            newInstance.withParameter("null");
        }

        return aMethod()
                .withAccessModifier("public")
                .withStaticFlag(true)
                .withReturnType(matcherType)
                .withTypeParameters(typeParameterDecls())
                .withName(generatorProperties.getFactoryMethodPrefix() + getSourceClassName() + generatorProperties.getFactoryMethodSuffix())
                .withStatement(aReturnStatement()
                        .withExpression(newInstance));
    }

    private MethodBuilder generateLikeStaticFactoryMethod(TypeBuilder matcherType, TypeBuilder matchedType) {
        return aMethod()
                .withAccessModifier("public")
                .withStaticFlag(true)
                .withReturnType(matcherType)
                .withTypeParameters(typeParameterDecls())
                .withName(generatorProperties.getFactoryMethodPrefix() + getSourceClassName() + generatorProperties.getTemplateFactoryMethodSuffix())
                .withParameter(aParameter()
                        .withFinalFlag(generatorProperties.isMakeMethodParametersFinal())
                        .withType(matchedType)
                        .withName("template"))
                .withStatement(aReturnStatement()
                        .withExpression(aNewInstance()
                                .withType(matcherType)
                                .withParameter("MATCHED_OBJECT_DESCRIPTION")
                                .withParameter("template")));
    }

    private MethodBuilder generateSelfMethod(TypeBuilder typeParameterR) {
        return aMethod()
                .withAnnotation(anAnnotation()
                        .withType(aType()
                                .withName("java.lang.SuppressWarnings"))
                        .withParameter(expressionText("\"unchecked\"")))
                .withAccessModifier("private")
                .withReturnType(typeParameterR)
                .withName("self")
                .withStatement(aReturnStatement()
                        .withExpression(anExpression()
                                .withTerm(aCast().withType(typeParameterR))
                                .withText("this")));
    }

    private List<MethodBuilder> generateMatcherSetters(@NotNull Property property, TypeBuilder returnType) {
        List<MethodBuilder> methods = new ArrayList<MethodBuilder>();

        final StaticMethodCallBuilder equalToCall = aStaticMethodCall()
                .withType(aType()
                        .withName("org.hamcrest.CoreMatchers"))
                .withName("equalTo")
                .withParameter(property.getFieldName());

        final TypeBuilder boxedPropertyType = getPropertyType(property, true);

        if (boxedPropertyType.containsWildcard()) {
            equalToCall.withTypeBinding(boxedPropertyType);
        }

        methods.add(aMethod()
                .withAccessModifier("public")
                .withReturnType(returnType)
                .withName(setterMethodName(property))
                .withParameter(aParameter()
                        .withFinalFlag(generatorProperties.isMakeMethodParametersFinal())
                        .withType(getPropertyType(property, false))
                        .withName(property.getFieldName()))
                .withStatement(aReturnStatement()
                        .withExpression(aMethodCall()
                                .withName(setterMethodName(property))
                                .withParameter(equalToCall))));

        ExpressionBuilder returnExpression = generatorProperties.isExtensible()
                ? anExpression().withTerm(aMethodCall().withName("self"))
                : anExpression().withText("this");

        methods.add(aMethod()
                .withAccessModifier("public")
                .withReturnType(returnType)
                .withName(setterMethodName(property))
                .withParameter(aParameter()
                        .withFinalFlag(generatorProperties.isMakeMethodParametersFinal())
                        .withType(aType()
                                .withName("org.hamcrest.Matcher")
                                .withTypeBinding(aTypeParameter()
                                        .withType(boxedPropertyType)
                                        .withSuperTypes(true)))
                        .withName(matcherAttributeName(property)))
                .withStatement(anExpressionStatement()
                        .withExpression(aMethodCall()
                                .withObject("this." + matcherAttributeName(property))
                                .withName("setMatcher")
                                .withParameter(anExpression().withText(matcherAttributeName(property)))))
                .withStatement(aReturnStatement()
                        .withExpression(returnExpression)));
        return methods;
    }

    private String nestedClassName() {
        return generatorProperties.isExtensible() ? generatorProperties.getClassName() + "Type" : generatorProperties.getClassName();
    }

    private String matcherAttributeName(@NotNull Property property) {
        return property.getFieldName() + "Matcher";
    }

    private String setterMethodName(@NotNull Property property) {
        final String propertyName = generatorProperties.getSetterPrefix().equals("") ?
                property.getName() :
                property.getCapitalisedName();

        return generatorProperties.getSetterPrefix() + propertyName + generatorProperties.getSetterSuffix();
    }

    private PsiClass getSourceClass() {
        return generatorProperties.getSourceClass();
    }

    private String getSourceClassFQName() {
        return getSourceClass().getQualifiedName();
    }

    private String getSourceClassName() {
        return getSourceClass().getName();
    }
}
