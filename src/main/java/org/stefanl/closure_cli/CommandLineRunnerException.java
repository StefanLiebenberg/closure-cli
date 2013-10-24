package org.stefanl.closure_cli;


public class CommandLineRunnerException extends Exception {
    public CommandLineRunnerException() {
    }

    public CommandLineRunnerException(String message) {
        super(message);
    }

    public CommandLineRunnerException(String message, Throwable cause) {
        super(message, cause);
    }

    public CommandLineRunnerException(Throwable cause) {
        super(cause);
    }

    public CommandLineRunnerException(String message, Throwable cause,
                                      boolean enableSuppression,
                                      boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
