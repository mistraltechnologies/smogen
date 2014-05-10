package com.mistraltech.smogen.property;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifierListOwner;
import com.mistraltech.smogen.utils.PsiUtils;
import com.mistraltech.smogen.utils.Visibility;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public final class PropertyLocator {
    private static Set IGNORED_METHODS = new HashSet<String>(Arrays.asList("getClass"));

    /**
     * Identifies the list of properties on the supplied class and optionally its superclasses.
     *
     * @param owner the class to scan for properties
     * @param includeSuperclasses whether to include properties declared in superclasses
     * @param minVisibility the minimum acceptable visibility of the property for it to be included
     * @return the list of qualifying properties
     */
    @NotNull
    public static List<Property> locateProperties(@NotNull PsiClass owner, boolean includeSuperclasses, @NotNull Visibility minVisibility) {
        List<Property> matchedProperties = new ArrayList<Property>();

        PsiMethod[] methods = includeSuperclasses ? owner.getAllMethods() : owner.getMethods();
        for (PsiMethod method : methods) {
            if (Property.isAccessor(method) && hasMinVisibility(method, minVisibility) && !isIgnorable(method)) {
                matchedProperties.add(new Property(method));
            }
        }

        return matchedProperties;
    }

    private static boolean isIgnorable(PsiMethod method) {
        return IGNORED_METHODS.contains(method.getName());
    }

    private static boolean hasMinVisibility(PsiModifierListOwner element, Visibility minVisibility) {
        return PsiUtils.getVisibility(element).getRank() >= minVisibility.getRank();
    }
}

