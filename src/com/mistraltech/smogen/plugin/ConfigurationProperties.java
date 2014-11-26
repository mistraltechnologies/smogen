package com.mistraltech.smogen.plugin;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.project.Project;

public class ConfigurationProperties {
    private static final String MATCHER_CLASS_NAME_PREFIX = "smogen.matcherPrefix";
    private static final String MATCHER_CLASS_NAME_PREFIX_DEFAULT = "";
    private static final String MATCHER_CLASS_NAME_SUFFIX = "smogen.matcherSuffix";
    private static final String MATCHER_CLASS_NAME_SUFFIX_DEFAULT = "Matcher";
    private static final String FACTORY_METHOD_SUFFIX = "smogen.factoryMethodSuffix";
    private static final String FACTORY_METHOD_SUFFIX_DEFAULT = "That";
    private static final String TEMPLATE_FACTORY_METHOD_SUFFIX = "smogen.templateFactoryMethodSuffix";
    private static final String TEMPLATE_FACTORY_METHOD_SUFFIX_DEFAULT = "Like";
    private static final String SETTER_PREFIX = "smogen.setterPrefix";
    private static final String SETTER_PREFIX_DEFAULT = "has";
    private static final String SETTER_SUFFIX = "smogen.setterSuffix";
    private static final String SETTER_SUFFIX_DEFAULT = "";
    private static final String USE_REFLECTING_PROPERTY_MATCHER = "smogen.useReflectingPropertyMatcher";
    private static final String USE_REFLECTING_PROPERTY_MATCHER_DEFAULT = Boolean.TRUE.toString();
    private static final String GENERATE_TEMPLATE_FACTORY_METHOD = "smogen.generateTemplateFactoryMethod";
    private static final String GENERATE_TEMPLATE_FACTORY_METHOD_DEFAULT = Boolean.TRUE.toString();
    private static final String MAKE_METHOD_PARAMETERS_FINAL = "smogen.makeMethodParametersFinal";
    private static final String MAKE_METHOD_PARAMETERS_FINAL_DEFAULT = Boolean.TRUE.toString();
    private static final String BASE_CLASS = "smogen.baseClass";
    private static final String BASE_CLASS_DEFAULT = "com.mistraltech.smog.core.CompositePropertyMatcher";
    private static final String MAKE_EXTENSIBLE = "smogen.makeExtensible";
    private static final String MAKE_EXTENSIBLE_DEFAULT = Boolean.FALSE.toString();

    PropertiesComponent properties;

    public ConfigurationProperties(Project project) {
        this.properties = PropertiesComponent.getInstance(project);
    }

    public String getMatcherClassNamePrefix() {
        return properties.getValue(MATCHER_CLASS_NAME_PREFIX, MATCHER_CLASS_NAME_PREFIX_DEFAULT);
    }

    public void setMatcherClassNamePrefix(String prefix) {
        properties.setValue(MATCHER_CLASS_NAME_PREFIX, prefix);
    }

    public String getMatcherClassNameSuffix() {
        return properties.getValue(MATCHER_CLASS_NAME_SUFFIX, MATCHER_CLASS_NAME_SUFFIX_DEFAULT);
    }

    public void setMatcherClassNameSuffix(String suffix) {
        properties.setValue(MATCHER_CLASS_NAME_SUFFIX, suffix);
    }

    public String getFactoryMethodSuffix() {
        return properties.getValue(FACTORY_METHOD_SUFFIX, FACTORY_METHOD_SUFFIX_DEFAULT);
    }

    public void setFactoryMethodSuffix(String suffix) {
        properties.setValue(FACTORY_METHOD_SUFFIX, suffix);
    }

    public String getTemplateFactoryMethodSuffix() {
        return properties.getValue(TEMPLATE_FACTORY_METHOD_SUFFIX, TEMPLATE_FACTORY_METHOD_SUFFIX_DEFAULT);
    }

    public void setTemplateFactoryMethodSuffix(String suffix) {
        properties.setValue(TEMPLATE_FACTORY_METHOD_SUFFIX, suffix);
    }

