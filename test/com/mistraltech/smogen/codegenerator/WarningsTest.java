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

public class WarningsTest extends AbstractGeneratorTest {
    private static final String TEST_NAME = "warnings";
    private MatcherGeneratorProperties generatorProperties;

    private void setupTestFixture() {
        preLoadClasses();

        final PsiClass sourceClass = loadTestClassFromFile(TEST_NAME, "input.java");

        generatorProperties = super.defaultGeneratorProperties()
                .setSourceRoot(getSourceRoot())
                .setSourceClass(sourceClass);
    }

    public void testSuperclassDoesNotExistWarning() {
        setupTestFixture();

        generatorProperties.setMatcherSuperClassName("NoSuchClass");

        final Generator generator = new MatcherGenerator(generatorProperties);

        try {
            generator.generate();
            fail("Expected runtime exception caused by a call to show a dialog");
        } catch (RuntimeException r) {
            assertThat(r.getMessage(), is(equalTo(
                    expectedWarningMessage("Superclass " + generatorProperties.getMatcherSuperClassName() + " was not found in the project."))));
        }
    }

    public void testSuperclassFinalWarning() {
        setupTestFixture();
        loadTestClassFromFile(TEST_NAME, "final_superclass.java");

        generatorProperties.setMatcherSuperClassName("SuperClass");

        final Generator generator = new MatcherGenerator(generatorProperties);

        try {
            generator.generate();
            fail("Expected runtime exception caused by a call to show a dialog");
        } catch (RuntimeException r) {
            assertThat(r.getMessage(), is(equalTo(
                    expectedWarningMessage("Superclass " + generatorProperties.getMatcherSuperClassName() + " is final."))));
        }
    }

    public void testSuperclassDoesNotExtendBaseClass() {
        setupTestFixture();
        loadTestClassFromFile(TEST_NAME, "base_class.java");
        loadTestClassFromFile(TEST_NAME, "ineligible_superclass.java");

        generatorProperties.setMatcherSuperClassName("SuperClass").setBaseClassName("BaseClass");

        final Generator generator = new MatcherGenerator(generatorProperties);

        try {
            generator.generate();
            fail("Expected runtime exception caused by a call to show a dialog");
        } catch (RuntimeException r) {
            assertThat(r.getMessage(), is(equalTo(expectedWarningMessage("Superclass SuperClass does not extend BaseClass."))));
        }
    }

    public void testBaseClassDoesNotExist() {
        setupTestFixture();
        loadTestClassFromFile(TEST_NAME, "ineligible_superclass.java");

        generatorProperties.setMatcherSuperClassName("SuperClass").setBaseClassName("BaseClass");

        final Generator generator = new MatcherGenerator(generatorProperties);

        try {
            generator.generate();
            fail("Expected runtime exception caused by a call to show a dialog");
        } catch (RuntimeException r) {
            assertThat(r.getMessage(), is(equalTo(expectedWarningMessage("Could not find base class BaseClass in the project."))));
        }
    }

    public void testMultipleWarnings() {
        setupTestFixture();
        loadTestClassFromFile(TEST_NAME, "base_class.java");
        loadTestClassFromFile(TEST_NAME, "ineligible_final_superclass.java");

        generatorProperties.setMatcherSuperClassName("SuperClass").setBaseClassName("BaseClass");

        final Generator generator = new MatcherGenerator(generatorProperties);

        try {
            generator.generate();
            fail("Expected runtime exception caused by a call to show a dialog");
        } catch (RuntimeException r) {
            assertThat(r.getMessage(), is(equalTo(
                    expectedWarningMessage("Superclass SuperClass is final.", "Superclass SuperClass does not extend BaseClass."))));
        }
    }

    public void testWarningsConfirmed() {
        setupTestFixture();

        final Generator generator = getGeneratorWithWarnings(true);

        generator.generate();

        // Generated file should exist, so no exception will be thrown
        getGeneratedFile(generatorProperties);
    }

    public void testWarningsDeclined() {
        setupTestFixture();

        final Generator generator = getGeneratorWithWarnings(false);

        generator.generate();

        // Generated file should not exist
        try {
            getGeneratedFile(generatorProperties);
            fail("Expected IllegalArgumentException caused by absence of generated file");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), startsWith("could not find generated file"));
        }
    }

    private Generator getGeneratorWithWarnings(final boolean shouldContinue) {
        return new MatcherGenerator(generatorProperties) {
            @Override
            protected boolean shouldContinueWithWarnings(List<String> warnings) {
                return shouldContinue;
            }

            @Override
            protected List<String> checkProperties() {
                return Lists.newArrayList("a warning");
            }
        };
    }

    private String expectedWarningMessage(String... messages) {
        StringBuilder sb = new StringBuilder("The generated source may not compile for the following reasons:");
        for (String message : messages) {
            sb.append("\n").append(message);
        }
        return sb.toString();
    }

}
