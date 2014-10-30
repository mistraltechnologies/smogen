package com.mistraltech.smogen.codegenerator.javabuilder;

import java.util.List;

public final class BuilderUtils {

    private static String buildList(JavaBuilderContext context, String openBrace, List<?> elements, String closeBrace, String separator, boolean mandatory) {
        StringBuilder sb = new StringBuilder();

        if (elements.size() > 0 || mandatory) {
            sb.append(openBrace);
        }
        for (int i = 0; i < elements.size(); i++) {
            if (i > 0) {
                sb.append(separator);
            }
            sb.append(asString(elements.get(i), context));
        }

        if (elements.size() > 0 || mandatory) {
            sb.append(closeBrace);
        }

        return sb.toString();
    }

    public static String buildMandatoryList(JavaBuilderContext context, String openBrace, List<?> elements, String closeBrace, String separator) {
        return buildList(context, openBrace, elements, closeBrace, separator, true);
    }

    public static String buildList(JavaBuilderContext context, String openBrace, List<?> elements, String closeBrace, String separator) {
        return buildList(context, openBrace, elements, closeBrace, separator, false);
    }

    private static String asString(Object o, JavaBuilderContext context) {
        return (o instanceof Builder) ? ((Builder) o).build(context) : o.toString();
    }
}