package com.mistraltech.smogen.codegenerator;

public class GetterThrowsExceptionIFTest extends AbstractInterfaceGeneratorTest {
    public void testGetterThrowsException() {
        doTest("getter_throws_if", defaultGeneratorProperties());
    }
}
