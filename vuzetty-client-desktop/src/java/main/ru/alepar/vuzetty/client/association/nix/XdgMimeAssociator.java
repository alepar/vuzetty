package ru.alepar.vuzetty.client.association.nix;

import ru.alepar.vuzetty.client.association.Associator;
import ru.alepar.vuzetty.client.association.Template;
import ru.alepar.vuzetty.client.os.JavaInstallation;
import ru.alepar.vuzetty.client.os.OsUtilities;
import ru.alepar.vuzetty.client.run.CmdResolver;
import ru.alepar.vuzetty.client.run.CmdRunner;

import java.io.File;

public class XdgMimeAssociator implements Associator {

    public static final String TEMPLATE_PATH = "ru/alepar/vuzetty/client/association/nix/desktop.template";
    public static final String DESKTOP_FILE_PATH = ".local/share/applications";
    public static final String DESKTOP_FILE_NAME = "vuzetty.desktop";

    public static final String XDGMIME_COMMAND = "xdg-mime";

    public static final String MIMETYPE_TORRENT = "application/x-bittorrent";
    public static final String MIMETYPE_MAGNET = "x-scheme-handler/magnet";

    private final JavaInstallation javaInstallation;
    private final CmdResolver cmdResolver;
    private final CmdRunner cmdRunner;

    private boolean fileCreated;

    public XdgMimeAssociator(JavaInstallation javaInstallation, CmdResolver cmdResolver, CmdRunner cmdRunner) {
        this.javaInstallation = javaInstallation;
        this.cmdResolver = cmdResolver;
        this.cmdRunner = cmdRunner;
    }

    @Override
    public void associateWithTorrentFiles() {
        createDesktopFile();
        associateWith(MIMETYPE_TORRENT);
    }

    @Override
    public void associateWithMagnetLinks() {
        createDesktopFile();
        associateWith(MIMETYPE_MAGNET);
    }

    private void createDesktopFile() {
        if(!fileCreated) {
            fileCreated = true;
            try {
                final File desktopPath = new File(OsUtilities.getUserHome(), DESKTOP_FILE_PATH);
                final File desktopFile = new File(desktopPath, DESKTOP_FILE_NAME);

                final Template template = new Template(TEMPLATE_PATH);
                template.populateWith(javaInstallation);
                template.writeToFile(desktopFile);
            } catch (Exception e) {
                throw new RuntimeException("failed to create desktop file", e);
            }
        }
    }

    private void associateWith(String mime) {
        final String xdgMimeCmd = cmdResolver.resolve(XDGMIME_COMMAND);
        cmdRunner.exec(new String[]{
                xdgMimeCmd,
                "default",
                DESKTOP_FILE_NAME,
                mime
        });
		cmdRunner.waitForExit();
    }

}
