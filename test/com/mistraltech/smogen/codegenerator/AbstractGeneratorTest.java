package com.mistraltech.smogen.codegenerator;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import com.mistraltech.smogen.codegenerator.matchergenerator.MatcherGenerator;
import com.mistraltech.smogen.codegenerator.matchergenerator.MatcherGeneratorProperties;
import com.mistraltech.smogen.utils.ActionUtils;
import com.mistraltech.smogen.utils.PsiUtils;
import com.mistraltech.smogen.utils.SourceRootUtils;

import java.io.File;
import java.util.Optional;

public abstract class AbstractGeneratorTest extends LightCodeInsightFixtureTestCase {
    @Override
    protected String getTestDataPath() {
        return new File("").getAbsolutePath().replace(File.separatorChar, '/') + "/testdata";
    }

    protected void doTest(String testName, String inputFileName, String expectedGeneratedFileName, MatcherGeneratorProperties generatorProperties) {
        preLoadClasses();

        final PsiClass sourceClass = loadTestClassFromFile(testName, inputFileName);

        generatorProperties.setSourceRoot(getSourceRoot())
                .setSourceClass(sourceClass);

        new MatcherGenerator(generatorProperties).generate();

        String expectedGeneratedFilePath = testName + "/" + expectedGeneratedFileName;
        myFixture.checkResultByFile(getGeneratedFilePath(generatorProperties), expectedGeneratedFilePath, false);
    }

    protected void preLoadClasses() {
        loadDefaultBaseClass();
    }

    protected void doTest(String testName, MatcherGeneratorProperties generatorProperties) {
        doTest(testName, "input.java", "generated.java", generatorProperties);
    }

    protected String getGeneratedFilePath(MatcherGeneratorProperties generatorProperties) {
        return generatorProperties.getPackageName().replace('.', '/') + "/" + generatorProperties.getClassName() + ".java";
    }

    protected VirtualFile getSourceRoot() {
        return SourceRootUtils.getSourceAndTestSourceRoots(getProject()).get(0);
    }

    protected MatcherGeneratorProperties defaultGeneratorProperties() {
        return new MatcherGeneratorProperties()
                .setProject(getProject())
                .setPackageName("")
                .setClassName("WidgetMatcher")
                .setExtensible(false)
                .setFactoryMethodPrefix("a")
                .setSetterPrefix("has")
                .setSetterSuffix("")
                .setBaseClassName("com.mistraltech.smog.core.CompositePropertyMatcher")
                .setExtensible(false)
                .setFactoryMethodSuffix("That")
                .setTemplateFactoryMethodSuffix("Like")
                .setUseReflectingPropertyMatcher(true)
                .setGenerateTemplateFactoryMethod(true)
                .setMakeMethodParametersFinal(true)
                .setGenerateFactoryMethods(true);
    }

    protected void createPackage(final String packageName) {
        assert packageName != null && packageName.length() > 0;

        final PsiDirectory baseDirectory = myFixture.getPsiManager().findDirectory(getSourceRoot());

        assert baseDirectory != null;

        ActionUtils.runAction(() -> PsiUtils.createMissingDirectoriesForPackage(baseDirectory, packageName));
    }

    protected PsiFile getGeneratedFile(MatcherGeneratorProperties generatorProperties) {
        final String path = getGeneratedFilePath(generatorProperties);

        final VirtualFile generatedVirtualFile = myFixture.findFileInTempDir(path);
        if (generatedVirtualFile == null) {
            throw new IllegalArgumentException("could not find generated file " + path);
        }

        final PsiFile generatedPsiFile = myFixture.getPsiManager().findFile(generatedVirtualFile);
        assert generatedPsiFile != null;

        return generatedPsiFile;
    }

    protected PsiClass loadTestClassFromFile(String testName, String fileName) {
        String inputFilePath = testName + "/" + fileName;

        final PsiFile sourceFile = myFixture.configureByFile(inputFilePath);

        return PsiUtils.getPublicClassFromFile(sourceFile)
                .orElseThrow(() -> new IllegalStateException("Couldn't load test class from file "
                        + inputFilePath + " for test " + testName));
    }

    protected Optional<PsiClass> loadDefaultBaseClass() {
        return loadTestClassFromText("com/mistraltech/smog/core/CompositePropertyMatcher.java",
                "package com.mistraltech.smog.core;\n" +
                        "public class CompositePropertyMatcher<T> {}\n");
    }

    protected Optional<PsiClass> loadTestClassFromText(String className, String code) {
        final PsiFile sourceFile =  myFixture.addFileToProject(className, code);
        return PsiUtils.getPublicClassFromFile(sourceFile);
    }
}
