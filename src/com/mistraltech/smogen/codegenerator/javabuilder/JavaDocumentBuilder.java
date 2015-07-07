package com.mistraltech.smogen.codegenerator.javabuilder;

import java.util.ArrayList;
import java.util.List;

public class JavaDocumentBuilder {
    private String packageName;
    private List<AbstractTypeBuilder> typeBuilderList = new ArrayList<AbstractTypeBuilder>();

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
        this.typeBuilderList.add(classBuilder);
        return this;
    }

    public JavaDocumentBuilder addInterface(InterfaceBuilder interfaceBuilder) {
        this.typeBuilderList.add(interfaceBuilder);
        return this;
    }

    public String build() {
        JavaBuilderContextImpl context = new JavaBuilderContextImpl();
        StringBuilder sbBody = new StringBuilder();

        for (AbstractTypeBuilder typeBuilder : typeBuilderList) {
            sbBody.append(typeBuilder.build(context));
        }

        StringBuilder sb = new StringBuilder();

        if (packageName != null && !packageName.isEmpty()) {
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
