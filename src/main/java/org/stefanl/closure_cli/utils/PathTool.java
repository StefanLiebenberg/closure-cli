package org.stefanl.closure_cli.utils;


import java.io.File;
import java.nio.file.Paths;

public class PathTool {
    public static File getFileInPwd(File pwd, String path) {
        if (Paths.get(path).isAbsolute()) {
            return new File(path);
        } else {
            return new File(pwd, path);
        }
    }
}
