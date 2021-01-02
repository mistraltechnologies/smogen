package com.mistraltech.smogen.codegenerator;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.mistraltech.smogen.codegenerator.matchergenerator.MatcherGeneratorProperties;
import org.jetbrains.annotations.NotNull;

import java.io.File;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class OverwriteTest extends AbstractGeneratorTest {
    private static final String TEST_NAME = "overwrite";
    private MatcherGeneratorProperties generatorProperties;

    private void setupTestFixture() {

        final PsiClass sourceClass = loadTestClassFromFile(TEST_NAME, "input.java");

        generatorProperties = super.defaultGeneratorProperties()
                .setSourceRoot(getSourceRoot())
                .setSourceClass(sourceClass);
    }

    public void testOverwriteDialog() {
        setupTestFixture();

        final Generator generator = new Generator(generatorProperties);

        // First time creates matcher class
        generator.generate();

        // Second time raises overwrite dialog
        try {
            generator.generate();
            fail("Expected runtime exception caused by a call to show a dialog");
        } catch (RuntimeException r) {
            assertEquals(
                    "File " + File.separator + "src" + File.separator + "WidgetMatcher.java already exists. Overwrite?",
                    r.getMessage());
        }
    }

    public void testOverwriteConfirmed() {
        setupTestFixture();

        final Generator generator = new Generator(generatorProperties) {
            @Override
            protected boolean shouldOverwriteFile(@NotNull PsiFile existingFile) {
                return true;
            }
        };

        // First time creates matcher class
        generator.generate();
        long originalFileModificationTimestamp = getGeneratedFile(generatorProperties).getModificationStamp();

        // Second time should overwrite
        generator.generate();
        long currentFileModificationTimestamp = getGeneratedFile(generatorProperties).getModificationStamp();

        assertTrue("Timestamp should have changed", currentFileModificationTimestamp > originalFileModificationTimestamp);
    }

    public void testOverwriteDeclined() {
        setupTestFixture();

        final Generator generator = new Generator(generatorProperties) {
            @Override
            protected boolean shouldOverwriteFile(PsiFile existingFile) {
                return false;
            }
        };

        // First time creates matcher class
        generator.generate();
        long originalFileModificationTimestamp = getGeneratedFile(generatorProperties).getModificationStamp();

        // Second time should overwrite
        generator.generate();
        long currentFileModificationTimestamp = getGeneratedFile(generatorProperties).getModificationStamp();

        assertThat("Timestamp should not have changed", currentFileModificationTimestamp, is(equalTo(originalFileModificationTimestamp)));
    }
}
