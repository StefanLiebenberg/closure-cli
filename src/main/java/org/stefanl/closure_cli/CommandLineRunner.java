package org.stefanl.closure_cli;

import com.esotericsoftware.yamlbeans.YamlConfig;
import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;
import com.esotericsoftware.yamlbeans.YamlWriter;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import jline.Terminal;
import jline.console.ConsoleReader;
import jline.console.completer.Completer;
import jline.console.completer.StringsCompleter;
import org.kohsuke.args4j.CmdLineParser;
import org.stefanl.closure_cli.config.ClosureConfig;
import org.stefanl.closure_cli.config.ConfigYamlReader;
import org.stefanl.closure_cli.parser.CommandCLIConfigurable;
import org.stefanl.closure_cli.runners.MainRunner;
import org.stefanl.closure_utilities.closure.ClosureOptions;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.*;
import java.util.LinkedList;
import java.util.List;

public class CommandLineRunner {

    public static Boolean isTesting = false;

    public static void setTesting() {
        isTesting = true;
    }

    public final MainRunner mainRunner;
    private final InputStream inputStream;
    private final OutputStream outputStream;

    private CommandLineRunner(
            @Nonnull final ClosureOptions closureOptions,
            @Nonnull final ClosureConfig closureConfig) {
        this.mainRunner = new MainRunner(closureOptions, closureConfig);
        this.inputStream = System.in;
        this.outputStream = System.out;
    }


    public String getSampleConfigFileContent(ClosureConfig options) {
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

    public Function<String, File> STRING_TO_FILE =
            new Function<String, File>() {
                @Nullable
                @Override
                public File apply(@Nullable String input) {
                    return new File(input);
                }
            };


    public final static String PROMPT = " closure-cli > ";


    private static ClosureConfig getConfigOptions(
            @Nonnull final File configFile) throws IOException {
        try (FileReader fileReader = new FileReader(configFile)) {
            final YamlReader yamlReader = new YamlReader(fileReader);
            final ClosureConfig configOptions =
                    yamlReader.read(ClosureConfig.class);
            yamlReader.close();
            if (configOptions != null) {
                return configOptions;
            } else {
                return ConfigurationFactory.createEmpty();
            }
        }
    }

    @Nonnull
    public static File getWorkingDirectory(
            @Nonnull final CommandCLIConfigurable configurable) {

        if (configurable.pwdDirectory != null) {
            return configurable.pwdDirectory.getAbsoluteFile();
        }

        return new File("").getAbsoluteFile();
    }

    private static final List<String> CONFIG_FILES =
            Lists.newArrayList("closure.yaml", "config/closure.yaml");

    public static File getConfigurationFile(
            @Nonnull final CommandCLIConfigurable configurable) {

        if (configurable.configFile != null) {

            if (!configurable.configFile.exists()) {
                throw new RuntimeException("Configuration file " +
                        configurable.configFile + " does not exist");
            }

            return configurable.configFile;
        }

        File pwd = getWorkingDirectory(configurable);

        for (String configFileName : CONFIG_FILES) {
            File configFile = new File(pwd, configFileName);
            if (configFile.exists()) {
                return configFile;
            }
        }

        throw new RuntimeException("No config file found in working " +
                "directory: " + pwd);
    }

    @Nonnull
    public static ClosureConfig getClosureConfig(
            @Nonnull final CommandCLIConfigurable configurable)
            throws IOException {
        final File pwd = getWorkingDirectory(configurable);
        final File cfg = getConfigurationFile(configurable);
        final ConfigYamlReader configYamlReader = new ConfigYamlReader(pwd);
        final ClosureConfig closureConfig = configYamlReader.read(cfg);
        if (closureConfig == null) {
            throw new RuntimeException("Fatal, failed to create closure " +
                    "config.");
        }
        return closureConfig;

    }

    public static ClosureOptions getClosureOptions(
            @Nonnull final CommandCLIConfigurable configurable,
            @Nonnull final ClosureConfig closureConfig) {
        final ClosureOptions closureOptions = new ClosureOptions();
        closureConfig.load(closureOptions);
        configurable.load(closureOptions);
        return closureOptions;
    }

    public void run(final String... args) throws Exception {
        mainRunner.run(args);
    }

    private LinkedList<Completer> getCompleters() {
        final LinkedList<Completer> completers = new LinkedList<>();
        List<String> stringCommands =
                Lists.transform(Lists.newArrayList(Command.values()),
                        new Function<Command,
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
        return completers;
    }

    public void interactive() throws IOException {
        final ConsoleReader reader = new ConsoleReader(System.in, System.out);
        reader.setPrompt(PROMPT);
        reader.setBellEnabled(false);
        final Terminal terminal = reader.getTerminal();
        terminal.setEchoEnabled(false);
        for (Completer c : getCompleters()) {
            reader.addCompleter(c);
        }

        final PrintWriter out = new PrintWriter(reader.getOutput());
        String line;
        while ((line = reader.readLine()) != null) {
            try {
                String[] args = line.split(" ");
                if (args.length != 0) {
                    String stringCommand = args[0];
                    if (stringCommand.trim().equalsIgnoreCase("exit")) {
                        out.close();
                        System.exit(0);
                    }
                    run(args);
                }
            } catch (Exception exception) {
                out.println("Exception occurred.");
                exception.printStackTrace(out);
            }
            out.flush();
        }
        out.close();

    }

    protected static void mainInternal(String... args) throws Exception {
        final CommandCLIConfigurable configurable = new
                CommandCLIConfigurable();
        final CmdLineParser cmdLineParser = new CmdLineParser(configurable);
        cmdLineParser.parseArgument(args);
        ClosureConfig closureConfig = getClosureConfig(configurable);
        ClosureOptions closureOptions = getClosureOptions(configurable,
                closureConfig);
        CommandLineRunner commandLineRunner =
                new CommandLineRunner(closureOptions, closureConfig);
        if (configurable.args != null && configurable.args.length != 0) {
            commandLineRunner.run(configurable.args);
        } else {
            commandLineRunner.interactive();
        }
    }

    public static void main(String... args) throws Exception {
        mainInternal(args);
        System.exit(0);
    }
}
