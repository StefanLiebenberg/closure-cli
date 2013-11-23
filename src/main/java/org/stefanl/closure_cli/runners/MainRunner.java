package org.stefanl.closure_cli.runners;


import org.stefanl.closure_cli.Command;
import org.stefanl.closure_cli.config.ClosureConfig;
import org.stefanl.closure_utilities.closure.ClosureOptions;

import javax.annotation.Nonnull;
import java.io.PrintStream;
import java.util.Arrays;

public class MainRunner implements RunnerInterface {

    private final BuildRunner buildRunner;

    private final InitializeRunner initializeRunner;

    private final TestCLIRunner testCLIRunner;

    private final PrintStream printStream;

    public MainRunner(@Nonnull ClosureOptions closureOptions,
                      @Nonnull ClosureConfig closureConfig) {
        this(closureOptions, closureConfig, System.out);
    }

    public MainRunner(@Nonnull ClosureOptions closureOptions,
                      @Nonnull ClosureConfig closureConfig,
                      @Nonnull PrintStream printStream) {
        this.buildRunner = new BuildRunner(closureOptions, closureConfig);
        this.testCLIRunner = new TestCLIRunner(closureOptions);
        this.initializeRunner = new InitializeRunner();
        this.printStream = printStream;
    }

    @Override
    public void help() throws Exception {
        printStream.println("Main Runner Available Commands: ");
        for (Command command : Command.values()) {
            printStream.print(" ");
            printStream.print(command.toString());
        }
        printStream.println(".");
        printStream.flush();
    }

    public void run(@Nonnull final Command command, final String... args)
            throws Exception {
        switch (command) {
            case BUILD:
                buildRunner.run(args);
                break;
            case TEST:
                testCLIRunner.run(args);
                break;
            case INITIALIZE:
                initializeRunner.run(args);
                break;
            case HELP:
                help();
                break;
        }
    }

    @Override
    public void run(@Nonnull String[] args) throws Exception {
        String stringCommand = args[0];
        String[] otherArgs = Arrays.copyOfRange(args, 1, args.length);
        try {
            run(Command.fromString(stringCommand), otherArgs);
        } catch (IllegalArgumentException illegalArgument) {
            printStream.println("Unknown Command: " + stringCommand);
            run(Command.HELP);
        }
    }
}
