package org.stefanl.closure_cli.config;


import org.stefanl.closure_utilities.closure.ClosureOptions;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.Set;

public class SoyConfig {

    public Set<File> sourceDirectories;

    public File outputDirectory;

    public void load(@Nonnull ClosureOptions options) {
        if (outputDirectory != null) {
            options.setSoyOutputDirectory(outputDirectory);
        }

        if (sourceDirectories != null) {
            options.setSoySourceDirectories(sourceDirectories);
        }
    }
}
