package com.mistraltech.smogen.codegenerator;

import com.intellij.psi.PsiClass;
import com.mistraltech.smogen.codegenerator.AbstractGeneratorTest;
import com.mistraltech.smogen.codegenerator.matchergenerator.MatcherGeneratorProperties;

public abstract class AbstractInterfaceGeneratorTest extends AbstractGeneratorTest {
    @Override
    protected MatcherGeneratorProperties defaultGeneratorProperties() {
        return super.defaultGeneratorProperties()
                .setGenerateInterface(true);
    }

    protected PsiClass loadDefaultBaseClass() {
        return loadTestClassFromText("org/hamcrest/Matcher.java",
                "package org.hamcrest;\n" +
                        "public class Matcher<T> {}\n");
    }
}
