package com.mistraltech.smogen.plugin;

import com.intellij.openapi.project.Project;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class SmogenConfiguration {

    private final Project project;
    private JPanel mainPanel;
    private JTextField matcherClassNameSuffixTextField;
    private JTextField matcherClassNamePrefixTextField;
    private JTextField factoryMethodSuffixTextField;
    private JTextField templateFactoryMethodSuffixTextField;
    private JTextField setterPrefixTextField;
    private JTextField setterSuffixTextField;
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
                        factoryMethodSuffixTextField.getText().equals(currentFactoryMethodSuffix) &&
                        templateFactoryMethodSuffixTextField.getText().equals(currentTemplateFactoryMethodSuffix) &&
                        setterPrefixTextField.getText().equals(currentSetterPrefix) &&
                        setterSuffixTextField.getText().equals(currentSetterSuffix) &&
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

        generateTemplateFactoryMethodCheckBox.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                onGenerateTemplateFactoryMethodChange();
            }
        });

        onGenerateTemplateFactoryMethodChange();

        reset();
    }

    private void onGenerateTemplateFactoryMethodChange() {
        templateFactoryMethodSuffixTextField.setEnabled(generateTemplateFactoryMethodCheckBox.isSelected());
    }

    public void save() {
        ConfigurationProperties properties = new ConfigurationProperties(project);

        currentMatcherClassNamePrefix = matcherClassNamePrefixTextField.getText();
        currentMatcherClassNameSuffix = matcherClassNameSuffixTextField.getText();
        currentFactoryMethodSuffix = factoryMethodSuffixTextField.getText();
        currentTemplateFactoryMethodSuffix = templateFactoryMethodSuffixTextField.getText();
        currentSetterPrefix = setterPrefixTextField.getText();
        currentSetterSuffix = setterSuffixTextField.getText();
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
        factoryMethodSuffixTextField.setText(currentFactoryMethodSuffix);
        templateFactoryMethodSuffixTextField.setText(currentTemplateFactoryMethodSuffix);
        setterPrefixTextField.setText(currentSetterPrefix);
        setterSuffixTextField.setText(currentSetterSuffix);
        useReflectingPropertyMatcherCheckBox.setSelected(currentUseReflectingPropertyMatcher);
        generateTemplateFactoryMethodCheckBox.setSelected(currentGenerateTemplateFactoryMethod);
        makeMethodParametersFinalCheckBox.setSelected(currentMakeMethodParametersFinal);
        baseClassTextField.setText(currentBaseClass);
        makeExtensibleCheckBox.setSelected(currentMakeExtensible);
    }
}
