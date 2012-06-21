package ru.alepar.vuzetty.api;

import java.io.Serializable;

public class FileInfo implements Serializable {

    private final String name;
    private final long length;
    private final String url;

    public FileInfo(String name, long length, String url) {
        this.name = name;
        this.length = length;
        this.url = url;
    }
}
