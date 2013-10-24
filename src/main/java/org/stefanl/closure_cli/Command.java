package org.stefanl.closure_cli;

public enum Command {

    INITIALIZE,
    BUILD,
    HELP,
    EXIT,
    STYLESHEETS,
    TEMPLATES,
    JAVASCRIPT,
    HTML;

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }
}
