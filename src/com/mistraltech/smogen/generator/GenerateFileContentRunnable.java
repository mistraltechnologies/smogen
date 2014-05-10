package com.mistraltech.smogen.generator;

import com.intellij.openapi.editor.Document;
import com.intellij.psi.*;
import com.mistraltech.smogen.property.Property;
import com.mistraltech.smogen.property.PropertyLocator;
import com.mistraltech.smogen.utils.GeneratorUtils;
import com.mistraltech.smogen.utils.Visibility;
import org.jetbrains.annotations.NotNull;

import java.util.List;

class GenerateFileContentRunnable implements Runnable {
    private final GeneratorProperties generatorProperties;
    private final PsiClass sourceClass;
    private final PsiDocumentManager documentManager;
    private final PsiFile existingFile;
    private final PsiDirectory parentDirectory;
    private final String fileName;
    private PsiFile targetFile;

    public GenerateFileContentRunnable(final GeneratorProperties generatorProperties, final PsiClass sourceClass,
                                       final PsiFile existingFile, final PsiDirectory parentDirectory) {
        this.generatorProperties = generatorProperties;
        this.sourceClass = sourceClass;
        this.documentManager = PsiDocumentManager.getInstance(parentDirectory.getProject());
        this.existingFile = existingFile;
        this.parentDirectory = parentDirectory;
        this.fileName = generatorProperties.getClassName() + ".java";
    }

    @Override
    public void run() {
        targetFile = existingFile != null ? existingFile : parentDirectory.createFile(fileName);

        Document document = documentManager.getDocument(targetFile);
        assert document != null;

        PsiPackage targetPackage = JavaDirectoryService.getInstance().getPackage(parentDirectory);
        assert targetPackage != null;

        document.setText(generateDocumentContent(targetPackage));

        documentManager.commitDocument(document);
    }

    /**
     * Gets the file containing the newly generated matcher class.
     *
     * @return the matcher class file
     */
    public PsiFile getTargetFile() {
        return targetFile;
    }

    @NotNull
    private String generateDocumentContent(@NotNull PsiPackage targetPackage) {
        boolean includeSuperClassProperties = generatorProperties.getSuperClassName() == null;
        List<Property> sourceClassProperties = PropertyLocator.locateProperties(sourceClass, includeSuperClassProperties, Visibility.PUBLIC);

        StringBuilder documentText = new StringBuilder();

        generatePackageStatement(documentText, targetPackage);
        generateImports(documentText);
        generateClassSignature(documentText);
        generateMatcherVariables(documentText, sourceClassProperties);
        generateConstructor(documentText, sourceClassProperties);
        generateInnerClass(documentText);
        generateStaticFactoryMethod(documentText);
        generateLikeStaticFactoryMethod(documentText);
        generateSelfMethod(documentText);
        generateMatcherSetters(documentText, sourceClassProperties);

        documentText.append("}\n");
        return documentText.toString();
    }

    private void generatePackageStatement(@NotNull StringBuilder documentText, @NotNull PsiPackage targetPackage) {
        GeneratorUtils.addPackageStatement(documentText, targetPackage.getQualifiedName());
    }

    private void generateImports(StringBuilder documentText) {
        if (generatorProperties.getSuperClassName() == null) {
            GeneratorUtils.addImport(documentText, "com.mistraltech.smog.core.CompositePropertyMatcher");
        }

        GeneratorUtils.addImport(documentText, "com.mistraltech.smog.core.ReflectingPropertyMatcher");
        GeneratorUtils.addImport(documentText, "com.mistraltech.smog.core.PropertyMatcher");
        GeneratorUtils.addImport(documentText, "org.hamcrest.Matcher");
        GeneratorUtils.addStaticImport(documentText, "org.hamcrest.CoreMatchers.equalTo");
        documentText.append("\n");
    }

    private void generateClassSignature(@NotNull StringBuilder documentText) {
        documentText
                .append("public ")
                .append(generatorProperties.isExtensible() ? "abstract " : "final ")
                .append("class ")
                .append(generatorProperties.getClassName())
                .append(generatorProperties.isExtensible() ? "<R, T extends " + sourceClass.getName() + ">" : "")
                .append(" extends ");

        if (generatorProperties.getSuperClassName() != null) {
            documentText.append(generatorProperties.getSuperClassName())
                    .append("<")
                    .append(generatorProperties.isExtensible() ? "R" : generatorProperties.getClassName())
                    .append(", ")
                    .append(generatorProperties.isExtensible() ? "T" : sourceClass.getName())
                    .append("> {\n");
        } else {
            documentText.append("CompositePropertyMatcher<")
                .append(generatorProperties.isExtensible() ? "T" : sourceClass.getName())
                .append("> {\n");
        }

    }

    private void generateMatcherVariables(@NotNull StringBuilder documentText, @NotNull List<Property> properties) {
        for (Property property : properties) {
            generateMatcherVariable(documentText, property);
        }
        documentText.append("\n");
    }

    private void generateMatcherVariable(@NotNull StringBuilder documentText, @NotNull Property property) {
        documentText
                .append(String.format("private PropertyMatcher<%s> %s = new ReflectingPropertyMatcher<%s>(\"%s\", this);\n",
                        property.getBoxedType(),
                        matcherAttributeName(property),
                        property.getBoxedType(),
                        property.getName()));
    }

