package org.stefanl.closure_cli.runners;


import org.stefanl.closure_utilities.closure.ClosureOptions;

import javax.annotation.Nonnull;

public class ReplRunner implements RunnerInterface {

    public final MainRunner mainRunner;

    public ReplRunner(MainRunner mainRunner) {
        this.mainRunner = mainRunner;
    }

    @Override
    public void help() throws Exception {

    }

    @Override
    public void run(@Nonnull String[] args) throws Exception {

    }
}
