package ru.alepar.vuzetty.server.filetype;

import ru.alepar.vuzetty.common.api.FileType;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static ru.alepar.vuzetty.common.util.FileNameUtil.*;

public class ExtensionBasedFileTypeRecognizer implements FileTypeRecognizer {

    private Set<String> VIDEO_EXTENSIONS = new HashSet<String>(Arrays.asList(
            "avi",
            "mkv",
            "m2ts",
            "vob",
            "ts",
            "mov",
            "mp4",
            "m4v"
    ));

    @Override
    public FileType recognize(File file) {
        final String fileExt = extractFileExtension(file.getName());
        if(VIDEO_EXTENSIONS.contains(fileExt)) {
            return FileType.VIDEO;
        }
        return FileType.UNKNOWN;
    }

}
