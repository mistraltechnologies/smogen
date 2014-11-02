package com.mistraltech.smogen.codegenerator.javabuilder;

import java.util.ArrayList;

import static com.mistraltech.smogen.codegenerator.javabuilder.BuilderUtils.buildList;

public class AnnotationBuilder extends AbstractBuilder<AnnotationBuilder> {
    private TypeBuilder type;
    private ArrayList<ExpressionTermBuilder<?>> parameters = new ArrayList<ExpressionTermBuilder<?>>();

    private AnnotationBuilder() {
    }

    public static AnnotationBuilder anAnnotation() {
        return new AnnotationBuilder();
    }

    public AnnotationBuilder withType(TypeBuilder type) {
        this.type = type;
        return this;
    }

    public AnnotationBuilder withParameter(ExpressionTermBuilder<?> param) {
        parameters.add(param);
        return this;
    }

    @Override
    public String build(JavaBuilderContext context) {
        StringBuilder sb = new StringBuilder();

        sb.append("@")
                .append(type.build(context))
                .append(buildList(context, "(", parameters, ")", ", "));

        return sb.toString();
    }
}
