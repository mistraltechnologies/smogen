package com.mistraltech.smogen.codegenerator;

public class GenerateIntoNonExistentNonDefaultPackageIFTest extends AbstractInterfaceGeneratorTest {
    public void testGenerateIntoNonExistentNonDefaultPackage() {
        doTest("non_default_package_if", defaultGeneratorProperties().setPackageName("com.acme"));
    }
}
