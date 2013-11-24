package liebenberg.closure_cli.config;

import liebenberg.closure_utilities.closure.ClosureBuilder;
import liebenberg.closure_utilities.closure.ClosureOptions;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ClosureConfig implements ConfigInterface {

    public String build;

    public File assetsDirectory;

    public File outputDirectory;

    public Boolean shouldCompile = false;

    public Boolean shouldDebug = false;

    public Boolean shouldInline = false;


    public Set<File> gssSourceDirectories;

    public List<String> gssEntryPoints;

    public JavascriptConfig javascript;
    public SoyConfig templates;
    public GssConfig stylesheets;


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
    }


}
