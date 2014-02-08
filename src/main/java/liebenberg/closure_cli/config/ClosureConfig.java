package liebenberg.closure_cli.config;

import liebenberg.closure_utilities.build.ClosureBuilder;
import liebenberg.closure_utilities.build.ClosureOptions;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ClosureConfig implements ConfigInterface {

    public String build;

    public File assetsDirectory;

    public File outputDirectory;

    public Boolean shouldCompile = false;

    public Boolean shouldDebug = false;

    public Boolean shouldInline = false;

    public JavascriptConfig javascript;
    public SoyConfig templates;
    public GssConfig stylesheets;

    public List<File> propertyFiles;

    public List<String> externalStylesheets;

    public List<String> externalScripts;

    public String htmlContent;

    public List<ClosureBuilder.BuildCommand> getBuildCommands() {
        List<ClosureBuilder.BuildCommand> commands = new ArrayList<>();
        if (build != null && !build.isEmpty()) {
            for (String command : build.split(" ")) {
                commands.add(ClosureBuilder.BuildCommand.fromText(command
                        .trim()));
            }
        } else {
            commands.add(ClosureBuilder.BuildCommand.ALL);
        }
        return commands;
    }

    public void load(@Nonnull final ClosureOptions options) {

        if (outputDirectory != null) {
            options.setOutputDirectory(outputDirectory);
        }

        if (shouldCompile != null) {
            options.setShouldCompile(shouldCompile);
        }

        if (shouldDebug != null) {
            options.setShouldDebug(shouldDebug);
        }

        if (stylesheets != null) {
            stylesheets.load(options);
        }

        if (templates != null) {
            templates.load(options);
        }

        if (javascript != null) {
            javascript.load(options);
        }

        if (assetsDirectory != null) {
            options.setAssetsDirectory(assetsDirectory);
        }

        if (propertyFiles != null) {
            List<Configuration> configurations = new ArrayList<>();
            for (File propertyFile : propertyFiles) {
                try {
                    configurations.add(
                            new PropertiesConfiguration(propertyFile));
                } catch (ConfigurationException configurationException) {
                    throw new RuntimeException(configurationException);
                }
            }
            options.setConfigurations(configurations);
        }

    }


}
