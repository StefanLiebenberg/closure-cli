package org.stefanl.closure_cli;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import javax.annotation.Nonnull;

public class ConfigurationFactory {
    public enum Flavour {
        MAVEN, BASIC
    }

    @Nonnull
    public static ConfigurationOptions createEmpty() {
        return new ConfigurationOptions();
    }

    @Nonnull
    public static ConfigurationOptions createBase() {
        ConfigurationOptions o = createEmpty();
        o.gssEntryPoints = Lists.newArrayList("company.example");
        o.javascriptEntryPoints = Lists.newArrayList("company.example");
        return o;
    }

    @Nonnull
    public static ConfigurationOptions createBasic() {
        ConfigurationOptions o = createBase();
        o.assetsDirectory = "src/assets";
        o.gssSourceDirectories = Sets.newHashSet("src/gss");
        o.soySourceDirectories = Sets.newHashSet("src/soy");
        o.javascriptSourceDirectories = Sets.newHashSet("src/javascript");

        o.outputDirectory = "build";
        o.cssClassRenameMap = "build/cssRenameMap.css";
        o.soyOutputDirectory = "build/compiled-templates";
        return o;
    }

    @Nonnull
    public static ConfigurationOptions createMaven() {
        ConfigurationOptions o = createBase();
        o.assetsDirectory = "src/main/resources/assets";
        o.gssSourceDirectories = Sets.newHashSet("src/main/resources/gss");
        o.soySourceDirectories = Sets.newHashSet("src/main/resources/soy");
        o.javascriptSourceDirectories = Sets.newHashSet("src/main/javascript");

        o.outputDirectory = "target/closure";
        o.cssClassRenameMap = "target/closure/cssRenameMap.css";
        o.soyOutputDirectory = "target/closure/compiled-templates";

        return o;
    }

    @Nonnull
    public static ConfigurationOptions create(
            @Nonnull final Flavour flavour) {
        switch (flavour) {
            case MAVEN:
                return createMaven();
            case BASIC:
                return createBasic();
            default:
                return createBase();
        }
    }
}
