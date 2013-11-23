package org.stefanl.closure_cli.parser;


import org.kohsuke.args4j.Argument;
import org.stefanl.closure_cli.Command;

public class BaseCLIConfiguratable {
    @Argument(multiValued = true)
    public String[] args;
}
