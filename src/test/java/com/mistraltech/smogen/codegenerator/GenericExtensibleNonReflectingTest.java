package com.mistraltech.smogen.codegenerator;

public class GenericExtensibleNonReflectingTest extends AbstractGeneratorTest {
    public void testGenericExtensibleNonReflecting() {
        doTest("generic_extensible_nonreflecting", defaultGeneratorProperties()
                .setExtensible(true)
                .setUseReflectingPropertyMatcher(false));
    }
}
