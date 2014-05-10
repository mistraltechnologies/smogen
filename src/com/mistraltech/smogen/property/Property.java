package com.mistraltech.smogen.property;

import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiPrimitiveType;
import com.intellij.psi.PsiType;
import com.mistraltech.smogen.utils.NameUtils;
import org.jetbrains.annotations.NotNull;

public class Property {
    private static String[] ACCESSOR_PREFIXES = {"get", "is"};

    private final PsiMethod accessorMethod;
    private final PsiType returnType;
    private String name;
    private String nameCapitalised;

    /**
     * Construct a property from its accessor method.
     *
     * @param accessorMethod the accessor method (e.g. getFoo or isBar)
     */
    public Property(@NotNull PsiMethod accessorMethod) {
        assert accessorMethod.getReturnType() != null;

        this.returnType = accessorMethod.getReturnType();
        this.accessorMethod = accessorMethod;
        this.name = NameUtils.removeNamePrefix(accessorMethod.getName(), ACCESSOR_PREFIXES);
        this.nameCapitalised = NameUtils.removePrefix(accessorMethod.getName(), ACCESSOR_PREFIXES);
    }

    /**
     * Gets the name of the property with the initial letter in lower case.
     *
     * @return the property name
     */
    @NotNull
    public String getName() {
        return name;
    }

    /**
     * Gets the name of the property, with initial letter capitalised (provided it was a capital letter
     * in the accessor method name).
     *
     * @return the property name
     */
    @NotNull
    public String getNameCapitalised() {
        return nameCapitalised;
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
        return returnType.getCanonicalText();
    }

    /**
     * Gets the type of the property, in boxed form if the property type is a primitive.
     *
     * @return the property type
     */
    @NotNull
    public String getBoxedType() {
        if (accessorMethod.getReturnType() instanceof PsiPrimitiveType) {
            return ((PsiPrimitiveType) accessorMethod.getReturnType()).getBoxedTypeName();
        } else {
            return returnType.getCanonicalText();
        }
    }

    /**
     * Determines whether a given method is a property accessor, based on return type, parameters and name.
     *
     * @param method the method to test
     * @return true if the method is a property accessor; false otherwise
     */
    public static boolean isAccessor(@NotNull PsiMethod method) {
        return !returnsVoid(method) && !takesParameters(method) && hasAccessorNamePrefix(method);
    }

    private static boolean hasAccessorNamePrefix(@NotNull PsiMethod method) {
        for (String prefix : ACCESSOR_PREFIXES) {
            if (method.getName().startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }

    private static boolean takesParameters(@NotNull PsiMethod method) {
        return method.getParameterList().getParametersCount() > 0;
    }

    private static boolean returnsVoid(@NotNull PsiMethod method) {
        return PsiPrimitiveType.VOID.equals(method.getReturnType());
    }
}
