package com.mistraltech.smogen.utils;

public final class GeneratorUtils {
    public static void addImport(StringBuilder documentText, String path) {
        documentText.append(String.format("import %s;\n", path));
    }

    public static void addStaticImport(StringBuilder documentText, String path) {
        documentText.append(String.format("import static %s;\n", path));
    }

    public static void addPackageStatement(StringBuilder documentText, String qualifiedName) {
        documentText.append(String.format("package %s;\n", qualifiedName));
    }
}
