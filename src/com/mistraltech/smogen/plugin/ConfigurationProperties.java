package com.mistraltech.smogen.plugin;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.project.Project;

public class ConfigurationProperties {
    private static final String MATCHER_CLASS_NAME_PREFIX = "smogen.matcherPrefix";
    private static final String MATCHER_CLASS_NAME_PREFIX_DEFAULT = "";
    private static final String MATCHER_CLASS_NAME_SUFFIX = "smogen.matcherSuffix";
    private static final String MATCHER_CLASS_NAME_SUFFIX_DEFAULT = "Matcher";

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

    public void reset() {
        setMatcherClassNamePrefix(MATCHER_CLASS_NAME_PREFIX_DEFAULT);
        setMatcherClassNameSuffix(MATCHER_CLASS_NAME_SUFFIX_DEFAULT);
    }
}
