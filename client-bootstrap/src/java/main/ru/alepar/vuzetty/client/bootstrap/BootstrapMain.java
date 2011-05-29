package ru.alepar.vuzetty.client.bootstrap;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class BootstrapMain {

    private static final SimpleDateFormat backupFolderFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");

    private static final String UPDATE_FILE_NAME = "vuzetty-update.zip";
    private static final String BOOTSTRAP_FILE_NAME = "vuzetty-bootstrap.jar";

    public static void main(String[] args) {
        Class<?> clientMain = null;

        // classpath has ru.alepar.vuzetty.client.ClientMain = dev mode - start it up, skip update
        try {
            clientMain = loadClientMainFrom(BootstrapMain.class.getClassLoader());
            System.out.println("ClientMain found on classpath, skipping update");
        } catch (ClassNotFoundException ignored) {}

        // do update if necessary
        if (clientMain == null) {
            File updateFile = new File(UPDATE_FILE_NAME);
            if(updateFile.exists() && updateFile.isFile() && updateFile.canRead()) {
                System.out.println("update file found");
                File backupFolder = backupAllJarsInCwdExceptBootstrap();
                System.out.println("backed up to " + backupFolder.getAbsolutePath());
                extractUpdate(updateFile);
                System.out.println("extracted update");
                try {
                    clientMain = loadClientMainFrom(addJarsFromCwdOver(BootstrapMain.class.getClassLoader()));
                    System.out.println("loaded main class from update");
                } catch (Exception e) {
                    System.out.println("update failed");
                    deleteFiles(listJarsIn(new File(".")));
                    restoreBackup(backupFolder);
                    deleteFile(backupFolder);
                    deleteFile(updateFile);
                }
            }
        }

        // no update or update failed - bootstrap from cwd
        if (clientMain == null) {
            System.out.println("no update, regular startup");
            try {
                clientMain = loadClientMainFrom(addJarsFromCwdOver(BootstrapMain.class.getClassLoader()));
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("no ClientMain found", e);
            }

        }

        invokeMain(clientMain, args);
    }

    private static void deleteFiles(File[] files) {
        for (File file : files) {
            deleteFile(file);
        }
    }

    private static void extractUpdate(File updateFile) {
        try {
            ZipFile zipFile = new ZipFile(updateFile);
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                String fileName = entry.getName();
                InputStream stream = zipFile.getInputStream(entry);
                saveStreamTo(new File(".", fileName), stream);
            }
        } catch (Exception e) {
            throw new RuntimeException("failed to extract update", e);
        }
    }

    private static void saveStreamTo(File file, InputStream stream) {
        try {
            OutputStream os = new FileOutputStream(file);
            try {
                byte buf[] = new byte[102400];
                int read;
                while((read = stream.read(buf)) != -1) {
                    os.write(buf, 0, read);
                }
            } finally {
                os.close();
            }
        } catch (Exception e) {
            throw new RuntimeException("failed to save file " + file.getAbsolutePath(), e);
        } finally {
            try {
                stream.close();
            } catch (IOException ignored) {}
        }
    }

    private static void deleteFile(File file) {
        System.out.println("deleting file " + file.getAbsolutePath());
        file.delete();
    }

    private static void restoreBackup(File backupFolder) {
        System.out.println("restoring backup " + backupFolder.getAbsolutePath());
        moveFilesTo(new File("."), listJarsIn(backupFolder));
    }

    private static File backupAllJarsInCwdExceptBootstrap() {
        File backupFolder = new File(".", backupFolderFormat.format(new Date()));
        if (backupFolder.exists()) {
            throw new RuntimeException("backup folder already exists: " + backupFolder.getAbsolutePath());
        }
        if (!backupFolder.mkdirs()) {
            throw new RuntimeException("couldnot create folder: " + backupFolder.getAbsolutePath());
        }

        File curFolder = new File(".");
        File[] jarFiles = listJarsIn(curFolder);
        moveFilesTo(backupFolder, jarFiles);

        return backupFolder;
    }

    private static void moveFilesTo(File folder, File[] files) {
        System.out.println("moving files to " + folder.getAbsolutePath());
        for (File jarFile : files) {
            jarFile.renameTo(new File(folder, jarFile.getName()));
        }
    }

    private static File[] listJarsIn(File curFolder) {
        System.out.println("listing files in " + curFolder.getAbsolutePath());
        return curFolder.listFiles(new FileFilter() {
                @Override
                public boolean accept(File file) {
                    return file.isFile() && !file.getName().equals(BOOTSTRAP_FILE_NAME) && file.getName().endsWith(".jar");
                }
            });
    }

    private static ClassLoader addJarsFromCwdOver(ClassLoader parent) {
        System.out.println("constructing classLoader from cwd");
        return new URLClassLoader(toUrl(listJarsIn(new File("."))), parent);
    }

    private static URL[] toUrl(File[] files) {
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

    private static Class<?> loadClientMainFrom(ClassLoader classLoader) throws ClassNotFoundException {
        return classLoader.loadClass("ru.alepar.vuzetty.client.ClientMain");
    }

    private static void invokeMain(Class<?> clientMain, String[] args) {
        System.out.println("invoking ClientMain");
        try {
            clientMain.getMethod("main", String[].class).invoke(null, new Object[]{args});
        } catch (Exception e) {
            throw new RuntimeException("failed to invoke main method", e);
        }
    }

}
