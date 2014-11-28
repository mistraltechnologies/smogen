package com.mistraltech.smogen.plugin;

import com.intellij.openapi.project.Project;

import javax.swing.*;

public class SmogenConfiguration {

    private final Project project;
    private JPanel mainPanel;
    private JTextField matcherClassNameSuffixTextField;
    private JTextField matcherClassNamePrefixTextField;
    private JCheckBox useReflectingPropertyMatcherCheckBox;
    private JCheckBox generateTemplateFactoryMethodCheckBox;
    private JCheckBox makeMethodParametersFinalCheckBox;
    private JTextField baseClassTextField;
    private JCheckBox makeExtensibleCheckBox;

    private String currentMatcherClassNamePrefix;
    private String currentMatcherClassNameSuffix;
    private String currentFactoryMethodSuffix;
    private String currentTemplateFactoryMethodSuffix;
    private String currentSetterPrefix;
    private String currentSetterSuffix;
    private boolean currentUseReflectingPropertyMatcher;
    private boolean currentGenerateTemplateFactoryMethod;
    private boolean currentMakeMethodParametersFinal;
    private String currentBaseClass;
    private boolean currentMakeExtensible;

    public SmogenConfiguration(Project project) {
        this.project = project;
        init();
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public boolean isModified() {
        return !(
                matcherClassNamePrefixTextField.getText().equals(currentMatcherClassNamePrefix) &&
                        matcherClassNameSuffixTextField.getText().equals(currentMatcherClassNameSuffix) &&
                        useReflectingPropertyMatcherCheckBox.isSelected() == currentUseReflectingPropertyMatcher &&
                        generateTemplateFactoryMethodCheckBox.isSelected() == currentGenerateTemplateFactoryMethod &&
                        makeMethodParametersFinalCheckBox.isSelected() == currentMakeMethodParametersFinal &&
                        baseClassTextField.getText().equals(currentBaseClass) &&
                        makeExtensibleCheckBox.isSelected() == currentMakeExtensible
        );
    }

    private void init() {
        ConfigurationProperties properties = new ConfigurationProperties(project);

        currentMatcherClassNamePrefix = properties.getMatcherClassNamePrefix();
        currentMatcherClassNameSuffix = properties.getMatcherClassNameSuffix();
        currentFactoryMethodSuffix = properties.getFactoryMethodSuffix();
        currentTemplateFactoryMethodSuffix = properties.getTemplateFactoryMethodSuffix();
        currentSetterPrefix = properties.getSetterPrefix();
        currentSetterSuffix = properties.getSetterSuffix();
        currentUseReflectingPropertyMatcher = properties.isUseReflectingPropertyMatcher();
        currentGenerateTemplateFactoryMethod = properties.isGenerateTemplateFactoryMethod();
        currentMakeMethodParametersFinal = properties.isMakeMethodParametersFinal();
        currentBaseClass = properties.getBaseClass();
        currentMakeExtensible = properties.isMakeExtensible();

        reset();
    }

    public void save() {
        ConfigurationProperties properties = new ConfigurationProperties(project);

        currentMatcherClassNamePrefix = matcherClassNamePrefixTextField.getText();
        currentMatcherClassNameSuffix = matcherClassNameSuffixTextField.getText();
        currentUseReflectingPropertyMatcher = useReflectingPropertyMatcherCheckBox.isSelected();
        currentGenerateTemplateFactoryMethod = generateTemplateFactoryMethodCheckBox.isSelected();
        currentMakeMethodParametersFinal = makeMethodParametersFinalCheckBox.isSelected();
        currentBaseClass = baseClassTextField.getText();
        currentMakeExtensible = makeExtensibleCheckBox.isSelected();

        properties.setMatcherClassNamePrefix(currentMatcherClassNamePrefix);
        properties.setMatcherClassNameSuffix(currentMatcherClassNameSuffix);
        properties.setFactoryMethodSuffix(currentFactoryMethodSuffix);
        properties.setTemplateFactoryMethodSuffix(currentTemplateFactoryMethodSuffix);
        properties.setSetterPrefix(currentSetterPrefix);
        properties.setSetterSuffix(currentSetterSuffix);
        properties.setUseReflectingPropertyMatcher(currentUseReflectingPropertyMatcher);
        properties.setGenerateTemplateFactoryMethod(currentGenerateTemplateFactoryMethod);
        properties.setMakeMethodParametersFinal(currentMakeMethodParametersFinal);
        properties.setBaseClass(currentBaseClass);
        properties.setMakeExtensible(currentMakeExtensible);
    }

    public void reset() {
        matcherClassNamePrefixTextField.setText(currentMatcherClassNamePrefix);
        matcherClassNameSuffixTextField.setText(currentMatcherClassNameSuffix);
        useReflectingPropertyMatcherCheckBox.setSelected(currentUseReflectingPropertyMatcher);
        generateTemplateFactoryMethodCheckBox.setSelected(currentGenerateTemplateFactoryMethod);
        makeMethodParametersFinalCheckBox.setSelected(currentMakeMethodParametersFinal);
        baseClassTextField.setText(currentBaseClass);
        makeExtensibleCheckBox.setSelected(currentMakeExtensible);
    }
}
