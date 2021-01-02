package com.mistraltech.smogen.codegenerator;

public class GenericExtensibleIFTest extends AbstractInterfaceGeneratorTest {
    public void testGenericExtensible() {
        doTest("generic_extensible_if", defaultGeneratorProperties().setExtensible(true));
    }
}
