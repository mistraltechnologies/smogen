package com.mistraltech.smogen.codegenerator;

public class GenericExtensibleTest extends AbstractGeneratorTest {
    public void testGenericExtensible() {
        doTest("generic_extensible", defaultGeneratorProperties().setExtensible(true));
    }
}
