package com.mistraltech.smogen;

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

    public void testPropertyTypes() {
        doTest("property_types", generatorProperties());
    }

    public void testGetterThrowsException() {
        doTest("getter_throws", generatorProperties());
    }

    public void testBasicGeneric() {
        doTest("basic_generic", generatorProperties());
    }

    public void testGenericExtensible() {
        doTest("generic_extensible", generatorProperties().setExtensible(true));
    }

    public void testLeafInClassHierarchy() {
        doTest("leaf_in_class_hierarchy", generatorProperties());
    }

    public void testBasicMatcherSubclass() {
        doTest("basic_subclass", generatorProperties().setMatcherSuperClassName("BaseWidgetMatcher"));
    }

    public void testExtensibleSubclass() {
        doTest("extensible_subclass", generatorProperties().setMatcherSuperClassName("BaseWidgetMatcher").setExtensible(true));
    }

    public void testGenericSubclass() {
        doTest("generic_subclass", generatorProperties().setMatcherSuperClassName("BaseWidgetMatcher"));
    }
}
