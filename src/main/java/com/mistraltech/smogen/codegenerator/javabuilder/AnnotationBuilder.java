package com.mistraltech.smogen.codegenerator.javabuilder;

import java.util.ArrayList;
import java.util.List;

import static com.mistraltech.smogen.codegenerator.javabuilder.BuilderUtils.buildList;

public class AnnotationBuilder extends AbstractBuilder<AnnotationBuilder> {
    private final List<ExpressionTermBuilder<?>> parameters = new ArrayList<>();
    private TypeBuilder type;

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
        return "@" +
                type.build(context) +
                buildList(context, "(", parameters, ")", ", ");
    }
}
