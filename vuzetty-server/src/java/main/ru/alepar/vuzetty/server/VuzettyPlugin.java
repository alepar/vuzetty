package ru.alepar.vuzetty.server;

import org.gudy.azureus2.plugins.Plugin;
import org.gudy.azureus2.plugins.PluginException;
import org.gudy.azureus2.plugins.PluginInterface;
import org.gudy.azureus2.plugins.download.DownloadManager;
import org.gudy.azureus2.plugins.torrent.TorrentManager;
import org.gudy.azureus2.plugins.utils.Utilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.alepar.vuzetty.server.api.VuzeTorrentApi;

import java.net.InetSocketAddress;

public class VuzettyPlugin implements Plugin {

    private final Logger log = LoggerFactory.getLogger(VuzettyPlugin.class);

    @Override
    public void initialize(PluginInterface pluginInterface) throws PluginException {
        log.info("starting up vuzetty server ");
        final ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());

        final InetSocketAddress bindAddress = new InetSocketAddress(31337);
        final TorrentManager torrentManager = pluginInterface.getTorrentManager();
        final DownloadManager downloadManager = pluginInterface.getDownloadManager();
        final Utilities utilities = pluginInterface.getUtilities();

        log.info("binding vuzetty to " + bindAddress);
        new VuzettyServer(bindAddress, new VuzeTorrentApi(torrentManager, downloadManager, utilities));

        Thread.currentThread().setContextClassLoader(oldClassLoader);
        log.info("vuzetty server is up and running");
    }

}
