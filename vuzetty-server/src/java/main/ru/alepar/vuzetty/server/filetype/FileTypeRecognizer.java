package ru.alepar.vuzetty.server.filetype;

import ru.alepar.vuzetty.common.api.FileType;

import java.io.File;

public interface FileTypeRecognizer {
    FileType recognize(File file);
}
