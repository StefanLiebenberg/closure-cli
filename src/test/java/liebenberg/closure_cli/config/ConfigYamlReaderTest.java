package liebenberg.closure_cli.config;


import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import liebenberg.closure_utilities.build.ClosureBuilder;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: stefan
 * Date: 2014/02/09
 * Time: 12:38 AM
 * To change this template use File | Settings | File Templates.
 */
public class ConfigYamlReaderTest {

    File pwd = new File("src/test/resources/library-example");

    ConfigYamlReader yamlReader;


    @Before
    public void setUp() throws Exception {
        yamlReader = new ConfigYamlReader(pwd);
    }

    @After
    public void tearDown() throws Exception {
        yamlReader = null;
    }

    private ClosureConfig getClosureConfig() throws Exception {
        File configFile = new File(pwd, "closure.yaml");
        ClosureConfig config = yamlReader.read(configFile);
        Assert.assertNotNull(config);
        return config;
    }

    @Test
    public void testgetBuildCommands() throws Exception {
        ClosureConfig config = getClosureConfig();
        List<ClosureBuilder.BuildCommand> expected =
                Lists.newArrayList(ClosureBuilder.BuildCommand.JAVASCRIPT);
        List<ClosureBuilder.BuildCommand> actual = config.getBuildCommands();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testShouldDebug() throws Exception {
        ClosureConfig config = getClosureConfig();
        Assert.assertFalse(config.shouldDebug);
    }

    @Test
    public void testShouldCompile() throws Exception {
        ClosureConfig config = getClosureConfig();
        Assert.assertTrue(config.shouldCompile);
    }

    @Test
    public void testHtmlContent() throws Exception {
        ClosureConfig config = getClosureConfig();
        Assert.assertNull(config.htmlContent);
    }

    @Test
    public void testAssetsDirectory() throws Exception {
        ClosureConfig config = getClosureConfig();
        Assert.assertNull(config.assetsDirectory);
    }

    @Test
    public void testExternScripts() throws Exception {
        ClosureConfig config = getClosureConfig();
        Assert.assertNull(config.externalScripts);
    }

    @Test
    public void testExternalStylesheets() throws Exception {
        ClosureConfig config = getClosureConfig();
        Assert.assertNull(config.externalStylesheets);
    }

    @Test
    public void testJavascript() throws Exception {
        ClosureConfig config = getClosureConfig();
        Assert.assertNotNull(config.javascript);

        File expectedFile, actualFile;

        Assert.assertNull(config.javascript.definesFile);
        Assert.assertNull(config.javascript.dependencyFile);

        List<String> expectedEntryPoints, actualEntryPoints;
        expectedEntryPoints = Lists.newArrayList("company");
        actualEntryPoints = config.javascript.entryPoints;
        Assert.assertEquals(expectedEntryPoints, actualEntryPoints);

        Set<File> expectedDirs, actualDirs;
        expectedDirs = Sets.newHashSet(
                new File(pwd.getParentFile(),
                        "closure-library").getAbsoluteFile(),
                new File(pwd, "src").getAbsoluteFile());
        actualDirs = config.javascript.sourceDirectories;
        Assert.assertEquals(expectedDirs, actualDirs);

        expectedDirs = Sets.newHashSet(
                new File(pwd, "test").getAbsoluteFile());
        actualDirs = config.javascript.testDirectories;
        Assert.assertEquals(expectedDirs, actualDirs);


    }

    @Test
    public void testStylesheets() throws Exception {
        ClosureConfig config = getClosureConfig();
        Assert.assertNull(config.stylesheets);
    }

    @Test
    public void testTemplates() throws Exception {
        ClosureConfig config = getClosureConfig();
        Assert.assertNull(config.templates);
    }
}
