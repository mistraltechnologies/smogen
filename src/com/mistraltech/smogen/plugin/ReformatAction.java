package com.mistraltech.smogen.plugin;

import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleManager;

public class ReformatAction implements Runnable {
    private final PsiFile targetFile;

    public ReformatAction(PsiFile targetFile) {
        this.targetFile = targetFile;
    }

    @Override
    public void run() {
        CodeStyleManager.getInstance(targetFile.getProject()).reformat(targetFile);
    }
}
