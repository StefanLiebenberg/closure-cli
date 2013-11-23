package org.stefanl.closure_cli.config;

import org.stefanl.closure_utilities.closure.ClosureOptions;
import org.stefanl.closure_utilities.utilities.Immuter;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.List;
import java.util.Set;

public class JavascriptConfig implements ConfigInterface {

    public File dependencyFile;
    public Set<File> sourceDirectories;
    public Set<File> testDirectories;
    public List<String> entryPoints;


    public void load(@Nonnull ClosureOptions options) {

        if (dependencyFile != null) {
            options.setJavascriptDependencyOutputFile(dependencyFile);
        }

        if (sourceDirectories != null) {
            options.setJavascriptSourceDirectories(sourceDirectories);
        }

        if (testDirectories != null) {
            // todo, remove imuter call.
            options.setJavascriptTestDirectories(Immuter.set(testDirectories));
        }

        if (entryPoints != null) {
            options.setJavascriptEntryPoints(entryPoints);
        }
    }
}
