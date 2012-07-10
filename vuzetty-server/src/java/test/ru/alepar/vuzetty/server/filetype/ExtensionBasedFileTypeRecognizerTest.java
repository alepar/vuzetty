package ru.alepar.vuzetty.server.filetype;

import org.junit.Test;
import ru.alepar.vuzetty.common.api.FileType;

import java.io.File;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class ExtensionBasedFileTypeRecognizerTest {

    private final FileTypeRecognizer recognizer = new ExtensionBasedFileTypeRecognizer();

    @Test
    public void recognizesExtensions() throws Exception {
        assertThat(recognizer.recognize(new File("c:/smth.avi")), equalTo(FileType.VIDEO));
        assertThat(recognizer.recognize(new File("c:\\smth.avi")), equalTo(FileType.VIDEO));
        assertThat(recognizer.recognize(new File("/home/alepar/smth.avi")), equalTo(FileType.VIDEO));
        assertThat(recognizer.recognize(new File("/home/alepar/smth.with.many.extensions.avi")), equalTo(FileType.VIDEO));
        assertThat(recognizer.recognize(new File("/home/alepar/smth.with.many.extensions.vob")), equalTo(FileType.VIDEO));
        assertThat(recognizer.recognize(new File("/home/alepar/smth.with.many.extensions.avob")), equalTo(FileType.UNKNOWN));
    }
}
