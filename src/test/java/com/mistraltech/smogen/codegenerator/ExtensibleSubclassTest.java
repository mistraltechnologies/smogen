package com.mistraltech.smogen.codegenerator;

public class ExtensibleSubclassTest extends AbstractGeneratorTest {
    public static final String TEST_NAME = "extensible_subclass";

    public void testExtensibleSubclass() {
        doTest(TEST_NAME, defaultGeneratorProperties().setMatcherSuperClassName("BaseWidgetMatcher").setExtensible(true));
    }

    @Override
    protected void preLoadClasses() {
        super.preLoadClasses();
        loadTestClassFromFile(TEST_NAME, "superclass.java");
    }
}
