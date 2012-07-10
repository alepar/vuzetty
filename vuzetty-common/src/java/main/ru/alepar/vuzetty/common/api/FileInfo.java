package ru.alepar.vuzetty.common.api;

import java.io.Serializable;

public class FileInfo implements Serializable {

    public final String name;
    public final long length;
    public final String url;
    public final FileType type;

    public FileInfo(String name, long length, String url, FileType type) {
        this.name = name;
        this.length = length;
        this.url = url;
        this.type = type;
    }
}
