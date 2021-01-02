package com.mistraltech.smogen.property

import com.intellij.psi.PsiClass
import com.intellij.psi.PsiMethod
import com.intellij.psi.util.PropertyUtil

object PropertyLocator {
    /**
     * Identifies the list of properties on the supplied class and optionally its superclasses.
     * Properties are located through their setters, ensuring that all properties are writable.
     * Located properties may or may not also have getters.
     *
     * @param owner the class to be scanned for properties
     * @param includeSuperclasses whether to include properties declared in superclasses
     * @return the list of qualifying properties
     */
    fun locatePropertiesFromSetters(owner: PsiClass, includeSuperclasses: Boolean): List<Property> {
        val propertyMap: Map<String, PsiMethod> = PropertyUtil.getAllProperties(
            owner,
            true,
            false,
            includeSuperclasses
        )

        return propertyMap
            .toSortedMap()
            .map { entry ->
                Property(
                    entry.key,
                    PropertyUtil.getPropertyType(entry.value)!!,
                    PropertyUtil.findPropertyGetter(owner, entry.key, false, includeSuperclasses),
                    entry.value
                )
            }
    }

    /**
     * Identifies the list of properties on the supplied class and optionally its superclasses.
     * Properties are located through their getters, ensuring that all properties are readable.
     * Located properties may or may not also have setters.
     *
     * @param owner the class to be scanned for properties
     * @param includeSuperclasses whether to include properties declared in superclasses
     * @return the list of qualifying properties
     */
    @JvmStatic
    fun locatePropertiesFromGetters(owner: PsiClass, includeSuperclasses: Boolean): List<Property> {
        val propertyMap: Map<String, PsiMethod> = PropertyUtil.getAllProperties(
            owner,
            false,
            true,
            includeSuperclasses
        )

        return propertyMap
            .toSortedMap()
            .map { entry ->
                Property(
                    entry.key,
                    PropertyUtil.getPropertyType(entry.value)!!,
                    entry.value,
                    PropertyUtil.findPropertySetter(owner, entry.key, false, includeSuperclasses),
                )
            }
    }
}
