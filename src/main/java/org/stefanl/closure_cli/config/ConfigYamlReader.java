package org.stefanl.closure_cli.config;


import com.esotericsoftware.yamlbeans.YamlConfig;
import com.esotericsoftware.yamlbeans.YamlReader;
import org.stefanl.closure_cli.ConfigurationFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

public class ConfigYamlReader {

    private final File pwd;

    public ConfigYamlReader(@Nonnull final File pwd) {
        this.pwd = pwd;
    }


    @Nullable
    public ClosureConfig read(@Nonnull final Reader reader) throws IOException {
        final YamlReader yamlReader = new YamlReader(reader, getYAMLConfig());
        final ClosureConfig configOptions =
                yamlReader.read(ClosureConfig.class);
        yamlReader.close();
        if (configOptions != null) {
            return configOptions;
        } else {
            return ConfigurationFactory.createEmpty();
        }
    }

    @Nullable
    public ClosureConfig read(@Nonnull final File inputFile)
            throws IOException {
        final FileReader fileReader = new FileReader(inputFile);
        final ClosureConfig closureConfig = read(fileReader);
        fileReader.close();
        return closureConfig;
    }

    private YamlConfig yamlConfig;

    @Nonnull
    private YamlConfig getYAMLConfig() {
        if (yamlConfig == null) {
            yamlConfig = new YamlConfig();
            yamlConfig.setPropertyElementType(GssConfig.class,
                    "sourceDirectories", File.class);
            yamlConfig.setPropertyElementType(SoyConfig.class,
                    "sourceDirectories", File.class);
            yamlConfig.setPropertyElementType(JavascriptConfig.class,
                    "sourceDirectories", File.class);
            yamlConfig.setPropertyElementType(JavascriptConfig.class,
                    "testDirectories", File.class);
            yamlConfig.setScalarSerializer(File.class,
                    new FilePWDSerializer(pwd));
        }
        return yamlConfig;
    }


}
