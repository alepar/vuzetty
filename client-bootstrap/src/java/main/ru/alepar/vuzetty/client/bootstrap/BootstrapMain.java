package ru.alepar.vuzetty.client.bootstrap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static ru.alepar.vuzetty.client.bootstrap.FileUtil.listJarsIn;

public class BootstrapMain {

    private static final SimpleDateFormat backupFolderFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
    private static final String UPDATE_FILE_NAME = "vuzetty-update.zip";

    private static final Logger log = LoggerFactory.getLogger(BootstrapMain.class);

    public static void main(String[] args) {
        MainStarter starter = new MainStarter("ru.alepar.vuzetty.client.ClientMain");

        // classpath has ru.alepar.vuzetty.client.ClientMain = dev mode - start it up, skip update
        if (starter.available()) {
            log.info("ClientMain found on classpath, skipping update");
            starter.invokeMain(args);
            return;
        }

        // do update if necessary
        File updateFile = new File(UPDATE_FILE_NAME);
        if(updateFile.exists() && updateFile.isFile() && updateFile.canRead()) {
            log.info("update file found");
            File backupFolder = backupAllJarsInCwdExceptBootstrap();
            log.info("backed up to {}", backupFolder.getAbsolutePath());
            extractUpdate(updateFile);
            log.info("extracted update");

            starter.setClasspathFolder(new File("."));
            if(starter.available()) {
                log.info("loaded main class from update");
            } else {
                log.info("update failed");
                deleteFiles(listJarsIn(new File(".")));
                restoreBackup(backupFolder);
                deleteFile(backupFolder);
                deleteFile(updateFile);
            }
        } else {
            log.info("no update, regular startup");
        }

        if(!starter.available()) {
            throw new RuntimeException("no ClientMain found");
        }

        starter.invokeMain(args);
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

}
