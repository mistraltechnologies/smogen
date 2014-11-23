package com.mistraltech.smogen.plugin;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class SmogenConfigurable implements Configurable {

    private final Project project;
    private SmogenConfiguration smogenConfiguration;

    public SmogenConfigurable(Project project) {
        this.project = project;
    }

    @Nls
    @Override
    public String getDisplayName() {
        return "SMOG Matcher Generator";
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        return null;
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        smogenConfiguration = new SmogenConfiguration(project);
        return smogenConfiguration.getMainPanel();
    }

    @Override
    public boolean isModified() {
        return smogenConfiguration != null && smogenConfiguration.isModified();
    }

    @Override
    public void apply() throws ConfigurationException {
        if (smogenConfiguration != null) {
            smogenConfiguration.save();
        }
    }

    @Override
    public void reset() {
        if (smogenConfiguration != null) {
            smogenConfiguration.reset();
        }
    }

    @Override
    public void disposeUIResources() {
        smogenConfiguration = null;
    }
}