    public String getSetterPrefix() {
        return properties.getValue(SETTER_PREFIX, SETTER_PREFIX_DEFAULT);
    }

    public void setSetterPrefix(String prefix) {
        properties.setValue(SETTER_PREFIX, prefix);
    }

    public String getSetterSuffix() {
        return properties.getValue(SETTER_SUFFIX, SETTER_SUFFIX_DEFAULT);
    }

    public void setSetterSuffix(String suffix) {
        properties.setValue(SETTER_SUFFIX, suffix);
    }

    public boolean isUseReflectingPropertyMatcher() {
        String value = properties.getValue(USE_REFLECTING_PROPERTY_MATCHER, USE_REFLECTING_PROPERTY_MATCHER_DEFAULT);
        return Boolean.valueOf(value);
    }

    public void setUseReflectingPropertyMatcher(boolean useReflectingPropertyMatcherExtensible) {
        properties.setValue(USE_REFLECTING_PROPERTY_MATCHER, Boolean.toString(useReflectingPropertyMatcherExtensible));
    }

    public boolean isGenerateTemplateFactoryMethod() {
        String value = properties.getValue(GENERATE_TEMPLATE_FACTORY_METHOD, GENERATE_TEMPLATE_FACTORY_METHOD_DEFAULT);
        return Boolean.valueOf(value);
    }

    public void setGenerateTemplateFactoryMethod(boolean generateTemplateFactoryMethod) {
        properties.setValue(GENERATE_TEMPLATE_FACTORY_METHOD, Boolean.toString(generateTemplateFactoryMethod));
    }

    public boolean isMakeMethodParametersFinal() {
        String value = properties.getValue(MAKE_METHOD_PARAMETERS_FINAL, MAKE_METHOD_PARAMETERS_FINAL_DEFAULT);
        return Boolean.valueOf(value);
    }

    public void setMakeMethodParametersFinal(boolean makeMethodParametersFinal) {
        properties.setValue(MAKE_METHOD_PARAMETERS_FINAL, Boolean.toString(makeMethodParametersFinal));
    }

    public String getBaseClass() {
        return properties.getValue(BASE_CLASS, BASE_CLASS_DEFAULT);
    }

    public void setBaseClass(String baseClass) {
        properties.setValue(BASE_CLASS, baseClass);
    }

    public boolean isMakeExtensible() {
        String value = properties.getValue(MAKE_EXTENSIBLE, MAKE_EXTENSIBLE_DEFAULT);
        return Boolean.valueOf(value);
    }

    public void setMakeExtensible(boolean makeExtensible) {
        properties.setValue(MAKE_EXTENSIBLE, Boolean.toString(makeExtensible));
    }

    public void reset() {
        setMatcherClassNamePrefix(MATCHER_CLASS_NAME_PREFIX_DEFAULT);
        setMatcherClassNameSuffix(MATCHER_CLASS_NAME_SUFFIX_DEFAULT);
        setFactoryMethodSuffix(FACTORY_METHOD_SUFFIX_DEFAULT);
        setTemplateFactoryMethodSuffix(TEMPLATE_FACTORY_METHOD_SUFFIX_DEFAULT);
        setSetterPrefix(SETTER_PREFIX_DEFAULT);
        setSetterSuffix(SETTER_SUFFIX_DEFAULT);
        setUseReflectingPropertyMatcher(Boolean.valueOf(USE_REFLECTING_PROPERTY_MATCHER_DEFAULT));
        setGenerateTemplateFactoryMethod(Boolean.valueOf(GENERATE_TEMPLATE_FACTORY_METHOD_DEFAULT));
        setMakeMethodParametersFinal(Boolean.valueOf(MAKE_METHOD_PARAMETERS_FINAL_DEFAULT));
        setBaseClass(BASE_CLASS_DEFAULT);
        setMakeExtensible(Boolean.valueOf(MAKE_EXTENSIBLE_DEFAULT));
    }
}
