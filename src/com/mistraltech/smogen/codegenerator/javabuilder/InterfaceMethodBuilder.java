package com.mistraltech.smogen.codegenerator.javabuilder;

public class InterfaceMethodBuilder extends MethodSignatureBuilder<InterfaceMethodBuilder> {
    private InterfaceMethodBuilder() {
    }

    public static InterfaceMethodBuilder anInterfaceMethod() {
        return new InterfaceMethodBuilder();
    }

    @Override
    public String build(JavaBuilderContext context) {
        return super.build(context) + ";";
    }
}
