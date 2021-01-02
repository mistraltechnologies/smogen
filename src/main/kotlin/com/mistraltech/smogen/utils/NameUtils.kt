package com.mistraltech.smogen.utils

object NameUtils {
    /**
     * Convert the first character of the name to lowercase.
     *
     * @param name name to be converted
     * @return the name with the first character in lowercase
     */
    @JvmStatic
    fun deCapitalise(name: String): String {
        return if (name.isEmpty() || Character.isLowerCase(name[0])) {
            name
        } else Character.toLowerCase(name[0]).toString() + name.substring(1)
    }

    /**
     * From a fully-qualified name, get the unqualified name part.
     * E.g. from java.lang.String returns String
     *
     * @param fqn the fully-qualified name
     * @return the unqualified name
     */
    @JvmStatic
    fun getUnqualifiedName(fqn: String): String {
        val idx = fqn.lastIndexOf('.')
        return if (idx >= 0) fqn.substring(idx + 1) else fqn
    }

    /**
     * From a fully-qualified name, get the the name of the parent element.
     * E.g. from java.lang.String returns java.lang
     *
     * @param fqn the fully-qualified name
     * @return the parent element name
     */
    @JvmStatic
    fun dropUnqualifiedName(fqn: String): String {
        val idx = fqn.lastIndexOf('.')
        return if (idx > 0) fqn.substring(0, idx) else ""
    }

    /**
     * Construct a fully-qualified name from the parent element name and the unqualified name.
     *
     * @param path the fully-qualified name of the parent element
     * @param name the unqualified name
     * @return the fully-qualified name
     */
    @JvmStatic
    fun createFQN(path: String, name: String): String {
        return if (path.isEmpty()) name else "$path.$name"
    }
}
