package com.mistraltech.smogen.codegenerator.matchergenerator;

import com.intellij.psi.JavaDirectoryService;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiPackage;
import com.mistraltech.smogen.codegenerator.CodeWriter;
import com.mistraltech.smogen.codegenerator.javabuilder.BlockStatementBuilder;
import com.mistraltech.smogen.codegenerator.javabuilder.JavaClassBuilder;
import com.mistraltech.smogen.codegenerator.javabuilder.JavaDocumentBuilder;
import com.mistraltech.smogen.codegenerator.javabuilder.MethodBuilder;
import com.mistraltech.smogen.codegenerator.javabuilder.MethodCallBuilder;
import com.mistraltech.smogen.codegenerator.javabuilder.TypeParameterBuilder;
import com.mistraltech.smogen.codegenerator.javabuilder.VariableBuilder;
import com.mistraltech.smogen.property.Property;
import com.mistraltech.smogen.property.PropertyLocator;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.mistraltech.smogen.codegenerator.javabuilder.BlockStatementBuilder.aBlockStatement;
import static com.mistraltech.smogen.codegenerator.javabuilder.ExpressionStatementBuilder.anExpressionStatement;
import static com.mistraltech.smogen.codegenerator.javabuilder.JavaClassBuilder.aJavaClass;
import static com.mistraltech.smogen.codegenerator.javabuilder.JavaDocumentBuilder.aJavaDocument;
import static com.mistraltech.smogen.codegenerator.javabuilder.MethodCallBuilder.aMethodCall;
import static com.mistraltech.smogen.codegenerator.javabuilder.NewInstanceBuilder.aNewInstance;
import static com.mistraltech.smogen.codegenerator.javabuilder.ParameterBuilder.aParameter;
import static com.mistraltech.smogen.codegenerator.javabuilder.TypeBuilder.aType;
import static com.mistraltech.smogen.codegenerator.javabuilder.TypeParameterBuilder.aTypeParameter;
import static com.mistraltech.smogen.codegenerator.javabuilder.VariableBuilder.aVariable;

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
                .addClass(generateMatcherClass());

        return document.build();
    }

    private JavaClassBuilder generateMatcherClass() {
        boolean includeSuperClassProperties = generatorProperties.getSuperClassName() == null;
        List<Property> sourceClassProperties = PropertyLocator.locateProperties(getSourceClass(), includeSuperClassProperties);

        JavaClassBuilder clazz = generateClassSignature();
        clazz.withVariables(generateMatcherVariables(sourceClassProperties))
                .withMethod(generateConstructor(sourceClassProperties));

        return clazz;
//
//        StringBuilder documentText = new StringBuilder();
//        generateClassSignature(documentText);
//        generateMatcherVariables(documentText, sourceClassProperties);
//        generateConstructor(documentText, sourceClassProperties);

//        generateInnerClass(documentText);
//        generateStaticFactoryMethod(documentText);
//        generateLikeStaticFactoryMethod(documentText);
//        generateSelfMethod(documentText);
//        generateMatcherSetters(documentText, sourceClassProperties);
//        documentText.append("}\n");
//
//        document.setClassBody(documentText);

//    private void generateDefaultImports(JavaDocumentBuilder document) {
//        if (generatorProperties.getSuperClassName() == null) {
//            document.addImport("com.mistraltech.smog.core.CompositePropertyMatcher");
//        }
//
//        document.addImport("com.mistraltech.smog.core.ReflectingPropertyMatcher");
//        document.addImport("com.mistraltech.smog.core.PropertyMatcher");
//        document.addImport("org.hamcrest.Matcher");
//        document.addStaticImport("org.hamcrest.CoreMatchers.equalTo");
//    }


    }

    private JavaClassBuilder generateClassSignature() {
        JavaClassBuilder clazz = aJavaClass()
                .withAccessModifier("public")
                .withAbstractFlag(generatorProperties.isExtensible())
                .withFinalFlag(!generatorProperties.isExtensible())
                .withName(generatorProperties.getClassName());

        if (generatorProperties.isExtensible()) {
            final TypeParameterBuilder typeParameterR = aTypeParameter().withName("R");
            final TypeParameterBuilder typeParameterT = aTypeParameter().withName("T")
                    .withExtends(getSourceClassName());

            clazz.withTypeParameter(typeParameterR)
                    .withTypeParameter(typeParameterT);

            if (generatorProperties.getSuperClassName() != null) {
                clazz.withSuperclass(aType()
                        .withName(generatorProperties.getSuperClassName())
                        .withTypeBinding(typeParameterR)
                        .withTypeBinding(typeParameterT));
            } else {
                clazz.withSuperclass(aType()
                        .withName("com.mistraltech.smog.core.CompositePropertyMatcher")
                        .withTypeBinding(typeParameterT));
            }
        } else {
            if (generatorProperties.getSuperClassName() != null) {
                clazz.withSuperclass(aType()
                        .withName(generatorProperties.getSuperClassName())
                        .withTypeBinding(generatorProperties.getClassName())
                        .withTypeBinding(getSourceClassName()));
            } else {
                clazz.withSuperclass(aType()
                        .withName("com.mistraltech.smog.core.CompositePropertyMatcher")
                        .withTypeBinding(getSourceClassName()));
            }
        }

        return clazz;
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
                .withType(aType().withName("com.mistraltech.smog.core.PropertyMatcher").withTypeBinding(property.getBoxedType()))
                .withName(matcherAttributeName(property))
                .withInitialiser(aNewInstance()
                        .withType(aType()
                                .withName("com.mistraltech.smog.core.ReflectingPropertyMatcher")
                                .withTypeBinding(property.getBoxedType()))
                        .withParameter("\"" + property.getName() + "\"")
                        .withParameter("this"));
    }

    private MethodBuilder generateConstructor(@NotNull List<Property> sourceClassProperties) {
        MethodCallBuilder superMethodCall = aMethodCall()
                .withName("super")
                .withParameter("matchedObjectDescription");

        if (generatorProperties.getSuperClassName() != null) {
            superMethodCall.withParameter("template");
        }

        BlockStatementBuilder setFromTemplate = aBlockStatement()
                .withHeader("if (template != null)");

        for (Property property : sourceClassProperties) {
            setFromTemplate.withStatement(aMethodCall()
                    .withName(setterMethodName(property))
                    .withParameter(aMethodCall()
                            .withObject("template")
                            .withName(property.getAccessorName())));
        }

        return MethodBuilder.aMethod()
                .withAccessModifier(generatorProperties.isExtensible() ? "protected" : "private")
                .withName(generatorProperties.getClassName())
                .withParameter(aParameter()
                        .withFinalFlag(true)
                        .withType(aType()
                                .withName("java.lang.String"))
                        .withName("matchedObjectDescription"))
                .withParameter(aParameter()
                        .withFinalFlag(true)
                        .withType(aType()
                                .withName(getSourceClassName()))
                        .withName("template"))
                .withStatement(anExpressionStatement()
                        .withExpression(superMethodCall))
                .withStatement(setFromTemplate);

    }

    private void generateInnerClass(@NotNull StringBuilder documentText) {
        String innerClassName = generatorProperties.getConcreteSubclassName();
        if (innerClassName != null) {
            documentText
                    .append("public static class ")
                    .append(innerClassName)
                    .append(" extends ")
                    .append(generatorProperties.getClassName())
                    .append("<")
                    .append(innerClassName)
                    .append(", ")
                    .append(getSourceClassName())
                    .append("> {\n")
                    .append("public ")
                    .append(innerClassName)
                    .append("(final String matchedObjectDescription, final ")
                    .append(getSourceClassName())
                    .append(" template) {\n")
                    .append("super(matchedObjectDescription, template);\n")
                    .append("}\n")
                    .append("}\n");
        }
    }

    private void generateStaticFactoryMethod(@NotNull StringBuilder documentText) {
        String innerClassName = generatorProperties.getConcreteSubclassName();
        String targetClassName = innerClassName != null ? innerClassName : generatorProperties.getClassName();

        if (!generatorProperties.isExtensible() || innerClassName != null) {
            documentText
                    .append("public static ")
                    .append(targetClassName)
                    .append(" ")
                    .append(generatorProperties.getFactoryMethodPrefix())
                    .append(getSourceClassName())
                    .append("That()\n")
                    .append("{\n")
                    .append("return ")
                    .append(generatorProperties.getFactoryMethodPrefix())
                    .append(getSourceClassName())
                    .append("Like(null);\n")
                    .append("}\n");
        }
    }

    private void generateLikeStaticFactoryMethod(@NotNull StringBuilder documentText) {
        String innerClassName = generatorProperties.getConcreteSubclassName();
        String targetClassName = innerClassName != null ? innerClassName : generatorProperties.getClassName();

        if (!generatorProperties.isExtensible() || innerClassName != null) {
            documentText
                    .append("public static ")
                    .append(targetClassName)
                    .append(" ")
                    .append(generatorProperties.getFactoryMethodPrefix())
                    .append(getSourceClassName())
                    .append("Like(final ")
                    .append(getSourceClassName())
                    .append(" template)\n")
                    .append("{\n")
                    .append("return new ")
                    .append(targetClassName)
                    .append("(\"")
                    .append(generatorProperties.getFactoryMethodPrefix())
                    .append(" ")
                    .append(getSourceClassName())
                    .append("\", template);\n")
                    .append("}\n");
        }
    }

    private void generateSelfMethod(@NotNull StringBuilder documentText) {
        if (generatorProperties.isExtensible()) {
            documentText
                    .append("@SuppressWarnings(\"unchecked\")\n")
                    .append("private R self()\n")
                    .append("{\n")
                    .append("return (R) this;\n")
                    .append("}\n");
        }
    }

    private void generateMatcherSetters(@NotNull StringBuilder documentText, @NotNull List<Property> properties) {
        for (Property property : properties) {
            generateMatcherSetter(documentText, property);
        }
    }

    private void generateMatcherSetter(@NotNull StringBuilder documentText, @NotNull Property property) {
        documentText
                .append("public ")
                .append(generatorProperties.isExtensible() ? "R" : generatorProperties.getClassName())
                .append(" ")
                .append(setterMethodName(property))
                .append("(final ")
                .append(property.getType())
                .append(" ")
                .append(property.getName())
                .append(") {\n")
                .append("return this.")
                .append(setterMethodName(property))
                .append("(equalTo(")
                .append(property.getName())
                .append("));\n")
                .append("}\n");

        documentText
                .append("public ")
                .append(generatorProperties.isExtensible() ? "R" : generatorProperties.getClassName())
                .append(" ")
                .append(setterMethodName(property))
                .append("(Matcher<? super ")
                .append(property.getBoxedType())
                .append("> ")
                .append(matcherAttributeName(property))
                .append(") {\n")
                .append("this.")
                .append(matcherAttributeName(property))
                .append(".setMatcher(")
                .append(matcherAttributeName(property))
                .append(");\n")
                .append("return ")
                .append(generatorProperties.isExtensible() ? "self()" : "this")
                .append(";\n")
                .append("}\n");
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

    private String getSourceClassName() {
        return getSourceClass().getQualifiedName();
    }
}
