package com.mistraltech.smogen.codegenerator.javabuilder;

public interface JavaBuilderContext {
    String normaliseClassReference(String classFQN);

    String normaliseClassMemberReference(String memberFQN, String typeParams);
}
