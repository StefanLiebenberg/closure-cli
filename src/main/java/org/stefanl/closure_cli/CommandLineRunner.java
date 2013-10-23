package org.stefanl.closure_cli;

import com.esotericsoftware.yamlbeans.YamlConfig;
import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;
import com.esotericsoftware.yamlbeans.YamlWriter;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import jline.Terminal;
import jline.console.ConsoleReader;
import jline.console.completer.Completer;
import jline.console.completer.StringsCompleter;
import org.codehaus.plexus.util.StringUtils;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.stefanl.closure_utilities.closure.ClosureBuildOptions;
import org.stefanl.closure_utilities.closure.ClosureBuilder;
import org.stefanl.closure_utilities.closure.iClosureBuildOptions;
import org.stefanl.closure_utilities.internal.BuildException;
import org.stefanl.closure_utilities.utilities.FsTool;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.*;
import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class CommandLineRunner {

    public final static ClosureBuilder builder =
            new ClosureBuilder();

    public enum Command {
        INITIALIZE,
        BUILD,
        HELP,
        EXIT,
        STYLESHEETS,
        TEMPLATES,
        JAVASCRIPT,
        HTML
    }

    public static List<Command> SUPPORTED_COMMANDS =
            Lists.newArrayList(
                    Command.EXIT,
                    Command.HELP,
                    Command.BUILD,
                    Command.HTML,
                    Command.STYLESHEETS,
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
        final String strippedCommand = StringUtils.strip(commandName);
        if (commandName != null && !commandName.isEmpty()) {
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


    public static File getExistingFile(@Nonnull final List<File> files) {
        for (File file : files) {
            if (file != null && file.exists()) {
                return file;
            }
        }
        return null;
    }

    public static List<File> getPossibleConfigFiles(@Nullable File
                                                            userConfigFile) {
        return Lists.newArrayList(
                userConfigFile,
                new File("closure.yml"),
                new File("config/closure.yml"));
    }

    public static File getConfigFile(File userConfigFile) {
        return getExistingFile(getPossibleConfigFiles(userConfigFile));
    }

    @Nonnull
    public static ConfigurationOptions loadConfigurationFromFile(
            @Nonnull final File configurationFile) throws IOException {
        try (FileReader fileReader = new FileReader(configurationFile)) {
            final YamlReader yamlReader = new YamlReader(fileReader);
            final ConfigurationOptions configOptions =
                    yamlReader.read(ConfigurationOptions.class);
            yamlReader.close();
            return configOptions;
        }
    }

    public static iClosureBuildOptions getBuildOptions(
            @Nullable final File userConfigFile) throws IOException {

        ClosureBuildOptions buildOptions = new ClosureBuildOptions();
        File configFile = getConfigFile(userConfigFile);
        if (configFile != null) {
            ConfigurationOptions configOptions =
                    loadConfigurationFromFile(configFile);

            if (configOptions.assetsDirectory != null) {
                buildOptions.setAssetsDirectory(new File(configOptions
                        .assetsDirectory));
            }

            if (configOptions.cssClassRenameMap != null) {
                buildOptions.setCssClassRenameMap(new File(configOptions
                        .cssClassRenameMap));
            }

            if (configOptions.javascriptEntryPoints != null) {
                buildOptions.setJavascriptEntryPoints(configOptions
                        .javascriptEntryPoints);
            }

            if (configOptions.javascriptSourceDirectories != null) {
                buildOptions.setJavascriptSourceDirectories(
                        Lists.transform(
                                configOptions.javascriptEntryPoints,
                                STRING_TO_FILE));
            }

            if (configOptions.outputDirectory != null) {
                buildOptions.setOutputDirectory(
                        new File(configOptions.outputDirectory));
            }

            if (configOptions.soyOutputDirectory != null) {
                buildOptions.setSoyOutputDirectory(new File(configOptions
                        .soyOutputDirectory));
            }

            if (configOptions.gssSourceDirectories != null) {
                buildOptions.setGssSourceDirectories(
                        Collections2.transform(
                                configOptions.gssSourceDirectories,
                                STRING_TO_FILE));
            }

            if (configOptions.soySourceDirectories != null) {
                buildOptions.setSoySourceDirectories(
                        Collections2.transform(
                                configOptions.soySourceDirectories,
                                STRING_TO_FILE
                        )
                );
            }

            if (configOptions.gssEntryPoints != null) {
                buildOptions.setGssEntryPoints(configOptions
                        .gssEntryPoints);
            }


        }
        return buildOptions;
    }


    public static void loadConfiguration(final File configFile)
            throws IOException {
        builder.reset();
        builder.setBuildOptions(getBuildOptions(configFile));
    }

    public static void runHtml() throws BuildException {
        builder.buildHtml();
    }

    public static void runTemplates() throws BuildException {
        builder.buildSoy();
    }

    public static void runBuild() throws BuildException {
        builder.build();
    }

    @Nonnull
    public static Integer maxFileEntry(@Nonnull final Collection<File> files) {
        Integer n = 0, length;
        String path;
        for (File f : files) {
            if (f != null) {
                path = f.getPath();
                length = path.length();
                if (length > n) {
                    n = length;
                }
            }
        }
        return n;
    }

    public static void printEntry(String entry, String value, String prefix,
                                  Integer n) {
        System.out.print(prefix);
        System.out.print(entry);
        for (Integer i = entry.length(); i < n; i++) {
            System.out.print(".");
        }
        System.out.print(" : ");
        System.out.println(value);
    }

    public static void ensureDirectories(Collection<File> directories,
                                         String prefix) {
        if (directories != null && !directories.isEmpty()) {
            final Integer n = maxFileEntry(directories) + 5;
            for (File directory : directories) {
                if (directory != null) {
                    String path = directory.getPath();
                    if (directory.exists()) {
                        printEntry(path, "exists!", prefix, n);
                    } else {
//                    directory.mkdirs();
                        printEntry(path, "created!", prefix, n);
                    }
                }
            }
        }
    }

    @Nullable
    public static File findFile(
            @Nonnull final Collection<File> optionalFiles,
            @Nonnull final String prefix) {
        final Integer n = maxFileEntry(optionalFiles);
        for (File optFile : optionalFiles) {
            if (optFile != null) {
                String path = optFile.getPath();
                if (optFile.exists()) {
                    printEntry(path, "Found", prefix, n);
                    return optFile;
                } else {
                    printEntry(path, "Not Found", prefix, n);
                }
            }
        }
        return null;
    }

    @Nonnull
    public static File ensureFile(
            @Nonnull final List<File> optionalFiles,
            @Nonnull final String prefix,
            @Nonnull final String content) throws IOException {

        if (optionalFiles.isEmpty()) {
            throw new RuntimeException("No optional files to create file " +
                    "from!");
        }

        File foundFile = findFile(optionalFiles, prefix);
        if (foundFile == null) {
            for (File f : optionalFiles) {
                if (f != null) {
                    System.out.println("creating example config file at " + f
                            .getPath());
                    System.out.println("writing...");
                    System.out.println(content);
                    FsTool.write(f.getAbsoluteFile(), content);
                    return f;
                }
            }
            throw new RuntimeException("No optional files to create.");
        } else {
            return foundFile;
        }
    }

    public static String getSampleConfigFileContent() {
        try {
            ConfigurationOptions options =
                    ConfigurationFactory.create(ConfigurationFactory.Flavour
                            .BASIC);
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

    public static void runInitialize() throws IOException {
        System.out.println("checking for configuration file:");
        File foundConfigFile =
                ensureFile(getPossibleConfigFiles(null), "  ",
                        getSampleConfigFileContent());

        ConfigurationOptions configurationOptions =
                loadConfigurationFromFile(foundConfigFile);


        Collection<File> javascriptSourceDirs =
                Collections2.transform(configurationOptions
                        .javascriptSourceDirectories, STRING_TO_FILE);

        System.out.println("Checking for javascript source directories...");
        if (javascriptSourceDirs != null && !javascriptSourceDirs.isEmpty()) {
            ensureDirectories(javascriptSourceDirs, "   ");
        } else {
            System.out.println("WARNING: please specify javascript " +
                    "directories...");
        }

        Collection<File> soySourceDirectories =
                Collections2.transform(configurationOptions
                        .soySourceDirectories, STRING_TO_FILE);

        if (soySourceDirectories != null && !soySourceDirectories.isEmpty()) {
            ensureDirectories(soySourceDirectories, "   ");
        } else {
            System.out.println("WARNING: please specify soy " +
                    "directories...");
        }

        Collection<File> gssSourceDirectories =
                Collections2.transform(configurationOptions
                        .gssSourceDirectories,
                        STRING_TO_FILE);

        if (gssSourceDirectories != null && !gssSourceDirectories.isEmpty()) {
            ensureDirectories(gssSourceDirectories, "   ");
        } else {
            System.out.println("WARNING: please specify gss " +
                    "directories...");
        }
    }


    public static void runCommand(@Nonnull final Command command)
            throws BuildException, IOException {
        switch (command) {
            case BUILD:
                runBuild();
            case TEMPLATES:
                runTemplates();
            case HTML:
                runHtml();
                break;
            case INITIALIZE:
                runInitialize();
                break;
        }
    }

    public static void runInteractiveCommandLine() throws IOException {

        ConsoleReader reader = new ConsoleReader();
        reader.setPrompt(" closure-cli > ");
        reader.setBellEnabled(false);
        Terminal terminal = reader.getTerminal();
        terminal.setEchoEnabled(false);


        LinkedList<Completer> completers = new LinkedList<Completer>();

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
            try {
                Command command =
                        selectCommand(SUPPORTED_COMMANDS, line, Command.HELP);
                switch (command) {
                    case EXIT:
                        out.println("exiting...");
                        out.close();
                        System.exit(0);
                        break;
                    case HELP:
                        out.print("Available Commands:");
                        for (Command c : SUPPORTED_COMMANDS) {
                            out.print(" ");
                            out.print(c.toString());
                        }
                        out.println(".");
                        break;
                    default:
                        runCommand(command);
                        break;
                }
            } catch (BuildException e) {
                e.printStackTrace(out);
            }
            out.flush();
        }
    }

    public static CommandLineOptions parseArguments(String[] args)
            throws CmdLineException {
        CommandLineOptions options = new CommandLineOptions();
        CmdLineParser parser = new CmdLineParser(options);
        parser.parseArgument(args);
        return options;
    }


    public static void main(String[] args) throws Exception {

        args = new String[]{
                "initialize"
        };

        CommandLineOptions options = parseArguments(args);
        loadConfiguration(options.configFile);
        final Command command =
                selectCommand(SUPPORTED_COMMANDS, options.command);
        if (command != null) {
            runCommand(command);
        } else {
            runInteractiveCommandLine();
        }

    }
}
