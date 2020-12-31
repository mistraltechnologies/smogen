package com.mistraltech.smogen.codegenerator;

public class BasicExtensibleMatcherIFTest extends AbstractInterfaceGeneratorTest {
    public void testBasicExtensibleMatcher() {
        doTest("basic_extensible_if", defaultGeneratorProperties().setExtensible(true));
    }
}
