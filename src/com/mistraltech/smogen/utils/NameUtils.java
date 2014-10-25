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

        return String.valueOf(toLowerCase(name.charAt(0))) + (name.length() > 1 ? name.substring(1) : "");
    }

    /**
     * Strip off any of a list of prefixes and return the result. Only the first matching prefix is removed. It may be
     * that no prefix is stripped if none of the prefixes matches.
     *
     * @param name the input name
     * @param prefixes list of prefixes to strip off
     * @return the resulting name
     */
    @NotNull
    public static String removePrefix(@NotNull String name, String... prefixes) {
        for (String prefix : prefixes) {
            if (name.startsWith(prefix) && name.length() > prefix.length()) {
                return name.substring(prefix.length());
            }
        }

        return name;
    }

    /**
     * Prepends the prefix to the name.
     *
     * @param name the input name
     * @param prefix the prefix to prepend
     * @return the resulting name
     */
    @NotNull
    public static String addPrefix(@NotNull String name, @NotNull String prefix) {
        return prefix + name;
    }

    /**
     * Gets the first component of a package name.
     *
     * @param packageName the package name
     * @return the first component
     */
    @NotNull
    public static String getFirstPackage(@NotNull String packageName) {
        int idx = packageName.indexOf('.');
        return idx >= 0 ? packageName.substring(0, idx) : packageName;
    }

    /**
     * Gets the remainder of a package name after the first component has been removed.
     *
     * @param packageName the package name
     * @return the remainder after dropping the first component
     */
    @NotNull
    public static String dropFirstPackage(@NotNull String packageName) {
        int idx = packageName.indexOf('.');
        return idx >= 0 ? packageName.substring(idx + 1) : "";
    }
}
