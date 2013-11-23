package org.stefanl.closure_cli.runners;


import org.stefanl.closure_cli.config.ClosureConfig;
import org.stefanl.closure_utilities.closure.ClosureBuilder;
import org.stefanl.closure_utilities.closure.ClosureOptions;

import javax.annotation.Nonnull;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

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


    @Override
    public void run(@Nonnull final String... args) throws Exception {

        List<ClosureBuilder.BuildCommand> buildCommands = new ArrayList<>();

        if (args.length != 0) {
            for (String arg : args) {
                buildCommands.add(ClosureBuilder.BuildCommand.fromText(arg));
            }
        } else {
            buildCommands.addAll(closureConfig.getBuildCommands());
        }

        builder.buildCommands(closureOptions,
                buildCommands.toArray(
                        new ClosureBuilder.BuildCommand[buildCommands.size()]));
    }

    @Override
    public void help() throws Exception {
        log("Usage: build <command>");
        log("       Commands allowed are: ");
        for (ClosureBuilder.BuildCommand buildCommand : ClosureBuilder
                .BuildCommand.values()) {
            log(buildCommand);
        }

    }
}
