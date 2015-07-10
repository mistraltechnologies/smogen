package com.mistraltech.smogen.codegenerator.matchergenerator;

import com.intellij.psi.PsiPackage;
import com.mistraltech.smogen.codegenerator.javabuilder.AbstractMatcherCodeWriter;
import com.mistraltech.smogen.codegenerator.javabuilder.BlockStatementBuilder;
import com.mistraltech.smogen.codegenerator.javabuilder.ExpressionBuilder;
import com.mistraltech.smogen.codegenerator.javabuilder.InterfaceBuilder;
import com.mistraltech.smogen.codegenerator.javabuilder.InterfaceMethodBuilder;
import com.mistraltech.smogen.codegenerator.javabuilder.JavaDocumentBuilder;
import com.mistraltech.smogen.codegenerator.javabuilder.MethodBuilder;
import com.mistraltech.smogen.codegenerator.javabuilder.MethodCallBuilder;
import com.mistraltech.smogen.codegenerator.javabuilder.MethodSignatureBuilder;
import com.mistraltech.smogen.codegenerator.javabuilder.NestedClassBuilder;
import com.mistraltech.smogen.codegenerator.javabuilder.NewInstanceBuilder;
import com.mistraltech.smogen.codegenerator.javabuilder.StaticMethodCallBuilder;
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
import static com.mistraltech.smogen.codegenerator.javabuilder.ExpressionBuilder.anExpression;
import static com.mistraltech.smogen.codegenerator.javabuilder.ExpressionStatementBuilder.anExpressionStatement;
import static com.mistraltech.smogen.codegenerator.javabuilder.ExpressionTextBuilder.expressionText;
import static com.mistraltech.smogen.codegenerator.javabuilder.FieldTermBuilder.aField;
import static com.mistraltech.smogen.codegenerator.javabuilder.InterfaceBuilder.aJavaInterface;
import static com.mistraltech.smogen.codegenerator.javabuilder.InterfaceMethodBuilder.anInterfaceMethod;
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

public class MatcherInterfaceCodeWriter extends AbstractMatcherCodeWriter {
    public MatcherInterfaceCodeWriter(MatcherGeneratorProperties matcherGeneratorProperties) {
        super(matcherGeneratorProperties);
    }

    @Override
    protected void generateDocumentContent(JavaDocumentBuilder document) {
        document.addInterface(generateMatcherInterface());
    }

    private InterfaceBuilder generateMatcherInterface() {
        final PsiPackage targetPackage = getPackage();
        final String generatedClassFQN = createFQN(targetPackage.getQualifiedName(), generatorProperties.getClassName());

        InterfaceBuilder clazz = aJavaInterface()
                .withAccessModifier("public")
                .withName(generatorProperties.getClassName())
                .withTypeParameters(typeParameterDecls())
                .withAnnotation(anAnnotation()
                        .withType(aType()
                                .withName("com.mistraltech.smog.core.annotation.Matches"))
                        .withParameter(anExpression().withText("value=" + getSourceClassFQName() + ".class"))
                        .withParameter(anExpression().withText("description=\"" + getMatchedObjectDescription() + "\"")));

        final TypeBuilder matchedType = aType()
                .withName(getSourceClassFQName())
                .withTypeBindings(typeParameters());

        final TypeBuilder returnType;
        final TypeBuilder matchedTypeParam;

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

            clazz.withTypeParameter(returnTypeDecl)
                    .withTypeParameter(matchedTypeDecl);
        } else {
            returnType = aType()
                    .withName(generatedClassFQN)
                    .withTypeBindings(typeParameters());
            matchedTypeParam = matchedType;
        }

        applySuperInterface(clazz, returnType, matchedTypeParam);

        applyInterfaceBody(clazz, returnType);

        return clazz;
    }

    private void applySuperInterface(InterfaceBuilder clazz, TypeBuilder returnType, TypeBuilder matchedTypeParam) {
        if (generatorProperties.getMatcherSuperClassName() != null) {
            TypeBuilder superType = aType()
                    .withName(generatorProperties.getMatcherSuperClassName())
                    .withTypeBindings(getSourceSuperClassParameters())
                    .withTypeBinding(returnType)
                    .withTypeBinding(matchedTypeParam);
            clazz.withImplementedInterface(superType);
        }
    }

    private void applyInterfaceBody(InterfaceBuilder clazz, TypeBuilder returnType) {
        final boolean includeSuperClassProperties = generatorProperties.getMatcherSuperClassName() == null;
        final List<Property> sourceClassProperties = PropertyLocator.locateProperties(getSourceClass(), includeSuperClassProperties);

        for (Property property : sourceClassProperties) {
            clazz.withMethods(generateMatcherSetters(property, returnType));
        }
    }

//    private MethodSignatureBuilder generateStaticFactoryMethod(TypeBuilder matcherType) {
//        final NewInstanceBuilder newInstance = aNewInstance()
//                .withType(matcherType)
//                .withParameter("MATCHED_OBJECT_DESCRIPTION");
//
//        if (generatorProperties.isGenerateTemplateFactoryMethod()) {
//            newInstance.withParameter("null");
//        }
//
//        return aMethod()
//                .withAccessModifier("public")
//                .withStaticFlag(true)
//                .withReturnType(matcherType)
//                .withTypeParameters(typeParameterDecls())
//                .withName(generatorProperties.getFactoryMethodPrefix() + getSourceClassName() + generatorProperties.getFactoryMethodSuffix())
//                .withStatement(aReturnStatement()
//                        .withExpression(newInstance));
//    }

    private MethodSignatureBuilder generateLikeStaticFactoryMethod(TypeBuilder matcherType, TypeBuilder matchedType) {
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

    private List<InterfaceMethodBuilder> generateMatcherSetters(@NotNull Property property, TypeBuilder returnType) {
        List<InterfaceMethodBuilder> methods = new ArrayList<InterfaceMethodBuilder>();

        final TypeBuilder boxedPropertyType = getPropertyType(property, true);

        methods.add(anInterfaceMethod()
                .withReturnType(returnType)
                .withName(setterMethodName(property))
                .withParameter(aParameter()
                        .withFinalFlag(generatorProperties.isMakeMethodParametersFinal())
                        .withType(getPropertyType(property, false))
                        .withName(property.getFieldName())));

        methods.add(anInterfaceMethod()
                .withReturnType(returnType)
                .withName(setterMethodName(property))
                .withParameter(aParameter()
                        .withFinalFlag(generatorProperties.isMakeMethodParametersFinal())
                        .withType(aType()
                                .withName("org.hamcrest.Matcher")
                                .withTypeBinding(aTypeParameter()
                                        .withType(boxedPropertyType)
                                        .withSuperTypes(true)))
                        .withName(matcherAttributeName(property))));

        return methods;
    }
}
