package com.mistraltech.smogen.codegenerator;

public class ExtensibleGenericSubclassTest extends AbstractGeneratorTest {
    public void testExtensibleGenericSubclass() {
        doTest("extensible_generic_subclass", defaultGeneratorProperties().setMatcherSuperClassName("BaseWidgetMatcher").setExtensible(true));
    }
}
