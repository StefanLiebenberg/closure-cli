package org.stefanl.closure_cli.runners;

import javax.annotation.Nonnull;

public class CLIRunner implements RunnerInterface {

    private final MainRunner mainRunner;

    public CLIRunner(MainRunner mainRunner) {
        this.mainRunner = mainRunner;
    }

    @Override
    public void help() throws Exception {

    }

    @Override
    public void run(@Nonnull String[] args) throws Exception {
        mainRunner.run(args);
    }
}
