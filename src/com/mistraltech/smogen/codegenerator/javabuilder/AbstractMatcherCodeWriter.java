package com.mistraltech.smogen.codegenerator.javabuilder;

import com.intellij.psi.JavaDirectoryService;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiPackage;
import com.intellij.psi.PsiTypeParameter;
import com.mistraltech.smogen.codegenerator.CodeWriter;
import com.mistraltech.smogen.codegenerator.PsiTypeConverter;
import com.mistraltech.smogen.codegenerator.matchergenerator.MatcherGeneratorProperties;
import com.mistraltech.smogen.property.Property;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.mistraltech.smogen.codegenerator.javabuilder.JavaDocumentBuilder.aJavaDocument;
import static com.mistraltech.smogen.codegenerator.javabuilder.TypeParameterBuilder.aTypeParameter;
import static com.mistraltech.smogen.codegenerator.javabuilder.TypeParameterDeclBuilder.aTypeParameterDecl;

public abstract class AbstractMatcherCodeWriter implements CodeWriter {
    public static final String MATCHES_PROPERTY_ANNOTATION_CLASSNAME = "com.mistraltech.smog.core.annotation.MatchesProperty";
    public static final String DEFAULT_SETTER_METHOD_PREFIX = "has";
    public static final String DEFAULT_SETTER_METHOD_SUFFIX = "";
    protected MatcherGeneratorProperties generatorProperties;

    public AbstractMatcherCodeWriter(MatcherGeneratorProperties matcherGeneratorProperties) {
        this.generatorProperties = matcherGeneratorProperties;
    }

    @Override
    public final String writeCode() {
        JavaDocumentBuilder document = aJavaDocument()
                .setPackageName(getPackage().getQualifiedName());

        generateDocumentContent(document);

        return document.build();
    }

    protected abstract void generateDocumentContent(JavaDocumentBuilder document);

    protected String getTypeParameter(int n) {
        return "P" + (n + 1);
    }

    protected List<TypeParameterBuilder> typeParameters() {
        int typeParameterCount = generatorProperties.getSourceClass().getTypeParameters().length;
        List<TypeParameterBuilder> typeParameters = new ArrayList<TypeParameterBuilder>(typeParameterCount);

        for (int i = 0; i < typeParameterCount; i++) {
            typeParameters.add(aTypeParameter()
                    .withName(getTypeParameter(i)));
        }

        return typeParameters;
    }

    protected List<TypeParameterDeclBuilder> typeParameterDecls() {
        int typeParameterCount = generatorProperties.getSourceClass().getTypeParameters().length;
        List<TypeParameterDeclBuilder> typeParameters = new ArrayList<TypeParameterDeclBuilder>(typeParameterCount);

        for (int i = 0; i < typeParameterCount; i++) {
            typeParameters.add(aTypeParameterDecl()
                    .withName(getTypeParameter(i)));
        }

        return typeParameters;
    }

    @NotNull
    protected PsiPackage getPackage() {
        PsiPackage targetPackage = JavaDirectoryService.getInstance().getPackage(generatorProperties.getParentDirectory());
        assert targetPackage != null;
        return targetPackage;
    }

    protected PsiClass getSourceClass() {
        return generatorProperties.getSourceClass();
    }

    protected String getSourceClassFQName() {
        return getSourceClass().getQualifiedName();
    }

    protected String getSourceClassName() {
        return getSourceClass().getName();
    }

    protected List<TypeParameterBuilder> getSourceSuperClassParameters() {
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

    protected TypeBuilder getPropertyType(@NotNull Property property, boolean boxed) {
        PsiTypeConverter typeConverter = new PsiTypeConverter(boxed, typeParameterMap());

        property.accept(typeConverter);

        return typeConverter.getTypeBuilder();
    }

    private Map<String, String> typeParameterMap() {
        PsiTypeParameter[] typeParameters = getSourceClass().getTypeParameters();
        Map<String, String> typeParameterMap = new HashMap<String, String>();
        for (int i = 0; i < typeParameters.length; i++) {
            typeParameterMap.put(typeParameters[i].getName(), getTypeParameter(i));
        }
        return typeParameterMap;
    }

    protected String matcherAttributeName(@NotNull Property property) {
        return property.getFieldName() + "Matcher";
    }

    protected String setterMethodName(@NotNull Property property) {
        final String propertyName = generatorProperties.getSetterPrefix().equals("") ?
                property.getName() :
                property.getCapitalisedName();

        return generatorProperties.getSetterPrefix() + propertyName + generatorProperties.getSetterSuffix();
    }

    protected String getMatchedObjectDescription() {
        return generatorProperties.getFactoryMethodPrefix().isEmpty() ?
                getSourceClassName() :
                generatorProperties.getFactoryMethodPrefix() + " " + getSourceClassName();
    }
}
