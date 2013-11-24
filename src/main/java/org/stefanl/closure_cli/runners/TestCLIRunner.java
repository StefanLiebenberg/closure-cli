package org.stefanl.closure_cli.runners;


import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import org.stefanl.closure_utilities.closure.ClosureBuilder;
import org.stefanl.closure_utilities.closure.ClosureOptions;
import org.stefanl.closure_utilities.closure.ClosureResult;
import org.stefanl.closure_utilities.javascript.TestRunner;
import org.stefanl.closure_utilities.utilities.FS;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

public class TestCLIRunner
        extends AbstractRunner
        implements RunnerInterface {

    private final ClosureOptions closureOptions;

    public enum TestCommand {
        RUN, CREATE;

        public static TestCommand fromText(String value) {
            return valueOf(value.trim().toUpperCase());
        }
    }

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
    private HashSet<File> getTestFilesInArray(@Nonnull final String[]
                                                      args) {
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

    public void runTest(@Nonnull final File testFile,
                        @Nonnull final Collection<File> sourceDirectories)
            throws Exception {
        log("Running test file: " + testFile);
        new TestRunner(testFile, sourceDirectories).run();
    }

    public void runTests(@Nonnull final String... args) throws Exception {
        ImmutableSet.Builder<File> sourceDirectoryBuilder =
                new ImmutableSet.Builder<>();
        sourceDirectoryBuilder.addAll(
                closureOptions.getJavascriptSourceDirectories(true));
        File templatesDir = buildTemplates();
        if (templatesDir != null) {
            sourceDirectoryBuilder.add(templatesDir);
        }
        ImmutableSet<File> sourceDirectories = sourceDirectoryBuilder.build();
        if (sourceDirectories != null && !sourceDirectories.isEmpty()) {
            Integer failedCount = 0, count = 0;
            for (File testFile : getTestFiles(args, sourceDirectories)) {
                try {
                    count++;
                    runTest(testFile, sourceDirectories);
                } catch (TestRunner.TestException testException) {
                    testException.printStackTrace();
                    failedCount++;
                }
            }
            log("Tests: " + count + ", Failed: " + failedCount);
            if (failedCount > 0) {
                log("Test Failure");
                System.exit(1);
            } else {
                log("Test Success");
            }
        }
    }

    public static ClosureBuilder closureBuilder = new ClosureBuilder();

    public void createTest(@Nonnull final File testFile,
                           @Nonnull final Collection<File> sourceDirectories)
            throws Exception {
        log("Creating test file: " + testFile);
        ClosureOptions closureOpts = new ClosureOptions();
        closureOpts.setJavascriptSourceDirectories(sourceDirectories);
        closureOpts.setOutputDirectory(closureOptions.getOutputDirectory());
        closureOpts.setOutputHtmlFile(new File(testFile.getPath() + ".html"));
        closureOpts.setJavascriptEntryFiles(Lists.newArrayList(testFile));
        closureBuilder.buildCommands(closureOpts,
                ClosureBuilder.BuildCommand.JAVASCRIPT,
                ClosureBuilder.BuildCommand.HTML);
    }

    @Nullable
    private File buildTemplates() throws Exception {
        ClosureOptions opts = new ClosureOptions();
        File soyOuput = closureOptions.getSoyOutputDirectory();
        ImmutableCollection<File> soySources = closureOptions
                .getSoySourceDirectories();
        if (soySources != null) {
            if (soyOuput != null) {
                opts.setSoyOutputDirectory(soyOuput);
            }
            opts.setSoySourceDirectories(soySources);
            opts.setOutputDirectory(closureOptions.getOutputDirectory());
            ClosureResult result = closureBuilder.buildCommands(opts,
                    ClosureBuilder.BuildCommand.TEMPLATES);
            return result.getSoyOutputDirectory();
        } else {
            return null;
        }
    }

    public void createTests(@Nonnull final String... args) throws Exception {
        ImmutableSet.Builder<File> sourceDirectoryBuilder =
                new ImmutableSet.Builder<>();
        sourceDirectoryBuilder.addAll(
                closureOptions.getJavascriptSourceDirectories(true));
        File templatesDir = buildTemplates();
        if (templatesDir != null) {
            sourceDirectoryBuilder.add(templatesDir);
        }
        ImmutableSet<File> sourceDirectories = sourceDirectoryBuilder.build();
        if (sourceDirectories != null && !sourceDirectories.isEmpty()) {
            for (File testFile : getTestFiles(args, sourceDirectories)) {
                createTest(testFile, sourceDirectories);
            }
        }
    }

    public void run(@Nonnull final TestCommand testCommand,
                    @Nonnull final String... args)
            throws Exception {
        switch (testCommand) {
            case RUN:
                runTests(args);
                break;
            case CREATE:
                createTests(args);
                break;
        }
    }

    @Override
    public void run(@Nonnull final String[] args) throws Exception {
        final String stringCommand = args[0];
        final String[] otherArgs = Arrays.copyOfRange(args, 1, args.length);
        run(TestCommand.fromText(stringCommand), otherArgs);
    }

    @Override
    public void help() throws Exception {
        log("Usage: test");
        log("       test  <paths>");
    }
}
