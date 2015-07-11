package com.mistraltech.smogen.codegenerator;

public class ExtensibleGenericSubclassIFTest extends AbstractInterfaceGeneratorTest {
    public static final String TEST_NAME = "extensible_generic_subclass_if";

    public void testExtensibleGenericSubclass() {
        doTest(TEST_NAME, defaultGeneratorProperties().setMatcherSuperClassName("BaseWidgetMatcher").setExtensible(true));
    }

    @Override
    protected void preLoadClasses() {
        super.preLoadClasses();
        loadTestClassFromFile(TEST_NAME, "superclass.java");
    }
}
