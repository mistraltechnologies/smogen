package com.mistraltech.smogen.codegenerator.matchergenerator

import com.intellij.openapi.project.Project
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiModifier
import com.intellij.psi.PsiModifierList
import com.intellij.psi.search.GlobalSearchScope
import com.mistraltech.smogen.codegenerator.Generator

open class MatcherGenerator(private val generatorProperties: MatcherGeneratorProperties) :
    Generator(generatorProperties) {

    override fun checkProperties(): List<String> {
        val warnings: MutableList<String> = super.checkProperties().toMutableList()
        checkSuperType(warnings)
        return warnings
    }

    private fun checkSuperType(warnings: MutableList<String>) {
        val project: Project = generatorProperties.project!!

        val baseClassName: String =
            if (generatorProperties.isGenerateInterface) "org.hamcrest.Matcher" else generatorProperties.baseClassName!!

        val superClassName = generatorProperties.matcherSuperClassName ?: baseClassName

        val superClass: PsiClass? = findPsiClass(project, superClassName)

        if (superClass == null) {
            warnings.add("Supertype '$superClassName' was not found in the project.")
        } else {
            checkSuperClassModifiers(superClass, warnings)
            checkSuperClassInheritance(superClass, baseClassName, warnings)
        }
    }

    private fun findPsiClass(project: Project, className: String): PsiClass? {
        return JavaPsiFacade.getInstance(project).findClass(className, GlobalSearchScope.allScope(project))
    }

    private fun checkSuperClassModifiers(superClass: PsiClass, warnings: MutableList<String>) {
        val modifierList: PsiModifierList? = superClass.modifierList

        if (modifierList != null && modifierList.hasExplicitModifier(PsiModifier.FINAL)) {
            warnings.add("Supertype '" + superClass.qualifiedName + "' is final.")
        }
    }

    private fun checkSuperClassInheritance(
        superClass: PsiClass,
        baseClassName: String,
        warnings: MutableList<String>
    ) {
        val project: Project = generatorProperties.project!!

        val baseClass: PsiClass? = findPsiClass(project, baseClassName)

        if (baseClass == null) {
            warnings.add("Could not find base type '$baseClassName' in the project.")
        } else if (baseClass != superClass) {
            // check superclass extends baseclass
            val hasBaseClassInHierarchy = superClass.superTypes.mapNotNull { it.resolve() }.contains(baseClass)

            if (!hasBaseClassInHierarchy) {
                warnings.add("Supertype '${superClass.qualifiedName}' does not extend '${baseClass.qualifiedName}'.")
            }
        }
    }
}
