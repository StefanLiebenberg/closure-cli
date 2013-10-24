package org.stefanl.closure_cli;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ConfigurationFactory {
    public enum Flavour {
        MAVEN, BASIC, LIBRARY
    }

    @Nonnull
    public static ConfigurationOptions createEmpty() {
        return new ConfigurationOptions();
    }

    public static ConfigurationOptions createJsBase(
            @Nonnull final String outputDirectory) {
        final ConfigurationOptions o = createEmpty();
        o.outputDirectory = outputDirectory;
        o.javascriptDependencyOutputFile = outputDirectory + "/deps.js";
        o.shouldCompile = true;
        o.shouldDebug = false;
        return o;
    }

    @Nonnull
    public static ConfigurationOptions createBase(
            @Nonnull final String outputDirectory) {
        final ConfigurationOptions o = createJsBase(outputDirectory);
        o.cssClassRenameMap = outputDirectory + "/cssRenameMap.js";
        o.soyOutputDirectory = outputDirectory + "/compiled-templates";
        o.gssEntryPoints =
                Lists.newArrayList("company.example");
        o.javascriptEntryPoints =
                Lists.newArrayList("company.example");
        return o;
    }

    @Nonnull
    public static ConfigurationOptions createBasic(
            @Nullable String outputDirectory) {
        final ConfigurationOptions o = createBase(outputDirectory != null ?
                outputDirectory : "build");
        o.assetsDirectory = "src/assets";
        o.gssSourceDirectories = Sets.newHashSet("src/gss");
        o.soySourceDirectories = Sets.newHashSet("src/soy");
        o.javascriptSourceDirectories = Sets.newHashSet("src/javascript");
        return o;
    }

    @Nonnull
    public static ConfigurationOptions createMaven(
            @Nullable final String outputDirectory) {
        final ConfigurationOptions o = createBase(outputDirectory != null ?
                outputDirectory : "target/closure");
        o.assetsDirectory = "src/main/resources/assets";
        o.gssSourceDirectories = Sets.newHashSet("src/main/resources/gss");
        o.soySourceDirectories = Sets.newHashSet("src/main/resources/soy");
        o.javascriptSourceDirectories = Sets.newHashSet("src/main/javascript");
        return o;
    }

    @Nonnull
    public static ConfigurationOptions createLibrary(
            @Nullable final String outputDirectory) {
        final ConfigurationOptions o = createJsBase(
                outputDirectory != null ? outputDirectory : "src");
        o.javascriptSourceDirectories = Sets.newHashSet("src");
        o.javascriptEntryPoints = Lists.newArrayList("company");
        o.defaultBuild = Command.JAVASCRIPT.toString();
        return o;
    }

    @Nonnull
    public static ConfigurationOptions create(
            @Nonnull final Flavour flavour,
            @Nullable final String outputDirectory) {
        switch (flavour) {
            case MAVEN:
                return createMaven(outputDirectory);
            case BASIC:
                return createBasic(outputDirectory);
            case LIBRARY:
                return createLibrary(outputDirectory);
            default:
                return createBase(outputDirectory);
        }
    }
}
