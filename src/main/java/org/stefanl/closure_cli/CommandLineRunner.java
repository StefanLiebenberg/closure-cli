package org.stefanl.closure_cli;

import com.esotericsoftware.yamlbeans.YamlConfig;
import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;
import com.esotericsoftware.yamlbeans.YamlWriter;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import jline.Terminal;
import jline.console.ConsoleReader;
import jline.console.completer.Completer;
import jline.console.completer.StringsCompleter;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.stefanl.closure_cli.utils.CliTools;
import org.stefanl.closure_cli.utils.CommandLineProcess;
import org.stefanl.closure_cli.utils.CommandLineProcessException;
import org.stefanl.closure_utilities.closure.ClosureBuilder;
import org.stefanl.closure_utilities.internal.BuildException;
import org.stefanl.closure_utilities.javascript.TestRunner;
import org.stefanl.closure_utilities.utilities.FS;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.*;
import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class CommandLineRunner {

    public final ClosureBuilder builder = new ClosureBuilder();

    public final CommandLineOptions commandLineOptions;

    public ConfigurationOptions configurationOptions;

    public CommandLineRunner(
            @Nonnull final CommandLineOptions commandLineOptions) {
        this.commandLineOptions = commandLineOptions;
    }

    public void runCommand(
            @Nonnull final Command command,
            @Nonnull final PrintWriter output)
            throws Exception {
        switch (command) {
            case BUILD:
                runBuild();
                break;
            case JAVASCRIPT:
                runJavascript();
                break;
            case TEMPLATES:
                runTemplates();
                break;
            case STYLESHEETS:
                runStylesheets();
            case HTML:
                runHtml();
                break;
            case INITIALIZE:
                runInitialize();
                break;
            case TEST:
                runTest();
                break;
            case HELP:
                output.print("Commands: ");
                String delim = "";
                for (Command c : Command.values()) {
                    output.print(delim);
                    output.print(c.toString());
                    delim = ", ";
                }
                output.println(".");
                break;
        }
    }

    @Nonnull
    public File getConfigurationFile() {
        if (commandLineOptions.configFile != null) {
            return commandLineOptions.configFile;
        } else {
            File configFile = DEFAULT_CONFIG_FILE;
            if (!configFile.exists()) {
                for (File file : ALTERNATE_CONFIG_LOCATIONS) {
                    if (file.exists()) {
                        configFile = file;
                    }
                }
            }
            return configFile;
        }
    }

    public void loadConfig() throws IOException {
        configurationOptions = loadConfigurationFromFile(getConfigurationFile
                ());
        builder.setBuildOptions(configurationOptions.getBuildOptions());
    }

    public PrintWriter getPrintWriter() {
        return new PrintWriter(System.out);
    }

    public void run() throws Exception {
        loadConfig();

        final Command[] commands = commandLineOptions.commands;
        if (commands != null) {
            final PrintWriter printWriter = getPrintWriter();
            for (Command command : commands) {
                runCommand(command, printWriter);
            }
            printWriter.flush();
            printWriter.close();
        } else {
            runInteractiveCommandLine();
        }
    }

    public void runBuild() throws BuildException, IOException {
        loadConfig();
        if (configurationOptions.defaultBuild != null) {
            // not sure.
        } else {
            builder.setBuildOptions(configurationOptions.getBuildOptions());
            builder.build();
        }
    }


    public static List<Command> SUPPORTED_COMMANDS =
            Lists.newArrayList(
                    Command.EXIT,
                    Command.HELP,
                    Command.BUILD,
                    Command.HTML,
                    Command.STYLESHEETS,
                    Command.JAVASCRIPT,
                    Command.INITIALIZE);

    @Nonnull
    private static Boolean isCommand(
            @Nonnull final Command command,
            @Nonnull final String commandName) {
        return command.toString().equalsIgnoreCase(commandName);
    }

    @Nullable
    private static Command selectCommand(
            @Nonnull final Collection<Command> commands,
            @Nullable final String commandName) {
        if (commandName != null && !commandName.isEmpty()) {
            final String strippedCommand = commandName.trim();
            for (Command command : commands) {
                if (isCommand(command, strippedCommand)) {
                    return command;
                }
            }
        }
        return null;
    }


    @Nonnull
    private static Command selectCommand(
            @Nonnull final Collection<Command> commands,
            @Nullable final String commandName,
            @Nonnull final Command defaultCommand) {
        Command command = selectCommand(commands, commandName);
        if (command != null) {
            return command;
        } else {
            return defaultCommand;
        }
    }


    @Nonnull
    public ConfigurationOptions loadConfigurationFromFile(
            @Nonnull final File configurationFile) throws IOException {
        try (FileReader fileReader = new FileReader(configurationFile)) {
            final YamlReader yamlReader = new YamlReader(fileReader);
            final ConfigurationOptions configOptions =
                    yamlReader.read(ConfigurationOptions.class);
            yamlReader.close();
            if (configOptions != null) {
                return configOptions;
            } else {
                return ConfigurationFactory.createEmpty();
            }
        }
    }


    public void applyCommandLineOptions(
            @Nonnull ConfigurationOptions configOptions,
            @Nonnull CommandLineOptions cliOptions) {
        if (cliOptions.shouldCompile != null) {
            configOptions.shouldCompile = cliOptions.shouldCompile;
        }
        if (cliOptions.shouldDebug != null) {
            configOptions.shouldDebug = cliOptions.shouldDebug;
        }
    }


    @Nonnull
    public ConfigurationOptions getConfigurationOptions(
            @Nonnull final File configFile) throws IOException {
        final ConfigurationOptions configOptions =
                loadConfigurationFromFile(configFile);
        applyCommandLineOptions(configOptions, commandLineOptions);
        return configOptions;
    }


    public void runHtml() throws BuildException, IOException {
        loadConfig();
        builder.buildHtml();
    }

    public void runTemplates() throws BuildException, IOException {
        loadConfig();
        builder.buildSoy();
    }

    public void runStylesheets() throws BuildException, IOException {
        loadConfig();
        builder.buildGss();
    }

    public void runTest() throws Exception {
        ImmutableSet<File> sourceDirectories =
                configurationOptions.getJavascriptSourceDirectories();
        ImmutableSet<File> testDirectories =
                configurationOptions.getJavascriptTestDirectories();
        if (testDirectories != null && sourceDirectories != null) {
            System.out.println("Locating Testing...");
            TestRunner runner;
            for (File testFile : FS.find(testDirectories, "test.js")) {
                System.out.println("Testing " + testFile.getPath());
                runner = new TestRunner(testFile, sourceDirectories);
                runner.run();
            }
        } else {
            System.out.println("No Tests Found");
            System.out.println(sourceDirectories);
            System.out.println(testDirectories);
        }
    }

    public void runJavascript() throws BuildException, IOException {
        builder.buildJs();
    }


    public String getSampleConfigFileContent(ConfigurationOptions options) {
        try {
            StringWriter stringWriter = new StringWriter();
            YamlConfig yamlConfig = new YamlConfig();
            yamlConfig.writeConfig.setAlwaysWriteClassname(false);
            yamlConfig.writeConfig.setWriteRootTags(false);
            YamlWriter yamlWriter = new YamlWriter(stringWriter, yamlConfig);
            yamlWriter.write(options);
            yamlWriter.close();
            return stringWriter.toString();
        } catch (YamlException yamlException) {
            throw new RuntimeException(yamlException);
        }
    }

    @Nonnull
    public static <A, B> Function<A, B> getConvertor(
            @Nonnull final Class<A> aClass,
            @Nonnull final Class<B> bClass) {
        try {
            final Constructor<B> constructor = bClass.getConstructor(aClass);
            return new Function<A, B>() {
                @Nullable
                @Override
                public B apply(@Nullable A input) {
                    try {
                        return constructor.newInstance(input);
                    } catch (ReflectiveOperationException reflect) {
                        throw new RuntimeException(reflect);
                    }
                }
            };
        } catch (ReflectiveOperationException reflect) {
            throw new RuntimeException(reflect);
        }
    }

    private static Function<String, File> STRING_TO_FILE =
            getConvertor(String.class, File.class);

    public static File DEFAULT_CONFIG_FILE =
            new File("closure.yml");

    public static List<File> ALTERNATE_CONFIG_LOCATIONS =
            Lists.newArrayList(new File("config/closure.yml"));

    @Nonnull
    public static ImmutableList<File> getPossibleConfigFiles() {
        ImmutableList.Builder<File> listBuilder = new ImmutableList.Builder<>();
        listBuilder.add(DEFAULT_CONFIG_FILE);
        listBuilder.addAll(ALTERNATE_CONFIG_LOCATIONS);
        return listBuilder.build();
    }

    public void runInitialize() throws IOException {
        System.out.println("checking for configuration file:");

        final File configFile = getConfigurationFile();
        if (configFile.exists()) {
            configurationOptions = loadConfigurationFromFile(configFile);
        } else {
            configurationOptions = ConfigurationFactory.create(
                    ConfigurationFactory.Flavour.BASIC,
                    commandLineOptions.outputDirectory.getPath());
            final String optionsContent = getSampleConfigFileContent
                    (configurationOptions);
            if (!commandLineOptions.isDryBuild) {
                FS.write(configFile, optionsContent);
            }
        }


        System.out.println("Checking for javascript source directories...");
        if (configurationOptions.javascriptSourceDirectories != null &&
                !configurationOptions.javascriptSourceDirectories.isEmpty()) {
            final Collection<File> javascriptSourceDirs =
                    Collections2.transform(configurationOptions
                            .javascriptSourceDirectories, STRING_TO_FILE);
            CliTools.ensureDirectories(javascriptSourceDirs, "   ",
                    System.out, commandLineOptions.isDryBuild);
        } else {
            System.out.println("WARNING: please specify javascript " +
                    "directories...");
        }

        System.out.println("Checking for soy source directories...");
        if (configurationOptions.soySourceDirectories != null &&
                !configurationOptions.soySourceDirectories.isEmpty()) {
            final Collection<File> soySourceDirectories =
                    Collections2.transform(configurationOptions
                            .soySourceDirectories,
                            STRING_TO_FILE);
            CliTools.ensureDirectories(soySourceDirectories, "   ",
                    System.out, commandLineOptions.isDryBuild);
        } else {
            System.out.println("WARNING: please specify soy " +
                    "directories...");
        }

        System.out.println("Checking for gss source directories...");
        if (configurationOptions.gssSourceDirectories != null &&
                !configurationOptions.gssSourceDirectories.isEmpty()) {
            final Collection<File> gssSourceDirectories =
                    Collections2.transform(configurationOptions
                            .gssSourceDirectories,
                            STRING_TO_FILE);
            CliTools.ensureDirectories(gssSourceDirectories, "   ",
                    System.out, commandLineOptions.isDryBuild);
        } else {
            System.out.println("WARNING: please specify gss " +
                    "directories...");
        }
    }


    public final static String PROMPT = " closure-cli > ";

    private void runCommandLine(@Nonnull CommandLineProcess process) {
        try {
            final ConsoleReader reader = new ConsoleReader();
            reader.setPrompt(PROMPT);
            reader.setBellEnabled(false);
            final Terminal terminal = reader.getTerminal();
            terminal.setEchoEnabled(false);
            final LinkedList<Completer> completers = new LinkedList<>();

            List<String> stringCommands =
                    Lists.transform(SUPPORTED_COMMANDS, new Function<Command,
                            String>() {
                        @Nullable
                        @Override
                        public String apply(@Nullable Command input) {
                            if (input != null) {
                                return input.toString().toLowerCase();
                            } else {
                                return null;
                            }
                        }
                    });

            completers.add(new StringsCompleter(stringCommands));

            for (Completer c : completers) {
                reader.addCompleter(c);
            }

            final PrintWriter out = new PrintWriter(reader.getOutput());

            String line;
            while ((line = reader.readLine()) != null) {
                process.process(line, out);
                out.flush();
            }
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void runInteractiveCommandLine() throws IOException {
        runCommandLine(new CommandLineProcess<CommandLineRunner>(this) {
            @Override
            public void process(@Nonnull String line,
                                @Nonnull PrintWriter out)
                    throws CommandLineProcessException {
                try {
                    CommandLineOptions cmdOptions =
                            parseArguments(line.split(" "), null);
                    final Command[] commands = cmdOptions.commands;
                    if (commands != null && commands.length > 0) {
                        for (Command command : commands) {
                            switch (command) {
                                case EXIT:
                                    out.println("exiting...");
                                    out.close();
                                    System.exit(0);
                                    break;
                                default:
                                    target.runCommand(command, out);
                                    break;
                            }
                        }
                    } else {
                        System.err.println("err??");
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        });
    }

    public static CommandLineOptions parseArguments(
            @Nonnull final String[] args,
            @Nullable final OutputStream out)
            throws CmdLineException {
        final CommandLineOptions options = new CommandLineOptions();
        final CmdLineParser parser = new CmdLineParser(options);
        parser.parseArgument(args);

        if (out != null && options.showHelp) {
            parser.printUsage(out);
            System.exit(0);
        }

        return options;

    }


    public static void main(String[] args) throws Exception {
        CommandLineRunner runner =
                new CommandLineRunner(parseArguments(args, System.out));
        runner.run();
        System.exit(0);
    }
}
