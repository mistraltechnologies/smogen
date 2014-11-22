package com.mistraltech.smogen.codegenerator;

public class ExtensibleSubclassTest extends AbstractGeneratorTest {
    public void testExtensibleSubclass() {
        doTest("extensible_subclass", defaultGeneratorProperties().setMatcherSuperClassName("BaseWidgetMatcher").setExtensible(true));
    }
}
