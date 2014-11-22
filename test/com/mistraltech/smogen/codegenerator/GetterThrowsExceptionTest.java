package com.mistraltech.smogen.codegenerator;

public class GetterThrowsExceptionTest extends AbstractGeneratorTest {
    public void testGetterThrowsException() {
        doTest("getter_throws", defaultGeneratorProperties());
    }
}
