package com.mistraltech.smogen.plugin

import com.intellij.openapi.project.calcRelativeToProjectPath
import com.intellij.openapi.roots.FileIndex
import com.intellij.openapi.roots.JavaProjectRootsUtil
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiNameHelper
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.ProjectScope
import com.intellij.refactoring.ui.ClassNameReferenceEditor
import com.intellij.refactoring.ui.PackageNameReferenceEditorCombo
import com.intellij.ui.ReferenceEditorWithBrowseButton
import com.intellij.ui.SimpleListCellRenderer
import com.intellij.util.PlatformIcons
import com.mistraltech.smogen.utils.PsiUtils.isAbstract
import javax.swing.Icon
import javax.swing.JCheckBox
import javax.swing.JComboBox
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JList
import javax.swing.JPanel
import javax.swing.JRadioButton
import javax.swing.JTextField

class MatcherGeneratorOptionsPanel(private val dataSource: MatcherGeneratorOptionsPanelDataSource) {
    private var rootPanel: JPanel? = null
    private var classNameTextField: JTextField? = null
    private var makeExtensibleCheckBox: JCheckBox? = null
    private var packageComponent: PackageNameReferenceEditorCombo? = null
    private var destinationSourceRootComboBox: JComboBox<ListItemWrapper<VirtualFile>>? = null
    private var aRadioButton: JRadioButton? = null
    private var anRadioButton: JRadioButton? = null
    private var extendsCheckBox: JCheckBox? = null
    private var superClassChooser: ReferenceEditorWithBrowseButton? = null
    private var sourceClassName: JLabel? = null
    private var classRadioButton: JRadioButton? = null
    private var interfaceRadioButton: JRadioButton? = null

    @Suppress("unused")
    private var matchesLabel: JLabel? = null

    private fun initialiseSourceClassNameField() {
        sourceClassName!!.text = dataSource.matchedClass.qualifiedName
    }

    private fun initialiseGenerateTypeRadioButtons() {
        classRadioButton!!.isEnabled = true
        interfaceRadioButton!!.isEnabled = isSmogJavassistOnClasspath

        if (interfaceRadioButton!!.isEnabled) {
            interfaceRadioButton!!.isSelected = true
        } else {
            classRadioButton!!.isSelected = true
        }
    }

    private fun initialiseClassNameField() {
        classNameTextField!!.text = dataSource.defaultClassName
    }

    private fun initialiseExtendsFields() {
        superClassChooser!!.isEnabled = false
        extendsCheckBox!!.isSelected = false
        extendsCheckBox!!.addChangeListener { onExtendsCheckBoxStateChange() }
    }

    private fun onExtendsCheckBoxStateChange() {
        superClassChooser!!.isEnabled = extendsCheckBox!!.isSelected
    }

    private fun initialiseMakeExtensibleCheckBox() {
        // If we are matching an abstract class, likelihood is we want the matcher to be extensible
        makeExtensibleCheckBox!!.isSelected =
            if (isAbstract(dataSource.matchedClass)) true else dataSource.defaultIsExtensible
        makeExtensibleCheckBox!!.isEnabled = true
    }

    private fun initialiseFactoryMethodPrefixRadioButtons() {
        // The factory method prefix is either 'a' or 'an', selected by radio buttons
        // and labelled by matchesLabel.
        val matchedClassName = dataSource.matchedClass.name ?: throw IllegalStateException("Matched class has no name")

        aRadioButton!!.text = "a $matchedClassName"
        anRadioButton!!.text = "an $matchedClassName"

        if (hasVowelSound(matchedClassName)) {
            anRadioButton!!.isSelected = true
        } else {
            aRadioButton!!.isSelected = true
        }
    }

    private fun initialiseDestinationSourceRootComboBox() {
        dataSource.candidateRoots.forEach { candidateRoot ->
            val listItemWrapper = createSourceRootItemWrapper(candidateRoot)
            destinationSourceRootComboBox!!.addItem(listItemWrapper)

            if (candidateRoot == dataSource.defaultRoot) {
                destinationSourceRootComboBox!!.selectedItem = listItemWrapper
            }
        }

        destinationSourceRootComboBox!!.setRenderer(
            object : SimpleListCellRenderer<ListItemWrapper<VirtualFile>>() {
                override fun customize(
                    list: JList<out ListItemWrapper<VirtualFile>>,
                    value: ListItemWrapper<VirtualFile>?,
                    index: Int,
                    selected: Boolean,
                    hasFocus: Boolean
                ) {
                    icon = value?.icon
                    text = value?.text
                }
            }
        )
    }

