package org.stefanl.closure_cli.utils;

import javax.annotation.Nonnull;

public abstract class Counter<A> {
    public abstract int count(@Nonnull A item);
}
