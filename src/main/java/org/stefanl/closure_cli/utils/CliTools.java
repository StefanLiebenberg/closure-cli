package org.stefanl.closure_cli.utils;

import org.stefanl.closure_utilities.utilities.FS;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Collection;

public class CliTools {


    static <A> int maxEntrySize(@Nonnull final Collection<A> entries,
                                @Nonnull final Counter<A> counter) {
        int n = 0, l;
        for (A entry : entries) {
            if (entry != null) {
                l = counter.count(entry);
                if (l > n) {
                    n = l;
                }
            }
        }
        return n;
    }

    private static Counter<File> FILENAME_SIZE_COUNTER =
            new Counter<File>() {
                @Override
                public int count(@Nonnull File item) {
                    return item.getPath().length();
                }
            };


    private static final int MIN = 20;

    public static void printEntry(
            @Nonnull final String key,
            @Nonnull final String value,
            @Nonnull final String prefix,
            @Nonnull final PrintStream printStream,
            final int n) {
        printStream.print(prefix);
        printStream.print(key);
        printStream.print(" ");
        for (int i = Math.max(MIN, key.length()); i < n; i++) {
            printStream.print(".");
        }
        printStream.print(" : ");
        printStream.println(value);
    }

    @Nullable
    public static File findExistingFileFromCollection(
            @Nonnull final Collection<File> files,
            @Nonnull final String prefix,
            @Nonnull final PrintStream printStream) {
        final int n = maxEntrySize(files, FILENAME_SIZE_COUNTER);
        String path;
        for (File file : files) {
            if (file != null) {
                path = file.getPath();
                if (file.exists()) {
                    printEntry(path, "Found", prefix, printStream, n);
                    return file;
                } else {
                    printEntry(path, "Not Found", prefix, printStream, n);
                }
            }
        }
        return null;
    }

    @Nullable
    private static <A> A findFirstNotnullEntry(
            @Nonnull final Collection<A> entries) {
        for (A entry : entries) {
            if (entry != null) {
                return entry;
            }
        }
        return null;
    }

    @Nonnull
    public static File findOrCreateFileFromCollection(
            @Nonnull final Collection<File> files,
            @Nonnull final String contentToCreate,
            @Nonnull final String prefix,
            @Nonnull final PrintStream printStream) throws IOException {
        File foundFile =
                findExistingFileFromCollection(files, prefix, printStream);
        if (foundFile != null) {
            return foundFile;
        } else {
            foundFile = findFirstNotnullEntry(files);
            if (foundFile != null) {
                FS.write(foundFile, contentToCreate);
                return foundFile;
            } else {
                throw new NullPointerException("Cannot find a suitable file.");
            }
        }
    }

    public static void ensureDirectory(
            @Nonnull final File directory,
            @Nonnull final String prefix,
            @Nonnull final PrintStream printStream,
            final int n) {
        final String path = directory.getPath();
        if (directory.exists()) {
            printEntry(path, "exists", prefix, printStream, n);
        } else {
            if (directory.mkdirs()) {
                printEntry(path, "created", prefix, printStream, n);
            } else {
                printEntry(path, "missing", prefix, printStream, n);
            }

        }
    }

    public static void ensureDirectories(
            @Nonnull Collection<File> directories,
            @Nonnull String prefix,
            @Nonnull PrintStream printStream) {
        if (!directories.isEmpty()) {
            final int n = maxEntrySize(directories, FILENAME_SIZE_COUNTER) + 5;
            for (File directory : directories) {
                if (directory != null) {
                    ensureDirectory(directory, prefix, printStream, n);
                }
            }
        }
    }
}