    val selectedSourceRoot: VirtualFile
        get() = destinationSourceRootComboBox!!.getItemAt(destinationSourceRootComboBox!!.selectedIndex).item
    val selectedClassName: String
        get() = classNameTextField!!.text
    val selectedPackageName: String
        get() = packageComponent!!.text
    val isMakeExtensible: Boolean
        get() = makeExtensibleCheckBox!!.isSelected
    val isAn: Boolean
        get() = anRadioButton!!.isSelected
    val isGenerateInterface: Boolean
        get() = interfaceRadioButton!!.isSelected
    val superClassName: String?
        get() = if (extendsCheckBox!!.isSelected) {
            superClassChooser!!.text.trim { it <= ' ' }
        } else {
            null
        }
    val root: JComponent
        get() = rootPanel!!

    private fun hasVowelSound(matchedClassName: String): Boolean {
        return "aeiou".contains(matchedClassName.substring(0, 1).toLowerCase())
    }

    private fun createSourceRootItemWrapper(candidateRoot: VirtualFile): ListItemWrapper<VirtualFile> {
        val relativePath = calcRelativeToProjectPath(candidateRoot, dataSource.project, true, false, true)
        return ListItemWrapper(candidateRoot, relativePath, getSourceRootIcon(candidateRoot))
    }

    private fun createUIComponents() {
        // This is called by the form handler for custom-created components
        val project = dataSource.project

        packageComponent = PackageNameReferenceEditorCombo(
            dataSource.packageName,
            project,
            dataSource.recentsKey,
            "Choose Destination Package"
        )

        packageComponent!!.setTextFieldPreferredWidth(PANEL_WIDTH_CHARS)

        val scope = JavaProjectRootsUtil.getScopeWithoutGeneratedSources(ProjectScope.getProjectScope(project), project)
        superClassChooser = ClassNameReferenceEditor(project, null, scope)
        superClassChooser!!.setTextFieldPreferredWidth(PANEL_WIDTH_CHARS)
    }

    private fun getSourceRootIcon(virtualFile: VirtualFile): Icon {
        val fileIndex: FileIndex = ProjectRootManager.getInstance(dataSource.project).fileIndex

        return when {
            fileIndex.isInTestSourceContent(virtualFile) -> {
                PlatformIcons.MODULES_TEST_SOURCE_FOLDER
            }
            fileIndex.isInSourceContent(virtualFile) -> {
                PlatformIcons.MODULES_SOURCE_FOLDERS_ICON
            }
            else -> {
                PlatformIcons.FOLDER_ICON
            }
        }
    }

    fun doValidate(): ValidationInfo? {
        val className = classNameTextField!!.text.trim { it <= ' ' }

        return when {
            className.isEmpty() -> {
                ValidationInfo("Class name is empty", classNameTextField)
            }
            !PsiNameHelper.getInstance(dataSource.project).isIdentifier(className) -> {
                ValidationInfo("Class name is not a valid identifier", classNameTextField)
            }
            else -> null
        }
    }

    private val isSmogJavassistOnClasspath: Boolean
        get() {
            val project = dataSource.project
            val javaPsiFacade = JavaPsiFacade.getInstance(project)
            val generatorClass = javaPsiFacade.findClass(SMOG_JAVASSIST_GENERATOR, GlobalSearchScope.allScope(project))
            return generatorClass != null
        }

    private inner class ListItemWrapper<T> constructor(val item: T, val text: String, val icon: Icon)

    companion object {
        private const val PANEL_WIDTH_CHARS = 60
        private const val SMOG_JAVASSIST_GENERATOR = "com.mistraltech.smog.proxy.javassist.JavassistMatcherGenerator"
    }

    init {
        initialiseSourceClassNameField()
        initialiseGenerateTypeRadioButtons()
        initialiseClassNameField()
        initialiseMakeExtensibleCheckBox()
        initialiseExtendsFields()
        initialiseFactoryMethodPrefixRadioButtons()
        initialiseDestinationSourceRootComboBox()
    }
}
