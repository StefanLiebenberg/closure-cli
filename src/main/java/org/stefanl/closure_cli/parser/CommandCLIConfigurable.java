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

    @Option(name = "--config", usage = "Specifies the configuration file")
    public File configFile;

    @Option(name = "--pwd", usage = "Specifies the working directory")
    public File pwdDirectory;

    // todo implement this
    @Option(name = "--help", aliases = {"-h"}, usage = "show help")
    public Boolean showHelp = false;

    // todo implement this
    @Option(name = "--version", aliases = {"-v"}, usage = "Show the version.")
    public Boolean showVersion = false;

    // todo add support for --no-compile
    @Option(name = "--compile",
            usage = "Compile the app")
    public Boolean compile;

    // todo add support for --no-debug
    @Option(name = "--debug",
            usage = "Debug the app")
    public Boolean debug;

    @Option(name = "--javascriptOutputFile")
    public File scriptFile;

    public void load(@Nonnull final ClosureOptions closureOptions) {


        if (outputDirectory != null) {
            closureOptions.setOutputDirectory(outputDirectory);
        }

        if(scriptFile != null) {
            closureOptions.setJavascriptOutputFile(scriptFile);
        }

        if (compile != null) {
            closureOptions.setShouldCompile(compile);
        }

        if (debug != null) {
            closureOptions.setShouldDebug(debug);
        }
    }
}
