package org.stefanl.closure_cli.runners;


import org.stefanl.closure_cli.BuildCommand;
import org.stefanl.closure_cli.config.ClosureConfig;
import org.stefanl.closure_utilities.closure.ClosureBuilder;
import org.stefanl.closure_utilities.closure.ClosureOptions;
import org.stefanl.closure_utilities.internal.BuildException;

import javax.annotation.Nonnull;
import java.io.PrintStream;

public class BuildRunner
        extends AbstractRunner
        implements RunnerInterface {

    private final ClosureBuilder builder = new ClosureBuilder();

    private final ClosureOptions closureOptions;

    private final ClosureConfig closureConfig;

    public BuildRunner(@Nonnull final ClosureOptions options,
                       @Nonnull final ClosureConfig config) {
        super();
        closureOptions = options;
        closureConfig = config;
    }

    public BuildRunner(@Nonnull final ClosureOptions options,
                       @Nonnull final ClosureConfig config,
                       @Nonnull final PrintStream printStream) {
        super(printStream);
        closureOptions = options;
        closureConfig = config;
    }

    public void run(@Nonnull final BuildCommand buildCommand)
            throws BuildException {
        log("Building..." + buildCommand.toString());
        switch (buildCommand) {
            case ALL:
                builder.build(closureOptions);
                break;
            case GSS:
                builder.buildGssOnly(closureOptions);
                break;
            case HTML:
                builder.buildHtmlOnly(closureOptions);
                break;
            case JS:
                builder.buildJsOnly(closureOptions);
                break;
            case SOY:
                builder.buildSoyOnly(closureOptions);
                break;
        }
        log("Done.");
    }

    @Override
    public void run(@Nonnull final String... args) throws Exception {
        if (args.length != 0) {
            for (String arg : args) {
                run(BuildCommand.fromText(arg));
            }
        } else {
            for (BuildCommand buildCommand : closureConfig.getBuildCommands()) {
                run(buildCommand);
            }
        }
    }

    @Override
    public void help() throws Exception {
        log("Usage: build <command>");
        log("       Commands allowed are: ");
        for (BuildCommand buildCommand : BuildCommand.values()) {
            log(buildCommand);
        }

    }
}
