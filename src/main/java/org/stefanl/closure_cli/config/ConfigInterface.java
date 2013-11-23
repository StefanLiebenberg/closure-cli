package org.stefanl.closure_cli.config;

import org.stefanl.closure_utilities.closure.ClosureOptions;

import javax.annotation.Nonnull;

public interface ConfigInterface {
    public void load(@Nonnull ClosureOptions closureOptions);
}
