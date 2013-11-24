package liebenberg.closure_cli;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.junit.Assert;
import org.junit.Test;
import liebenberg.closure_cli.config.ClosureConfig;
import liebenberg.closure_cli.config.ConfigYamlReader;
import liebenberg.closure_cli.config.FilePWDSerializer;
import liebenberg.closure_utilities.closure.ClosureBuilder;
import liebenberg.closure_utilities.closure.ClosureOptions;
import liebenberg.closure_utilities.utilities.Immuter;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class ConfigurationOptionsTest {

    private static String RESOURCES = "src/test/resources/";

    @Nonnull
    public void getClosureConfig(@Nonnull File directory)
            throws IOException {
        final File configFile = new File(directory, "closure.yaml");
        Assert.assertTrue(configFile.exists());
        final ConfigYamlReader yamlReader = new ConfigYamlReader(directory);
        final ClosureConfig closureConfig = yamlReader.read(configFile);
        Assert.assertNotNull(closureConfig);
    }

    @Test
    public void testLibraryExample() throws Exception {

        final File exampleDirectory = new File(RESOURCES + "library-example");
        final File configFile = new File(exampleDirectory, "closure.yaml");
        Assert.assertTrue(configFile.exists());
        ConfigYamlReader yamlReader = new ConfigYamlReader(exampleDirectory);
        ClosureConfig closureConfig = yamlReader.read(configFile);
        Assert.assertNotNull(closureConfig);

        final ClosureOptions closureOptions = new ClosureOptions();
        closureConfig.load(closureOptions);

        final Function<String, File> toFile =
                new FilePWDSerializer.ToFilePWDFunction(exampleDirectory);

        Assert.assertEquals(
                Lists.newArrayList(ClosureBuilder.BuildCommand.JAVASCRIPT),
                closureConfig.getBuildCommands());

        Assert.assertEquals(
                Immuter.set(Sets.newHashSet(
                        toFile.apply("../closure-library"),
                        toFile.apply("src"))),
                closureOptions.getJavascriptSourceDirectories(false));

        Assert.assertEquals(
                toFile.apply("build"),
                closureOptions.getOutputDirectory());
        Assert.assertTrue(closureOptions.getShouldCompile());
        Assert.assertNull(closureOptions.getAssetsDirectory());
        Assert.assertNull(closureOptions.getAssetsUri());
        Assert.assertNull(closureOptions.getCssClassRenameMap());
        Assert.assertNull(closureOptions.getExternalScriptFiles());
        Assert.assertNull(closureOptions.getExternalStylesheets());
        Assert.assertNull(closureOptions.getGssEntryPoints());
        Assert.assertNull(closureOptions.getHtmlContent());
        Assert.assertNull(closureOptions.getOutputHtmlFile());
        Assert.assertNull(closureOptions.getOutputStylesheetFile());
        Assert.assertNull(closureOptions.getSoyOutputDirectory());
        Assert.assertNull(closureOptions.getSoySourceDirectories());
    }

    @Test
    public void testApplicationExample() throws Exception {

        final File exampleDirectory = new File(RESOURCES +
                "application-example");
        final File configFile = new File(exampleDirectory, "closure.yaml");
        Assert.assertTrue(configFile.exists());
        ConfigYamlReader yamlReader = new ConfigYamlReader(exampleDirectory);
        ClosureConfig closureConfig = yamlReader.read(configFile);
        Assert.assertNotNull(closureConfig);
        Assert.assertEquals(
                closureConfig.getBuildCommands(),
                Lists.newArrayList(ClosureBuilder.BuildCommand.ALL));

        final ClosureOptions closureOptions = new ClosureOptions();
        closureConfig.load(closureOptions);

        final Function<String, File> toFile =
                new FilePWDSerializer.ToFilePWDFunction(exampleDirectory);

        Assert.assertEquals(
                Immuter.set(Sets.newHashSet(
                        toFile.apply("src/gss"))),
                closureOptions.getGssSourceDirectories());

        Assert.assertNotNull(closureOptions.getSoySourceDirectories());
        Assert.assertEquals(
                Immuter.set(Sets.newHashSet(
                        toFile.apply("src/soy"))),
                closureOptions.getSoySourceDirectories());

        Assert.assertEquals(
                toFile.apply("build"),
                closureOptions.getOutputDirectory());
        Assert.assertNull(closureOptions.getAssetsDirectory());
        Assert.assertNull(closureOptions.getAssetsUri());
        Assert.assertNull(closureOptions.getCssClassRenameMap());
        Assert.assertNull(closureOptions.getExternalScriptFiles());
        Assert.assertNull(closureOptions.getExternalStylesheets());
        Assert.assertNotNull(closureOptions.getGssEntryPoints());
        Assert.assertEquals(
                (List) Lists.newArrayList("sample-import"),
                (List) closureOptions.getGssEntryPoints());
        Assert.assertNull(closureOptions.getHtmlContent());
        Assert.assertNull(closureOptions.getOutputHtmlFile());
        Assert.assertNull(closureOptions.getOutputStylesheetFile());
        Assert.assertNull(closureOptions.getSoyOutputDirectory());

    }

    @Test
    public void testMavenExample() throws Exception {
        final File closureLib = new File(RESOURCES + "closure-library");
        final File exampleDirectory = new File(RESOURCES +
                "maven-example");
        final File configFile = new File(exampleDirectory, "closure.yaml");
        Assert.assertTrue(configFile.exists());
        ConfigYamlReader yamlReader = new ConfigYamlReader(exampleDirectory);
        ClosureConfig closureConfig = yamlReader.read(configFile);
        Assert.assertNotNull(closureConfig);

        Assert.assertEquals(
                closureConfig.getBuildCommands(),
                Lists.newArrayList(ClosureBuilder.BuildCommand.ALL));

        final ClosureOptions closureOptions = new ClosureOptions();
        closureConfig.load(closureOptions);

        final Function<String, File> toFile =
                new FilePWDSerializer.ToFilePWDFunction(exampleDirectory);

        Assert.assertEquals(
                Immuter.set(Sets.newHashSet(
                        toFile.apply("src/main/gss"))),
                closureOptions.getGssSourceDirectories());

        Assert.assertEquals(
                Immuter.set(Sets.newHashSet(
                        toFile.apply("src/main/soy"))),
                closureOptions.getSoySourceDirectories());

        Assert.assertEquals(
                toFile.apply("target/closure"),
                closureOptions.getOutputDirectory());
        Assert.assertNull(closureOptions.getAssetsDirectory());
        Assert.assertNull(closureOptions.getAssetsUri());
        Assert.assertNull(closureOptions.getCssClassRenameMap());
        Assert.assertNull(closureOptions.getExternalScriptFiles());
        Assert.assertNull(closureOptions.getExternalStylesheets());
        Assert.assertNotNull(closureOptions.getGssEntryPoints());
        Assert.assertEquals(
                (List) Lists.newArrayList("sample-import"),
                (List) closureOptions.getGssEntryPoints());
        Assert.assertNull(closureOptions.getHtmlContent());
        Assert.assertNull(closureOptions.getOutputHtmlFile());
        Assert.assertNull(closureOptions.getOutputStylesheetFile());
        Assert.assertNull(closureOptions.getSoyOutputDirectory());
    }
}
