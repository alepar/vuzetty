package ru.alepar.vuzetty.client.bootstrap;

import java.io.File;
import java.net.URLClassLoader;

import static ru.alepar.vuzetty.client.bootstrap.FileUtil.listJarsIn;
import static ru.alepar.vuzetty.client.bootstrap.FileUtil.toUrl;

public class FolderClassLoader extends URLClassLoader {

    public FolderClassLoader(File folder, ClassLoader parent) {
        super(toUrl(listJarsIn(folder)), parent);
    }
}
