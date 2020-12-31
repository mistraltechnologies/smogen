package com.mistraltech.smogen.codegenerator;

public class BasicMatcherSubclassTest extends AbstractGeneratorTest {
    public static final String TEST_NAME = "basic_subclass";

    public void testBasicMatcherSubclass() {
        doTest(TEST_NAME, defaultGeneratorProperties().setMatcherSuperClassName("BaseWidgetMatcher"));
    }

    @Override
    protected void preLoadClasses() {
        super.preLoadClasses();
        loadTestClassFromFile(TEST_NAME, "superclass.java");
    }
}
