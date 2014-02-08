package liebenberg.closure_cli.parser;


import liebenberg.closure_utilities.build.ClosureOptions;
import org.kohsuke.args4j.Option;

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

    @Option(name = "--verbose", usage = "Print verbose messages")
    public Boolean verbose;

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

    @Option(name = "--javascript-definesOutputFile")
    public File javascriptDefinesOutputFile;

    @Option(name = "--javascript-dependencyOutputFile")
    public File javascriptDependencyOutputFile;

    public void load(@Nonnull final ClosureOptions closureOptions) {

        if (verbose != null) {
            closureOptions.setVerbose(verbose);
        }

        if (outputDirectory != null) {
            closureOptions.setOutputDirectory(outputDirectory);
        }

        if (scriptFile != null) {
            closureOptions.setJavascriptOutputFile(scriptFile);
        }

        if (javascriptDefinesOutputFile != null) {
            closureOptions.setJavascriptDefinesOutputFile(
                    javascriptDefinesOutputFile);
        }

        if (javascriptDependencyOutputFile != null) {
            closureOptions.setJavascriptDependencyOutputFile(
                    javascriptDependencyOutputFile);
        }

        if (compile != null) {
            closureOptions.setShouldCompile(compile);
        }

        if (debug != null) {
            closureOptions.setShouldDebug(debug);
        }
    }
}
