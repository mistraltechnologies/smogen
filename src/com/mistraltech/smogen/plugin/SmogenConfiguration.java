package com.mistraltech.smogen.plugin;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.project.Project;

import javax.swing.*;

public class SmogenConfiguration {

    private final Project project;
    private JPanel mainPanel;
    private JTextField matcherClassNameSuffixTextField;
    private JTextField matcherClassNamePrefixTextField;

    private String currentMatcherClassNamePrefix;
    private String currentMatcherClassNameSuffix;

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
                matcherClassNameSuffixTextField.getText().equals(currentMatcherClassNameSuffix)
        );
    }

    private void init()
    {
        ConfigurationProperties properties = new ConfigurationProperties(project);

        currentMatcherClassNamePrefix = properties.getMatcherClassNamePrefix();
        currentMatcherClassNameSuffix = properties.getMatcherClassNameSuffix();

        reset();
    }

    public void save() {
        ConfigurationProperties properties = new ConfigurationProperties(project);

        currentMatcherClassNamePrefix = matcherClassNamePrefixTextField.getText();
        currentMatcherClassNameSuffix = matcherClassNameSuffixTextField.getText();

        properties.setMatcherClassNamePrefix(currentMatcherClassNamePrefix);
        properties.setMatcherClassNameSuffix(currentMatcherClassNameSuffix);
    }

    public void reset() {
        matcherClassNamePrefixTextField.setText(currentMatcherClassNamePrefix);
        matcherClassNameSuffixTextField.setText(currentMatcherClassNameSuffix);
    }
}
