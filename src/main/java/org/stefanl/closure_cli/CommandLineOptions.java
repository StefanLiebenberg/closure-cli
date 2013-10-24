package org.stefanl.closure_cli;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.Option;

import java.io.File;

public class CommandLineOptions {

    @Argument(usage = "The build command")
    public Command command;

    @Option(name = "--help", usage = "prints this message")
    public Boolean showHelp = false;

    @Option(name = "--config", usage = "specifies a alternate config file")
    public File configFile;

    @Option(name = "--shouldCompile", usage = "forces the build to compile")
    public Boolean shouldCompile;

    @Option(name = "--shouldDebug", usage = "forces the build to debug")
    public Boolean shouldDebug;

    @Option(name = "--dry", usage = "Does not alter any files. WARNING: Don't trust this.")
    public Boolean isDryBuild = false;

    @Option(name = "--output-directory", usage = "specify a alternate build " +
            "directory")
    public File outputDirectory;

}
