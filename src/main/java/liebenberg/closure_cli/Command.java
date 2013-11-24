package liebenberg.closure_cli;

import javax.annotation.Nonnull;

public enum Command {

    BUILD,
    INITIALIZE,
    HELP,
    TEST;

    @Override
    public String toString() {
        return super.name().toLowerCase();
    }

    @Nonnull
    public static Command fromString(String text) {
        return valueOf(text.trim().toUpperCase());
    }
}
