package sk.kadlecek.htmlcsvtool.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {

    /**
     * Determines whether the directory with given path  exists.
     * @param path
     * @return
     */
    public static boolean doesDirectoryExist(String path) {
        return doesDirectoryExist(new File(path));
    }

    /**
     * Determines whether the file with given path exists.
     * @param path
     * @return
     */
    public static boolean doesFileExist(String path) {
        return doesFileExist(new File(path));
    }

    /**
     * Determines whether the file given by file instance exists.
     * @param file
     * @return
     */
    public static boolean doesDirectoryExist(File file) {
        return (file.exists() && file.isDirectory());
    }

    /**
     * Determines whether the directory given by file instance exists.
     * @param file
     * @return
     */
    public static boolean doesFileExist(File file) {
        return (file.exists() && !file.isDirectory());
    }

    /**
     * Determines whether given file is a directory.
     * @param path
     * @return
     */
    public static boolean isDirectory(String path) {
        return isDirectory(new File(path));
    }

    /**
     * Determines whether given file is a directory.
     * @param file
     * @return
     */
    public static boolean isDirectory(File file) {
        return file.isDirectory();
    }

    /**
     * Retrieves the list of all files and subdirectories in a given directory.
     * @param directory
     * @return
     */
    public static File[] getAllFilesAndSubdirectoriesInDirectory(File directory) {
        return directory.listFiles();
    }

    /**
     * Recursively retrieves the list of all files in given directory and its subdirectories.
     * @param directory
     * @return
     */
    public static List<File> retrieveAllFilesInDirectoryAndItsSubdirectories(File directory) {
        File[] filesAndSubdirs = getAllFilesAndSubdirectoriesInDirectory(directory);
        List<File> allFiles = new ArrayList<>();

        for (File f : filesAndSubdirs) {
            if (f.isDirectory()) {
                allFiles.addAll(retrieveAllFilesInDirectoryAndItsSubdirectories(f));
            }else {
                allFiles.add(f);
            }
        }
        return allFiles;
    }
}
