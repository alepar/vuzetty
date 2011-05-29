package ru.alepar.vuzetty.client.bootstrap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class MainStarter {

    private static final Logger log = LoggerFactory.getLogger(MainStarter.class);

    private ClassLoader classLoader;
    private final String className;

    public MainStarter(String className) {
        this.className = className;
        this.classLoader = this.getClass().getClassLoader();
    }

    public void setClasspathFolder(File folder) {
        classLoader = new FolderClassLoader(folder, this.getClass().getClassLoader());
    }

    public void invokeMain(String[] args) {
        log.info("invoking {}#main()", className);
        try {
            getMainClass().getMethod("main", String[].class).invoke(null, new Object[]{args});
        } catch (Exception e) {
            throw new RuntimeException("failed to invoke main method", e);
        }
    }

    public boolean available() {
        try {
            getMainClass();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Class<?> getMainClass() throws ClassNotFoundException {
        return classLoader.loadClass(className);
    }

}
