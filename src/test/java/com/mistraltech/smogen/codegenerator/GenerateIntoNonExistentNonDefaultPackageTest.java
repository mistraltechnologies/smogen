package com.mistraltech.smogen.codegenerator;

public class GenerateIntoNonExistentNonDefaultPackageTest extends AbstractGeneratorTest {
    public void testGenerateIntoNonExistentNonDefaultPackage() {
        doTest("non_default_package", defaultGeneratorProperties().setPackageName("com.acme"));
    }
}
