package com.mistraltech.smogen.codegenerator;

public class GenericSubclassTest extends AbstractGeneratorTest {

    public static final String TEST_NAME = "generic_subclass";

    public void testGenericSubclass() {
        doTest(TEST_NAME, defaultGeneratorProperties().setMatcherSuperClassName("BaseWidgetMatcher"));
    }

    @Override
    protected void preLoadClasses() {
        super.preLoadClasses();
        loadTestClassFromFile(TEST_NAME, "superclass.java");
    }
}
