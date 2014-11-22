package com.mistraltech.smogen.codegenerator;

public class GenericSubclassTest extends AbstractGeneratorTest {
    public void testGenericSubclass() {
        doTest("generic_subclass", defaultGeneratorProperties().setMatcherSuperClassName("BaseWidgetMatcher"));
    }
}
