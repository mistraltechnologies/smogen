package com.mistraltech.smogen;

import com.mistraltech.smogen.utils.PsiUtils;

public class GeneratorTest extends AbstractGeneratorTest {

    public void testMinimalClass() {
        doTest("minimal_class", generatorProperties());
    }

    public void testSimpleProperty() {
        doTest("simple_property", generatorProperties());
    }

    public void testBasicExtensibleMatcher() {
        doTest("basic_extensible", generatorProperties().setExtensible(true));
    }

    public void testGenerateIntoNonExistentNonDefaultPackage() {
        doTest("non_default_package", generatorProperties().setPackageName("com.acme"));
    }

    public void testGenerateIntoExistingNonDefaultPackage() {
        createPackage("com.acme");
        doTest("non_default_package", generatorProperties().setPackageName("com.acme"));
    }

    public void testPropertyNames() {
        doTest("property_names", generatorProperties());
    }
}
