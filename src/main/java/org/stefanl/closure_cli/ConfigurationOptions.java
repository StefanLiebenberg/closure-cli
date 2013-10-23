package org.stefanl.closure_cli;

import java.util.List;
import java.util.Set;

public class ConfigurationOptions {

    public String assetsDirectory;

    public String outputDirectory;

    public String soyOutputDirectory;

    public Boolean shouldCompile;

    public Boolean shouldDebug;

    public Boolean shouldInline;


    public Set<String> soySourceDirectories;

    public Set<String> gssSourceDirectories;

    public List<String> gssEntryPoints;

    public Set<String> javascriptSourceDirectories;

    public List<String> javascriptEntryPoints;

    public String cssClassRenameMap;




}
