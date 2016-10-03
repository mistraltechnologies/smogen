package com.mistraltech.smogen.codegenerator.matchergenerator;

import com.intellij.psi.JavaDirectoryService;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiPackage;
import com.intellij.psi.PsiTypeParameter;
import com.mistraltech.smogen.codegenerator.CodeWriter;
import com.mistraltech.smogen.codegenerator.javabuilder.JavaDocumentBuilder;
import com.mistraltech.smogen.codegenerator.javabuilder.TypeBuilder;
import com.mistraltech.smogen.codegenerator.javabuilder.TypeParameterBuilder;
import com.mistraltech.smogen.codegenerator.javabuilder.TypeParameterDeclBuilder;
import com.mistraltech.smogen.property.Property;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;

import static com.mistraltech.smogen.codegenerator.javabuilder.JavaDocumentBuilder.aJavaDocument;
import static com.mistraltech.smogen.codegenerator.javabuilder.TypeParameterBuilder.aTypeParameter;
import static com.mistraltech.smogen.codegenerator.javabuilder.TypeParameterDeclBuilder.aTypeParameterDecl;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@SuppressWarnings("WeakerAccess")
abstract class AbstractMatcherCodeWriter implements CodeWriter {
    public static final String MATCHES_PROPERTY_ANNOTATION_CLASS_NAME = "com.mistraltech.smog.core.annotation.MatchesProperty";

    public static final String DEFAULT_SETTER_METHOD_PREFIX = "has";

    public static final String DEFAULT_SETTER_METHOD_SUFFIX = "";

    protected MatcherGeneratorProperties generatorProperties;

    public AbstractMatcherCodeWriter(MatcherGeneratorProperties matcherGeneratorProperties) {
        this.generatorProperties = matcherGeneratorProperties;
    }

    @Override
    public final String writeCode() {
        JavaDocumentBuilder document = aJavaDocument();

        document.setPackageName(getPackage().getQualifiedName());

        generateDocumentContent(document);

        return document.build();
    }

    protected abstract void generateDocumentContent(JavaDocumentBuilder document);

    protected String getTypeParameter(int n) {
        return "P" + (n + 1);
    }

    protected List<TypeParameterBuilder> typeParameters() {
        int noOfTypeParameters = getSourceClass().getTypeParameters().length;

        return IntStream.range(0, noOfTypeParameters)
                .mapToObj(i -> aTypeParameter().withName(getTypeParameter(i)))
                .collect(toList());
    }

    protected List<TypeParameterDeclBuilder> typeParameterDecls() {
        int noOfTypeParameters = getSourceClass().getTypeParameters().length;

        return IntStream.range(0, noOfTypeParameters)
                .mapToObj(i -> aTypeParameterDecl().withName(getTypeParameter(i)))
                .collect(toList());
    }

    @NotNull
    protected PsiPackage getPackage() {
        PsiDirectory parentDirectory = generatorProperties.getParentDirectory();

        PsiPackage targetPackage = Optional.ofNullable(JavaDirectoryService.getInstance().getPackage(parentDirectory))
                .orElseThrow(() -> new IllegalStateException("Can't get package for directory " + parentDirectory.getName()));

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

    protected List<TypeParameterBuilder> getSourceSuperClassParameterBuilders() {
        return getSourceSuperClassTypeBuilder().map(TypeBuilder::getTypeBindings).orElse(Collections.emptyList());
    }

    private Optional<TypeBuilder> getSourceSuperClassTypeBuilder() {
        Optional<PsiClassType> sourceSuperClassType = generatorProperties.getSourceSuperClassType();

        return sourceSuperClassType
                .map(p -> p.accept(new PsiTypeConverter(true, typeParameterMap())));
    }

    protected TypeBuilder getPropertyTypeBuilder(@NotNull Property property, boolean boxed) {
        return property.accept(new PsiTypeConverter(boxed, typeParameterMap()));
    }

    private Map<String, String> typeParameterMap() {
        final PsiTypeParameter[] typeParameters = getSourceClass().getTypeParameters();

        int noOfTypeParameters = getSourceClass().getTypeParameters().length;

        return IntStream.range(0, noOfTypeParameters)
                .boxed()
                .collect(toMap(i -> typeParameters[i].getName(), this::getTypeParameter));
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
