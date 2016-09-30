package com.mistraltech.smogen.property;

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiPrimitiveType;
import com.intellij.psi.PsiType;
import com.intellij.psi.PsiTypeVisitor;
import com.intellij.psi.util.PropertyUtil;
import com.mistraltech.smogen.utils.NameUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Property {
    private final PsiMethod accessorMethod;
    private final PsiType type;
    private String name;

    /**
     * Construct a property from its accessor method.
     *
     * @param accessorMethod the accessor method (e.g. getFoo or isBar)
     */
    Property(@NotNull PsiMethod accessorMethod) {
        if (accessorMethod.getReturnType() == null) {
            throw new IllegalArgumentException("Property accessor can't be void");
        }

        this.type = accessorMethod.getReturnType();
        this.accessorMethod = accessorMethod;
        this.name = PropertyUtil.getPropertyName(accessorMethod);
    }

    /**
     * Gets the name of the property.
     *
     * @return the property name
     */
    @NotNull
    public String getName() {
        return name;
    }

    /**
     * Gets a name that can be used for a field that represents the property.
     * This is the same as the property name, unless the property name begins
     * with a capital letter (the result of the property name starting with initials),
     * in which case the first letter is converted to lower case.
     *
     * @return the property name
     */
    @NotNull
    public String getFieldName() {
        return NameUtils.deCapitalise(name);
    }

    /**
     * Gets the name of the property, with initial letter capitalised (provided it was a capital letter
     * in the accessor method name).
     *
     * @return the property name
     */
    @NotNull
    public String getCapitalisedName() {
        return StringUtil.capitalize(name);
    }

    /**
     * Gets the name of the accessor method.
     *
     * @return the accessor method name
     */
    @NotNull
    public String getAccessorName() {
        return accessorMethod.getName();
    }

    /**
     * Gets the name of the property type (the return type of the accessor method).
     *
     * @return the name of the property type
     */
    @NotNull
    public String getType() {
        return type.getCanonicalText();
    }

    /**
     * Accept a visitor on the property type.
     *
     * @return the result returned by the visitor
     */
    @Nullable
    public <T> T accept(PsiTypeVisitor<T> visitor) {
        return type.accept(visitor);
    }
}
