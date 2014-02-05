package liebenberg.closure_cli;

import com.google.common.collect.Lists;
import org.apache.commons.io.FileUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import liebenberg.closure_utilities.utilities.FS;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CommandLineRunnerTest {

    private File temporaryBuildDirectory;

    private static void main(@Nonnull final String... args) throws Exception {
        CommandLineRunner.mainInternal(args);
    }

    @Before
    public void setUp() throws Exception {
        temporaryBuildDirectory = FS.getTempDirectory();
        CommandLineRunner.setTesting();
    }

    @After
    public void tearDown() throws Exception {
        FileUtils.deleteDirectory(temporaryBuildDirectory);
    }

    @Test
    public void testMainLibrary() throws Exception {
        String configPath =
                "src/test/resources/library-example/closure.yaml";
        String outputPath = temporaryBuildDirectory.getPath();
        Assert.assertTrue(temporaryBuildDirectory.isDirectory());
        Assert.assertTrue(temporaryBuildDirectory.exists());
        main("build",
                "--pwd", new File("src/test/resources/library-example/")
                .getAbsolutePath(),
                "--config", configPath,
                "--outputDirectory", outputPath,
                "--compile");
        final File outputScriptFile = new File(temporaryBuildDirectory,
                "script.min.js");
        Assert.assertTrue(outputScriptFile.exists());
        Assert.assertTrue(outputScriptFile.isFile());
    }

    @Test
    public void testMainApplication() throws Exception {
        String configPath =
                "src/test/resources/application-example/closure.yaml";
        String outputPath = temporaryBuildDirectory.getPath();
        Assert.assertTrue(temporaryBuildDirectory.isDirectory());
        Assert.assertTrue(temporaryBuildDirectory.exists());
        main("build",
                "--pwd", new File("src/test/resources/application-example/")
                .getAbsolutePath(),
                "--config", configPath,
                "--outputDirectory", outputPath);
        final File outputScriptFile = new File(temporaryBuildDirectory,
                "script.min.js");
        Assert.assertFalse(outputScriptFile.exists());
    }

    @Test
    public void testMainMaven() throws Exception {

        String configPath =
                "src/test/resources/maven-example/closure.yaml";
        Assert.assertTrue(temporaryBuildDirectory.isDirectory());
        Assert.assertTrue(temporaryBuildDirectory.exists());

        String outputPath = temporaryBuildDirectory.getPath();
        main("build",
                "--pwd", new File("src/test/resources/maven-example/")
                .getAbsolutePath(),
                "--config", configPath,
                "--outputDirectory", outputPath,
                "--compile");

        final File outputScriptFile =
                new File(temporaryBuildDirectory, "script.min.js");
        Assert.assertTrue(outputScriptFile.exists());
        Assert.assertTrue(outputScriptFile.isFile());

        final File outputStylesheetFile =
                new File(temporaryBuildDirectory, "style.css");
        Assert.assertTrue(outputStylesheetFile.exists());
        Assert.assertTrue(outputStylesheetFile.isFile());

        final File outputHtmlFile =
                new File(temporaryBuildDirectory, "index.html");
        Assert.assertTrue(outputHtmlFile.exists());
        Assert.assertTrue(outputHtmlFile.isFile());

        Document document = Parser.parse(FS.read(outputHtmlFile),
                outputHtmlFile.toURI().toString());
        Assert.assertNotNull(document);
        Element headElement = document.head();
        Assert.assertNotNull(headElement);
        Elements scriptElements = headElement.getElementsByTag("script");
        List<String> stringList = new ArrayList<String>();
        for (Element element : scriptElements) {
            stringList.add(element.absUrl("src"));
        }
        Assert.assertEquals(
                Lists.newArrayList(outputScriptFile.getAbsoluteFile().toURI()
                        .toString()),
                stringList);

        Element bodyElement = document.body();
        Assert.assertNotNull(bodyElement);
    }
}
