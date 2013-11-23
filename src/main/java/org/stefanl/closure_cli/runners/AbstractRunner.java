package org.stefanl.closure_cli.runners;

import javax.annotation.Nonnull;
import java.io.PrintStream;

public abstract class AbstractRunner implements RunnerInterface {

    private final PrintStream printStream;

    public AbstractRunner(@Nonnull final PrintStream printStream) {
        this.printStream = printStream;
    }

    public AbstractRunner() {
        this(System.out);
    }

    protected void log(@Nonnull final Object first,
                       @Nonnull final Object... args) {
        printStream.print(first);
        for (Object arg : args) {
            printStream.print(" ");
            printStream.print(arg);
        }
        printStream.println("");
        printStream.flush();
    }
}
