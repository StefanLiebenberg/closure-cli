package liebenberg.closure_cli.runners;

import liebenberg.closure_cli.ConfigurationFactory;

import javax.annotation.Nonnull;

public class InitializeRunner implements RunnerInterface {

    @Override
    public void help() throws Exception {
        System.out.println("Printing the initializer help.");
    }

    @Override
    public void run(@Nonnull String[] args) throws Exception {

    }

    public void run(ConfigurationFactory.Flavour flavour) {

    }
}
