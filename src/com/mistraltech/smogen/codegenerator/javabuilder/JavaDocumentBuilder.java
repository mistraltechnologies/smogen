package com.mistraltech.smogen.codegenerator.javabuilder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class JavaDocumentBuilder extends AbstractBuilder<JavaDocumentBuilder> {
    private String packageName;
    private List<JavaClassBuilder> classBuilderList = new ArrayList<JavaClassBuilder>();

    private JavaDocumentBuilder() {
    }

    public static JavaDocumentBuilder aJavaDocument() {
        return new JavaDocumentBuilder();
    }

    public JavaDocumentBuilder setPackageName(String packageName)
    {
        assert this.packageName == null;

        this.packageName = packageName;
        return this;
    }

    public JavaDocumentBuilder addClass(JavaClassBuilder classBuilder) {
        this.classBuilderList.add(classBuilder);
        addNestedBuilder(classBuilder);
        return this;
    }

    public String build() {
        StringBuilder sb = new StringBuilder();

        if (packageName != null)
        {
            sb.append(String.format("package %s;\n", packageName));
        }

        for (String importPath : getClassReferences()) {
            sb.append(String.format("import %s;\n", importPath));
        }

        for (String importPath : getClassMemberReferences()) {
            sb.append(String.format("import static %s;\n", importPath));
        }

        for (JavaClassBuilder classBuilder : classBuilderList) {
            sb.append(classBuilder.build());
        }

        return sb.toString();
    }
}
