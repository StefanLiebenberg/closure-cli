package org.stefanl.closure_cli;


import javax.annotation.Nonnull;

public enum BuildCommand {

    ALL, STYLESHEETS, TEMPLATES, JAVASCRIPT, HTML;

    @Override
    public String toString() {
        return name().toLowerCase();
    }

    @Nonnull
    public static BuildCommand fromText(String text) {
        return valueOf(text.trim().toUpperCase());
    }
}
