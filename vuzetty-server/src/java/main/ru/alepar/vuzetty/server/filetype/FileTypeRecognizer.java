package ru.alepar.vuzetty.server.filetype;

import ru.alepar.vuzetty.api.FileType;

import java.io.File;

public interface FileTypeRecognizer {
    FileType recognize(File file);
}
