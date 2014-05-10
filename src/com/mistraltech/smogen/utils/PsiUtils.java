package com.mistraltech.smogen.utils;

import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtilBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class PsiUtils {

    /**
     * Gets the visibility of an element from its modifier list owner.
     *
     * @param modifierListOwner the modifier list owner of the element
     * @return the visibility of the element
     */
    @NotNull
    public static Visibility getVisibility(@NotNull PsiModifierListOwner modifierListOwner) {
        PsiModifierList modifiers = modifierListOwner.getModifierList();
        if (modifiers == null) {
            return Visibility.PACKAGE_PRIVATE;
        } else if (modifiers.hasModifierProperty(PsiModifier.PRIVATE)) {
            return Visibility.PRIVATE;
        } else if (modifiers.hasModifierProperty(PsiModifier.PROTECTED)) {
            return Visibility.PROTECTED;
        } else if (modifiers.hasModifierProperty(PsiModifier.PUBLIC)) {
            return Visibility.PUBLIC;
        }

        throw new IllegalStateException("Can't determine visibility");
    }

    /**
     * For a given element, determines the class element that is the the nearest
     * enclosing class element, excluding type parameters and anonymous inner classes.
     *
     * @param psiElement the input element
     * @return the nearest enclosing qualifying class element
     */
    @Nullable
    public static PsiClass findClassFromElement(@NotNull PsiElement psiElement) {
        PsiClass containingClass = PsiTreeUtil.getParentOfType(psiElement, PsiClass.class, false);

        while (containingClass instanceof PsiTypeParameter || containingClass instanceof PsiAnonymousClass) {
            containingClass = PsiTreeUtil.getParentOfType(containingClass, PsiClass.class);
        }

        return containingClass;
    }

    /**
     * For a given file element, gets the enclosed class that is publicly accessible.
     *
     * @param file the file
     * @return the enclosed public class, or null if not found
     */
    @Nullable
    public static PsiClass getClassFromFile(@NotNull PsiFile file) {
        if (file instanceof PsiJavaFile) {
            PsiElement[] children = file.getChildren();
            for (PsiElement child : children) {
                if (child instanceof PsiClass && ((PsiClass) child).hasModifierProperty("public")) {
                    return (PsiClass) child;
                }
            }
        }
        return null;
    }

    /**
     * Gets the element currently active either because it is under the caret in the editor or otherwise
     * the current selection.
     *
     * @param dataContext the ide data context
     * @param project the current project
     * @param editor the current editor
     * @return the active element
     */
    @Nullable
    public static PsiElement getCurrentElement(@NotNull DataContext dataContext, @NotNull Project project, @Nullable Editor editor) {
        PsiElement psiElement = null;
        if (editor == null) {
            psiElement = CommonDataKeys.PSI_ELEMENT.getData(dataContext);
        } else {
            final PsiFile file = PsiUtilBase.getPsiFileInEditor(editor, project);
            if (file != null) {
                psiElement = file.findElementAt(editor.getCaretModel().getOffset());
            }
        }

        return psiElement;
    }

    /**
     * Searching from the supplied base directory, finds the directory that represents a given package name.
     *
     * @param baseDir the base directory
     * @param packageName the package name
     * @return the directory relating to the package, or null if the directory does not exist
     */
    @Nullable
    public static PsiDirectory findDirectoryForPackage(@NotNull PsiDirectory baseDir, @NotNull String packageName) {
        if (packageName.length() > 0) {
            String packageStart = NameUtils.getFirstPackage(packageName);
            String packageRemainder = NameUtils.dropFirstPackage(packageName);

            PsiDirectory newBaseDir = baseDir.findSubdirectory(packageStart);

            return (newBaseDir == null) ? null : findDirectoryForPackage(newBaseDir, packageRemainder);
        } else {
            return baseDir;
        }
    }

    /**
     * For a given base directory and package name, ensures that the related package directory exists.
     *
     * @param baseDir the base directory
     * @param packageName the package name
     */
    public static void createMissingDirectoriesForPackage(@NotNull PsiDirectory baseDir, @NotNull String packageName) {
        if (packageName.length() > 0) {
            String packageStart = NameUtils.getFirstPackage(packageName);
            String packageRemainder = NameUtils.dropFirstPackage(packageName);

            PsiDirectory newBaseDir = baseDir.findSubdirectory(packageStart);

            if (newBaseDir == null) {
                newBaseDir = baseDir.createSubdirectory(packageStart);
            }

            createMissingDirectoriesForPackage(newBaseDir, packageRemainder);
        }
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
}
