package org.stefanl.closure_cli.runners;

import javax.annotation.Nonnull;

public interface RunnerInterface {
    public void help() throws Exception;
    public void run(@Nonnull String[] args) throws Exception;
}
