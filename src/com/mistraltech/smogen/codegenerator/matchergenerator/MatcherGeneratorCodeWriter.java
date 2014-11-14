package com.mistraltech.smogen.codegenerator.matchergenerator;

import com.intellij.psi.JavaDirectoryService;
import com.intellij.psi.PsiClass;
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
import com.mistraltech.smogen.codegenerator.javabuilder.StaticMethodCallBuilder;
import com.mistraltech.smogen.codegenerator.javabuilder.TypeBuilder;
import com.mistraltech.smogen.codegenerator.javabuilder.TypeParameterDeclBuilder;
import com.mistraltech.smogen.codegenerator.javabuilder.VariableBuilder;
import com.mistraltech.smogen.property.Property;
import com.mistraltech.smogen.property.PropertyLocator;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
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

    private int getTypeParameterIndex(String name) {
        PsiTypeParameter[] typeParameters = generatorProperties.getSourceClass().getTypeParameters();
        for (int i = 0; i < typeParameters.length; i++) {
            if (typeParameters[i].getText().equals(name)) {
                return i;
            }
        }
        throw new IllegalArgumentException("No type parameter with name [" + name + "]");
    }

    private List<TypeParameterDeclBuilder> typeParameters() {
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
                .withTypeParameters(typeParameters())
                .withAnnotation(anAnnotation()
                        .withType(aType()
                                .withName("com.mistraltech.smog.core.annotation.Matches"))
                        .withParameter(anExpression()
                                .withText(getSourceClassFQName() + ".class")));

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

        if (generatorProperties.getSuperClassName() != null) {
            superType = aType()
                    .withName(generatorProperties.getSuperClassName())
                    .withTypeBinding(returnType)
                    .withTypeBinding(matchedTypeParam);
        } else {
            superType = aType()
                    .withName("com.mistraltech.smog.core.CompositePropertyMatcher")
                    .withTypeBinding(matchedTypeParam);
        }

        clazz.withSuperclass(superType);
    }

    private void applyClassBody(AbstractClassBuilder clazz, TypeBuilder returnType, TypeBuilder matchedType,
                                TypeBuilder matchedTypeParam, TypeBuilder matcherType) {
        boolean includeSuperClassProperties = generatorProperties.getSuperClassName() == null;
        List<Property> sourceClassProperties = PropertyLocator.locateProperties(getSourceClass(), includeSuperClassProperties);

        clazz.withVariable(aVariable()
                .withAccessModifier("private")
                .withFinalFlag(true)
                .withStaticFlag(true)
                .withType(aType().withName("java.lang.String"))
                .withName("MATCHED_OBJECT_DESCRIPTION")
                .withInitialiser(anExpression()
                        .withText(String.format("\"%s %s\"", generatorProperties.getFactoryMethodPrefix(), getSourceClassName()))));

        clazz.withVariables(generateMatcherVariables(sourceClassProperties))
                .withMethod(generateConstructor(sourceClassProperties, matchedTypeParam));

        if (generatorProperties.isExtensible()) {
            clazz.withNestedClass(generateNestedClass(matcherType, matchedType));
        }

        clazz.withMethod(generateStaticFactoryMethod(matcherType));
        clazz.withMethod(generateLikeStaticFactoryMethod(matcherType, matchedType));

        if (generatorProperties.isExtensible()) {
            clazz.withMethod(generateSelfMethod(returnType));
        }

        for (Property property : sourceClassProperties) {
            clazz.withMethods(generateMatcherSetters(property, returnType));
        }

        clazz.withMethod(generateMatchesSafely(matchedType));
    }

    private MethodBuilder generateMatchesSafely(TypeBuilder matchedType) {
        return aMethod()
                .withAnnotation(anAnnotation()
                        .withType(aType()
                                .withName("java.lang.Override")))
                .withAccessModifier("protected")
                .withReturnType(VOID)
                .withName("matchesSafely")
                .withParameter(aParameter()
                        .withFinalFlag(true)
                        .withType(matchedType)
                        .withName("item"))
                .withParameter(aParameter()
                        .withFinalFlag(true)
                        .withType(aType()
                                .withName("com.mistraltech.smog.core.MatchAccumulator"))
                        .withName("matchAccumulator"))
                .withStatement(anExpressionStatement()
                        .withExpression(anExpression()
                                .withText("super.matchesSafely(item, matchAccumulator)")));
    }

    private List<VariableBuilder> generateMatcherVariables(@NotNull List<Property> properties) {
        List<VariableBuilder> variables = new ArrayList<VariableBuilder>();

        for (Property property : properties) {
            variables.add(generateMatcherVariable(property));
        }

        return variables;
    }

    private VariableBuilder generateMatcherVariable(@NotNull Property property) {
        return aVariable()
                .withAccessModifier("private")
                .withFinalFlag(true)
                .withType(aType()
                        .withName("com.mistraltech.smog.core.PropertyMatcher")
                        .withTypeBinding(getPropertyType(property, true)))
                .withName(matcherAttributeName(property))
                .withInitialiser(aNewInstance()
                        .withType(aType()
                                .withName("com.mistraltech.smog.core.ReflectingPropertyMatcher")
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

        if (generatorProperties.getSuperClassName() != null) {
            superMethodCall.withParameter("template");
        }

        final MethodBuilder constructor = aMethod()
                .withAccessModifier(generatorProperties.isExtensible() ? "protected" : "private")
                .withName(generatorProperties.getClassName())
                .withParameter(aParameter()
                        .withFinalFlag(true)
                        .withType(aType()
                                .withName("java.lang.String"))
                        .withName("matchedObjectDescription"))
                .withParameter(aParameter()
                        .withFinalFlag(true)
                        .withType(matchedType)
                        .withName("template"))
                .withStatement(anExpressionStatement()
                        .withExpression(superMethodCall));

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

        return constructor;
    }

    private NestedClassBuilder generateNestedClass(TypeBuilder matcherType, TypeBuilder matchedType) {
        NestedClassBuilder nestedClass = aNestedClass()
                .withAccessModifier("public")
                .withStaticFlag(true)
                .withName(nestedClassName())
                .withTypeParameters(typeParameters())
                .withSuperclass(aType()
                        .withName(generatorProperties.getClassName())
                        .withTypeBindings(typeParameters())
                        .withTypeBinding(matcherType)
                        .withTypeBinding(matchedType))
                .withMethod(aMethod()
                                .withAccessModifier("protected")
                                .withName(nestedClassName())
                                .withParameter(aParameter()
                                        .withFinalFlag(true)
                                        .withType(aType()
                                                .withName("java.lang.String"))
                                        .withName("matchedObjectDescription"))
                                .withParameter(aParameter()
                                        .withFinalFlag(true)
                                        .withType(matchedType)
                                        .withName("template"))
                                .withStatement(anExpressionStatement().withExpression(aMethodCall()
                                        .withName("super")
                                        .withParameter("matchedObjectDescription")
                                        .withParameter("template")))
                );

        return nestedClass;
    }

    private MethodBuilder generateStaticFactoryMethod(TypeBuilder matcherType) {
        return aMethod()
                .withAccessModifier("public")
                .withStaticFlag(true)
                .withReturnType(matcherType)
                .withTypeParameters(typeParameters())
                .withName(generatorProperties.getFactoryMethodPrefix() + getSourceClassName() + "That")
                .withStatement(aReturnStatement()
                        .withExpression(aNewInstance()
                                .withType(matcherType)
                                .withParameter("MATCHED_OBJECT_DESCRIPTION")
                                .withParameter("null")));
    }

    private MethodBuilder generateLikeStaticFactoryMethod(TypeBuilder matcherType, TypeBuilder matchedType) {
        return aMethod()
                .withAccessModifier("public")
                .withStaticFlag(true)
                .withReturnType(matcherType)
                .withTypeParameters(typeParameters())
                .withName(generatorProperties.getFactoryMethodPrefix() + getSourceClassName() + "Like")
                .withParameter(aParameter()
                        .withFinalFlag(true)
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
                        .withFinalFlag(true)
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
                        .withFinalFlag(true)
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
        return " has" + property.getCapitalisedName();
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
