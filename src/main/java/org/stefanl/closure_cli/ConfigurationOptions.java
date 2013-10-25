package org.stefanl.closure_cli;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.stefanl.closure_utilities.closure.ImmutableClosureBuildOptions;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.util.List;
import java.util.Set;

public class ConfigurationOptions {

    public String assetsDirectory;

    public String outputDirectory;

    public String soyOutputDirectory;

    public Boolean shouldCompile = false;

    public Boolean shouldDebug = false;

    public Boolean shouldInline = false;

    public Set<String> soySourceDirectories;

    public Set<String> gssSourceDirectories;

    public List<String> gssEntryPoints;

    public Set<String> javascriptSourceDirectories;

    public List<String> javascriptEntryPoints;

    public String cssClassRenameMap;

    public List<String> externalStylesheets;

    public List<String> externalScripts;

    public String javascriptDependencyOutputFile;

    public String htmlContent;

    public String defaultBuild;

    @Nullable
    private static File toFile(@Nullable String path) {
        if (path != null) {
            return new File(path);
        } else {
            return null;
        }
    }

    @Nullable
    private static ImmutableList<File> getFiles(@Nullable List<String> paths) {
        if (paths != null && !paths.isEmpty()) {
            final ImmutableList.Builder<File> builder =
                    new ImmutableList.Builder<>();
            for (String path : paths) {
                builder.add(new File(path));
            }
            return builder.build();
        } else {
            return null;
        }
    }

    @Nullable
    private static ImmutableSet<File> getFiles(@Nullable Set<String> paths) {
        if (paths != null && !paths.isEmpty()) {
            final ImmutableSet.Builder<File> builder =
                    new ImmutableSet.Builder<>();
            for (String path : paths) {
                builder.add(new File(path));
            }
            return builder.build();
        } else {
            return null;
        }
    }

    @Nullable
    private static <A> ImmutableList<A> getList(List<A> items) {
        if (items != null && !items.isEmpty()) {
            final ImmutableList.Builder<A> builder =
                    new ImmutableList.Builder<A>();
            builder.addAll(items);
            return builder.build();
        } else {
            return null;
        }
    }

    @Nonnull
    public ImmutableClosureBuildOptions getBuildOptions() {
        return new ImmutableClosureBuildOptions(
                getFiles(soySourceDirectories),
                toFile(cssClassRenameMap),
                toFile(assetsDirectory),
                getFiles(gssSourceDirectories),
                getFiles(externalStylesheets),
                getList(gssEntryPoints),
                getFiles(javascriptSourceDirectories),
                getFiles(externalScripts),
                getList(javascriptEntryPoints),
                toFile(javascriptDependencyOutputFile),
                toFile(soyOutputDirectory),
                toFile(outputDirectory),
                htmlContent,
                shouldCompile,
                shouldDebug,
                shouldInline);

    }

}
