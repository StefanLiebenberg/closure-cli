package org.stefanl.closure_cli.runners;


import org.stefanl.closure_utilities.closure.ClosureOptions;
import org.stefanl.closure_utilities.javascript.TestRunner;
import org.stefanl.closure_utilities.utilities.FS;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.PrintStream;
import java.util.Collection;
import java.util.HashSet;

public class TestCLIRunner
        extends AbstractRunner
        implements RunnerInterface {

    private final ClosureOptions closureOptions;

    public TestCLIRunner(
            @Nonnull final ClosureOptions closureOptions) {
        super();
        this.closureOptions = closureOptions;
    }

    public TestCLIRunner(
            @Nonnull final ClosureOptions closureOptions,
            @Nonnull final PrintStream printStream) {
        super(printStream);
        this.closureOptions = closureOptions;
    }

    @Nonnull
    private HashSet<File> getTestFilesInArray(@Nonnull final String[] args) {
        final HashSet<File> testFiles = new HashSet<>();
        for (String arg : args) {
            File testFile = new File(arg);
            if (testFile.exists()) {
                testFiles.add(testFile);
            }
        }
        return testFiles;
    }

    @Nonnull
    private HashSet<File> getTestFilesFromDirectories(
            @Nonnull final Collection<File> srcDirectories) {
        return FS.find(srcDirectories, "test.js");
    }

    @Nonnull
    private HashSet<File> getTestFiles(
            @Nonnull final String[] args,
            @Nonnull final Collection<File> srcDirectories) {
        if (args.length != 0) {
            return getTestFilesInArray(args);
        } else {
            return getTestFilesFromDirectories(srcDirectories);
        }
    }

    @Override
    public void run(@Nonnull final String[] args) throws Exception {
        final Collection<File> sourceDirectories =
                closureOptions.getJavascriptSourceDirectories();
        if (sourceDirectories != null && !sourceDirectories.isEmpty()) {
            for (File testFile : getTestFiles(args, sourceDirectories)) {
                log("Running test file: " + testFile);
                new TestRunner(testFile, sourceDirectories).run();
            }
        }
    }

    @Override
    public void help() throws Exception {
        log("Usage: test");
        log("       test  <paths>");
    }
}
