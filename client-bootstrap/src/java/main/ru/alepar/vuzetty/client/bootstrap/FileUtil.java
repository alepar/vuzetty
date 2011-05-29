package ru.alepar.vuzetty.client.bootstrap;

import java.io.File;
import java.io.FileFilter;
import java.net.MalformedURLException;
import java.net.URL;

public class FileUtil {

    private static final String BOOTSTRAP_FILE_NAME = "vuzetty-bootstrap.jar";

    static File[] listJarsIn(File curFolder) {
        System.out.println("listing files in " + curFolder.getAbsolutePath());
        return curFolder.listFiles(new FileFilter() {
                @Override
                public boolean accept(File file) {
                    return file.isFile() && !file.getName().equals(BOOTSTRAP_FILE_NAME) && file.getName().endsWith(".jar");
                }
            });
    }

    static URL[] toUrl(File[] files) {
        try {
            URL[] urls = new URL[files.length];
            for (int i = 0; i < files.length; i++) {
                urls[i] = files[i].toURI().toURL();
            }
            return urls;
        } catch (MalformedURLException e) {
            throw new RuntimeException("couldnot make url", e);
        }
    }
}
