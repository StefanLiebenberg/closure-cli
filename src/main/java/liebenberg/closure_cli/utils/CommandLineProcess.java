package liebenberg.closure_cli.utils;


import javax.annotation.Nonnull;
import java.io.PrintWriter;

public abstract class CommandLineProcess<A> {

    protected A target;

    public CommandLineProcess(A object) {
        target = object;
    }

    public abstract void process(@Nonnull String line,
                                 @Nonnull PrintWriter out)
            throws CommandLineProcessException;
}
