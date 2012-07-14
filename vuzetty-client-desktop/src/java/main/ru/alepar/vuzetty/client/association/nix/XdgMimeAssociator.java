package ru.alepar.vuzetty.client.association.nix;

import ru.alepar.vuzetty.client.association.Associator;
import ru.alepar.vuzetty.client.association.Template;
import ru.alepar.vuzetty.client.gui.MonitorTorrent;
import ru.alepar.vuzetty.client.os.JavaInstallation;
import ru.alepar.vuzetty.client.os.OsUtilities;
import ru.alepar.vuzetty.client.run.CmdRunner;

import java.io.*;

public class XdgMimeAssociator implements Associator {

    public static final String TEMPLATE_PATH = "ru/alepar/vuzetty/client/association/nix/desktop.template";
    public static final String DESKTOP_FILE_PATH = ".local/share/applications";
    public static final String DESKTOP_FILE_NAME = "vuzetty.desktop";
	public static final String ICON_FILE_PATH = ".local/share/icons";
	public static final String ICON_FILE_NAME = "vuze.png";

    public static final String XDGMIME_COMMAND = "/usr/bin/xdg-mime";

    public static final String MIMETYPE_TORRENT = "application/x-bittorrent";
    public static final String MIMETYPE_MAGNET = "x-scheme-handler/magnet";

	private final JavaInstallation javaInstallation;
    private final CmdRunner cmdRunner;

    private boolean fileCreated;

    public XdgMimeAssociator(JavaInstallation javaInstallation, CmdRunner cmdRunner) {
        this.javaInstallation = javaInstallation;
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
				createIcon();

				final File desktopPath = createUserHomePath(DESKTOP_FILE_PATH);
                final File desktopFile = new File(desktopPath, DESKTOP_FILE_NAME);

                final Template template = new Template(TEMPLATE_PATH);
                template.populateWith(javaInstallation);
                template.writeToFile(desktopFile);
            } catch (Exception e) {
                throw new RuntimeException("failed to create desktop file", e);
            }
        }
    }

	private static void createIcon() throws IOException {
		final File iconPath = createUserHomePath(ICON_FILE_PATH);
		final File iconFile = new File(iconPath, ICON_FILE_NAME);
		final FileOutputStream dst = new FileOutputStream(iconFile);
		try {
			final InputStream src = MonitorTorrent.class.getClassLoader().getResourceAsStream(MonitorTorrent.ICON_PATH);
			try {
				copyStream(src, dst);
			} finally {
    			src.close();
			}
		} finally {
			 dst.close();
		}
	}

	private static void copyStream(InputStream src, OutputStream dst) throws IOException {
		final byte[] buf = new byte[10240];
		int read;
		while((read = src.read(buf)) != -1) {
			dst.write(buf, 0, read);
		}
	}

	private static File createUserHomePath(String relPath) {
		final File fullPath = new File(OsUtilities.getUserHome(), relPath);
		//noinspection ResultOfMethodCallIgnored
		fullPath.mkdirs();  //best effort
		return fullPath;
	}

	private void associateWith(String mime) {
        cmdRunner.exec(new String[]{
				XDGMIME_COMMAND,
                "default",
                DESKTOP_FILE_NAME,
                mime
        });
		cmdRunner.waitForExit();
    }

}
