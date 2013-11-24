package liebenberg.closure_cli.parser;


import org.kohsuke.args4j.Argument;

public class BaseCLIConfiguratable {
    @Argument(multiValued = true)
    public String[] args;
}
