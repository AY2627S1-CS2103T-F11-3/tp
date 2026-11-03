package seedu.address;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class MainAppTest {

    @TempDir
    Path temporaryFolder;

    @Test
    void getApplicationDirectory_jarFile_returnsParentDirectory() throws Exception {
        Path jarFile = Files.createFile(temporaryFolder.resolve("squadlink.jar"));

        assertEquals(temporaryFolder, MainApp.getApplicationDirectory(jarFile));
    }

    @Test
    void getApplicationDirectory_classDirectory_returnsCodeSourceDirectory() {
        Path classDirectory = temporaryFolder.resolve("classes");

        assertEquals(classDirectory.toAbsolutePath(), MainApp.getApplicationDirectory(classDirectory));
    }
}
