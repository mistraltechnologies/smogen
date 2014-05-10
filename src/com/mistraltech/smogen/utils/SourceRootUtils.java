package com.mistraltech.smogen.utils;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ContentEntry;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.SourceFolder;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jps.model.java.JavaSourceRootProperties;
import org.jetbrains.jps.model.java.JavaSourceRootType;
import org.jetbrains.jps.model.module.JpsModuleSourceRootType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class SourceRootUtils {
    @NotNull
    private static Set<? extends JpsModuleSourceRootType<?>> toRootTypeSet(@NotNull JpsModuleSourceRootType<?> rootType) {
        HashSet<JpsModuleSourceRootType<?>> rootTypes = new HashSet<JpsModuleSourceRootType<?>>();
        rootTypes.add(rootType);
        return rootTypes;
    }

    @NotNull
    public static List<VirtualFile> getSourceRoots(@NotNull Project project, @NotNull JpsModuleSourceRootType<?> rootType, boolean excludeGeneratedRoots) {
        return getSourceRoots(project, toRootTypeSet(rootType), excludeGeneratedRoots);
    }

    @NotNull
    public static List<VirtualFile> getSourceRoots(@NotNull Project project, @NotNull Set<? extends JpsModuleSourceRootType<?>> rootTypes, boolean excludeGeneratedRoots) {
        List<VirtualFile> roots = new ArrayList<VirtualFile>();
        for (Module module : ModuleManager.getInstance(project).getModules()) {
            roots.addAll(getSourceRoots(module, rootTypes, excludeGeneratedRoots));
        }
        return roots;
    }

    @NotNull
    public static List<VirtualFile> getSourceRoots(@NotNull Module module, @NotNull JpsModuleSourceRootType<?> rootType, boolean excludeGeneratedRoots) {
        return getSourceRoots(module, toRootTypeSet(rootType), excludeGeneratedRoots);
    }

    @NotNull
    public static List<VirtualFile> getSourceRoots(@NotNull Module module, @NotNull Set<? extends JpsModuleSourceRootType<?>> rootTypes, boolean excludeGeneratedRoots) {
        List<VirtualFile> roots = new ArrayList<VirtualFile>();

        for (ContentEntry entry : ModuleRootManager.getInstance(module).getContentEntries()) {
            for (SourceFolder sourceFolder : entry.getSourceFolders(rootTypes)) {
                if (!(excludeGeneratedRoots && isForGeneratedSources(sourceFolder))) {
                    ContainerUtil.addIfNotNull(roots, sourceFolder.getFile());
                }
            }
        }

        return roots;
    }

    private static boolean isForGeneratedSources(@NotNull SourceFolder sourceFolder) {
        JavaSourceRootProperties properties = sourceFolder.getJpsElement().getProperties(JavaSourceRootType.TEST_SOURCE);
        return properties != null && properties.isForGeneratedSources();
    }
}
