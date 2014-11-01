package com.mistraltech.smogen;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import com.mistraltech.smogen.codegenerator.Generator;
import com.mistraltech.smogen.codegenerator.matchergenerator.MatcherGeneratorProperties;
import com.mistraltech.smogen.utils.PsiUtils;
import com.mistraltech.smogen.utils.SourceRootUtils;

import java.io.File;
import java.util.List;

public abstract class AbstractGeneratorTest extends LightCodeInsightFixtureTestCase {

    @Override
    protected String getTestDataPath() {
        return new File("").getAbsolutePath().replace(File.separatorChar, '/') + "/testData";
    }

    protected void doTest(String inputFilePath, String generatedFilePath, MatcherGeneratorProperties generatorProperties) {
        List<VirtualFile> sourceRoots = SourceRootUtils.getSourceAndTestSourceRoots(getProject());

        final PsiFile sourceFile = myFixture.configureByFile(inputFilePath);
        final PsiClass sourceClass = PsiUtils.getClassFromFile(sourceFile);

        generatorProperties.setSourceRoot(sourceRoots.get(0))
                .setSourceClass(sourceClass);

        new Generator(generatorProperties).generate();

        myFixture.checkResultByFile("WidgetMatcher.java", generatedFilePath, false);
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
}
