package com.mistraltech.smogen.codegenerator;

public class ConfigurationIFTest extends AbstractInterfaceGeneratorTest {
    private static final String TEST_NAME = "configuration_if";

    public void testBaseClass() {
        loadTestClassFromText("com/mistraltech/smog/core/LoggingCompositePropertyMatcher.java",
                "package com.mistraltech.smog.core;\n" +
                        "public class LoggingCompositePropertyMatcher<T> {}\n");

        doTest(TEST_NAME, "input.java", "base_class.java", defaultGeneratorProperties()
                .setBaseClassName("com.mistraltech.smog.core.LoggingCompositePropertyMatcher"));
    }

    public void testFactoryMethodPrefix() {
        doTest(TEST_NAME, "input.java", "factory_method_prefix.java", defaultGeneratorProperties()
                .setFactoryMethodPrefix("abc")
                .setFactoryMethodSuffix("")
                .setTemplateFactoryMethodSuffix(""));
    }

    public void testFactoryMethodSuffix() {
        doTest(TEST_NAME, "input.java", "factory_method_suffix.java", defaultGeneratorProperties()
                .setFactoryMethodPrefix("")
                .setFactoryMethodSuffix("abc")
                .setTemplateFactoryMethodSuffix("def"));
    }

    public void testSetterPrefix() {
        doTest(TEST_NAME, "input.java", "setter_prefix.java", defaultGeneratorProperties()
                .setSetterPrefix("abc")
                .setSetterSuffix(""));
    }

    public void testSetterSuffix() {
        doTest(TEST_NAME, "input.java", "setter_suffix.java", defaultGeneratorProperties()
                .setSetterPrefix("")
                .setSetterSuffix("abc"));
    }

    public void testUseReflectingPropertyMatcher() {
        doTest(TEST_NAME, "input.java", "no_reflecting_property_matcher.java", defaultGeneratorProperties()
                .setUseReflectingPropertyMatcher(false));
    }

    public void testGenerateTemplateFactoryMethod() {
        doTest(TEST_NAME, "input.java", "no_template_factory_method.java", defaultGeneratorProperties()
                .setGenerateTemplateFactoryMethod(false));
    }

    public void testGenerateTemplateFactoryMethodSubclass() {
        loadTestClassFromFile(TEST_NAME, "superclass.java");
        doTest(TEST_NAME, "input.java", "no_template_factory_method_subclass.java", defaultGeneratorProperties()
                .setGenerateTemplateFactoryMethod(false)
                .setMatcherSuperClassName("BaseWidgetMatcher"));
    }

    public void testGenerateTemplateFactoryMethodExtensible() {
        loadTestClassFromFile(TEST_NAME, "superclass.java");
        doTest(TEST_NAME, "input.java", "no_template_factory_method_extensible.java", defaultGeneratorProperties()
                .setGenerateTemplateFactoryMethod(false)
                .setExtensible(true));
    }

    public void testMethodParametersFinal() {
        doTest(TEST_NAME, "input.java", "non_final_parameters.java", defaultGeneratorProperties()
                .setMakeMethodParametersFinal(false));
    }
}
