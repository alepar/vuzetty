package ru.alepar.vuzetty.common.util;

public class FileNameUtil {

    public static String extractFileExtension(String fileName) {
        return fileName.replaceAll(".*\\.([^.]*)", "$1");
    }

}
