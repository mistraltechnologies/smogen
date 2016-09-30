package com.mistraltech.smogen.utils;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiAnonymousClass;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiModifier;
import com.intellij.psi.PsiTypeParameter;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtilBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Stream;

import static com.mistraltech.smogen.utils.JavaUtils.optionalToStream;

public final class PsiUtils {

    /**
     * For a given element, determines the class element that is the the nearest
     * enclosing class element, excluding type parameters and anonymous inner classes.
     *
     * @param psiElement the input element
     * @return the nearest enclosing qualifying class element
     */
    @SuppressWarnings("WeakerAccess")
    @NotNull
    public static Optional<PsiClass> findClassFromElement(@NotNull PsiElement psiElement) {
        PsiClass containingClass = PsiTreeUtil.getParentOfType(psiElement, PsiClass.class, false);

        while (containingClass instanceof PsiTypeParameter || containingClass instanceof PsiAnonymousClass) {
            containingClass = PsiTreeUtil.getParentOfType(containingClass, PsiClass.class);
        }

        return Optional.ofNullable(containingClass);
    }

    /**
     * For a given file element, gets the enclosed class that is publicly accessible.
     *
     * @param file the file
     * @return the enclosed public class if found
     */
    @NotNull
    public static Optional<PsiClass> getPublicClassFromFile(@NotNull PsiFile file) {
        Optional<PsiFile> javaFile = Optional.of(file)
                .filter(f -> f instanceof PsiJavaFile);

        Stream<PsiClass> childClasses = optionalToStream(javaFile)
                .flatMap(f -> Stream.of(f.getChildren()))
                .filter(c -> c instanceof PsiClass)
                .map(c -> (PsiClass) c);

        Optional<PsiClass> publicClass = childClasses
                .filter(c -> c.hasModifierProperty("public"))
                .findFirst();

        return publicClass;
    }

    /**
     * Searching from the supplied base directory, finds the directory that represents a given package name.
     *
     * @param baseDir the base directory
     * @param packageName the package name
     * @return the directory relating to the package, or null if the directory does not exist
     */
    @Nullable
    public static Optional<PsiDirectory> findDirectoryForPackage(@NotNull PsiDirectory baseDir, @NotNull String packageName) {
        return processPackagesOptional(baseDir, PsiUtils::findSubdirectory, getPackageAsComponents(packageName));
    }

    /**
     * For a given base directory and package name, ensures that the related package directory exists.
     *
     * @param baseDir the base directory
     * @param packageName the package name
     * @return the directory relating to the package
     */
    public static PsiDirectory createMissingDirectoriesForPackage(@NotNull PsiDirectory baseDir, @NotNull String packageName) {
        return processPackages(baseDir, PsiUtils::findOrCreateSubdirectory, getPackageAsComponents(packageName));
    }

    @NotNull
    private static Optional<PsiDirectory> findSubdirectory(@NotNull PsiDirectory baseDir, @NotNull String name) {
        return Optional.ofNullable(baseDir.findSubdirectory(name));
    }

    @NotNull
    private static PsiDirectory findOrCreateSubdirectory(PsiDirectory baseDir, String name) {
        return findSubdirectory(baseDir, name).orElseGet(() -> baseDir.createSubdirectory(name));
    }

    @NotNull
    private static PsiDirectory processPackages(@NotNull PsiDirectory baseDir,
            @NotNull BiFunction<PsiDirectory, String, PsiDirectory> function, String... packageNames) {
        if (packageNames.length == 0) {
            return baseDir;
        }

        String packageNamesHead = packageNames[0];
        String[] packageNamesTail = Arrays.copyOfRange(packageNames, 1, packageNames.length);

        PsiDirectory subdirectory = function.apply(baseDir, packageNamesHead);

        return processPackages(subdirectory, function, packageNamesTail);
    }

    @NotNull
    private static Optional<PsiDirectory> processPackagesOptional(@NotNull PsiDirectory baseDir,
            @NotNull BiFunction<PsiDirectory, String, Optional<PsiDirectory>> function, @NotNull String... packageNames) {
        if (packageNames.length == 0) {
            return Optional.of(baseDir);
        }

        String packageNamesHead = packageNames[0];
        String[] packageNamesTail = Arrays.copyOfRange(packageNames, 1, packageNames.length);

        Optional<PsiDirectory> subdirectory = function.apply(baseDir, packageNamesHead);

        return subdirectory.flatMap(d -> processPackagesOptional(d, function, packageNamesTail));
    }

    private static String[] getPackageAsComponents(String packageName) {
        if (packageName.isEmpty()) {
            return new String[0];
        }

        return packageName.split("\\.");
    }

    /**
     * Determine if the supplied class is abstract.
     *
     * @param psiClass the class
     * @return true if psiClass is abstract; false otherwise
     */
    public static boolean isAbstract(PsiClass psiClass) {
        return psiClass.hasModifierProperty(PsiModifier.ABSTRACT);
    }

    /**
     * Gets the selected class for the event if available.
     * <p>
     * If there is a current editor and the caret is over a class, uses that class.
     * Else if there is a selected element in the data context that can be mapped to a class, uses that class.
     * Else attempts to get a public class from the current selected file.
     *
     * @param event an event that has been triggered
     * @return optionally, the most appropriate class selection for that event
     */
    @NotNull
    public static Optional<PsiClass> getSelectedClass(@NotNull AnActionEvent event) {
        final PsiFile file = ActionEventUtils.getTargetFile(event);
        final Project project = ActionEventUtils.getTargetProject(event);
        final Optional<Editor> editor = ActionEventUtils.getOptionalTargetEditor(event);

        Optional<PsiClass> targetClass = editor.flatMap(e -> getSelectedClassFromEditor(project, e));

        if (!targetClass.isPresent()) {
            targetClass = getSelectedElement(event.getDataContext())
                    .flatMap(PsiUtils::findClassFromElement);
        }

        if (!targetClass.isPresent()) {
            targetClass = getPublicClassFromFile(file);
        }

        return targetClass;
    }

    /**
     * Gets the class that wraps the element under the caret in the editor, if available.
     *
     * @param project the project
     * @param editor the editor
     * @return optionally, the class containing the element under the caret
     */
    @NotNull
    private static Optional<PsiClass> getSelectedClassFromEditor(@NotNull Project project, @NotNull Editor editor) {
        return getCurrentElementInEditor(project, editor)
                .flatMap(PsiUtils::findClassFromElement);
    }

    /**
     * Gets the element under the caret of the given editor, if available.
     *
     * @param project the project
     * @param editor the editor
     * @return optionally, the element under the caret
     */
    @NotNull
    private static Optional<PsiElement> getCurrentElementInEditor(@NotNull Project project, @NotNull Editor editor) {
        return Optional.ofNullable(PsiUtilBase.getPsiFileInEditor(editor, project))
                .map(f -> f.findElementAt(editor.getCaretModel().getOffset()));
    }

    /**
     * Gets the currently selected element, if available.
     *
     * @param dataContext data context
     * @return optionally, the currently selected element
     */
    @NotNull
    private static Optional<PsiElement> getSelectedElement(@NotNull DataContext dataContext) {
        return Optional.ofNullable(CommonDataKeys.PSI_ELEMENT.getData(dataContext));
    }
}
