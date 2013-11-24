package liebenberg.closure_cli.config;


import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.scalar.ScalarSerializer;
import com.google.common.base.Function;
import liebenberg.closure_utilities.utilities.FS;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.nio.file.Paths;

public class FilePWDSerializer implements ScalarSerializer<File> {

    public static class ToFilePWDFunction implements Function<String, File> {

        public final File pwd;

        public ToFilePWDFunction(@Nonnull final File pwdFile) {
            this.pwd = pwdFile;
        }

        @Nullable
        @Override
        public File apply(@Nullable String input) {
            return Paths.get(pwd.getPath(), input).normalize().toFile()
                    .getAbsoluteFile();
        }
    }

    public static class FromFilePWDFunction implements Function<File, String> {

        public final File pwd;

        public FromFilePWDFunction(@Nonnull final File pwdFile) {
            this.pwd = pwdFile;
        }


        @Nullable
        @Override
        public String apply(@Nullable File input) {
            if (input != null) {
                return FS.getRelative(input, pwd);
            } else {
                return ".";
            }
        }
    }


    private final ToFilePWDFunction toFilePWDFunction;
    private final FromFilePWDFunction fromFilePWDFunction;

    public FilePWDSerializer(@Nonnull final File pwd) {
        toFilePWDFunction = new ToFilePWDFunction(pwd);
        fromFilePWDFunction = new FromFilePWDFunction(pwd);
    }

    @Override
    public File read(String value) throws YamlException {
        return toFilePWDFunction.apply(value);
    }

    @Override
    public String write(File object) throws YamlException {
        return fromFilePWDFunction.apply(object);
    }
}
