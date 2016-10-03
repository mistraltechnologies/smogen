package com.mistraltech.smogen.codegenerator.javabuilder;

import java.util.ArrayList;
import java.util.List;

public class JavaDocumentBuilder {
    private String packageName;
    private List<AbstractTypeBuilder> typeBuilderList = new ArrayList<>();

    private JavaDocumentBuilder() {
    }

    public static JavaDocumentBuilder aJavaDocument() {
        return new JavaDocumentBuilder();
    }

    public JavaDocumentBuilder setPackageName(String packageName) {
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

        typeBuilderList.forEach(tb -> sbBody.append(tb.build(context)));

        StringBuilder sb = new StringBuilder();

        if (packageName != null && !packageName.isEmpty()) {
            sb.append(String.format("package %s;\n", packageName));
        }

        context.getClassReferences().forEach(r -> sb.append(String.format("import %s;\n", r)));
        context.getClassMemberReferences().forEach(r -> sb.append(String.format("import static %s;\n", r)));

        sb.append(sbBody);

        return sb.toString();
    }
}
