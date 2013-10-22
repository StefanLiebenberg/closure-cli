package org.stefanl.closure_cli;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.Option;

import java.io.File;

public class CommandLineOptions {

    @Argument()
    public String command;

    @Option(name = "--help")
    public Boolean showHelp = false;

    @Option(name = "--config")
    public File configFile;

}
