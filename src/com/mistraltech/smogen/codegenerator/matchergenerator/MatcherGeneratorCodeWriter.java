package com.mistraltech.smogen.codegenerator.matchergenerator;

import com.intellij.psi.JavaDirectoryService;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiPackage;
import com.mistraltech.smogen.codegenerator.CodeWriter;
import com.mistraltech.smogen.codegenerator.javabuilder.AbstractClassBuilder;
import com.mistraltech.smogen.codegenerator.javabuilder.BlockStatementBuilder;
import com.mistraltech.smogen.codegenerator.javabuilder.ExpressionBuilder;
import com.mistraltech.smogen.codegenerator.javabuilder.JavaDocumentBuilder;
import com.mistraltech.smogen.codegenerator.javabuilder.MethodBuilder;
import com.mistraltech.smogen.codegenerator.javabuilder.MethodCallBuilder;
import com.mistraltech.smogen.codegenerator.javabuilder.NestedClassBuilder;
import com.mistraltech.smogen.codegenerator.javabuilder.TypeBuilder;
import com.mistraltech.smogen.codegenerator.javabuilder.TypeParameterDeclBuilder;
import com.mistraltech.smogen.codegenerator.javabuilder.VariableBuilder;
import com.mistraltech.smogen.property.Property;
import com.mistraltech.smogen.property.PropertyLocator;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

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

    private AbstractClassBuilder generateMatcherClass(PsiPackage targetPackage) {
        final String generatedClassFQN = createFQN(targetPackage.getQualifiedName(), generatorProperties.getClassName());

        AbstractClassBuilder clazz = aJavaClass()
                .withAccessModifier("public")
                .withName(generatorProperties.getClassName())
                .withAnnotation(anAnnotation()
                        .withType(aType()
                                .withName("com.mistraltech.smog.core.annotation.Matches"))
                        .withParameter(anExpression()
                                .withText(getSourceClassFQName() + ".class")));


        TypeBuilder returnType;
        TypeBuilder matchedType;

        if (generatorProperties.isExtensible()) {
            TypeParameterDeclBuilder returnTypeDecl = aTypeParameterDecl()
                    .withName("R")
                    .withExtends(aType()
                            .withName(generatedClassFQN)
                            .withTypeBinding(aTypeParameter()
                                    .withName("R")));
            returnType = returnTypeDecl.getType();

            TypeParameterDeclBuilder matchedTypeDecl = aTypeParameterDecl().withName("T")
                    .withExtends(aType()
                            .withName(getSourceClassFQName()));

            matchedType = matchedTypeDecl.getType();

            clazz.withTypeParameter(returnTypeDecl)
                    .withTypeParameter(matchedTypeDecl);
        } else {
            clazz.withFinalFlag(true);
            returnType = aType().withName(generatedClassFQN);
            matchedType = aType().withName(getSourceClassFQName());
        }

        applySuperClass(clazz, returnType, matchedType);

        applyClassBody(clazz, returnType, matchedType);

        return clazz;
    }

    private void applySuperClass(AbstractClassBuilder clazz, TypeBuilder returnType, TypeBuilder matchedType) {
        TypeBuilder superType;

        if (generatorProperties.getSuperClassName() != null) {
            superType = aType()
                    .withName(generatorProperties.getSuperClassName())
                    .withTypeBinding(returnType)
                    .withTypeBinding(matchedType);
        } else {
            superType = aType()
                    .withName("com.mistraltech.smog.core.CompositePropertyMatcher")
                    .withTypeBinding(matchedType);
        }

        clazz.withSuperclass(superType);
    }

    private void applyClassBody(AbstractClassBuilder clazz, TypeBuilder returnType, TypeBuilder matchedType) {
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
                .withMethod(generateConstructor(sourceClassProperties, matchedType));

        if (generatorProperties.getConcreteSubclassName() != null) {
            clazz.withNestedClass(generateNestedClass());
        }

        clazz.withMethod(generateStaticFactoryMethod());
        clazz.withMethod(generateLikeStaticFactoryMethod(matchedType));

        if (generatorProperties.isExtensible()) {
            clazz.withMethod(generateSelfMethod(returnType));
        }

        for (Property property : sourceClassProperties) {
            clazz.withMethods(generateMatcherSetters(property, returnType));
        }
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
                        .withTypeBinding(property.getBoxedType()))
                .withName(matcherAttributeName(property))
                .withInitialiser(aNewInstance()
                        .withType(aType()
                                .withName("com.mistraltech.smog.core.ReflectingPropertyMatcher")
                                .withTypeBinding(property.getBoxedType()))
                        .withParameter("\"" + property.getName() + "\"")
                        .withParameter("this"));
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

    private NestedClassBuilder generateNestedClass() {
        String nestedClassName = generatorProperties.getConcreteSubclassName();

        NestedClassBuilder nestedClass = aNestedClass()
                .withAccessModifier("public")
                .withStaticFlag(true)
                .withName(nestedClassName)
                .withSuperclass(aType()
                        .withName(generatorProperties.getClassName())
                        .withTypeBinding(nestedClassName)
                        .withTypeBinding(getSourceClassFQName()))
                .withMethod(aMethod()
                        .withAccessModifier("public")
                        .withName(nestedClassName)
                        .withParameter(aParameter()
                                .withFinalFlag(true)
                                .withType(aType()
                                        .withName("java.lang.String"))
                                .withName("matchedObjectDescription"))
                        .withParameter(aParameter()
                                .withFinalFlag(true)
                                .withType(aType()
                                        .withName(getSourceClassFQName()))
                                .withName("template")));

        return nestedClass;
    }

    private MethodBuilder generateStaticFactoryMethod() {
        String innerClassName = generatorProperties.getConcreteSubclassName();
        String targetClassName = innerClassName != null ? innerClassName : generatorProperties.getClassName();

        return aMethod()
                .withAccessModifier("public")
                .withStaticFlag(true)
                .withReturnType(aType()
                        .withName(targetClassName))
                .withName(generatorProperties.getFactoryMethodPrefix() + getSourceClassName() + "That")
                .withStatement(aReturnStatement()
                        .withExpression(aNewInstance()
                                .withType(aType()
                                        .withName(targetClassName))
                                .withParameter("MATCHED_OBJECT_DESCRIPTION")
                                .withParameter("null")));
    }

    private MethodBuilder generateLikeStaticFactoryMethod(TypeBuilder matchedType) {
        String innerClassName = generatorProperties.getConcreteSubclassName();
        String targetClassName = innerClassName != null ? innerClassName : generatorProperties.getClassName();

        return aMethod()
                .withAccessModifier("public")
                .withStaticFlag(true)
                .withReturnType(aType()
                        .withName(targetClassName))
                .withName(generatorProperties.getFactoryMethodPrefix() + getSourceClassName() + "Like")
                .withParameter(aParameter()
                        .withFinalFlag(true)
                        .withType(matchedType)
                        .withName("template"))
                .withStatement(aReturnStatement()
                        .withExpression(aNewInstance()
                                .withType(aType()
                                        .withName(targetClassName))
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

        methods.add(aMethod()
                .withAccessModifier("public")
                .withReturnType(returnType)
                .withName(setterMethodName(property))
                .withParameter(aParameter()
                        .withFinalFlag(true)
                        .withType(aType().withName(property.getType()))
                        .withName(property.getFieldName()))
                .withStatement(aReturnStatement()
                        .withExpression(aMethodCall()
                                .withName(setterMethodName(property))
                                .withParameter(aStaticMethodCall()
                                        .withType(aType()
                                                .withName("org.hamcrest.CoreMatchers"))
                                        .withName("equalTo")
                                        .withParameter(property.getFieldName())))));

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
                                        .withName(property.getBoxedType())
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


//        documentText
//                .append("public ")
//                .append(generatorProperties.isExtensible() ? "R" : generatorProperties.getClassName())
//                .append(" ")
//                .append(setterMethodName(property))
//                .append("(Matcher<? super ")
//                .append(property.getBoxedType())
//                .append("> ")
//                .append(matcherAttributeName(property))
//                .append(") {\n")
//                .append("this.")
//                .append(matcherAttributeName(property))
//                .append(".setMatcher(")
//                .append(matcherAttributeName(property))
//                .append(");\n")
//                .append("return ")
//                .append(generatorProperties.isExtensible() ? "self()" : "this")
//                .append(";\n")
//                .append("}\n");
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
