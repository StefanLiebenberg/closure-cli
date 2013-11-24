package liebenberg.closure_cli;

import liebenberg.closure_cli.config.ClosureConfig;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ConfigurationFactory {
    public enum Flavour {
        MAVEN, BASIC, LIBRARY
    }

    @Nonnull
    public static ClosureConfig createEmpty() {
        return new ClosureConfig();
    }

    public static ClosureConfig createJsBase(
            @Nonnull final String outputDirectory) {
        final ClosureConfig o = createEmpty();
//        o.outputDirectory = new File(outputDirectory;
//        o.javascriptDependencyOutputFile = outputDirectory + "/deps.js";
        o.shouldCompile = true;
        o.shouldDebug = false;
        return o;
    }

    @Nonnull
    public static ClosureConfig createBase(
            @Nonnull final String outputDirectory) {
        final ClosureConfig o = createJsBase(outputDirectory);
//        o.cssClassRenameMap = outputDirectory + "/cssRenameMap.js";
//        o.soyOutputDirectory = outputDirectory + "/compiled-templates";
//        o.gssEntryPoints =
//                Lists.newArrayList("company.example");
//        o.javascriptEntryPoints =
//                Lists.newArrayList("company.example");
        return o;
    }

    @Nonnull
    public static ClosureConfig createBasic(
            @Nullable String outputDirectory) {
        final ClosureConfig o = createBase(outputDirectory != null ?
                outputDirectory : "build");
//        o.assetsDirectory = "src/assets";
//        o.gssSourceDirectories = Sets.newHashSet("src/gss");
//        o.sourceDirectories = Sets.newHashSet("src/soy");
//        o.javascriptSourceDirectories =
//                Sets.newHashSet("src/javascript");
        return o;
    }

    @Nonnull
    public static ClosureConfig createMaven(
            @Nullable final String outputDirectory) {
        final ClosureConfig o = createBase(outputDirectory != null ?
                outputDirectory : "target/closure");
//        o.assetsDirectory = "src/main/resources/assets";
//        o.gssSourceDirectories = Sets.newHashSet("src/main/resources/gss");
//        o.sourceDirectories = Sets.newHashSet("src/main/resources/soy");
//        o.javascriptSourceDirectories =
//                Sets.newHashSet("src/main/javascript");
        return o;
    }

    @Nonnull
    public static ClosureConfig createLibrary(
            @Nullable final String outputDirectory) {
        final ClosureConfig o = createJsBase(
                outputDirectory != null ? outputDirectory : "src");
//        o.javascriptSourceDirectories = Sets.newHashSet("src");
//        o.javascriptEntryPoints = Lists.newArrayList("company");
//        o.build = Sets.newHashSet(BuildCommand.JAVASCRIPT);
        return o;
    }

    @Nonnull
    public static ClosureConfig create(
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
