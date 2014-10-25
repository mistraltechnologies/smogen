package com.mistraltech.smogen.property;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.util.PropertyUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class PropertyLocator {
    /**
     * Identifies the list of properties on the supplied class and optionally its superclasses.
     *
     * @param owner the class to scan for properties
     * @param includeSuperclasses whether to include properties declared in superclasses
     * @return the list of qualifying properties
     */
    @NotNull
    public static List<Property> locateProperties(@NotNull PsiClass owner, boolean includeSuperclasses) {

        Map<String, PsiMethod> propertyMap = PropertyUtil.getAllProperties(owner, false, true, includeSuperclasses);
        List<Property> matchedProperties = new ArrayList<Property>();
        for (PsiMethod method : propertyMap.values()) {
            matchedProperties.add(new Property(method));
        }

        return matchedProperties;
    }
}

