package com.mistraltech.smogen.utils;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ContentEntry;
import com.intellij.openapi.roots.ContentFolder;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.roots.SourceFolder;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jps.model.java.JavaSourceRootProperties;
import org.jetbrains.jps.model.java.JavaSourceRootType;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.jetbrains.jps.model.java.JavaSourceRootType.TEST_SOURCE;

public final class SourceRootUtils {

    /**
     * Gets the source and test source root directories for a project.
     *
     * @param project the project
     * @return list of source and test source root directories
     */
    @NotNull
    public static List<VirtualFile> getSourceAndTestSourceRoots(@NotNull Project project) {
        return getSourceRoots(project, JavaSourceRootType.SOURCE, TEST_SOURCE);
    }

    /**
     * Gets the source root directories (excluding test source root directories) for a project.
     *
     * @param project the project
     * @return list of source root directories
     */
    @NotNull
    public static List<VirtualFile> getSourceRoots(@NotNull Project project) {
        return getSourceRoots(project, JavaSourceRootType.SOURCE);
    }

    /**
     * Gets the test source root directories for a project.
     *
     * @param project the project
     * @return list of test source root directories
     */
    @NotNull
    public static List<VirtualFile> getTestSourceRoots(@NotNull Project project) {
        return getSourceRoots(project, TEST_SOURCE);
    }

    /**
     * Gets the source root directories (excluding test source root directories) for a module.
     *
     * @param module the module
     * @return list of source root directories
     */
    @NotNull
    public static List<VirtualFile> getSourceRoots(@NotNull Module module) {
        return getSourceRoots(module, JavaSourceRootType.SOURCE);
    }

    /**
     * Gets the test source root directories for a module.
     *
     * @param module the module
     * @return list of test source root directories
     */
    @NotNull
    public static List<VirtualFile> getTestSourceRoots(@NotNull Module module) {
        return getSourceRoots(module, TEST_SOURCE);
    }

    @NotNull
    private static List<VirtualFile> getSourceRoots(@NotNull Project project, JavaSourceRootType... rootTypes) {
        return getSourceRoots(project, toRootTypeSet(rootTypes));
    }

    @NotNull
    private static List<VirtualFile> getSourceRoots(@NotNull Project project, @NotNull Set<JavaSourceRootType> rootTypes) {
        Module[] modules = ModuleManager.getInstance(project).getModules();

        List<VirtualFile> roots = Stream.of(modules)
                .flatMap(m -> getSourceRoots(m, rootTypes).stream())
                .collect(toList());

        return roots;
    }

    @NotNull
    private static List<VirtualFile> getSourceRoots(@NotNull Module module, JavaSourceRootType... rootTypes) {
        return getSourceRoots(module, toRootTypeSet(rootTypes));
    }

    @NotNull
    private static Set<JavaSourceRootType> toRootTypeSet(@NotNull JavaSourceRootType... rootTypes) {
        return Stream.of(rootTypes).collect(toSet());
    }

    @NotNull
    private static List<VirtualFile> getSourceRoots(@NotNull Module module, @NotNull Set<JavaSourceRootType> rootTypes) {
        ContentEntry[] contentEntries = ModuleRootManager.getInstance(module).getContentEntries();

        List<VirtualFile> roots = Stream.of(contentEntries)
                .flatMap(ce -> ce.getSourceFolders(rootTypes).stream())
                .filter(sf -> !isForGeneratedSources(sf))
                .map(ContentFolder::getFile)
                .filter(vf -> vf != null)
                .collect(toList());

        return roots;
    }

    private static boolean isForGeneratedSources(@NotNull SourceFolder sourceFolder) {
        JavaSourceRootType rootType = (JavaSourceRootType) sourceFolder.getRootType();
        JavaSourceRootProperties properties = sourceFolder.getJpsElement().getProperties(rootType);

        return properties != null && properties.isForGeneratedSources();
    }

    /**
     * Gets the most suitable source root from those available.
     * <p/>
     * Returns the first matching case:
     * <ol>
     * <li>A current module is specified, that module has test roots and the selected file belongs to one of those test roots => that test root</li>
     * <li>A current module is specified and that module has test roots => the first test root in the module</li>
     * <li>A current module is specified, that module has source roots and the selected file belongs to one of those source roots => that source root</li>
     * <li>A current module is specified and that module has source roots = the first source root in the module</li>
     * <li>The project has test roots => the first test root</li>
     * <li>The project has source roots => the first source root</li>
     * <li>Otherwise returns null</li>
     * </ol>
     *
     * @param project the project
     * @param module optional current module - will be null if a library class is selected
     * @param selectedFile the currently selected file
     * @return the preferred source root if one exists; otherwise null
     */
    @NotNull
    public static Optional<VirtualFile> getPreferredSourceRootForTestClass(@NotNull Project project, @Nullable Module module, @Nullable VirtualFile selectedFile) {
        List<VirtualFile> candidateSourceRoots = getCandidatePreferredSourceRoots(project, module);

        if (candidateSourceRoots.isEmpty()) {
            return Optional.empty();
        }

        Optional<VirtualFile> currentSourceRoot = Optional.ofNullable(selectedFile)
                .map(f -> getFileIndex(project).getSourceRootForFile(f));

        VirtualFile preferredRoot = currentSourceRoot.filter(candidateSourceRoots::contains)
                .orElse(candidateSourceRoots.get(0));

        return Optional.of(preferredRoot);
    }

    @NotNull
    private static ProjectFileIndex getFileIndex(@NotNull Project project) {
        return ProjectRootManager.getInstance(project).getFileIndex();
    }

    @NotNull
    private static List<VirtualFile> getCandidatePreferredSourceRoots(@NotNull Project project, @Nullable Module module) {

        Optional<Module> optModule = Optional.ofNullable(module);

        List<VirtualFile> validTestSourceRoots = optModule
                .map(SourceRootUtils::getTestSourceRoots)
                .orElseGet(() -> getTestSourceRoots(project));

        if (!validTestSourceRoots.isEmpty()) {
            return validTestSourceRoots;
        }

        List<VirtualFile> validSourceRoots = optModule
                .map(SourceRootUtils::getSourceRoots)
                .orElseGet(() -> getSourceRoots(project));

        return validSourceRoots;
    }
}
