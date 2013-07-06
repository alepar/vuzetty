package ru.alepar.vuzetty.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;
import ru.alepar.vuzetty.client.config.Configuration;
import ru.alepar.vuzetty.client.config.ConfigurationFactory;
import ru.alepar.vuzetty.client.gui.MonitorTorrent;
import ru.alepar.vuzetty.client.os.OsInteractionFactory;
import ru.alepar.vuzetty.client.remote.Client;
import ru.alepar.vuzetty.client.remote.VuzettyClient;
import ru.alepar.vuzetty.client.remote.VuzettyRemote;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClientMain {

    private static final Logger log = LoggerFactory.getLogger(ClientMain.class);

    public static void main(String[] args) throws Exception {
        rerouteJavaUtilLoggingToSlf4j();
        setUIAndExceptionHandler();
        args = cleanupJwsMess(args);
        log.debug("args = {}", Arrays.toString(args));

		final OsInteractionFactory osInteractionFactory = OsInteractionFactory.Native.create();
        osInteractionFactory.fixWmClass();
		final Configuration config = prepConfig();
		prepAssociations(config, osInteractionFactory);

        log.debug("looking for existing monitor");
        final VuzettyDiscovery discovery = new VuzettyDiscovery();
        try {
            VuzettyRemote vuzetty = discovery.discover();

            boolean shouldExit = false;
            if(vuzetty == null) {
                log.debug("no vuzetty found, creating new one");
				final Client client = new VuzettyClient(config.getServerAddress(), config.getNickname());
				client.setPollOldTorrents(config.showOwnTorrentsByDefault());
				vuzetty = new MonitorTorrent(config, client);
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

    private static void rerouteJavaUtilLoggingToSlf4j() {
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
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

    private static void setUIAndExceptionHandler() throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        Thread.setDefaultUncaughtExceptionHandler(new DefaultThreadExceptionHandler());
    }

    private static String[] cleanupJwsMess(String[] args) {
        final List<String> newArgs = new ArrayList<String>(args.length);
        for (String arg : args) {
            if(!"-open".equals(arg)) {
                newArgs.add(cleanupArg(arg));
            }
        }
        return newArgs.toArray(new String[newArgs.size()]);
    }

	private static String cleanupArg(String arg) {
		if(arg.startsWith("_")) {
			return arg.substring(1, arg.length());
		} else {
			return arg;
		}
	}

}
