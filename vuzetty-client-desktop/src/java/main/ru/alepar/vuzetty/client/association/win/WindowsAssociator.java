package ru.alepar.vuzetty.client.association.win;

import ru.alepar.vuzetty.client.association.Associator;
import ru.alepar.vuzetty.client.association.Template;
import ru.alepar.vuzetty.client.os.JavaInstallation;
import ru.alepar.vuzetty.client.run.CmdRunner;

import java.io.File;
import java.io.IOException;

public class WindowsAssociator implements Associator {

    public static final String TORRENT_TEMPLATE_PATH = "ru/alepar/vuzetty/client/association/win/torrent.reg";
    public static final String MAGNET_TEMPLATE_PATH = "ru/alepar/vuzetty/client/association/win/magnet.reg";

    private final JavaInstallation javaInstallation;
    private final CmdRunner cmdRunner;

    public WindowsAssociator(JavaInstallation javaInstallation, CmdRunner cmdRunner) {
        this.javaInstallation = javaInstallation;
        this.cmdRunner = cmdRunner;
    }

    @Override
    public void associateWithTorrentFiles() {
        try {
            final File tempFile = File.createTempFile("torrent", ".reg");
            try {
                final Template template = new Template(TORRENT_TEMPLATE_PATH);
                template.populateWith(javaInstallation);
                template.writeToFile(tempFile);

                importToRegistry(tempFile);
            } finally {
                //noinspection ResultOfMethodCallIgnored
                tempFile.delete();
            }
        } catch (Exception e) {
            throw new RuntimeException("torrent association failed", e);
        }
    }

    @Override
    public void associateWithMagnetLinks() {
        try {
            final File tempFile = File.createTempFile("magnet", ".reg");
            try {
                final Template template = new Template(MAGNET_TEMPLATE_PATH);
                template.populateWith(javaInstallation);
                template.writeToFile(tempFile);

                importToRegistry(tempFile);
            } finally {
                //noinspection ResultOfMethodCallIgnored
                tempFile.delete();
            }
        } catch (Exception e) {
            throw new RuntimeException("torrent association failed", e);
        }
    }

    private void importToRegistry(File tempFile) throws IOException {
        cmdRunner.exec(new String[] {
                "C:\\WINDOWS\\system32\\reg.exe",
                "import",
                tempFile.getCanonicalPath()
        });

        final int retCode = cmdRunner.waitForExit();
        if(retCode != 0) {
            throw new RuntimeException("non-zero retCode");
        }
    }

}
