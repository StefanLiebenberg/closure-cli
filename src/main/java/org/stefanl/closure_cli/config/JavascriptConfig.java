package org.stefanl.closure_cli.config;

import org.stefanl.closure_utilities.closure.ClosureOptions;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.List;
import java.util.Set;

public class JavascriptConfig implements ConfigInterface {

    public File dependencyFile;
    public Set<File> sourceDirectories;
    public List<String> entryPoints;
    public Set<File> testDirectories;

    public void load(@Nonnull ClosureOptions options) {

        if (dependencyFile != null) {
            options.setJavascriptDependencyOutputFile(dependencyFile);
        }

        if (sourceDirectories != null) {
            options.setJavascriptSourceDirectories(sourceDirectories);
        }

        if (entryPoints != null) {
            options.setJavascriptEntryPoints(entryPoints);
        }
    }
}
