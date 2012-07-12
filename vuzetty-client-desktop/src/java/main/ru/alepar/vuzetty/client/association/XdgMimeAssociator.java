package ru.alepar.vuzetty.client.association;

import ru.alepar.vuzetty.client.os.JavaInstallation;
import ru.alepar.vuzetty.client.os.OsUtilities;
import ru.alepar.vuzetty.client.run.CmdResolver;
import ru.alepar.vuzetty.client.run.CmdRunner;

import java.io.*;

public class XdgMimeAssociator implements Associator {

    public static final String DESKTOP_FILE_PATH = ".local/share/applications";
    public static final String DESKTOP_FILE_NAME = "vuzetty.desktop";
    public static final String XDGMIME_COMMAND = "xdg-mime";

    public static final String MIMETYPE_TORRENT = "application/x-bittorrent";
    public static final String MIMETYPE_MAGNET = "x-scheme-handler/magnet";

    private final JavaInstallation javaInstallation;
    private final CmdResolver cmdResolver;
    private final CmdRunner cmdRunner;

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
        final File desktopPath = new File(OsUtilities.getUserHome(), DESKTOP_FILE_PATH);
        final File desktopFile = new File(desktopPath, DESKTOP_FILE_NAME);

        try {
            final String desktopFileContents = assembleDesktopFileContents();
            final Writer writer = new FileWriter(desktopFile);
            try {
                writer.write(desktopFileContents);
            } finally {
                writer.close();
            }
        } catch (Exception e) {
            throw new RuntimeException("failed to create desktop file", e);
        }
    }

    private String assembleDesktopFileContents() {
        return readTemplate()
                .replaceAll("%JAVAWS%", javaInstallation.getJavawsBinaryPath())
                .replaceAll("%EXT_ARGS%", javaInstallation.getJavawsExtArguments())
                .replaceAll("%OPEN_ARG%", javaInstallation.getJavawsOpenArgument());
    }

    private static String readTemplate() {
        String template;
        try {
            final StringBuilder result = new StringBuilder();

            final InputStream is = XdgMimeAssociator.class.getClassLoader().getResourceAsStream("ru/alepar/vuzetty/client/association/desktop.template");
            final Reader reader = new InputStreamReader(is);

            int read;
            char[] buf = new char[10240];
            try {
                while((read = reader.read(buf)) != -1) {
                    result.append(buf, 0, read);
                }
            } finally {
                reader.close();
            }

            template = result.toString();
        } catch (Exception e) {
            throw new RuntimeException("failed to read desktop file template", e);
        }
        return template;
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
