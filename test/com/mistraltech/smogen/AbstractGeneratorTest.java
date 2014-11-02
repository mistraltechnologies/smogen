package com.mistraltech.smogen;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import com.mistraltech.smogen.codegenerator.Generator;
import com.mistraltech.smogen.codegenerator.matchergenerator.MatcherGeneratorProperties;
import com.mistraltech.smogen.utils.ActionUtils;
import com.mistraltech.smogen.utils.PsiUtils;
import com.mistraltech.smogen.utils.SourceRootUtils;

import java.io.File;
import java.util.List;

public abstract class AbstractGeneratorTest extends LightCodeInsightFixtureTestCase {
    @Override
    protected String getTestDataPath() {
        return new File("").getAbsolutePath().replace(File.separatorChar, '/') + "/testData";
    }

    protected void doTest(String inputFilePath, String expectedGeneratedFilePath, MatcherGeneratorProperties generatorProperties) {
        final PsiFile sourceFile = myFixture.configureByFile(inputFilePath);
        final PsiClass sourceClass = PsiUtils.getClassFromFile(sourceFile);

        generatorProperties.setSourceRoot(getSourceRoot())
                .setSourceClass(sourceClass);

        new Generator(generatorProperties).generate();

        final String generatedFile = generatorProperties.getPackageName().replace('.','/') + "/" + generatorProperties.getClassName() + ".java";
        myFixture.checkResultByFile(generatedFile, expectedGeneratedFilePath, false);
    }

    private VirtualFile getSourceRoot() {
        return SourceRootUtils.getSourceAndTestSourceRoots(getProject()).get(0);
    }

    protected MatcherGeneratorProperties generatorProperties() {
        return new MatcherGeneratorProperties()
                .setProject(getProject())
                .setPackageName("")
                .setClassName("WidgetMatcher")
                .setExtensible(false)
                .setConcreteSubclassName(null)
                .setFactoryMethodPrefix("a");
    }

    protected void doTest(String testName, MatcherGeneratorProperties generatorProperties) {
        String inputFilePath = testName + "/" + "input.java";
        String generatedFilePath = testName + "/" + "generated.java";
        doTest(inputFilePath, generatedFilePath, generatorProperties);
    }

    protected void createPackage(final String packageName) {
        final PsiDirectory baseDirectory = myFixture.getPsiManager().findDirectory(getSourceRoot());
        assert baseDirectory != null;
        ActionUtils.runAction(new Runnable() {
            @Override
            public void run() {
                PsiUtils.createMissingDirectoriesForPackage(baseDirectory, packageName);
            }
        });
    }
}
