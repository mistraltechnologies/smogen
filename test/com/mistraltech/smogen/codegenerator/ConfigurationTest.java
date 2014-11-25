package com.mistraltech.smogen.codegenerator;

import com.google.common.collect.Lists;
import com.intellij.psi.PsiClass;
import com.mistraltech.smogen.codegenerator.matchergenerator.MatcherGenerator;
import com.mistraltech.smogen.codegenerator.matchergenerator.MatcherGeneratorProperties;

import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;

public class ConfigurationTest extends AbstractGeneratorTest {
    private static final String TEST_NAME = "configuration";
    private MatcherGeneratorProperties generatorProperties;

    private void setupTestFixture() {
        preLoadClasses();

        final PsiClass sourceClass = loadTestClassFromFile(TEST_NAME, "input.java");

        generatorProperties = super.defaultGeneratorProperties()
                .setSourceRoot(getSourceRoot())
                .setSourceClass(sourceClass);
    }

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
}
