package com.mistraltech.smogen.plugin;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.FileIndex;
import com.intellij.openapi.roots.JavaProjectRootsUtil;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.JavaCodeFragment;
import com.intellij.psi.JavaCodeFragmentFactory;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiJavaCodeReferenceCodeFragment;
import com.intellij.psi.PsiNameHelper;
import com.intellij.psi.PsiPackage;
import com.intellij.psi.impl.JavaPsiFacadeEx;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.ProjectScope;
import com.intellij.refactoring.ui.ClassNameReferenceEditor;
import com.intellij.refactoring.ui.PackageNameReferenceEditorCombo;
import com.intellij.ui.ListCellRendererWrapper;
import com.intellij.ui.ReferenceEditorWithBrowseButton;
import com.intellij.util.PlatformIcons;
import com.mistraltech.smogen.utils.PsiUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.math.BigDecimal;

public class MatcherGeneratorOptionsPanel {
    private static final int PANEL_WIDTH_CHARS = 60;
    private static final String SMOG_JAVASSIST_GENERATOR = "com.mistraltech.smog.proxy.javassist.JavassistMatcherGenerator";

    private final MatcherGeneratorOptionsPanelDataSource dataSource;

    private JPanel rootPanel;
    private JTextField classNameTextField;
    private JCheckBox makeExtensibleCheckBox;
    private PackageNameReferenceEditorCombo packageComponent;
    private JComboBox destinationSourceRootComboBox;
    private JRadioButton aRadioButton;
    private JRadioButton anRadioButton;
    private JLabel matchesLabel;
    private JCheckBox extendsCheckBox;
    private ReferenceEditorWithBrowseButton superClassChooser;
    private JLabel sourceClassName;
    private JRadioButton classRadioButton;
    private JRadioButton interfaceRadioButton;
    private JRadioButton interfaceWithFactoryRadioButton;
    private boolean generateFactoryMethods;

    public MatcherGeneratorOptionsPanel(@NotNull MatcherGeneratorOptionsPanelDataSource dataSource) {
        this.dataSource = dataSource;

        initialiseSourceClassNameField();
        initialiseGenerateTypeRadioButtons();
        initialiseClassNameField();
        initialiseMakeExtensibleCheckBox();
        initialiseExtendsFields();
        initialiseFactoryMethodPrefixRadioButtons();
        initialiseDestinationSourceRootComboBox();
    }

    private void initialiseSourceClassNameField() {
        final String qualifiedName = dataSource.getMatchedClass().getQualifiedName();
        sourceClassName.setText(qualifiedName);
    }
    
    private void initialiseGenerateTypeRadioButtons() {
        boolean smogJavassist = isSmogJavassistOnClasspath();
        boolean java8 = isProjectJava8();

        classRadioButton.setEnabled(true);
        interfaceRadioButton.setEnabled(smogJavassist);
        interfaceWithFactoryRadioButton.setEnabled(smogJavassist && java8);

        if (interfaceWithFactoryRadioButton.isEnabled()) {
            interfaceWithFactoryRadioButton.setSelected(true);
        } else if (interfaceRadioButton.isEnabled()) {
            interfaceRadioButton.setSelected(true);
        } else {
            classRadioButton.setSelected(true);
        }
    }

    private void initialiseClassNameField() {
        classNameTextField.setText(dataSource.getDefaultClassName());
    }

