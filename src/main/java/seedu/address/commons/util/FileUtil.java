package seedu.address.commons.util;

import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * Writes and reads files
 */
public class FileUtil {

    private static final String CHARSET = "UTF-8";
    private static final String TEMP_FILE_PREFIX = "squadlink-";
    private static final String TEMP_FILE_SUFFIX = ".tmp";

    /**
     * Creates a file if it does not exist along with its missing parent directories.
     * @throws IOException if the file or directory cannot be created.
     */
    public static void createIfMissing(Path file) throws IOException {
        if (Files.exists(file)) {
            return;
        }

        createParentDirsOfFile(file);

        Files.createFile(file);
    }

    /**
     * Creates parent directories of file if it has a parent directory
     */
    private static void createParentDirsOfFile(Path file) throws IOException {
        Path parentDir = file.getParent();

        if (parentDir != null) {
            Files.createDirectories(parentDir);
        }
    }

    /**
     * Assumes file exists
     */
    public static String readFromFile(Path file) throws IOException {
        return new String(Files.readAllBytes(file), CHARSET);
    }

    /**
     * Writes given string to a file.
     * Will create the file if it does not exist yet.
     */
    public static void writeToFile(Path file, String content) throws IOException {
        Path absoluteFile = file.toAbsolutePath();
        createParentDirsOfFile(absoluteFile);
        Path temporaryFile = Files.createTempFile(absoluteFile.getParent(), TEMP_FILE_PREFIX, TEMP_FILE_SUFFIX);

        try {
            Files.write(temporaryFile, content.getBytes(CHARSET));
            replaceFile(temporaryFile, absoluteFile);
        } finally {
            Files.deleteIfExists(temporaryFile);
        }
    }

    /**
     * Replaces {@code destination} with {@code source}, using an atomic move when supported.
     */
    private static void replaceFile(Path source, Path destination) throws IOException {
        try {
            Files.move(source, destination, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException e) {
            Files.move(source, destination, StandardCopyOption.REPLACE_EXISTING);
        }
    }

}
