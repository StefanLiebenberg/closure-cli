package liebenberg.closure_cli.config;

import liebenberg.closure_utilities.closure.ClosureOptions;

import javax.annotation.Nonnull;

public interface ConfigInterface {
    public void load(@Nonnull ClosureOptions closureOptions);
}
