package liebenberg.closure_cli.config;


import liebenberg.closure_utilities.build.ClosureOptions;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.List;
import java.util.Set;

public class GssConfig implements ConfigInterface {

    public File renameMap;

    public Set<File> sourceDirectories;

    public List<String> entryPoints;

    @Override
    public void load(@Nonnull ClosureOptions closureOptions) {
        if (renameMap != null) {
            closureOptions.setCssClassRenameMap(renameMap);
        }

        if(sourceDirectories != null) {
            closureOptions.setGssSourceDirectories(sourceDirectories);
        }

        if(entryPoints != null) {
            closureOptions.setGssEntryPoints(entryPoints);
        }
    }
}
