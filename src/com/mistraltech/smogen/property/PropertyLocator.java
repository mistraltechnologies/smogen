package com.mistraltech.smogen.property;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.util.PropertyUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

public final class PropertyLocator {
    /**
     * Identifies the list of properties on the supplied class and optionally its superclasses.
     *
     * @param owner the class to be scanned for properties
     * @param includeSuperclasses whether to include properties declared in superclasses
     * @return the list of qualifying properties
     */
    @NotNull
    public static List<Property> locateProperties(@NotNull PsiClass owner, boolean includeSuperclasses) {

        Map<String, PsiMethod> propertyMap = PropertyUtil.getAllProperties(owner,
                false,  // Don't accept setters
                true,   // Do accept getters
                includeSuperclasses);

        List<Property> matchedProperties = propertyMap
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .map(Map.Entry::getValue)
                .map(Property::new)
                .collect(toList());

        return matchedProperties;
    }
}

