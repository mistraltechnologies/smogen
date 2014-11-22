package com.mistraltech.smogen.codegenerator;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.mistraltech.smogen.codegenerator.matchergenerator.MatcherGeneratorProperties;
import com.mistraltech.smogen.utils.PsiUtils;
import org.hamcrest.Matchers;

import java.io.File;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;

public class OverwriteTest extends AbstractGeneratorTest {

    private MatcherGeneratorProperties generatorProperties;

    private void setupTestFixture() {
        String testName = "overwrite";
        String inputFilePath = testName + "/" + "input.java";

        final PsiFile sourceFile = myFixture.configureByFile(inputFilePath);
        final PsiClass sourceClass = PsiUtils.getClassFromFile(sourceFile);

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
            assertEquals("File " + File.separator + "src" + File.separator + "WidgetMatcher.java already exists. Overwrite?", r.getMessage());
        }
    }

    public void testOverwriteConfirmed() {
        setupTestFixture();

        final Generator generator = new Generator(generatorProperties) {
            @Override
            protected boolean shouldOverwriteFile(PsiFile existingFile) {
                return true;
            }
        };

        // First time creates matcher class
        generator.generate();
        long originalFileModificationTimestamp = getGeneratedFile(generatorProperties).getModificationStamp();

        // Second time should overwrite
        generator.generate();
        long currentFileModificationTimestamp = getGeneratedFile(generatorProperties).getModificationStamp();

        assertThat("Timestamp should have changed", currentFileModificationTimestamp, is(greaterThan(originalFileModificationTimestamp)));
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

    protected PsiFile getGeneratedFile(MatcherGeneratorProperties generatorProperties)
    {
        final String path = getGeneratedFilePath(generatorProperties);

        final VirtualFile generatedVirtualFile = myFixture.findFileInTempDir(path);
        if (generatedVirtualFile == null) {
            throw new IllegalArgumentException("could not find results file " + path);
        }

        final PsiFile generatedPsiFile = myFixture.getPsiManager().findFile(generatedVirtualFile);
        assert generatedPsiFile != null;

        return generatedPsiFile;
    }

}
