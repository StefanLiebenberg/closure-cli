package org.stefanl.closure_cli;


import javax.annotation.Nonnull;

public enum BuildCommand {

    ALL, GSS, SOY, JS, HTML;

    @Override
    public String toString() {
        return name().toLowerCase();
    }

    @Nonnull
    public static BuildCommand fromText(String text) {
        return valueOf(text.trim().toUpperCase());
    }
}
