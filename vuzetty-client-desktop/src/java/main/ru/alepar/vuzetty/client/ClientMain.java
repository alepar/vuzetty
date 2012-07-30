package ru.alepar.vuzetty.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.alepar.vuzetty.client.config.Configuration;
import ru.alepar.vuzetty.client.config.ConfigurationFactory;
import ru.alepar.vuzetty.client.gui.MonitorTorrent;
import ru.alepar.vuzetty.client.os.OsInteractionFactory;
import ru.alepar.vuzetty.client.remote.VuzettyClient;
import ru.alepar.vuzetty.client.remote.VuzettyRemote;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClientMain {

    private static final Logger log = LoggerFactory.getLogger(ClientMain.class);

    public static void main(String[] args) throws Exception {
		fixWmClass();
        args = cleanupJwsMess(args);
        log.debug("args = {}", Arrays.toString(args));

		final OsInteractionFactory osInteractionFactory = OsInteractionFactory.Native.create();
		prepStuff();
		final Configuration config = prepConfig();
		prepAssociations(config, osInteractionFactory);

        log.debug("looking for existing monitor");
        final VuzettyDiscovery discovery = new VuzettyDiscovery();
        try {
            VuzettyRemote vuzetty = discovery.discover();

            boolean shouldExit = false;
            if(vuzetty == null) {
                log.debug("no vuzetty found, creating new one");
                vuzetty = new MonitorTorrent(config, new VuzettyClient(config.getServerAddress(), config.getNickname()));
                discovery.announce(vuzetty);
            } else {
                log.debug("already running vuzetty found");
                shouldExit = true;
            }

            if (args.length > 0 && args[0] != null && !args[0].trim().isEmpty()) {
                log.debug("submitting torrent to vuze...");
                vuzetty.addTorrent(args[0]);
                log.debug("...ok");
            }

            if(shouldExit) {
                System.exit(0);
            }
        } catch (Exception e) {
            log.error("caught exception", e);
            System.exit(1);
        }
    }

	private static void fixWmClass() {
		try {
			Toolkit xToolkit = Toolkit.getDefaultToolkit();
			Field awtAppClassNameField = xToolkit.getClass().getDeclaredField("awtAppClassName");
			awtAppClassNameField.setAccessible(true);
			awtAppClassNameField.set(xToolkit, "ru-alepar-vuzetty_client");
		} catch (Exception e) {
			log.warn("failed to fix WM_CLASS", e);
		}
	}

	private static void prepAssociations(Configuration config, OsInteractionFactory osInteractionFactory) {
		if (config.associateWithMagnetLinks()) {
            osInteractionFactory.getAssociator().associateWithMagnetLinks();
        }
        if (config.associateWithTorrentFiles()) {
            osInteractionFactory.getAssociator().associateWithTorrentFiles();
        }
    }

    private static Configuration prepConfig() {
        ConfigurationFactory.askForUserInputIfNeeded();
        return ConfigurationFactory.makeConfiguration();
    }

    private static void prepStuff() throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        Thread.setDefaultUncaughtExceptionHandler(new DefaultThreadExceptionHandler());
    }

    private static String[] cleanupJwsMess(String[] args) {
        final List<String> newArgs = new ArrayList<String>(args.length);
        for (String arg : args) {
            if(!"-open".equals(arg)) {
                newArgs.add(arg);
            }
        }
        return newArgs.toArray(new String[newArgs.size()]);
    }

}
