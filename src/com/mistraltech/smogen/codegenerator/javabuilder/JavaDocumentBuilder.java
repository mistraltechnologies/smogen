package com.mistraltech.smogen.codegenerator.javabuilder;

import java.util.ArrayList;
import java.util.List;

public class JavaDocumentBuilder {
    private String packageName;
    private List<AbstractClassBuilder> classBuilderList = new ArrayList<AbstractClassBuilder>();

    private JavaDocumentBuilder() {
    }

    public static JavaDocumentBuilder aJavaDocument() {
        return new JavaDocumentBuilder();
    }

    public JavaDocumentBuilder setPackageName(String packageName) {
        assert this.packageName == null;

        this.packageName = packageName;
        return this;
    }

    public JavaDocumentBuilder addClass(AbstractClassBuilder classBuilder) {
        this.classBuilderList.add(classBuilder);
        return this;
    }

    public String build() {
        JavaBuilderContextImpl context = new JavaBuilderContextImpl();
        StringBuilder sbBody = new StringBuilder();

        for (AbstractClassBuilder classBuilder : classBuilderList) {
            sbBody.append(classBuilder.build(context));
        }

        StringBuilder sb = new StringBuilder();

        if (packageName != null) {
            sb.append(String.format("package %s;\n", packageName));
        }

        for (String importPath : context.getClassReferences()) {
            sb.append(String.format("import %s;\n", importPath));
        }

        for (String importPath : context.getClassMemberReferences()) {
            sb.append(String.format("import static %s;\n", importPath));
        }

        sb.append(sbBody);

        return sb.toString();
    }
}
