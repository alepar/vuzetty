package ru.alepar.vuzetty.server.filetype;

import ru.alepar.vuzetty.common.api.FileType;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static ru.alepar.vuzetty.common.util.FileNameUtil.extractFileExtension;

public class ExtensionBasedFileTypeRecognizer implements FileTypeRecognizer {

    private Set<String> VIDEO_EXTENSIONS = new HashSet<String>(Arrays.asList(
            "avi",
            "mkv",
            "bdmv",
            "vob",
            "ts",
            "mov",
            "mp4"
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
