package com.mistraltech.smogen.property

import com.intellij.openapi.util.text.StringUtil
import com.intellij.psi.PsiMethod
import com.intellij.psi.PsiType
import com.intellij.psi.PsiTypeVisitor
import com.mistraltech.smogen.utils.NameUtils.deCapitalise

class Property(
    val name: String,
    private val type: PsiType,
    private val accessorMethod: PsiMethod?,
    private val mutatorMethod: PsiMethod?
) {

    /**
     * Gets a name that can be used for a field that represents the property.
     * This is the same as the property name, unless the property name begins
     * with a capital letter (the result of the property name starting with initials),
     * in which case the first letter is converted to lower case.
     *
     * @return the property name
     */
    val fieldName: String
        get() = deCapitalise(name)

    /**
     * Gets the name of the property, with initial letter capitalised (provided it was a capital letter
     * in the accessor method name).
     *
     * @return the property name
     */
    val capitalisedName: String
        get() = StringUtil.capitalize(name)

    /**
     * Gets the name of the accessor method.
     *
     * @return the accessor method name
     */
    val accessorName: String?
        get() = accessorMethod?.name

    /**
     * Gets the name of the mutator method.
     *
     * @return the mutator method name
     */
    val mutatorName: String?
        get() = mutatorMethod?.name

    /**
     * Gets the name of the property type (the return type of the accessor method).
     *
     * @return the name of the property type
     */
    fun getType(): String {
        return type.canonicalText
    }

    /**
     * Accept a visitor on the property type.
     *
     * @return the result returned by the visitor
     */
    fun <T> accept(visitor: PsiTypeVisitor<T>): T {
        return type.accept(visitor)
    }
}
