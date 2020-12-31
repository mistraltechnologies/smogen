package com.mistraltech.smogen.codegenerator;

public class GenerateIntoExistingNonDefaultPackageTest extends AbstractGeneratorTest {
    public void testGenerateIntoExistingNonDefaultPackage() {
        createPackage("com.acme");
        doTest("non_default_package", defaultGeneratorProperties().setPackageName("com.acme"));
    }
}
