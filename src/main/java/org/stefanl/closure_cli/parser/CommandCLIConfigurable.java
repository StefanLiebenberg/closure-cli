package org.stefanl.closure_cli.parser;


import org.kohsuke.args4j.Option;
import org.stefanl.closure_utilities.closure.ClosureOptions;

import javax.annotation.Nonnull;
import java.io.File;

public class CommandCLIConfigurable extends BaseCLIConfiguratable {

    @Option(name = "--outputDirectory",
            aliases = {"-O"},
            usage = "The output directory")
    public File outputDirectory;

    @Option(name = "--config")
    public File configFile;

    @Option(name = "--pwd", usage = "Specifies the working directory")
    public File pwdDirectory;

    @Option(name = "--help", aliases = {"-h"}, usage = "show help")
    public Boolean showHelp = false;

    @Option(name = "--version", aliases = {"-v"}, usage = "Show the version.")
    public Boolean showVersion = false;

    @Option(name = "--compile", aliases = {"-C"},
            usage = "Compile the app")
    public Boolean compile;


    public void load(@Nonnull final ClosureOptions closureOptions) {
        if (outputDirectory != null) {
            closureOptions.setOutputDirectory(outputDirectory);
        }

        if (compile != null) {
            closureOptions.setShouldCompile(compile);
        }
    }
}