    private void generateConstructor(@NotNull StringBuilder documentText, @NotNull List<Property> sourceClassProperties) {
        documentText
                .append(generatorProperties.isExtensible() ? "protected " : "private ")
                .append(generatorProperties.getClassName())
                .append("(final String matchedObjectDescription, final ")
                .append(sourceClass.getName())
                .append(" template) {\n")
                .append("super(matchedObjectDescription")
                .append(generatorProperties.getSuperClassName() != null ? ", template" : "")
                .append(");\n")
                .append("if (template != null) {");

        for (Property property : sourceClassProperties) {
            documentText
                    .append(setterMethodName(property))
                    .append("(template.")
                    .append(property.getAccessorName()).append("()")
                    .append(");\n");
        }

        documentText
                .append("}\n")
                .append("}\n");
    }

    private void generateInnerClass(@NotNull StringBuilder documentText) {
        String innerClassName = generatorProperties.getConcreteSubclassName();
        if (innerClassName != null) {
            documentText
                    .append("public static class ")
                    .append(innerClassName)
                    .append(" extends ")
                    .append(generatorProperties.getClassName())
                    .append("<")
                    .append(innerClassName)
                    .append(", ")
                    .append(sourceClass.getName())
                    .append("> {\n")
                    .append("public ")
                    .append(innerClassName)
                    .append("(final String matchedObjectDescription, final ")
                    .append(sourceClass.getName())
                    .append(" template) {\n")
                    .append("super(matchedObjectDescription, template);\n")
                    .append("}\n")
                    .append("}\n");
        }
    }

    private void generateStaticFactoryMethod(@NotNull StringBuilder documentText) {
        String innerClassName = generatorProperties.getConcreteSubclassName();
        String targetClassName = innerClassName != null ? innerClassName : generatorProperties.getClassName();

        if (!generatorProperties.isExtensible() || innerClassName != null) {
            documentText
                    .append("public static ")
                    .append(targetClassName)
                    .append(" ")
                    .append(generatorProperties.getFactoryMethodPrefix())
                    .append(sourceClass.getName())
                    .append("That()\n")
                    .append("{\n")
                    .append("return ")
                    .append(generatorProperties.getFactoryMethodPrefix())
                    .append(sourceClass.getName())
                    .append("Like(null);\n")
                    .append("}\n");
        }
    }

    private void generateLikeStaticFactoryMethod(@NotNull StringBuilder documentText) {
        String innerClassName = generatorProperties.getConcreteSubclassName();
        String targetClassName = innerClassName != null ? innerClassName : generatorProperties.getClassName();

        if (!generatorProperties.isExtensible() || innerClassName != null) {
            documentText
                    .append("public static ")
                    .append(targetClassName)
                    .append(" ")
                    .append(generatorProperties.getFactoryMethodPrefix())
                    .append(sourceClass.getName())
                    .append("Like(final ")
                    .append(sourceClass.getName())
                    .append(" template)\n")
                    .append("{\n")
                    .append("return new ")
                    .append(targetClassName)
                    .append("(\"")
                    .append(generatorProperties.getFactoryMethodPrefix())
                    .append(" ")
                    .append(sourceClass.getName())
                    .append("\", template);\n")
                    .append("}\n");
        }
    }

    private void generateSelfMethod(@NotNull StringBuilder documentText) {
        if (generatorProperties.isExtensible()) {
            documentText
                    .append("@SuppressWarnings(\"unchecked\")\n")
                    .append("private R self()\n")
                    .append("{\n")
                    .append("return (R) this;\n")
                    .append("}\n");
        }
    }

    private void generateMatcherSetters(@NotNull StringBuilder documentText, @NotNull List<Property> properties) {
        for (Property property : properties) {
            generateMatcherSetter(documentText, property);
        }
    }

    private void generateMatcherSetter(@NotNull StringBuilder documentText, @NotNull Property property) {
        documentText
                .append("public ")
                .append(generatorProperties.isExtensible() ? "R" : generatorProperties.getClassName())
                .append(" ")
                .append(setterMethodName(property))
                .append("(final ")
                .append(property.getType())
                .append(" ")
                .append(property.getName())
                .append(") {\n")
                .append("return this.")
                .append(setterMethodName(property))
                .append("(equalTo(")
                .append(property.getName())
                .append("));\n")
                .append("}\n");

        documentText
                .append("public ")
                .append(generatorProperties.isExtensible() ? "R" : generatorProperties.getClassName())
                .append(" ")
                .append(setterMethodName(property))
                .append("(Matcher<? super ")
                .append(property.getBoxedType())
                .append("> ")
                .append(matcherAttributeName(property))
                .append(") {\n")
                .append("this.")
                .append(matcherAttributeName(property))
                .append(".setMatcher(")
                .append(matcherAttributeName(property))
                .append(");\n")
                .append("return ")
                .append(generatorProperties.isExtensible() ? "self()" : "this")
                .append(";\n")
                .append("}\n");
    }

    private String matcherAttributeName(@NotNull Property property) {
        return property.getName() + "Matcher";
    }

    private String setterMethodName(@NotNull Property property) {
        return " has" + property.getNameCapitalised();
    }
}
