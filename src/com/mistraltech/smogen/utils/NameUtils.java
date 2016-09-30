package com.mistraltech.smogen.utils;

import org.jetbrains.annotations.NotNull;

import static java.lang.Character.toLowerCase;

public final class NameUtils {

    /**
     * Convert the first character of the name to lowercase.
     *
     * @param name name to be converted
     * @return the name with the first character in lowercase
     */
    @NotNull
    public static String deCapitalise(@NotNull String name) {
        if (name.isEmpty() || Character.isLowerCase(name.charAt(0))) {
            return name;
        }

        return toLowerCase(name.charAt(0)) + name.substring(1);
    }

    /**
     * From a fully-qualified name, get the unqualified name part.
     * E.g. from java.lang.String returns String
     *
     * @param fqn the fully-qualified name
     * @return the unqualified name
     */
    @NotNull
    public static String getUnqualifiedName(@NotNull String fqn) {
        int idx = fqn.lastIndexOf('.');
        return idx >= 0 ? fqn.substring(idx + 1) : fqn;
    }

    /**
     * From a fully-qualified name, get the the name of the parent element.
     * E.g. from java.lang.String returns java.lang
     *
     * @param fqn the fully-qualified name
     * @return the parent element name
     */
    @NotNull
    public static String dropUnqualifiedName(@NotNull String fqn) {
        int idx = fqn.lastIndexOf('.');
        return idx > 0 ? fqn.substring(0, idx) : "";
    }

    /**
     * Construct a fully-qualified name from the parent element name and the unqualified name.
     *
     * @param path the fully-qualified name of the parent element
     * @param name the unqualified name
     * @return the fully-qualified name
     */
    @NotNull
    public static String createFQN(@NotNull String path, @NotNull String name) {
        return path.isEmpty() ? name : path + "." + name;
    }
}
