package com.mistraltech.smogen.codegenerator;

public class BasicMatcherSubclassTest extends AbstractGeneratorTest {
    public void testBasicMatcherSubclass() {
        doTest("basic_subclass", generatorProperties().setMatcherSuperClassName("BaseWidgetMatcher"));
    }
}