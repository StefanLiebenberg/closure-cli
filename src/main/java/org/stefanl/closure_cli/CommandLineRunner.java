package org.stefanl.closure_cli;

import com.esotericsoftware.yamlbeans.YamlReader;
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
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
        System.err.println("Unknown command:" + strippedCommand);
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

    public static Function<String, File> STRING_TO_FILE =
            new Function<String, File>() {
                @Nullable
                @Override
                public File apply(@Nullable String
                                          input) {
                    if (input != null) {
                        return new File(input);
                    } else {
                        return null;
                    }
                }
            };

    public static iClosureBuildOptions getBuildOptions(
            @Nullable final File userConfigFile) throws IOException {

        ClosureBuildOptions buildOptions = new ClosureBuildOptions();

        File configFile = getConfigFile(userConfigFile);
        if (configFile != null) {
            try (FileReader fileReader = new FileReader(configFile)) {
                YamlReader yamlReader = new YamlReader(fileReader);
                ConfigurationOptions configOptions =
                        yamlReader.read(ConfigurationOptions.class);
                yamlReader.close();

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
                    //            FsTool.write(foundFile, content);
                    return f;
                }
            }
            throw new RuntimeException("No optional files to create.");
        } else {
            return foundFile;
        }
    }

    public static void runInitialize() throws IOException {
        System.out.println("checking for configuration file:");
        File foundConfigFile = ensureFile(getPossibleConfigFiles(null), "  ",
                "");


        Collection<File> javascriptSourceDirs = new ArrayList<>();
        javascriptSourceDirs.add(new File("src/javascript"));

        System.out.println("Checking for javascript source directories...");
        if (javascriptSourceDirs != null && !javascriptSourceDirs.isEmpty()) {
            ensureDirectories(javascriptSourceDirs, "   ");
        } else {
            System.out.println("WARNING: please specify javascript " +
                    "directories...");
        }

        Collection<File> soySourceDirectories = new ArrayList<File>();
        soySourceDirectories.add(new File("src/soy"));
        System.out.println("Checking for soy source directories...");
        if (soySourceDirectories != null && !soySourceDirectories.isEmpty()) {
            ensureDirectories(soySourceDirectories, "   ");
        } else {
            System.out.println("WARNING: please specify soy " +
                    "directories...");
        }

        Collection<File> gssSourceDirectories = new ArrayList<File>();
        gssSourceDirectories.add(new File("src/gss"));
        System.out.println("Checking for gss source directories...");
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
            default:
                System.err.println("ignoring the command: " + command
                        .toString().toLowerCase());
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


    public static void main(final String[] args) throws Exception {
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
