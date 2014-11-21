package com.mistraltech.smogen.codegenerator;

public class BasicExtensibleMatcherTest extends AbstractGeneratorTest {
    public void testBasicExtensibleMatcher() {
        doTest("basic_extensible", generatorProperties().setExtensible(true));
    }
}
