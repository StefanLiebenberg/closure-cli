package org.stefanl.closure_cli.utils;


public class CommandLineProcessException extends Exception {
    public CommandLineProcessException() {
    }

    public CommandLineProcessException(String message) {
        super(message);
    }

    public CommandLineProcessException(String message, Throwable cause) {
        super(message, cause);
    }

    public CommandLineProcessException(Throwable cause) {
        super(cause);
    }

    public CommandLineProcessException(String message, Throwable cause,
                                       boolean enableSuppression,
                                       boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
