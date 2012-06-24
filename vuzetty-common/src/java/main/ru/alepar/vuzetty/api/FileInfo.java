package ru.alepar.vuzetty.api;

import java.io.Serializable;

public class FileInfo implements Serializable {

    public final String name;
    public final long length;
    public final String url;

    public FileInfo(String name, long length, String url) {
        this.name = name;
        this.length = length;
        this.url = url;
    }
}