    private void initialiseExtendsFields() {
        superClassChooser.setEnabled(false);
        extendsCheckBox.setSelected(false);
        extendsCheckBox.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                onExtendsCheckBoxStateChange();
            }
        });
    }

    private void initialiseMakeExtensibleCheckBox() {
        // If we are matching an abstract class, likelihood is we want the matcher to be extensible
        if (PsiUtils.isAbstract(dataSource.getMatchedClass())) {
            makeExtensibleCheckBox.setSelected(true);
        } else {
            makeExtensibleCheckBox.setSelected(dataSource.getDefaultIsExtensible());
        }
        makeExtensibleCheckBox.setEnabled(true);
    }

    private void initialiseFactoryMethodPrefixRadioButtons() {
        // The factory method prefix is either 'a' or 'an', selected by radio buttons
        // and labelled by matchesLabel.
        String matchedClassName = dataSource.getMatchedClass().getName();

        aRadioButton.setText("a " + matchedClassName);
        anRadioButton.setText("an " + matchedClassName);

        if (hasVowelSound(matchedClassName)) {
            anRadioButton.setSelected(true);
        } else {
            aRadioButton.setSelected(true);
        }
    }

    private void initialiseDestinationSourceRootComboBox() {
        for (VirtualFile candidateRoot : dataSource.getCandidateRoots()) {
            ListItemWrapper<?> listItemWrapper = createSourceRootItemWrapper(candidateRoot);
            destinationSourceRootComboBox.addItem(listItemWrapper);
            if (candidateRoot.equals(dataSource.getDefaultRoot())) {
                destinationSourceRootComboBox.setSelectedItem(listItemWrapper);
            }
        }

        destinationSourceRootComboBox.setRenderer(new ListCellRendererWrapper<ListItemWrapper<?>>() {
            @Override
            public void customize(JList list, ListItemWrapper<?> listItemWrapper, int index, boolean selected, boolean hasFocus) {
                setIcon(listItemWrapper.getIcon());
                setText(listItemWrapper.getText());
            }
        });
    }

    private void onExtendsCheckBoxStateChange() {
        superClassChooser.setEnabled(extendsCheckBox.isSelected());
    }

    @NotNull
    public VirtualFile getSelectedSourceRoot() {
        //noinspection unchecked
        return ((ListItemWrapper<VirtualFile>) destinationSourceRootComboBox.getSelectedItem()).getItem();
    }

    @NotNull
    public String getSelectedClassName() {
        return classNameTextField.getText();
    }

    @NotNull
    public String getSelectedPackageName() {
        return packageComponent.getText();
    }

    public boolean isMakeExtensible() {
        return makeExtensibleCheckBox.isSelected();
    }

    public boolean isAn() {
        return anRadioButton.isSelected();
    }

    public boolean isGenerateInterface() {
        return interfaceRadioButton.isSelected() || interfaceWithFactoryRadioButton.isSelected();
    }

    public boolean isGenerateFactoryMethods() {
        return classRadioButton.isSelected() || interfaceWithFactoryRadioButton.isSelected();
    }

    @Nullable
    public String getSuperClassName() {
        if (extendsCheckBox.isSelected()) {
            return StringUtil.trimLeading(StringUtil.trimTrailing(superClassChooser.getText()));
        } else {
            return null;
        }
    }

    @NotNull
    public JComponent getRoot() {
        return rootPanel;
    }

    private boolean hasVowelSound(@NotNull String matchedClassName) {
        return "aeiou".contains(matchedClassName.substring(0, 1).toLowerCase());
    }

    @NotNull
    private ListItemWrapper<VirtualFile> createSourceRootItemWrapper(@NotNull VirtualFile candidateRoot) {
        String relativePath = ProjectUtil.calcRelativeToProjectPath(candidateRoot, dataSource.getProject(), true, false, true);
        return new ListItemWrapper<VirtualFile>(candidateRoot, relativePath, getSourceRootIcon(candidateRoot));
    }

    private void createUIComponents() {
        // This is called by the form handler for custom-created components
        Project project = dataSource.getProject();
        packageComponent = new PackageNameReferenceEditorCombo(dataSource.getPackageName(),
                project, dataSource.getRecentsKey(), "Choose Destination Package");
        packageComponent.setTextFieldPreferredWidth(PANEL_WIDTH_CHARS);

        GlobalSearchScope scope = JavaProjectRootsUtil.getScopeWithoutGeneratedSources(ProjectScope.getProjectScope(project), project);
        superClassChooser = new ClassNameReferenceEditor(project, null, scope);
        superClassChooser.setTextFieldPreferredWidth(PANEL_WIDTH_CHARS);
    }

    @NotNull
    private Icon getSourceRootIcon(@NotNull VirtualFile virtualFile) {
        FileIndex fileIndex = ProjectRootManager.getInstance(dataSource.getProject()).getFileIndex();

        if (fileIndex.isInTestSourceContent(virtualFile)) {
            return PlatformIcons.MODULES_TEST_SOURCE_FOLDER;
        } else if (fileIndex.isInSourceContent(virtualFile)) {
            return PlatformIcons.MODULES_SOURCE_FOLDERS_ICON;
        } else {
            return PlatformIcons.FOLDER_ICON;
        }
    }

    public ValidationInfo doValidate() {
        String className = StringUtil.trimLeading(StringUtil.trimTrailing(classNameTextField.getText()));
        if (StringUtil.isEmpty(className)) {
            return new ValidationInfo("Class name is empty", classNameTextField);
        }

        if (!PsiNameHelper.getInstance(dataSource.getProject()).isIdentifier(className)) {
            return new ValidationInfo("Class name is not a valid identifier", classNameTextField);
        }

        return null;
    }

    private boolean isSmogJavassistOnClasspath() {
        final Project project = dataSource.getProject();
        final JavaPsiFacade javaPsiFacade = JavaPsiFacade.getInstance(project);
        final PsiClass generatorClass = javaPsiFacade.findClass(SMOG_JAVASSIST_GENERATOR, GlobalSearchScope.allScope(project));
        return generatorClass != null;
    }

    private boolean isProjectJava8() {
        final Project project = dataSource.getProject();
        final Sdk projectSdk = ProjectRootManager.getInstance(project).getProjectSdk();
        try {
            final float ver = Float.parseFloat(projectSdk.getName());
            return ver >= 1.8f;
        } catch (Exception e) {
            return false;
        }
    }

    private class ListItemWrapper<T> {
        private T item;
        private String text;
        private Icon icon;

        ListItemWrapper(T item, String text, Icon icon) {
            this.item = item;
            this.text = text;
            this.icon = icon;
        }

        public T getItem() {
            return item;
        }

        public String getText() {
            return text;
        }

        public Icon getIcon() {
            return icon;
        }
    }
}
